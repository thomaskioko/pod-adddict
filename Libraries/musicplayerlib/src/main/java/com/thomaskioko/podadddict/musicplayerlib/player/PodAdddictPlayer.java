package com.thomaskioko.podadddict.musicplayerlib.player;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.DrawableRes;

import com.thomaskioko.podadddict.musicplayerlib.R;
import com.thomaskioko.podadddict.musicplayerlib.model.Track;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Encapsulate network and player features.
 *
 * @author kioko
 */
public final class PodAdddictPlayer implements Action1<ArrayList<Track>> {

    private static final int STATE_STOPPED = 0x00000000;
    private static final int STATE_PAUSED = 0x00000001;
    private static final int STATE_PLAYING = 0x00000002;

    /**
     * Instance, singleton pattern.
     */
    private static PodAdddictPlayer sInstance;

    /**
     * WeakReference on the application context.
     */
    private WeakReference<Context> mApplicationContext;


    /**
     * Internal listener used to catch service callbacks
     */
    private PlaybackListener mInternalListener;

    /**
     * Listener which should be notified of playback events.
     */
    private ArrayList<PodAdddictPlayerListener> mPodAdddictPlayerListeners;

    /**
     * Listener which should be notified of playlist events.
     */
    private ArrayList<PodAdddictPlaylistListener> mPodAdddictPlaylistListeners;

    /**
     * Manage the playlist used by the player.
     */
    private PlayerPlaylist mPlayerPlaylist;

    /**
     * Manage the notification.
     */
    private NotificationManager mNotificationManager;

    /**
     * Used to know the player state
     */
    private int mState;

    /**
     * Used to know if the current client instance has been closed.
     */
    private boolean mIsClosed;

    /**
     * Used to know if the player destroy should be delayed. Is delayed, destroy will be done
     * once the {@link PlaybackService} stop self.
     */
    private boolean mDestroyDelayed;

    /**
     * Private default constructor.
     */
    private PodAdddictPlayer() {

    }

    /**
     * Singleton pattern.
     *
     * @param applicationContext context used to initiate 
     *
     */
    private PodAdddictPlayer(Context applicationContext) {

        mIsClosed = false;
        mDestroyDelayed = false;
        mState = STATE_STOPPED;
        mPodAdddictPlayerListeners = new ArrayList<>();
        mPodAdddictPlaylistListeners = new ArrayList<>();

        mApplicationContext = new WeakReference<>(applicationContext);

        mPlayerPlaylist = PlayerPlaylist.getInstance();
        mNotificationManager = NotificationManager.getInstance(getContext());

        initInternalListener(applicationContext);
    }

    /**
     * Simple Sound cloud client initialized with a client id.
     *
     * @param context  context used to instantiate internal components, no hard reference will be kept.
     * @return instance of {@link PodAdddictPlayer}
     */
    private static PodAdddictPlayer getInstance(Context context) {
        if (sInstance == null || sInstance.mIsClosed) {
            sInstance = new PodAdddictPlayer(context.getApplicationContext());
        }
        // reset destroy request each time an instance is requested.
        sInstance.mDestroyDelayed = false;
        return sInstance;
    }

    @Override
    public void call(ArrayList<Track> tracks) {
        addTracks(tracks);
    }

    /**
     * Release resources associated with the player.
     */
    public void destroy() {
        if (mIsClosed) {
            return;
        }
        if (mState != STATE_STOPPED) {
            mDestroyDelayed = true;
            return;
        }
        mIsClosed = true;

        PlaybackService.unregisterListener(getContext(), mInternalListener);
        mInternalListener = null;

        mApplicationContext.clear();
        mApplicationContext = null;

        mPlayerPlaylist = null;
        mPodAdddictPlayerListeners.clear();
    }

    /**
     * Start the playback. First track of the queue will be played.
     * <p/>
     * If the PodAdddict player is currently paused, the current track will be restart at the stopped position.
     */
    public void play() {
        checkState();
        if (mState == STATE_PAUSED) {
            PlaybackService.resume(getContext());
        } else if (mState == STATE_STOPPED) {
            Track track = mPlayerPlaylist.getCurrentTrack();
            if (track != null) {
                PlaybackService.play(getContext(), track);
            } else {
                return;
            }
        }
        mState = STATE_PLAYING;
    }

