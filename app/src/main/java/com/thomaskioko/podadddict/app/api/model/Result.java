package com.thomaskioko.podadddict.app.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Kioko
 */

public class Result {
    private String wrapperType;
    private String kind;
    private Integer artistId;
    private Integer collectionId;
    private Integer trackId;
    private String artistName;
    private String collectionName;
    private String trackName;
    private String collectionCensoredName;
    private String trackCensoredName;
    private String artistViewUrl;
    private String collectionViewUrl;
    private String feedUrl;
    private String trackViewUrl;
    private String artworkUrl30;
    private String artworkUrl60;
    private String artworkUrl100;
    private Double collectionPrice;
    private Double trackPrice;
    private Integer trackRentalPrice;
    private Integer collectionHdPrice;
    private Integer trackHdPrice;
    private Integer trackHdRentalPrice;
    private String releaseDate;
    private String collectionExplicitness;
    private String trackExplicitness;
    private Integer trackCount;
    private String country;
    private String currency;
    private String primaryGenreName;
    private String artworkUrl600;
    private List<String> genreIds = new ArrayList<String>();
    private List<String> genres = new ArrayList<String>();

    /**
     * @return The wrapperType
     */
    public String getWrapperType() {
        return wrapperType;
    }

    /**
     * @param wrapperType The wrapperType
     */
    public void setWrapperType(String wrapperType) {
        this.wrapperType = wrapperType;
    }

    /**
     * @return The kind
     */
    public String getKind() {
        return kind;
    }

    /**
     * @param kind The kind
     */
    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     * @return The artistId
     */
    public Integer getArtistId() {
        return artistId;
    }

    /**
     * @param artistId The artistId
     */
    public void setArtistId(Integer artistId) {
        this.artistId = artistId;
    }

    /**
     * @return The collectionId
     */
    public Integer getCollectionId() {
        return collectionId;
    }

    /**
     * @param collectionId The collectionId
     */
    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * @return The trackId
     */
    public Integer getTrackId() {
        return trackId;
    }

    /**
     * @param trackId The trackId
     */
    public void setTrackId(Integer trackId) {
        this.trackId = trackId;
    }

    /**
     * @return The artistName
     */
    public String getArtistName() {
        return artistName;
    }

    /**
     * @param artistName The artistName
     */
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    /**
     * @return The collectionName
     */
    public String getCollectionName() {
        return collectionName;
    }

    /**
     * @param collectionName The collectionName
     */
    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    /**
     * @return The trackName
     */
    public String getTrackName() {
        return trackName;
    }

    /**
     * @param trackName The trackName
     */
    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    /**
     * @return The collectionCensoredName
     */
    public String getCollectionCensoredName() {
        return collectionCensoredName;
    }

    /**
     * @param collectionCensoredName The collectionCensoredName
     */
    public void setCollectionCensoredName(String collectionCensoredName) {
        this.collectionCensoredName = collectionCensoredName;
    }

    /**
     * @return The trackCensoredName
     */
    public String getTrackCensoredName() {
        return trackCensoredName;
    }

    /**
     * @param trackCensoredName The trackCensoredName
     */
    public void setTrackCensoredName(String trackCensoredName) {
        this.trackCensoredName = trackCensoredName;
    }

    /**
     * @return The artistViewUrl
     */
    public String getArtistViewUrl() {
        return artistViewUrl;
    }

    /**
     * @param artistViewUrl The artistViewUrl
     */
    public void setArtistViewUrl(String artistViewUrl) {
        this.artistViewUrl = artistViewUrl;
    }

    /**
     * @return The collectionViewUrl
     */
    public String getCollectionViewUrl() {
        return collectionViewUrl;
    }

    /**
     * @param collectionViewUrl The collectionViewUrl
     */
    public void setCollectionViewUrl(String collectionViewUrl) {
        this.collectionViewUrl = collectionViewUrl;
    }

    /**
     * @return The feedUrl
     */
    public String getFeedUrl() {
        return feedUrl;
    }

    /**
     * @param feedUrl The feedUrl
     */
    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    /**
     * @return The trackViewUrl
     */
    public String getTrackViewUrl() {
        return trackViewUrl;
    }

    /**
     * @param trackViewUrl The trackViewUrl
     */
    public void setTrackViewUrl(String trackViewUrl) {
        this.trackViewUrl = trackViewUrl;
    }

    /**
     * @return The artworkUrl30
     */
    public String getArtworkUrl30() {
        return artworkUrl30;
    }

    /**
     * @param artworkUrl30 The artworkUrl30
     */
    public void setArtworkUrl30(String artworkUrl30) {
        this.artworkUrl30 = artworkUrl30;
    }

