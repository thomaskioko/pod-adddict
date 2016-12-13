package com.thomaskioko.podadddict.app.interfaces;

import com.thomaskioko.podadddict.musicplayerlib.model.Track;

/**
 * Player interface.
 *
 * @author Thomas Kioko
 */

public interface TrackListener {

    /**
     * Called when the user clicked on the track view.
     *
     * @param track model of the view.
     */
    void onTrackClicked(Track track);

}