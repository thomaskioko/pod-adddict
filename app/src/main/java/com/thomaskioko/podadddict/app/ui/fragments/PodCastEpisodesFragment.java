package com.thomaskioko.podadddict.app.ui.fragments;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.api.ApiClient;
import com.thomaskioko.podadddict.app.api.model.Enclosure;
import com.thomaskioko.podadddict.app.api.model.Item;
import com.thomaskioko.podadddict.app.api.model.responses.PodCastPlaylistResponse;
import com.thomaskioko.podadddict.app.data.PodCastContract;
import com.thomaskioko.podadddict.app.data.db.DbUtils;
import com.thomaskioko.podadddict.app.ui.adapter.PodcastEpisodeListAdapter;
import com.thomaskioko.podadddict.app.util.ApplicationConstants;
import com.thomaskioko.podadddict.app.util.LogUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Thomas Kioko
 */
public class PodCastEpisodesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.toolbar)
    android.widget.Toolbar toolbar;
    @Bind(R.id.photo)
    ImageView imageView;
    @Bind(R.id.recycler_view_list)
    RecyclerView mRecyclerView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.textViewMessage)
    TextView mTvErrorMessage;
    @BindInt(R.integer.detail_desc_slide_duration)
    int slideDuration;
    private Uri mUri;

    private Palette mPalette;
    private static final int LOADER_ID = 100;
    public static final String DETAIL_URI = "URI";
    private static final String LOG_TAG = PodCastEpisodesFragment.class.getSimpleName();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PodCastEpisodesFragment() {
    }



    public interface EpisodeCallback {
        void onEpisodeSelected(Uri uri, Item item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.podcast_detail, container, false);
        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    getActivity().finishAfterTransition();
                }
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide(Gravity.BOTTOM);
            slide.addTarget(R.id.description);
            slide.setInterpolator(AnimationUtils.loadInterpolator(getActivity(), android.R.interpolator
                    .linear_out_slow_in));
            slide.setDuration(slideDuration);
            getActivity().getWindow().setEnterTransition(slide);
        }

        RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());

        return rootView;
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
            String feedId = data.getString(ApplicationConstants.COLUMN_SUBSCRIBED_PODCAST_FEED_IMAGE_URL);
            String imageUrl = data.getString(ApplicationConstants.COLUMN_SUBSCRIBED_PODCAST_FEED_IMAGE_URL);

            Glide.with(getActivity())
                    .load(imageUrl)
                    .crossFade()
                    .placeholder(R.color.placeholder)
                    .into(imageView);


            String mFeedUrl = data.getString(ApplicationConstants.COLUMN_SUBSCRIBED_PODCAST_FEED_URL);

            try {
                //Check if there is any data saved locally
                if(DbUtils.episodeDbHasRecords(getActivity(), feedId)){
                    //Load data from DB
                    loadDataFromDb(feedId);
                }else {
                    fetchFeedData(feedId, mFeedUrl);
                }
            } catch (UnsupportedEncodingException e) {
                LogUtils.showErrorLog(LOG_TAG, "@onLoadFinished: " + e.getMessage());
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Helper method to load episodes from DB
     *
     * @param feedId Feed Id related to the episode.
     */
    private void loadDataFromDb(String feedId) {

        Uri podCastEpisodeUri = PodCastContract.PodCastEpisodeEntry.buildPodCastEpisode(Integer.parseInt(feedId));

        Cursor cursor = getActivity().getContentResolver().query(podCastEpisodeUri, null, null, null, null);
        List<Item> itemList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()){
                Item item = new Item();
                Enclosure enclosure = new Enclosure();
                enclosure.setUrl(cursor.getString(ApplicationConstants.COLUMN_PODCAST_EPISODE_STREAM_URL));

                item.setTitle(cursor.getString(ApplicationConstants.COLUMN_PODCAST_EPISODE_TITLE));
                item.setItunesAuthor(cursor.getString(ApplicationConstants.COLUMN_PODCAST_EPISODE_AUTHOR));
                item.setPubDate(cursor.getString(ApplicationConstants.COLUMN_PODCAST_EPISODE_PUBLISH_DATE));
                item.setItunesSummary(cursor.getString(ApplicationConstants.COLUMN_PODCAST_EPISODE_SUMMARY));
                item.setItunesDuration(cursor.getString(ApplicationConstants.COLUMN_PODCAST_EPISODE_DURATION));
                item.setEnclosure(enclosure);

                itemList.add(item);
            }

            mRecyclerView.setAdapter(new PodcastEpisodeListAdapter(getActivity(), itemList, mUri,
                    PodCastEpisodesFragment.this, new PodcastEpisodeListAdapter.PodCastEpisodeAdapterOnClickHandler() {
                @Override
                public void onClick(Uri uri, Item feedItem, View.OnClickListener vh) {


                }
            }));
        }
    }
    /**
     * Helper method to fetch podcast playlist
     *
     * @param feedUrl {@link String} Url containing podacast playlist
     */
    private void fetchFeedData(final String feedId, String feedUrl) throws UnsupportedEncodingException {

        //TODO:: Check if data exists locally. If not invoke the API
        ApiClient apiClient = new ApiClient();
        apiClient.setIsDebug(true);
        apiClient.setEndpointUrl(ApplicationConstants.LOCAL_SERVER_END_POINT);

        if (feedUrl.contains("%253A")) {
            feedUrl = feedUrl.replace("%253A", ":");
        }
        if (feedUrl.contains("%252F")) {
            feedUrl = feedUrl.replace("%252F", "/");
        }
        if (feedUrl.contains("%253D")) {
            feedUrl = feedUrl.replace("%253D", ".");
        }
        if (feedUrl.contains("%253Fid%253D")) {
            feedUrl = feedUrl.replace("%253Fid%253D", "?id=");
        }


        Call<PodCastPlaylistResponse> podCastPlaylistResponseCall = apiClient.iTunesServices()
                .getPodCastPlaylistResponse(
                        URLEncoder.encode(feedUrl, "UTF-8") //Encode the URl ensuring it's in the right format.
                );
        podCastPlaylistResponseCall.enqueue(new Callback<PodCastPlaylistResponse>() {
            @Override
            public void onResponse(Call<PodCastPlaylistResponse> call, Response<PodCastPlaylistResponse> response) {
                mProgressBar.setVisibility(View.GONE);
                mTvErrorMessage.setVisibility(View.GONE);

                if (response.code() == 200) {
                    List<Item> links = response.body().getRss().getChannel().getItem();

                    //Save the Podcast episode data locally
                    int record = DbUtils.insertPodcastEpisode(getActivity(), Integer.parseInt(feedId),
                            links);
                    if(record != 0){
                        mRecyclerView.setAdapter(new PodcastEpisodeListAdapter(getActivity(), links, mUri,
                                PodCastEpisodesFragment.this, new PodcastEpisodeListAdapter.PodCastEpisodeAdapterOnClickHandler() {
                            @Override
                            public void onClick(Uri uri, Item feedItem, View.OnClickListener vh) {


                            }
                        }));
                    }else{
                        //Notify the user something went wrong
                        mTvErrorMessage.setText(getResources().getString(R.string.error_no_message));
                    }


                }
            }

            @Override
            public void onFailure(Call<PodCastPlaylistResponse> call, Throwable t) {

                mProgressBar.setVisibility(View.VISIBLE);
                mTvErrorMessage.setText(t.getLocalizedMessage());

            }
        });
    }

    public void onClick(Uri mUri, Item feedItem) {

        ((PodCastEpisodesFragment.EpisodeCallback) getActivity())
                .onEpisodeSelected(mUri, feedItem );
    }
}