    /**
     * @return The artworkUrl60
     */
    public String getArtworkUrl60() {
        return artworkUrl60;
    }

    /**
     * @param artworkUrl60 The artworkUrl60
     */
    public void setArtworkUrl60(String artworkUrl60) {
        this.artworkUrl60 = artworkUrl60;
    }

    /**
     * @return The artworkUrl100
     */
    public String getArtworkUrl100() {
        return artworkUrl100;
    }

    /**
     * @param artworkUrl100 The artworkUrl100
     */
    public void setArtworkUrl100(String artworkUrl100) {
        this.artworkUrl100 = artworkUrl100;
    }

    /**
     * @return The collectionPrice
     */
    public Double getCollectionPrice() {
        return collectionPrice;
    }

    /**
     * @param collectionPrice The collectionPrice
     */
    public void setCollectionPrice(Double collectionPrice) {
        this.collectionPrice = collectionPrice;
    }

    /**
     * @return The trackPrice
     */
    public Double getTrackPrice() {
        return trackPrice;
    }

    /**
     * @param trackPrice The trackPrice
     */
    public void setTrackPrice(Double trackPrice) {
        this.trackPrice = trackPrice;
    }

    /**
     * @return The trackRentalPrice
     */
    public Integer getTrackRentalPrice() {
        return trackRentalPrice;
    }

    /**
     * @param trackRentalPrice The trackRentalPrice
     */
    public void setTrackRentalPrice(Integer trackRentalPrice) {
        this.trackRentalPrice = trackRentalPrice;
    }

    /**
     * @return The collectionHdPrice
     */
    public Integer getCollectionHdPrice() {
        return collectionHdPrice;
    }

    /**
     * @param collectionHdPrice The collectionHdPrice
     */
    public void setCollectionHdPrice(Integer collectionHdPrice) {
        this.collectionHdPrice = collectionHdPrice;
    }

    /**
     * @return The trackHdPrice
     */
    public Integer getTrackHdPrice() {
        return trackHdPrice;
    }

    /**
     * @param trackHdPrice The trackHdPrice
     */
    public void setTrackHdPrice(Integer trackHdPrice) {
        this.trackHdPrice = trackHdPrice;
    }

    /**
     * @return The trackHdRentalPrice
     */
    public Integer getTrackHdRentalPrice() {
        return trackHdRentalPrice;
    }

    /**
     * @param trackHdRentalPrice The trackHdRentalPrice
     */
    public void setTrackHdRentalPrice(Integer trackHdRentalPrice) {
        this.trackHdRentalPrice = trackHdRentalPrice;
    }

    /**
     * @return The releaseDate
     */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * @param releaseDate The releaseDate
     */
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * @return The collectionExplicitness
     */
    public String getCollectionExplicitness() {
        return collectionExplicitness;
    }

    /**
     * @param collectionExplicitness The collectionExplicitness
     */
    public void setCollectionExplicitness(String collectionExplicitness) {
        this.collectionExplicitness = collectionExplicitness;
    }

    /**
     * @return The trackExplicitness
     */
    public String getTrackExplicitness() {
        return trackExplicitness;
    }

    /**
     * @param trackExplicitness The trackExplicitness
     */
    public void setTrackExplicitness(String trackExplicitness) {
        this.trackExplicitness = trackExplicitness;
    }

    /**
     * @return The trackCount
     */
    public Integer getTrackCount() {
        return trackCount;
    }

    /**
     * @param trackCount The trackCount
     */
    public void setTrackCount(Integer trackCount) {
        this.trackCount = trackCount;
    }

    /**
     * @return The country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return The currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @param currency The currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @return The primaryGenreName
     */
    public String getPrimaryGenreName() {
        return primaryGenreName;
    }

    /**
     * @param primaryGenreName The primaryGenreName
     */
    public void setPrimaryGenreName(String primaryGenreName) {
        this.primaryGenreName = primaryGenreName;
    }

    /**
     * @return The artworkUrl600
     */
    public String getArtworkUrl600() {
        return artworkUrl600;
    }

    /**
     * @param artworkUrl600 The artworkUrl600
     */
    public void setArtworkUrl600(String artworkUrl600) {
        this.artworkUrl600 = artworkUrl600;
    }

    /**
     * @return The genreIds
     */
    public List<String> getGenreIds() {
        return genreIds;
    }

    /**
     * @param genreIds The genreIds
     */
    public void setGenreIds(List<String> genreIds) {
        this.genreIds = genreIds;
    }

    /**
     * @return The genres
     */
    public List<String> getGenres() {
        return genres;
    }

    /**
     * @param genres The genres
     */
    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
}
