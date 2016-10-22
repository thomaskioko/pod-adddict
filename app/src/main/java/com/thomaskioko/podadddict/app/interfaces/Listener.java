package com.thomaskioko.podadddict.app.interfaces;

/**
 * @author Kioko
 */

public interface Listener {
    /**
     * Called when user pressed the toggle play/pause button.
     */
    void onTogglePlayPressed();

    /**
     * Called when user pressed the previous button.
     */
    void onPreviousPressed();

    /**
     * Called when user pressed the next button.
     */
    void onNextPressed();

    /**
     * Called when user touch the seek bar to request a seek to action.
     *
     * @param milli milli second to which  the player should seek to.
     */
    void onSeekToRequested(int milli);
}
