package com.thomaskioko.podadddict.app.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.interfaces.Listener;
import com.thomaskioko.podadddict.app.interfaces.TrackListener;
import com.thomaskioko.podadddict.app.ui.adapter.NowPlayingPagerAdapter;
import com.thomaskioko.podadddict.app.ui.views.ProgressBarCompat;
import com.thomaskioko.podadddict.musicplayerlib.model.Track;
import com.thomaskioko.podadddict.musicplayerlib.player.PodAdddictPlayer;
import com.thomaskioko.podadddict.musicplayerlib.player.PodAdddictPlayerListener;
import com.thomaskioko.podadddict.musicplayerlib.player.PodAdddictPlaylistListener;
import com.viewpagerindicator.CirclePageIndicator;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * @author Thomas Kioko
 */
public class NowPlayingActivity extends AppCompatActivity implements
        PodAdddictPlayerListener, SeekBar.OnSeekBarChangeListener, Listener,
        TrackListener, PodAdddictPlaylistListener {

    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.pager)
    ViewPager mPager;
    @Bind(R.id.page_indicator)
    CirclePageIndicator mCirclePageIndicator;
    @Bind(R.id.playback_view_current_time)
    TextView mCurrentTime;
    @Bind(R.id.playback_view_duration)
    TextView mDuration;
    @Bind(R.id.playback_view_toggle_play)
    ImageView mPlayPause;
    @Bind(R.id.playback_view_seekbar)
    SeekBar mSeekBar;
    @Bind(R.id.playback_view_loader)
    ProgressBarCompat mLoader;

    private boolean mSeeking;
    private Track mTrack;
    PodAdddictPlayer mPodAdddictPlayer;
    PodAdddictPlayerListener mAdddictPlayerListener;
    TrackListener mRetrieveTracksListener = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        ButterKnife.bind(this);

        //Create player instance
        mPodAdddictPlayer = new PodAdddictPlayer.Builder()
                .from(this)
                .notificationActivity(NowPlayingActivity.class)
                .notificationIcon(R.drawable.ic_notification)
                .build();

        //Get the current playing track
        mTrack = mPodAdddictPlayer.getCurrentTrack();

        mToolBar.setTitle(mTrack.getTitle());

        setSupportActionBar(mToolBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(mTrack.getTitle());
        }


        mAdddictPlayerListener = this;

        NowPlayingPagerAdapter pagerAdapter = new NowPlayingPagerAdapter(getSupportFragmentManager(),
                mTrack, mRetrieveTracksListener);
        mPager.setAdapter(pagerAdapter);

        mCirclePageIndicator.setStrokeColor(getResources().getColor(R.color.white));
        mCirclePageIndicator.setFillColor(getResources().getColor(R.color.white));

        mCirclePageIndicator.setViewPager(mPager);

        mSeekBar.setOnSeekBarChangeListener(this);

        // synchronize the player view with the current player (loaded track, playing state, etc.)
        synchronize(mPodAdddictPlayer);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPodAdddictPlayer.registerPlayerListener(this);
        mPodAdddictPlayer.registerPlayerListener(mAdddictPlayerListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPodAdddictPlayer.unregisterPlayerListener(this);
        mPodAdddictPlayer.unregisterPlayerListener(mAdddictPlayerListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPodAdddictPlayer.destroy();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int[] secondMinute = getSecondMinutes(progress);
        mCurrentTime.setText(String.format(getResources().getString(R.string.playback_view_time), secondMinute[0], secondMinute[1]));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mSeeking = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mSeeking = false;
        this.onSeekToRequested(seekBar.getProgress());
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
    public void onTrackClicked(Track track) {

        mPodAdddictPlayer.addTrack(track, true);

//        if (mPodAdddictPlayer.getTracks().contains(track)) {
//            mPodAdddictPlayer.play(track);
//        } else {
//            boolean playNow = !mPodAdddictPlayer.isPlaying();
//
//            mPodAdddictPlayer.addTrack(track, playNow);
//
//        }

    }

    @Override
    public void onPlayerPlay(Track track, int position) {

    }

    @Override
    public void onPlayerPause() {
        mPlayPause.setImageResource(R.drawable.ic_play_white);
        if (mPlayPause.getVisibility() == INVISIBLE) {
            mLoader.setVisibility(INVISIBLE);
            mPlayPause.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onPlayerSeekTo(int milli) {
        mSeekBar.setProgress(milli);
    }

    @Override
    public void onPlayerDestroyed() {
        mPlayPause.setImageResource(R.drawable.ic_play_white);
    }

    @Override
    public void onBufferingStarted() {
        mLoader.setVisibility(VISIBLE);
        mPlayPause.setVisibility(INVISIBLE);
    }

    @Override
    public void onBufferingEnded() {
        mLoader.setVisibility(INVISIBLE);
        mPlayPause.setVisibility(VISIBLE);
    }

    @Override
    public void onProgressChanged(int milli) {
        if (!mSeeking) {
            mSeekBar.setProgress(milli);
            int[] secondMinute = getSecondMinutes(milli);
            String duration = String.format(getResources().getString(R.string.playback_view_time),
                    secondMinute[0], secondMinute[1]);
            mCurrentTime.setText(duration);
        }
    }

    @Override
    public void onErrorOccurred() {

    }

    @Override
    public void onTrackAdded(Track track) {

    }

    @Override
    public void onTrackRemoved(Track track, boolean isEmpty) {

    }


    /**
     * Helper method that converts time in milliseconds to standard time hh:mm
     *
     * @param milli time in milliseconds
     * @return formatted time.
     */
    private int[] getSecondMinutes(long milli) {
        int inSeconds = (int) milli / 1000;
        return new int[]{inSeconds / 60, inSeconds % 60};
    }

    /**
     * Synchronize the player view with the current player state.
     * <p/>
     * Basically, check if a track is loaded as well as the playing state.
     *
     * @param player player currently used.
     */
    public void synchronize(PodAdddictPlayer player) {
        mLoader.setVisibility(INVISIBLE);
        mPlayPause.setVisibility(VISIBLE);
    }
}
