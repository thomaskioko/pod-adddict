package com.thomaskioko.podadddict.app.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.netcosports.recyclergesture.library.swipe.SwipeToDismissDirection;
import com.netcosports.recyclergesture.library.swipe.SwipeToDismissGesture;
import com.netcosports.recyclergesture.library.swipe.SwipeToDismissStrategy;
import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.api.ApiClient;
import com.thomaskioko.podadddict.app.api.model.Item;
import com.thomaskioko.podadddict.app.api.model.responses.PodCastPlaylistResponse;
import com.thomaskioko.podadddict.app.data.PodCastContract;
import com.thomaskioko.podadddict.app.data.db.DbUtils;
import com.thomaskioko.podadddict.app.data.tasks.DatabaseAsyncTask;
import com.thomaskioko.podadddict.app.data.tasks.InsertEpisodesAsyncTask;
import com.thomaskioko.podadddict.app.interfaces.DbTaskCallback;
import com.thomaskioko.podadddict.app.interfaces.InsertEpisodesCallback;
import com.thomaskioko.podadddict.app.ui.adapter.TracksAdapter;
import com.thomaskioko.podadddict.app.ui.views.CroutonView;
import com.thomaskioko.podadddict.app.ui.views.PlaybackView;
import com.thomaskioko.podadddict.app.ui.views.TrackView;
import com.thomaskioko.podadddict.app.util.ApplicationConstants;
import com.thomaskioko.podadddict.app.util.Converter;
import com.thomaskioko.podadddict.app.util.GoogleAnalyticsUtil;
import com.thomaskioko.podadddict.app.util.LogUtils;
import com.thomaskioko.podadddict.musicplayerlib.model.Track;
import com.thomaskioko.podadddict.musicplayerlib.player.PodAdddictPlayer;
import com.thomaskioko.podadddict.musicplayerlib.player.PodAdddictPlaylistListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity used to display and play podcast episodes. This class also implements classes that
 * enable an episode to be played/streamed using {@link android.media.MediaPlayer} once selected.
 * <p>
 * This class also fetches podcast episodes and saves them locally adding offline viewing.
 *
 * @author kioko
 */
public class PodCastEpisodeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        DbTaskCallback, InsertEpisodesCallback, PlaybackView.Listener, PodAdddictPlaylistListener, TracksAdapter.Listener {

    //Bind view with butterknife
    @Bind(R.id.activity_artist_progress)
    ProgressBar mProgress;
    @Bind(R.id.activity_artist_callback)
    TextView mCallback;
    @Bind(R.id.activity_artist_list)
    RecyclerView mRetrieveTracksRecyclerView;
    @Bind(R.id.photo)
    ImageView mImageBanner;
    @Bind(R.id.activity_artist_banner)
    View mBanner;
    @Bind(R.id.activity_artist_playlist)
    RecyclerView mPlaylistRecyclerView;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private ArrayList<Track> mPlaylistTracks;
    private ArrayList<Track> mRetrievedTracks;

    private TracksAdapter mAdapter;
    private TracksAdapter mPlaylistAdapter;

    private PodAdddictPlayer mPodAdddictPlayer;
    private PlaybackView mPlaybackView;
    private Crouton mCrouton;
    private TrackView.Listener mRetrieveTracksListener;

    private Uri mUri;

    private int mScrollY;
    private int mRetrieveTrackListPaddingBottom;
    private int mRetrieveTrackListPaddingTop;
    private static final int LOADER_ID = 100;

    private DbTaskCallback mDbTaskCallback = this;
    private InsertEpisodesCallback mInsertEpisodesCallback = this;

