package com.thomaskioko.podadddict.musicplayerlib.player;

import com.thomaskioko.podadddict.musicplayerlib.model.Track;

/**
 * Listener interface used to catch {@link PodAdddictPlayer}
 * events.
 */
public interface PodAdddictPlayerListener {
    /**
     * Called when a track starts to be played.
     *
     * @param track    played track.
     * @param position position of the played track in the playlist.
     */
    void onPlayerPlay(Track track, int position);

    /**
     * Called when a the player has been paused.
     */
    void onPlayerPause();

    /**
     * Called when the player complete a seek action.
     *
     * @param milli time in milli of the seek.
     */
    void onPlayerSeekTo(int milli);

    /**
     * Called when the player has been destroyed.
     */
    void onPlayerDestroyed();

    /**
     * Called when the player paused due to buffering more data.
     */
    void onBufferingStarted();

    /**
     * Called when the player resumed due after having buffered enough data.
     */
    void onBufferingEnded();

    /**
     * Called when current position time changed.
     *
     * @param milli current time in milli seconds.
     */
    void onProgressChanged(int milli);

    /**
     *
     */
    void onErrorOccurred();

}
