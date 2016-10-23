package com.thomaskioko.podadddict.app.ui.fragments;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.api.ApiClient;
import com.thomaskioko.podadddict.app.api.model.Item;
import com.thomaskioko.podadddict.app.api.model.responses.PodCastPlaylistResponse;
import com.thomaskioko.podadddict.app.data.PodCastContract;
import com.thomaskioko.podadddict.app.data.db.DbUtils;
import com.thomaskioko.podadddict.app.data.tasks.DatabaseAsyncTask;
import com.thomaskioko.podadddict.app.interfaces.DbTaskCallback;
import com.thomaskioko.podadddict.app.ui.adapter.PodcastEpisodeListAdapter;
import com.thomaskioko.podadddict.app.util.ApplicationConstants;
import com.thomaskioko.podadddict.app.util.Converter;
import com.thomaskioko.podadddict.app.util.GoogleAnalyticsUtil;
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
public class PodCastEpisodesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        DbTaskCallback {

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

    private DbTaskCallback mDbTaskCallback = this;
    private List<Item> itemList;
    private static final int LOADER_ID = 100;
    public static final String DETAIL_URI = "URI";
    private String mFeedId, mImageUrl, mFeedUrl;
    private int mPosition = RecyclerView.NO_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private static final String KEY_EPISODE_OBJECTS = "episode_objects";
    private static final String KEY_EPISODE_FEED_ID = "feed_id";
    private static final String KEY_EPISODE_IMAGE_URL = "image_url";
    private static final String KEY_EPISODE_FEED_URL = "feed_url";
    private static final String LOG_TAG = PodCastEpisodesFragment.class.getSimpleName();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PodCastEpisodesFragment() {
    }

    /**
     * Callback Interface
     */
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

