package com.thomaskioko.podadddict.app.ui.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.thomaskioko.podadddict.app.PodAddictApplication;
import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.api.ApiClient;
import com.thomaskioko.podadddict.app.api.model.responses.ItunesLookUpResponse;
import com.thomaskioko.podadddict.app.data.PodCastContract;
import com.thomaskioko.podadddict.app.data.db.DbUtils;
import com.thomaskioko.podadddict.app.ui.PodCastEpisodeActivity;
import com.thomaskioko.podadddict.app.ui.PodCastListActivity;
import com.thomaskioko.podadddict.app.util.ApplicationConstants;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment to display PodCast details using a BottomSheet.
 *
 * @author Thomas Kioko
 */
public class PodcastBottomSheetDialogFragment extends BottomSheetDialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.imageView)
    ImageView mImageView;
    @Bind(R.id.imageViewBackground)
    RelativeLayout mRelativeLayout;
    @Bind(R.id.podcast_title)
    TextView mPodcastTitle;
    @Bind(R.id.podcast_artist_name)
    TextView mPodcastArtistName;
    @Bind(R.id.podcast_description)
    TextView mPodcastDescription;
    @Bind(R.id.description_title)
    TextView mTvDescriptionTitle;
    @Bind(R.id.button_subscribe)
    Button mBtnSubscribe;

    static PodCastListActivity mPodCastListActivity;
    private static Uri mUri;
    private int mPodcastFeedId, mRowId;
    private static final int LOADER_ID = 100;
    private static final String LOG_TAG = PodCastEpisodesFragment.class.getSimpleName();

    /**
     * Constructor
     *
     * @param uri UrI with selected item.
     * @return Fragment instance
     */
    public static PodcastBottomSheetDialogFragment newInstance(PodCastListActivity podCastListActivity, Uri uri) {
        mUri = uri;
        mPodCastListActivity = podCastListActivity;
        return new PodcastBottomSheetDialogFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.podcast_detail_bottom_sheet, container, false);
        ButterKnife.bind(this, view);


        return view;
    }

    @OnClick({R.id.button_subscribe})
    void onButtonSubscribeClicked(View view) {
        switch (view.getId()) {
            case R.id.button_subscribe:

                /**
                 * Check what text is set on the button. If the text is Subscribe, invoke lookUp api
                 * and save response in db
                 */
                if (mBtnSubscribe.getText().equals(getResources().getString(R.string.button_subscribe))) {
                    ApiClient apiClient = PodAddictApplication.getApiClientInstance();
                    apiClient.setEndpointUrl(ApplicationConstants.ITUNES_END_POINT);

                    mBtnSubscribe.setText(getResources().getString(R.string.loading));
                    mBtnSubscribe.setEnabled(false);

                    Call<ItunesLookUpResponse> iTunesLookUpResponseCall = apiClient.iTunesServices()
                            .getLookUpResponse(String.valueOf(mPodcastFeedId));
                    iTunesLookUpResponseCall.enqueue(new Callback<ItunesLookUpResponse>() {
                        @Override
                        public void onResponse(Call<ItunesLookUpResponse> call, Response<ItunesLookUpResponse> response) {
                            mBtnSubscribe.setText(getResources().getString(R.string.button_subscribed));

                            long dbRecordId = DbUtils.insertSubscriptionFeed(getActivity(), response.body().getResults(), mPodcastFeedId);
                            Log.i(LOG_TAG, "@onButtonSubscribeClicked:: " + dbRecordId);

                        }

                        @Override
                        public void onFailure(Call<ItunesLookUpResponse> call, Throwable t) {
                        }
                    });
                } else {
                    //Navigate the user to the playlist screen passing the track ID

                    Uri subscriptionUri = PodCastContract.PodcastFeedSubscriptionEntry.buildSubscriptionUri(mPodcastFeedId);
                    Intent intent = new Intent(mPodCastListActivity, PodCastEpisodeActivity.class).setData(subscriptionUri);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mPodCastListActivity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(mPodCastListActivity, mImageView,
                                mImageView.getTransitionName()).toBundle());
                    } else {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getActivity().startActivity(intent);
                    }
                }


                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    null,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // We need to start the enter transition after the data has loaded
        while (data.moveToNext()) {
            String imageUrl = data.getString(ApplicationConstants.COLUMN_PODCAST_FEED_IMAGE_URL);

            //We use glide to load the image
            Glide.with(getActivity())
                    .load(imageUrl)
                    .asBitmap()
                    .placeholder(R.color.placeholder)
                    .into(new BitmapImageViewTarget(mImageView) {
                        @Override
                        public void onResourceReady(Bitmap bitmap, final GlideAnimation glideAnimation) {
                            super.onResourceReady(bitmap, glideAnimation);
                            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {

                                    if (palette.getDarkVibrantSwatch() != null) {
                                        mRelativeLayout.setBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
                                        mBtnSubscribe.setTextColor(palette.getDarkVibrantSwatch().getRgb());

                                    } else if (palette.getMutedSwatch() != null) {
                                        mRelativeLayout.setBackgroundColor(palette.getMutedSwatch().getRgb());
                                        mBtnSubscribe.setTextColor(palette.getMutedSwatch().getRgb());
                                    }
                                    if (palette.getLightVibrantSwatch() != null) {
                                        mBtnSubscribe.setTextColor(palette.getLightVibrantSwatch().getRgb());
                                        mTvDescriptionTitle.setTextColor(palette.getLightVibrantSwatch().getRgb());
                                    } else if (palette.getLightMutedSwatch() != null) {
                                        mBtnSubscribe.setTextColor(palette.getLightMutedSwatch().getRgb());
                                        mTvDescriptionTitle.setTextColor(palette.getLightMutedSwatch().getRgb());
                                    }
                                }
                            });
                        }
                    });


            mRowId = data.getInt(ApplicationConstants.COLUMN_PODCAST_FEED_ROW_ID);
            mPodcastFeedId = data.getInt(ApplicationConstants.COLUMN_PODCAST_FEED_ID);
            mPodcastTitle.setText(data.getString(ApplicationConstants.COLUMN_PODCAST_FEED_TITLE));
            mPodcastArtistName.setText(data.getString(ApplicationConstants.COLUMN_PODCAST_FEED_ARTIST));
            mPodcastDescription.setText(data.getString(ApplicationConstants.COLUMN_PODCAST_FEED_SUMMARY));

            if (DbUtils.dbHasRecord(getActivity(), String.valueOf(mPodcastFeedId))) {
                mBtnSubscribe.setText(getResources().getString(R.string.button_subscribed));
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
