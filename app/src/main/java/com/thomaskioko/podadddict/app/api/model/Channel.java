package com.thomaskioko.podadddict.app.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Kioko
 */

public class Channel {
    private String itunesExplicit;
    private String itunesSubtitle;
    private String itunesAuthor;
    private String itunesSummary;
    private ItunesOwner itunesOwner;
    private ItunesImage itunesImage;
    private ItunesCategory itunesCategory;
    private List<AtomLink> atomLink = new ArrayList<AtomLink>();
    private String title;
    private String link;
    private String language;
    private String copyright;
    private String description;
    private List<Item> item = new ArrayList<Item>();

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
     * @return The itunesOwner
     */
    public ItunesOwner getItunesOwner() {
        return itunesOwner;
    }

    /**
     * @param itunesOwner The itunes:owner
     */
    public void setItunesOwner(ItunesOwner itunesOwner) {
        this.itunesOwner = itunesOwner;
    }

    /**
     * @return The itunesImage
     */
    public ItunesImage getItunesImage() {
        return itunesImage;
    }

    /**
     * @param itunesImage The itunes:image
     */
    public void setItunesImage(ItunesImage itunesImage) {
        this.itunesImage = itunesImage;
    }

    /**
     * @return The itunesCategory
     */
    public ItunesCategory getItunesCategory() {
        return itunesCategory;
    }

    /**
     * @param itunesCategory The itunes:category
     */
    public void setItunesCategory(ItunesCategory itunesCategory) {
        this.itunesCategory = itunesCategory;
    }

    /**
     * @return The atomLink
     */
    public List<AtomLink> getAtomLink() {
        return atomLink;
    }

    /**
     * @param atomLink The atom:link
     */
    public void setAtomLink(List<AtomLink> atomLink) {
        this.atomLink = atomLink;
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
     * @return The link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link The link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @return The language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language The language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return The copyright
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * @param copyright The copyright
     */
    public void setCopyright(String copyright) {
        this.copyright = copyright;
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
     * @return The item
     */
    public List<Item> getItem() {
        return item;
    }

    /**
     * @param item The item
     */
    public void setItem(List<Item> item) {
        this.item = item;
    }
}