    private String mFeedId, mImageUrl, mFeedUrl;
    public static final String LOG_TAG = PodCastEpisodeActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast_detail);

        ButterKnife.bind(this);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        //Initialize the player
        mPodAdddictPlayer = new PodAdddictPlayer.Builder()
                .from(this)
                .notificationActivity(NowPlayingActivity.class)
                .notificationIcon(R.drawable.ic_notification)
                .build();

        initRetrieveTracksRecyclerView();
        initPlaylistTracksRecyclerView();
        setTrackListPadding();

        // check if tracks are already loaded into the player.
        ArrayList<Track> currentsTracks = mPodAdddictPlayer.getTracks();
        if (currentsTracks != null) {
            mPlaylistTracks.addAll(currentsTracks);
        }

        // synchronize the player view with the current player (loaded track, playing state, etc.)
        mPlaybackView.synchronize(mPodAdddictPlayer);
        mPlaybackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NowPlayingActivity.class));
            }
        });

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            mUri = getIntent().getData();

            // Being here means we are in animation mode
            supportPostponeEnterTransition();

        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    /**
     * Helper method that fetches and displays tracks fetched in the players playlist.
     */
    private void initRetrieveTracksRecyclerView() {

        mRetrieveTracksListener = new TrackView.Listener() {
            @Override
            public void onTrackClicked(Track track) {
                if (mPodAdddictPlayer.getTracks().contains(track)) {
                    mPodAdddictPlayer.play(track);
                } else {
                    boolean playNow = !mPodAdddictPlayer.isPlaying();

                    mPodAdddictPlayer.addTrack(track, playNow);
                    mPlaylistAdapter.notifyDataSetChanged();

                    if (!playNow) {
                        toast(R.string.toast_track_added);
                    }
                }
            }
        };

        mRetrievedTracks = new ArrayList<>();
        mRetrieveTracksRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
        );

        mAdapter = new TracksAdapter(mRetrieveTracksListener, mRetrievedTracks);
        mRetrieveTracksRecyclerView.setAdapter(mAdapter);

        mScrollY = 0;
        RecyclerView.OnScrollListener mRetrieveTracksScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mScrollY += dy;
                mBanner.setTranslationY(-mScrollY / 2f);
            }
        };
        mRetrieveTracksRecyclerView.setOnScrollListener(mRetrieveTracksScrollListener);

        mRetrieveTrackListPaddingTop = getResources().getDimensionPixelSize(R.dimen.dimen_frame_height);
        mRetrieveTrackListPaddingBottom = getResources().getDimensionPixelSize(R.dimen.playback_view_height);
        mRetrieveTracksRecyclerView.setPadding(0, mRetrieveTrackListPaddingTop, 0, 0);
    }

    /**
     * Helper method to initialise the tracks recyclerView
     */
    private void initPlaylistTracksRecyclerView() {

        TrackView.Listener mPlaylistTracksListener = new TrackView.Listener() {
            @Override
            public void onTrackClicked(Track track) {
                mPodAdddictPlayer.play(track);
            }
        };

        mPlaybackView = new PlaybackView(this);
        mPlaybackView.setListener(this);

        mPlaylistTracks = new ArrayList<>();
        mPlaylistAdapter = new TracksAdapter(mPlaylistTracksListener, mPlaylistTracks);
        mPlaylistAdapter.setHeaderView(mPlaybackView);

        mPlaylistRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mPlaylistAdapter.setAdapterListener(this);

    }

    /**
     * Helper method to initialise the playlist recyclerView
     */
    private void setTrackListPadding() {
        mPlaylistRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mPlaylistRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                int headerListHeight = getResources().getDimensionPixelOffset(R.dimen.playback_view_height);
                mPlaylistRecyclerView.setPadding(0, mPlaylistRecyclerView.getHeight() - headerListHeight, 0, 0);
                mPlaylistRecyclerView.setAdapter(mPlaylistAdapter);

                // attach the dismiss gesture.
                new SwipeToDismissGesture.Builder(SwipeToDismissDirection.HORIZONTAL)
                        .on(mPlaylistRecyclerView)
                        .apply(new DismissStrategy())
                        .backgroundColor(getResources().getColor(R.color.grey))
                        .build();

                // hide if current play playlist is empty.
                if (mPlaylistTracks.isEmpty()) {
                    mPlaybackView.setTranslationY(headerListHeight);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, PodCastListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPodAdddictPlayer.registerPlayerListener(mPlaybackView);
        mPodAdddictPlayer.registerPlayerListener(mPlaylistAdapter);
        mPodAdddictPlayer.registerPlaylistListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPodAdddictPlayer.unregisterPlayerListener(mPlaybackView);
        mPodAdddictPlayer.unregisterPlayerListener(mPlaylistAdapter);
        mPodAdddictPlayer.unregisterPlaylistListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPodAdddictPlayer.destroy();
    }

    @Override
    public void onBackPressed() {
        if (mPlaybackView.getTop() < mPlaylistRecyclerView.getHeight() - mPlaybackView.getHeight()) {
            mPlaylistRecyclerView.getLayoutManager().smoothScrollToPosition(mPlaylistRecyclerView, null, 0);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onTogglePlayPressed() {
        mPodAdddictPlayer.togglePlayback();
    }

    @Override
    public void onPreviousPressed() {
        mPodAdddictPlayer.previous();
    }

    @Override
    public void onNextPressed() {
        mPodAdddictPlayer.next();
    }

    @Override
    public void onSeekToRequested(int milli) {
        mPodAdddictPlayer.seekTo(milli);
    }

    @Override
    public void onTrackAdded(Track track) {

        if (mPlaylistTracks.isEmpty()) {
            mPlaylistRecyclerView.animate().translationY(0);
            mRetrieveTracksRecyclerView.setPadding(0,
                    mRetrieveTrackListPaddingTop, 0, mRetrieveTrackListPaddingBottom);
        }
        mPlaylistTracks.add(track);
        mPlaylistAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTrackRemoved(Track track, boolean isEmpty) {
        if (mPlaylistTracks.remove(track)) {
            mPlaylistAdapter.notifyDataSetChanged();
        }
        if (isEmpty) {
            mPlaylistRecyclerView.animate().translationY(mPlaybackView.getHeight());
            mRetrieveTracksRecyclerView.setPadding(0, mRetrieveTrackListPaddingTop, 0, 0);
        }
    }

    // Adapter callbacks.
    @Override
    public void onTrackDismissed(int i) {
        mPodAdddictPlayer.removeTrack(i);
    }

    /**
     * Used to display crouton toast.
     *
     * @param message text to be displayed.
     */
    private void toast(@StringRes int message) {
        if (mCrouton != null) {
            mCrouton.cancel();
            mCrouton = null;
        }
        CroutonView mCroutonView = new CroutonView(this, getString(message));

        mCrouton = Crouton.make(this, mCroutonView, R.id.activity_artist_main_container);
        mCrouton.show();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
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


            Glide.with(this)
                    .load(mImageUrl)
                    .crossFade()
                    .placeholder(R.color.placeholder)
                    .into(mImageBanner);
            loadData();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Helper method that loads data from the DB. This helps us reused code since this functionality
     * is used in two places.
     * 1. When the fragment is first created.
     * 2. When the screen orientation changes and {@link #onSaveInstanceState(Bundle)} is invoked.
     */
    private void loadData() {
        try {

            /**
             * Check if there is any data saved locally. If not we fetch the data, save it locally
             * and load it from Sql.
             */
            if (DbUtils.episodeDbHasRecords(getApplicationContext(), mFeedId)) {

                Uri podCastEpisodeUri = PodCastContract.PodCastEpisodeEntry.buildPodCastEpisode(Integer.parseInt(mFeedId));

                //Use an async task to load the data. prevent the app from hanging
                new DatabaseAsyncTask(getApplicationContext(), mDbTaskCallback).execute(podCastEpisodeUri);
            } else {
                fetchFeedData(mFeedId, mFeedUrl);
            }
        } catch (UnsupportedEncodingException e) {
            LogUtils.showErrorLog(LOG_TAG, "@loadData: " + e.getMessage());
            GoogleAnalyticsUtil.trackException(getApplicationContext(), e);
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
                    List<Item> feedItemList = response.body().getRss().getChannel().getItem();

                    new InsertEpisodesAsyncTask(getApplicationContext(), mInsertEpisodesCallback,
                            feedItemList).execute(feedId);

                }
            }

            @Override
            public void onFailure(Call<PodCastPlaylistResponse> call, Throwable t) {
                //TODO:: Display error mesage
            }
        });
    }

    @Override
    public void CallbackRequest(int recordCount) {
        if (recordCount != 0) {
            loadData();
        }
    }

    @Override
    public void CallbackRequest(List<Item> resultArrayList) {

        mProgress.setVisibility(View.GONE);

        for (Item item : resultArrayList) {

            Track track = new Track();
            track.setTitle(item.getTitle());
            track.setArtist(item.getItunesAuthor());
            track.setDescription(item.getItunesSummary());
            track.setStreamUrl(item.getEnclosure().getUrl());
            track.setCreationDate(Converter.stringToDate(item.getPubDate()));
            track.setArtworkUrl(mImageUrl);

            if (!item.getItunesDuration().contains(":")) {
                track.setDurationInMilli(Long.parseLong(item.getItunesDuration()));
            }

            mRetrievedTracks.add(track);
        }

        mAdapter = new TracksAdapter(mRetrieveTracksListener, mRetrievedTracks);
        mRetrieveTracksRecyclerView.setAdapter(mAdapter);

    }


    /**
     * Swipe to dismiss strategy used to disable swipe to dismiss on the header.
     */
    private static class DismissStrategy extends SwipeToDismissStrategy {
        @Override
        public SwipeToDismissDirection getDismissDirection(int position) {
            if (position == 0) {
                return SwipeToDismissDirection.NONE;
            } else {
                return SwipeToDismissDirection.HORIZONTAL;
            }
        }
    }
}
