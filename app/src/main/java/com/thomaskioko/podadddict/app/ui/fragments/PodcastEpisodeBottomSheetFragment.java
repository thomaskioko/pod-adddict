package com.thomaskioko.podadddict.app.ui.fragments;


import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.github.clans.fab.FloatingActionButton;
import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.api.model.Item;
import com.thomaskioko.podadddict.app.util.ApplicationConstants;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Podcast episode BottomSheet fragment class.
 *
 * @author kioko
 */
public class PodcastEpisodeBottomSheetFragment extends BottomSheetDialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * BindView
     */
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
    @Bind(R.id.fabDownload)
    FloatingActionButton mFloatingActionButton;

    private static Uri mUri;
    private static Item mItem;
    private static final int LOADER_ID = 100;
    private static final String LOG_TAG = PodcastEpisodeBottomSheetFragment.class.getSimpleName();

    /**
     * Constructor
     *
     * @param item Feed Item
     * @param uri  UrI with selected item.
     * @return Fragment instance
     */
    public static PodcastEpisodeBottomSheetFragment newInstance(Item item, Uri uri) {
        mUri = uri;
        mItem = item;
        return new PodcastEpisodeBottomSheetFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_podcast_episode_bottom_sheet, container, false);
        ButterKnife.bind(this, view);

        //Set content on the views.
        mPodcastTitle.setText(mItem.getTitle());
        mPodcastDescription.setText(mItem.getItunesSummary());
        mPodcastArtistName.setText(mItem.getItunesAuthor());
        return view;
    }

    @OnClick({R.id.fabDownload})
    void onButtonSubscribeClicked(View view) {
        switch (view.getId()) {
            case R.id.fabDownload:
                //TODO:: Download the Episode or change this to favorite.
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
            String imageUrl = data.getString(ApplicationConstants.COLUMN_SUBSCRIBED_PODCAST_FEED_IMAGE_URL);

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
                                    //Set the background of the relative layout.
                                    if (palette.getDarkVibrantSwatch() != null) {
                                        mRelativeLayout.setBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
                                    } else if (palette.getMutedSwatch() != null) {
                                        mRelativeLayout.setBackgroundColor(palette.getMutedSwatch().getRgb());
                                    }

                                    //Set the color of the floating actionbar
                                    if (palette.getLightVibrantSwatch() != null) {
                                        mFloatingActionButton.setColorNormal(palette.getLightVibrantSwatch().getRgb());
                                    } else if (palette.getLightMutedSwatch() != null) {
                                        mFloatingActionButton.setColorNormal(palette.getLightMutedSwatch().getRgb());
                                    }

                                }
                            });
                        }
                    });
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