    /**
     * Play a track at a given position in the player playlist.
     *
     * @param position position of the track in the playlist.
     */
    public void play(int position) {
        checkState();
        ArrayList<Track> tracks = mPlayerPlaylist.getPlaylist().getTracks();
        if (position >= 0 && position < tracks.size()) {
            Track trackToPlay = tracks.get(position);
            mPlayerPlaylist.setPlayingTrack(position);
            PlaybackService.play(getContext(), trackToPlay);
        }

    }

    /**
     * Play a track which have been added to the player playlist.
     * <p/>
     * See also {@link PodAdddictPlayer#addTrack(Track)}
     * {@link PodAdddictPlayer#addTracks(List)}
     *
     * @param track the track to play.
     */
    public void play(Track track) {
        checkState();

        ArrayList<Track> tracks = mPlayerPlaylist.getPlaylist().getTracks();
        int position = tracks.indexOf(track);
        if (position > -1) {
            mPlayerPlaylist.setPlayingTrack(position);
            PlaybackService.play(getContext(), track);
        }
    }

    /**
     * Pause the playback.
     */
    public void pause() {
        checkState();
        if (mState == STATE_PLAYING) {
            PlaybackService.pause(getContext());
            mState = STATE_PAUSED;
        }
    }

    /**
     * Toggle playback.
     * <p/>
     * Basically, pause the player if playing and play if paused.
     */
    public void togglePlayback() {
        switch (mState) {
            case STATE_STOPPED:
            case STATE_PAUSED:
                play();
                break;
            case STATE_PLAYING:
                pause();
                break;
            default:
                break;
        }
    }

    /**
     * Stop the current played track and load the next one if the playlist isn't empty.
     * <p/>
     * If the current played track is the last one, the first track will be loaded.
     *
     * @return false if current playlist is empty.
     */
    public boolean next() {
        checkState();
        if (mPlayerPlaylist.isEmpty()) {
            return false;
        }
        PlaybackService.play(getContext(), mPlayerPlaylist.next());
        return true;
    }

    /**
     * Stop the current played track and load the previous one.
     * <p/>
     * If the current played track is the first one, the last track will be loaded.
     *
     * @return false if current playlist is empty.
     */
    public boolean previous() {
        checkState();
        if (mPlayerPlaylist.isEmpty()) {
            return false;
        }
        PlaybackService.play(getContext(), mPlayerPlaylist.previous());
        return true;
    }

    /**
     * Seek to the precise track position.
     * <p/>
     * The current playing state of the PodAdddict player will be kept.
     * <p/>
     * If playing it remains playing, if paused it remains paused.
     *
     * @param milli time in milli of the position.
     */
    public void seekTo(int milli) {
        checkState();
        if (!mPlayerPlaylist.isEmpty()) {
            PlaybackService.seekTo(getContext(), milli);
        }
    }

    /**
     * Add a track to the current PodAdddict player playlist.
     * <p/>
     * See also {@link PodAdddictPlayer#addTrack(Track, boolean)}
     *
     * @param track {@link Track} to be
     *              added to the player.
     */
    public void addTrack(Track track) {
        addTrack(track, false);
    }

    /**
     * Add a track to the current PodAdddict player playlist.
     *
     * @param track   {@link Track} to be
     *                added to the player.
     * @param playNow true to play the track immediately.
     */
    public void addTrack(Track track, boolean playNow) {
        checkState();
        mPlayerPlaylist.add(track);
        for (PodAdddictPlaylistListener listener : mPodAdddictPlaylistListeners) {
            listener.onTrackAdded(track);
        }
        if (playNow) {
            play(track);
        }
    }

    /**
     * Add a list of track to thr current PodAdddict player playlist.
     *
     * @param tracks list of {@link Track}
     *               to be added to the player.
     */
    public void addTracks(List<Track> tracks) {
        checkState();
        for (Track track : tracks) {
            addTrack(track);
        }
    }