        itemList = new ArrayList<>();

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    //Finish the transition when back button is clicked
                    getActivity().finishAfterTransition();
                }
            });
        }

        // We need to start the enter transition after the data has loaded
        getActivity().supportStartPostponedEnterTransition();

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

        /**
         * If there's instance state, mine it for useful information. The end-goal here is that the
         * user never knows that turning their device sideways or magically appeared to take
         * advantage of room, but data or place in the app was never actually *lost*. does crazy
         * lifecycle related things.  It should feel like some stuff stretched out.
         */
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SELECTED_KEY)) {
                // The Recycler View probably hasn't even been populated yet.  Actually perform the
                // swapout in onLoadFinished.
                mPosition = savedInstanceState.getInt(SELECTED_KEY);
            }
            mUri = savedInstanceState.getParcelable(DETAIL_URI);
            itemList = savedInstanceState.getParcelable(KEY_EPISODE_OBJECTS);
            mFeedId = savedInstanceState.getString(KEY_EPISODE_FEED_ID);
            mImageUrl = savedInstanceState.getString(KEY_EPISODE_IMAGE_URL);
            mFeedUrl = savedInstanceState.getString(KEY_EPISODE_FEED_URL);

            loadData();
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mRecyclerView) {
            mRecyclerView.clearOnScrollListeners();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        if (mUri != null) {
            outState.putParcelable(PodCastEpisodesFragment.DETAIL_URI, mUri);
        }

        outState.putString(KEY_EPISODE_FEED_ID, mFeedId);
        outState.putString(KEY_EPISODE_IMAGE_URL, mImageUrl);
        outState.putString(KEY_EPISODE_FEED_URL, mFeedUrl);
        outState.putParcelableArrayList(KEY_EPISODE_OBJECTS, (ArrayList<? extends Parcelable>) itemList);

        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                mUri,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // We need to start the enter transition after the data has loaded
        while (data.moveToNext()) {
            mFeedId = data.getString(ApplicationConstants.COLUMN_SUBSCRIBED_PODCAST_FEED_ID);
            mImageUrl = data.getString(ApplicationConstants.COLUMN_SUBSCRIBED_PODCAST_FEED_IMAGE_URL);
            mFeedUrl = data.getString(ApplicationConstants.COLUMN_SUBSCRIBED_PODCAST_FEED_URL);

            loadData();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void CallbackRequest(List<Item> resultArrayList) {

        if (resultArrayList != null) {
            mProgressBar.setVisibility(View.GONE);
            mTvErrorMessage.setVisibility(View.GONE);

            mRecyclerView.setAdapter(new PodcastEpisodeListAdapter(getActivity(), resultArrayList, mUri,
                    PodCastEpisodesFragment.this, new PodcastEpisodeListAdapter.PodCastEpisodeAdapterOnClickHandler() {
                @Override
                public void onClick(Uri uri, Item feedItem, View.OnClickListener vh) {
                    //TODO:: Handle item download.
                }
            }));
        } else {
            mTvErrorMessage.setVisibility(View.VISIBLE);
            mTvErrorMessage.setText(getString(R.string.error_db_message));
        }
    }

    /**
     * Helper method that loads data from the DB. This helps us reused code since this functionality
     * is used in two places.
     * 1. When the fragment is first created.
     * 2. When the screen orientation changes and {@link #onSaveInstanceState(Bundle)} is invoked.
     */
    private void loadData() {
        try {

            if (mPosition != RecyclerView.NO_POSITION) {
                // If we don't need to restart the loader, and there's a desired position to restore
                // to, do so now.
                mRecyclerView.smoothScrollToPosition(mPosition);
            }

            Glide.with(getActivity())
                    .load(mImageUrl)
                    .crossFade()
                    .placeholder(R.color.placeholder)
                    .into(imageView);

            /**
             * Check if there is any data saved locally. If not we fetch the data, save it locally
             * and load it from Sql.
             */
            if (DbUtils.episodeDbHasRecords(getActivity(), mFeedId)) {

                Uri podCastEpisodeUri = PodCastContract.PodCastEpisodeEntry.buildPodCastEpisode(Integer.parseInt(mFeedId));

                //Use an async task to load the data. prevent the app from hanging
                new DatabaseAsyncTask(getContext(), mDbTaskCallback).execute(podCastEpisodeUri);
            } else {
                fetchFeedData(mFeedId, mFeedUrl);
            }
        } catch (UnsupportedEncodingException e) {
            LogUtils.showErrorLog(LOG_TAG, "@onCreateView: " + e.getMessage());
            GoogleAnalyticsUtil.trackException(getActivity(), e);
        }
    }


    /**
     * Helper method to fetch podcast playlist
     *
     * @param feedUrl {@link String} Url containing podacast playlist
     */
    private void fetchFeedData(final String feedId, String feedUrl) throws UnsupportedEncodingException {

        ApiClient apiClient = new ApiClient();
        apiClient.setIsDebug(true);
        apiClient.setEndpointUrl(ApplicationConstants.LOCAL_SERVER_END_POINT);

        //Invoke API Endpoint to fetch feed episodes
        Call<PodCastPlaylistResponse> podCastPlaylistResponseCall = apiClient.iTunesServices()
                .getPodCastPlaylistResponse(
                        URLEncoder.encode(Converter.formatUrl(feedUrl), "UTF-8") //Encode the URl ensuring it's in the right format.
                );
        podCastPlaylistResponseCall.enqueue(new Callback<PodCastPlaylistResponse>() {
            @Override
            public void onResponse(Call<PodCastPlaylistResponse> call, Response<PodCastPlaylistResponse> response) {

                if (response.code() == 200) {
                    List<Item> links = response.body().getRss().getChannel().getItem();

                    //Save the Podcast episode data locally
                    int record = DbUtils.insertPodcastEpisode(getActivity(), Integer.parseInt(feedId),
                            links);
                    if (record != 0) {
                        loadData();
                    } else {
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

    /**
     * Method used to pass data when an item on the recyclerView is clicked.
     *
     * @param mUri     {@link Uri} Selected item URI
     * @param feedItem {@link Item} Episode Item
     */
    public void onClick(Uri mUri, Item feedItem, int position) {

        mPosition = position;

        ((PodCastEpisodesFragment.EpisodeCallback) getActivity()).onEpisodeSelected(mUri, feedItem);
    }
}
