package com.thomaskioko.podadddict.app.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Thomas Kioko
 */

public class Item {
    @SerializedName(value = "itunes:author")
    private String itunesAuthor;
    @SerializedName(value = "itunes:subtitle")
    private String itunesSubtitle;
    @SerializedName(value = "itunes:summary")
    private String itunesSummary;
    @SerializedName(value = "itunes:duration")
    private String itunesDuration;
    private String itunesExplicit;
    private String title;
    private String pubDate;
    private Enclosure enclosure;

    /**
     * @return The itunesAuthor
     */
    public String getItunesAuthor() {
        return itunesAuthor;
    }

    /**
     * @param itunesAuthor The itunes:author
     */
    public void setItunesAuthor(String itunesAuthor) {
        this.itunesAuthor = itunesAuthor;
    }

    /**
     * @return The itunesSubtitle
     */
    public String getItunesSubtitle() {
        return itunesSubtitle;
    }

    /**
     * @param itunesSubtitle The itunes:subtitle
     */
    public void setItunesSubtitle(String itunesSubtitle) {
        this.itunesSubtitle = itunesSubtitle;
    }

    /**
     * @return The itunesSummary
     */
    public String getItunesSummary() {
        return itunesSummary;
    }

    /**
     * @param itunesSummary The itunes:summary
     */
    public void setItunesSummary(String itunesSummary) {
        this.itunesSummary = itunesSummary;
    }

    /**
     * @return The itunesDuration
     */
    public String getItunesDuration() {
        return itunesDuration;
    }

    /**
     * @param itunesDuration The itunes:duration
     */
    public void setItunesDuration(String itunesDuration) {
        this.itunesDuration = itunesDuration;
    }

    /**
     * @return The itunesExplicit
     */
    public String getItunesExplicit() {
        return itunesExplicit;
    }

    /**
     * @param itunesExplicit The itunes:explicit
     */
    public void setItunesExplicit(String itunesExplicit) {
        this.itunesExplicit = itunesExplicit;
    }

    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * @return The pubDate
     */
    public String getPubDate() {
        return pubDate;
    }

    /**
     * @param pubDate The pubDate
     */
    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }


    /**
     * @return The enclosure
     */
    public Enclosure getEnclosure() {
        return enclosure;
    }

    /**
     * @param enclosure The enclosure
     */
    public void setEnclosure(Enclosure enclosure) {
        this.enclosure = enclosure;
    }
}