    /**
     * Remove a track from the PodAdddict player playlist.
     * <p/>
     * If the track is currently played, it will be stopped before being removed.
     *
     * @param playlistIndex index of the track to be removed.
     */
    public void removeTrack(int playlistIndex) {
        checkState();
        Track currentTrack = mPlayerPlaylist.getCurrentTrack();
        Track removedTrack = mPlayerPlaylist.remove(playlistIndex);

        if (removedTrack == null) {
            // nothing removed
            return;
        }

        if (mPlayerPlaylist.isEmpty()) {
            // playlist empty after deletion, stop player;
            PlaybackService.stop(getContext());
        } else if (currentTrack != null && currentTrack.equals(removedTrack) && mState == STATE_PLAYING) {
            // play next track if removed one was the current and playing
            play(mPlayerPlaylist.getCurrentTrackIndex());
        }

        for (PodAdddictPlaylistListener listener : mPodAdddictPlaylistListeners) {
            listener.onTrackRemoved(removedTrack, mPlayerPlaylist.isEmpty());
        }
    }

    /**
     * Used to know if the player is playing or not.
     *
     * @return true if the player is playing a track.
     */
    public boolean isPlaying() {
        return mState == STATE_PLAYING;
    }


    /**
     * Retrieve the current tracks added to the playlist.
     *
     * @return current tracks loaded into the player.
     */
    public ArrayList<Track> getTracks() {
        checkState();
        // copy the playlist to avoid reordering, addition, deletion directly on the list.
        return new ArrayList<>(mPlayerPlaylist.getPlaylist().getTracks());
    }

    /**
     * Retrieve the current played track.
     *
     * @return current track.
     */
    public Track getCurrentTrack() {
        checkState();
        return mPlayerPlaylist.getCurrentTrack();
    }

    /**
     * Register a listener to catch player events.
     *
     * @param listener listener to register.
     */
        public void registerPlayerListener(PodAdddictPlayerListener listener) {
        checkState();
        mPodAdddictPlayerListeners.add(listener);
        if (mState == STATE_PLAYING) {
            listener.onPlayerPlay(mPlayerPlaylist.getCurrentTrack(), mPlayerPlaylist.getCurrentTrackIndex());
        } else if (mState == STATE_PAUSED) {
            listener.onPlayerPause();
        }
    }

    /**
     * Unregister listener used to catch player events.
     *
     * @param listener listener to unregister.
     */
    public void unregisterPlayerListener(PodAdddictPlayerListener listener) {
        checkState();
        mPodAdddictPlayerListeners.remove(listener);
    }

    /**
     * Register a listener to catch playlist events.
     *
     * @param listener listener to register.
     */
    public void registerPlaylistListener(PodAdddictPlaylistListener listener) {
        checkState();
        mPodAdddictPlaylistListeners.add(listener);
    }

    /**
     * Unregister listener used to catch playlist events.
     *
     * @param listener listener to unregister.
     */
    public void unregisterPlaylistListener(PodAdddictPlaylistListener listener) {
        checkState();
        mPodAdddictPlaylistListeners.remove(listener);
    }

    /**
     * Retrieve the context used at the creation.
     *
     * @return context.
     */
    private Context getContext() {
        if (mApplicationContext.get() == null) {
            throw new IllegalStateException("WeakReference on application context null");
        }
        return mApplicationContext.get();
    }

