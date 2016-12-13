package com.thomaskioko.podadddict.app.ui.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.github.clans.fab.FloatingActionButton;
import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.api.model.Item;
import com.thomaskioko.podadddict.app.interfaces.Listener;
import com.thomaskioko.podadddict.app.interfaces.TrackListener;
import com.thomaskioko.podadddict.app.service.PlayerWidgetService;
import com.thomaskioko.podadddict.app.ui.NowPlayingActivity;
import com.thomaskioko.podadddict.app.util.ApplicationConstants;
import com.thomaskioko.podadddict.app.util.Converter;
import com.thomaskioko.podadddict.musicplayerlib.model.Track;
import com.thomaskioko.podadddict.musicplayerlib.player.PodAdddictPlayer;
import com.thomaskioko.podadddict.musicplayerlib.player.PodAdddictPlayerListener;
import com.thomaskioko.podadddict.musicplayerlib.player.PodAdddictPlaylistListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Podcast episode BottomSheet fragment class.
 *
 * @author kioko
 */
public class PodcastEpisodeBottomSheetFragment extends BottomSheetDialogFragment implements
        LoaderManager.LoaderCallbacks<Cursor>, PodAdddictPlayerListener, Listener,
        TrackListener, PodAdddictPlaylistListener {

    /**
     * BindView
     */
    @Bind(R.id.imageView)
    ImageView mImageView;
    @Bind(R.id.imageViewBackground)
    RelativeLayout mRelativeLayout;
    @Bind(R.id.podcast_title)
    TextView mPodcastTitle;
    @Bind(R.id.podcast_artist_name)
    TextView mPodcastArtistName;
    @Bind(R.id.podcast_description)
    TextView mPodcastDescription;
    @Bind(R.id.description_title)
    TextView mTvDescriptionTitle;
    @Bind(R.id.fabDownload)
    FloatingActionButton mFloatingActionButton;
    @Bind(R.id.fabPlay)
    FloatingActionButton mFabPlay;
    @Bind(R.id.fabPlaylist)
    FloatingActionButton mFabPlaylist;

    private Track track;
    private ArrayList<Track> mPlaylistTracks = new ArrayList<>();
    private PodAdddictPlayer mPodAddictPlayer;
    PodAdddictPlayerListener mAdddictPlayerListener;
    private static Uri mUri;
    static TrackListener mListener;
    private static Item mItem;
    private static final int LOADER_ID = 100;
    private static final String LOG_TAG = PodcastEpisodeBottomSheetFragment.class.getSimpleName();

    /**
     * Constructor
     *
     * @param item     Feed Item
     * @param uri      UrI with selected item.
     * @param listener {@link TrackListener} interface
     * @return Fragment instance
     */
    public static PodcastEpisodeBottomSheetFragment newInstance(Item item, Uri uri, TrackListener listener) {
        mUri = uri;
        mItem = item;
        mListener = listener;
        return new PodcastEpisodeBottomSheetFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPodAddictPlayer = new PodAdddictPlayer.Builder()
                .from(getActivity())
                .notificationActivity(NowPlayingActivity.class)
                .notificationIcon(R.drawable.ic_notification)
                .build();

        mListener = this;
        mAdddictPlayerListener = this;

        // check if tracks are already loaded into the player.
        ArrayList<Track> currentsTracks = mPodAddictPlayer.getTracks();
        if (currentsTracks != null) {
            mPlaylistTracks.addAll(currentsTracks);
        }

        if (mPodAddictPlayer.isPlaying()) {
            mFabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp));
        }

        String imageUrl = "";
        Cursor cursor = getActivity().getContentResolver().query(mUri, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                imageUrl = cursor.getString(ApplicationConstants.COLUMN_SUBSCRIBED_PODCAST_FEED_IMAGE_URL);
            }
        }

        track = new Track();
        track.setTitle(mItem.getTitle());
        track.setStreamUrl(mItem.getEnclosure().getUrl());
        track.setArtist(mItem.getItunesAuthor());
        track.setArtworkUrl(imageUrl);
        track.setDurationInMilli(Converter.getMilliSeconds(mItem.getItunesDuration()));
        track.setDescription(mItem.getItunesSummary());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_podcast_episode_bottom_sheet, container, false);
        ButterKnife.bind(this, view);

        //Set content on the views.
        mPodcastTitle.setText(mItem.getTitle());
        mPodcastDescription.setText(mItem.getItunesSummary());
        mPodcastArtistName.setText(mItem.getItunesAuthor());
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        mPodAddictPlayer.unregisterPlayerListener(this);
        mPodAddictPlayer.unregisterPlayerListener(mAdddictPlayerListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPodAddictPlayer.destroy();
    }

    @OnClick({R.id.fabDownload, R.id.fabPlaylist, R.id.fabPlay})
    void onButtonSubscribeClicked(View view) {
        switch (view.getId()) {

            case R.id.fabDownload:
                //TODO:: Download the Episode or change this to favorite.
                break;

            case R.id.fabPlay:
                mListener.onTrackClicked(track);

                dismiss();
                break;
            default:
            case R.id.fabPlaylist:
                //Add the track to the playlist
                mPodAddictPlayer.addTrack(track, false);
                mFabPlaylist.setColorNormal(getResources().getColor(R.color.green_success));

                dismiss();
                break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    null,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // We need to start the enter transition after the data has loaded
        while (data.moveToNext()) {
            String imageUrl = data.getString(ApplicationConstants.COLUMN_SUBSCRIBED_PODCAST_FEED_IMAGE_URL);

            //We use glide to load the image
            Glide.with(getActivity())
                    .load(imageUrl)
                    .asBitmap()
                    .placeholder(R.color.placeholder)
                    .into(new BitmapImageViewTarget(mImageView) {
                        @Override
                        public void onResourceReady(Bitmap bitmap, final GlideAnimation glideAnimation) {
                            super.onResourceReady(bitmap, glideAnimation);
                            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    //Set the background of the relative layout.
                                    if (palette.getDarkVibrantSwatch() != null) {
                                        mRelativeLayout.setBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
                                    } else if (palette.getMutedSwatch() != null) {
                                        mRelativeLayout.setBackgroundColor(palette.getMutedSwatch().getRgb());
                                    }

                                    //Set the color of the floating actionbar
                                    if (palette.getLightVibrantSwatch() != null) {
                                        mFloatingActionButton.setColorNormal(palette.getLightVibrantSwatch().getRgb());
                                    } else if (palette.getLightMutedSwatch() != null) {
                                        mFloatingActionButton.setColorNormal(palette.getLightMutedSwatch().getRgb());
                                    }

                                }
                            });
                        }
                    });
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    @Override
    public void onTogglePlayPressed() {
        mPodAddictPlayer.play(track);
        mPodAddictPlayer.togglePlayback();
    }

    @Override
    public void onPreviousPressed() {
        mPodAddictPlayer.previous();
    }

    @Override
    public void onNextPressed() {
        mPodAddictPlayer.next();
    }

    @Override
    public void onSeekToRequested(int milli) {
        mPodAddictPlayer.seekTo(milli);
    }

    @Override
    public void onTrackClicked(Track track) {

        mPlaylistTracks.add(track);
        mPodAddictPlayer.addTracks(mPlaylistTracks);

        if (mPodAddictPlayer.getTracks().contains(track)) {
            mPodAddictPlayer.play(track);
        } else {
            boolean playNow = !mPodAddictPlayer.isPlaying();

            mPodAddictPlayer.addTrack(track, playNow);
        }
    }

    @Override
    public void onPlayerPlay(Track track, int position) {

        mPodAddictPlayer.play(track);

        //Invoke service to update the widget.
        Intent active = new Intent(getActivity(), PlayerWidgetService.class);
        active.setAction("ACTION_START_PLAYER");
        getActivity().startService(active);
    }

    @Override
    public void onPlayerPause() {
        mFabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_white));

    }

    @Override
    public void onPlayerSeekTo(int milli) {

    }

    @Override
    public void onPlayerDestroyed() {

    }

    @Override
    public void onBufferingStarted() {

    }

    @Override
    public void onBufferingEnded() {

    }

    @Override
    public void onProgressChanged(int milli) {

    }

    @Override
    public void onErrorOccurred() {

    }

    @Override
    public void onTrackAdded(Track track) {
        mPlaylistTracks.add(track);
    }

    @Override
    public void onTrackRemoved(Track track, boolean isEmpty) {

    }
}
