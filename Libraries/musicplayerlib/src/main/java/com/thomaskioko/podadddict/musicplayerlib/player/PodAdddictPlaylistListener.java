package com.thomaskioko.podadddict.musicplayerlib.player;


import com.thomaskioko.podadddict.musicplayerlib.model.Track;

/**
 * Listener used to catch events performed on the play playlist.
 */
public interface PodAdddictPlaylistListener {

    /**
     * Called when a tracks has been added to the player playlist.
     *
     * @param track track added.
     */
    void onTrackAdded(Track track);


    /**
     * Called when a tracks has been removed from the player playlist.
     *
     * @param track   track removed.
     * @param isEmpty true if the playlist is empty after deletion.
     */
    void onTrackRemoved(Track track, boolean isEmpty);
}
