package com.thomaskioko.podadddict.app.api.model;

/**
 * @author Thomas Kioko
 */

public class Item {
    private String itunesAuthor;
    private String itunesSubtitle;
    private String itunesSummary;
    private String itunesDuration;
    private String itunesExplicit;
    private String title;
    private String description;
    private String pubDate;
    private Guid guid;
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
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
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
     * @return The guid
     */
    public Guid getGuid() {
        return guid;
    }

    /**
     * @param guid The guid
     */
    public void setGuid(Guid guid) {
        this.guid = guid;
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
