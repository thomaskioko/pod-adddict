package com.thomaskioko.podadddict.app.api.model.responses;

import com.thomaskioko.podadddict.app.model.Rss;

/**
 * @author Thomas Kioko
 */

public class PodCastPlaylistResponse {

    private Rss rss;

    /**
     * @return The rss
     */
    public Rss getRss() {
        return rss;
    }

    /**
     * @param rss The rss
     */
    public void setRss(Rss rss) {
        this.rss = rss;
    }
}