    /**
     * Initialize the internal listener.
     *
     * @param context context used to register the internal listener.
     */
    private void initInternalListener(Context context) {
        mInternalListener = new PlaybackListener() {
            @Override
            protected void onPlay(Track track) {
                super.onPlay(track);
                mState = STATE_PLAYING;
                for (PodAdddictPlayerListener listener : mPodAdddictPlayerListeners) {
                    listener.onPlayerPlay(track, mPlayerPlaylist.getCurrentTrackIndex());
                }
            }

            @Override
            protected void onPause() {
                super.onPause();
                mState = STATE_PAUSED;
                for (PodAdddictPlayerListener listener : mPodAdddictPlayerListeners) {
                    listener.onPlayerPause();
                }
            }

            @Override
            protected void onPlayerDestroyed() {
                super.onPlayerDestroyed();
                mState = STATE_STOPPED;
                for (PodAdddictPlayerListener listener : mPodAdddictPlayerListeners) {
                    listener.onPlayerDestroyed();
                }
                if (mDestroyDelayed) {
                    PodAdddictPlayer.this.destroy();
                }
            }

            @Override
            protected void onSeekTo(int milli) {
                super.onSeekTo(milli);
                for (PodAdddictPlayerListener listener : mPodAdddictPlayerListeners) {
                    listener.onPlayerSeekTo(milli);
                }
            }

            @Override
            protected void onBufferingStarted() {
                super.onBufferingStarted();
                for (PodAdddictPlayerListener listener : mPodAdddictPlayerListeners) {
                    listener.onBufferingStarted();
                }
            }

            @Override
            protected void onBufferingEnded() {
                super.onBufferingEnded();
                for (PodAdddictPlayerListener listener : mPodAdddictPlayerListeners) {
                    listener.onBufferingEnded();
                }
            }

            @Override
            protected void onProgressChanged(int milli) {
                super.onProgressChanged(milli);
                for (PodAdddictPlayerListener listener : mPodAdddictPlayerListeners) {
                    listener.onProgressChanged(milli);
                }
            }
        };
        PlaybackService.registerListener(context, mInternalListener);
    }

    /**
     * Used to check the state of the client instance.
     */
    private void checkState() {
        if (mIsClosed) {
            throw new IllegalStateException("Client instance can't be used after being closed.");
        }
    }

    /**
     * Define the {@link NotificationConfig}
     * which will used to configure the playback notification.
     *
     * @param config started activity.
     */
    private void setNotificationConfig(NotificationConfig config) {
        mNotificationManager.setNotificationConfig(config);
    }

    /**
     * Builder used to build a {@link PodAdddictPlayer}
     */
    public static class Builder {

        private Context context;
        private NotificationConfig notificationConfig;

        /**
         * Default constructor.
         */
        public Builder() {
            notificationConfig = new NotificationConfig();
            notificationConfig.setNotificationIcon(R.drawable.notification_icon);
            notificationConfig.setNotificationIconBackground(R.drawable.notification_icon_background);
        }

        /**
         * Context from which the client will be build.
         *
         * @param context context used to instantiate internal components.
         * @return {@link PodAdddictPlayer.Builder}
         */
        public Builder from(Context context) {
            this.context = context;
            return this;
        }


        /**
         * Define the drawable used as icon in the notification displayed while playing.
         *
         * @param resId icon res id.
         * @return {@link PodAdddictPlayer.Builder}
         */
        public Builder notificationIcon(@DrawableRes int resId) {
            notificationConfig.setNotificationIcon(resId);
            return this;
        }

        /**
         * Define the background of the notification icon.
         * <p/>
         * Only for Lollipop device.
         *
         * @param resId notification icon background.
         * @return {@link PodAdddictPlayer.Builder}
         */
        public Builder notificationIconBackground(@DrawableRes int resId) {
            notificationConfig.setNotificationIconBackground(resId);
            return this;
        }

        /**
         * Define the activity which will be started when the user touches the player notification.
         * <p/>
         * This activity should display a media controller.
         *
         * @param activity started activity.
         * @return {@link PodAdddictPlayer.Builder}
         */
        public Builder notificationActivity(Class<? extends Activity> activity) {
            notificationConfig.setNotificationActivity(activity);
            return this;
        }

        /**
         * Build the client.
         *
         * @return {@link PodAdddictPlayer}
         */
        public PodAdddictPlayer build() {
            if (this.context == null) {
                throw new IllegalStateException("Context should be passed using 'Builder.from' to build the client.");
            }

            PodAdddictPlayer instance = getInstance(this.context);
            instance.setNotificationConfig(notificationConfig);

            return instance;
        }
    }

}
