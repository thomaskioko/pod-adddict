package com.thomaskioko.podadddict.musicplayerlib.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Encapsulate data of a PodAdddict playlist.
 *
 * @author kioko
 */
public class PodAdddictPlaylist implements Parcelable {

    /**
     * Parcelable.
     */
    public static final Parcelable.Creator<PodAdddictPlaylist> CREATOR
            = new Parcelable.Creator<PodAdddictPlaylist>() {
        public PodAdddictPlaylist createFromParcel(Parcel source) {
            return new PodAdddictPlaylist(source);
        }

        public PodAdddictPlaylist[] newArray(int size) {
            return new PodAdddictPlaylist[size];
        }
    };

    private ArrayList<Track> mTracks;

    /**
     * Default constructor.
     */
    public PodAdddictPlaylist() {
        mTracks = new ArrayList<>();
    }

    private PodAdddictPlaylist(Parcel in) {
        this();
        in.readTypedList(this.mTracks, Track.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mTracks);
    }

    @Override
    public String toString() {
        return "PodAdddictPlaylist{"
                + "mTracks=" + mTracks
                + '}';
    }

    /**
     * Get the tracks added in the playlist.
     *
     * @return list of tracks.
     */
    public ArrayList<Track> getTracks() {
        return mTracks;
    }

    /**
     * Add a new track to the playlist.
     *
     * @param track track to add.
     */
    public void addTracks(Track track) {
        mTracks.add(track);
    }

    /**
     * Add a track at the given position.
     * <p/>
     * The track will be inserted before previous element at the specified position.
     *
     * @param position position at which the track will be inserted.
     * @param track    track to add.
     */
    public void addTrack(int position, Track track) {
        mTracks.add(position, track);
    }

    /**
     * Add a set a track.
     *
     * @param tracks tracks to be added.
     */
    public void addAllTracks(Collection<Track> tracks) {
        mTracks.addAll(tracks);
    }

}
