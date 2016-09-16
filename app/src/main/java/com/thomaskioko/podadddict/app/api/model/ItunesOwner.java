package com.thomaskioko.podadddict.app.api.model;

/**
 * @author Thomas Kioko
 */

public class ItunesOwner {
    private String itunesName;
    private String itunesEmail;

    /**
     * @return The itunesName
     */
    public String getItunesName() {
        return itunesName;
    }

    /**
     * @param itunesName The itunes:name
     */
    public void setItunesName(String itunesName) {
        this.itunesName = itunesName;
    }

    /**
     * @return The itunesEmail
     */
    public String getItunesEmail() {
        return itunesEmail;
    }

    /**
     * @param itunesEmail The itunes:email
     */
    public void setItunesEmail(String itunesEmail) {
        this.itunesEmail = itunesEmail;
    }
}
