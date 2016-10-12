package com.thomaskioko.podadddict.musicplayerlib.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Encapsulate Track track data.
 *
 * @author kioko
 */
public class Track implements Parcelable {

    /**
     * Parcelable.
     */
    public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {
        public Track createFromParcel(Parcel source) {
            return new Track(source);
        }

        public Track[] newArray(int size) {
            return new Track[size];
        }
    };


    private long mDurationInMilli;
    private Date mCreationDate;
    private boolean mPublicSharing;
    private boolean mStreamable;
    private boolean mDownloadable;
    private String mArtworkUrl;
    private String mStreamUrl;
    private String mGenre;
    private String mTitle;
    private String mArtist;
    private String mDescription;

    /**
     * Default constructor.
     */
    public Track() {
    }

    private Track(Parcel in) {
        this.mDurationInMilli = in.readLong();
        long tmpMCreatationDate = in.readLong();
        this.mCreationDate = tmpMCreatationDate == -1 ? null : new Date(tmpMCreatationDate);
        this.mPublicSharing = in.readByte() != 0;
        this.mStreamable = in.readByte() != 0;
        this.mDownloadable = in.readByte() != 0;
        this.mArtworkUrl = in.readString();
        this.mStreamUrl = in.readString();
        this.mGenre = in.readString();
        this.mTitle = in.readString();
        this.mArtist = in.readString();
        this.mDescription = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(this.mDurationInMilli);
        dest.writeLong(mCreationDate != null ? mCreationDate.getTime() : -1);
        dest.writeByte(mPublicSharing ? (byte) 1 : (byte) 0);
        dest.writeByte(mStreamable ? (byte) 1 : (byte) 0);
        dest.writeByte(mDownloadable ? (byte) 1 : (byte) 0);
        dest.writeString(this.mArtworkUrl);
        dest.writeString(this.mStreamUrl);
        dest.writeString(this.mGenre);
        dest.writeString(this.mTitle);
        dest.writeString(this.mArtist);
        dest.writeString(this.mDescription);

    }

    @Override
    public String toString() {
        return "Track{"
                + "mDurationInMilli=" + mDurationInMilli
                + ", mCreationDate=" + mCreationDate
                + ", mPublicSharing=" + mPublicSharing
                + ", mStreamable=" + mStreamable
                + ", mDownloadable=" + mDownloadable
                + ", mArtworkUrl='" + mArtworkUrl + '\''
                + ", mStreamUrl='" + mStreamUrl + '\''
                + ", mGenre='" + mGenre + '\''
                + ", mTitle='" + mTitle + '\''
                + ", mDescription='" + mDescription + '\''
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }


        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    /**
     * Integer Id.
     *
     * @return Integer Id.
     */
    public int getId() {
        return 0;
    }

    /**
     * duration in milliseconds
     *
     * @return duration in milliseconds
     */
    public long getDurationInMilli() {
        return mDurationInMilli;
    }

    /**
     * Date of creation
     *
     * @return Date of creation
     */
    public Date getCreationDate() {
        return mCreationDate;
    }

    /**
     * Public sharing policy
     *
     * @return true if shared publicly.
     */
    public boolean isPublicSharing() {
        return mPublicSharing;
    }

    /**
     * streamable via API
     *
     * @return true if streamable via API
     */
    public boolean isStreamable() {
        return mStreamable;
    }

    /**
     * downloadable via API
     *
     * @return true if downloadable via API
     */
    public boolean isDownloadable() {
        return mDownloadable;
    }


    /**
     * URL to a JPEG image
     *
     * @return URL to a JPEG image
     */
    public String getArtworkUrl() {
        return mArtworkUrl;
    }

    /**
     * link to 128kbs mp3 stream
     *
     * @return link to 128kbs mp3 stream
     */
    public String getStreamUrl() {
        return mStreamUrl;
    }

    /**
     * genre
     * <p/>
     * example : "HipHop"
     *
     * @return genre
     */
    public String getGenre() {
        return mGenre;
    }

    /**
     * track title
     *
     * @return track title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * track artist.
     *
     * @return track artist.
     */
    public String getArtist() {
        return mArtist;
    }

    /**
     * HTML description
     *
     * @return HTML description
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * duration in milliseconds
     *
     * @param durationInMilli duration in milliseconds
     */
    public void setDurationInMilli(long durationInMilli) {
        this.mDurationInMilli = durationInMilli;
    }

    /**
     * Date of creation
     *
     * @param creationDate Date of creation
     */
    public void setCreationDate(Date creationDate) {
        this.mCreationDate = creationDate;
    }

    /**
     * Public sharing policy
     *
     * @param publicSharing true if shared publicly.
     */
    public void setPublicSharing(boolean publicSharing) {
        this.mPublicSharing = publicSharing;
    }

    /**
     * streamable via API
     *
     * @param streamable true if streamable via API
     */
    public void setStreamable(boolean streamable) {
        this.mStreamable = streamable;
    }

    /**
     * downloadable via API
     *
     * @param downloadable true if downloadable via API
     */
    public void setDownloadable(boolean downloadable) {
        this.mDownloadable = downloadable;
    }


    /**
     * URL to a JPEG image
     *
     * @param artworkUrl URL to a JPEG image
     */
    public void setArtworkUrl(String artworkUrl) {
        this.mArtworkUrl = artworkUrl;
    }

    /**
     * link to 128kbs mp3 stream
     *
     * @param streamUrl link to 128kbs mp3 stream
     */
    public void setStreamUrl(String streamUrl) {
        this.mStreamUrl = streamUrl;
    }

    /**
     * genre
     * <p/>
     * example : "HipHop"
     *
     * @param genre genre
     */
    public void setGenre(String genre) {
        this.mGenre = genre;
    }

    /**
     * track title
     *
     * @param title track title
     */
    public void setTitle(String title) {
        this.mTitle = title;
    }

    /**
     * track artist.
     *
     * @param artist track artist.
     */
    public void setArtist(String artist) {
        mArtist = artist;
    }

    /**
     * HTML description
     *
     * @param description HTML description
     */
    public void setDescription(String description) {
        this.mDescription = description;
    }

}
