package com.thomaskioko.podadddict.app.interfaces;

import com.thomaskioko.podadddict.musicplayerlib.model.Track;
import com.thomaskioko.podadddict.musicplayerlib.player.PodAdddictPlayerListener;

/**
 * Player interface.
 *
 * @author Thomas Kioko
 */

public interface TrackListener extends PodAdddictPlayerListener {

    /**
     * Called when the user clicked on the track view.
     *
     * @param track model of the view.
     */
    void onTrackClicked(Track track);

    @Override
    void onPlayerPlay(Track track, int position);

    @Override
    void onPlayerPause();

    @Override
    void onPlayerSeekTo(int milli);

    @Override
    void onPlayerDestroyed();

    @Override
    void onBufferingStarted();

    @Override
    void onBufferingEnded();

    @Override
    void onProgressChanged(int milli);

    @Override
    void onErrorOccurred();
}