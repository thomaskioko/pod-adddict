package com.thomaskioko.podadddict.app.ui.fragments;

import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.data.model.LocalTrack;
import com.thomaskioko.podadddict.app.data.model.Track;
import com.thomaskioko.podadddict.app.data.model.UnifiedTrack;
import com.thomaskioko.podadddict.app.interfaces.fullScreenListener;
import com.thomaskioko.podadddict.app.interfaces.onCompleteListener;
import com.thomaskioko.podadddict.app.interfaces.onEqualizerClickedListener;
import com.thomaskioko.podadddict.app.interfaces.onPlayPauseListener;
import com.thomaskioko.podadddict.app.interfaces.onPreparedLsitener;
import com.thomaskioko.podadddict.app.interfaces.onPreviousTrackListener;
import com.thomaskioko.podadddict.app.interfaces.onQueueClickListener;
import com.thomaskioko.podadddict.app.interfaces.onSettingsClickedListener;
import com.thomaskioko.podadddict.app.interfaces.onSmallPlayerTouchedListener;
import com.thomaskioko.podadddict.app.receiver.AudioPlayerBroadcastReceiver;
import com.thomaskioko.podadddict.app.ui.PodCastListActivity;
import com.thomaskioko.podadddict.app.ui.util.CustomProgressBar;
import com.thomaskioko.podadddict.app.ui.util.SlidingRelativeLayout;

import java.io.Serializable;
import java.util.Timer;

import butterknife.ButterKnife;

/**
 * @author kioko
 */
public class PlayerFragment extends Fragment implements
        Serializable,
        AudioPlayerBroadcastReceiver.onCallbackListener{

    public static MediaPlayer mMediaPlayer;
    public static Visualizer mVisualizer;
    public static Equalizer mEqualizer;
    public static BassBoost bassBoost;
    public static PresetReverb presetReverb;

    private static  String mSreamUrl;

    private static final long serialVersionUID = 1L;

    static boolean isPrepared = false;

    private float x1, x2;
    static final int MIN_DISTANCE = 200;

    View bufferingIndicator;

    static View fullscreenExtraSpaceOccupier;

    static CustomProgressBar cpb;

    Pair<String, String> temp;

    TextView currTime, totalTime;

    public static ImageView repeatIcon;

    public ImageView equalizerIcon;
    public static ImageView mainTrackController;
    public ImageView nextTrackController;
    public ImageView previousTrackController;
    public ImageView favouriteIcon;
    public ImageView queueIcon;

    public ImageView saveDNAToggle;

    boolean isFav = false;

    static RelativeLayout bottomContainer;
    static RelativeLayout seekBarContainer;
    static RelativeLayout toggleContainer;

    public static ImageView selected_track_image;
    public static TextView selected_track_title;
    public static ImageView player_controller;

    static Toolbar smallPlayer;


    public static SeekBar progressBar;

    public static int durationInMilliSec;
    boolean completed = false;
    boolean pauseClicked = false;
    boolean isTracking = false;

    public static boolean localIsPlaying = false;

    Timer t;

    public static Track track;
    public static LocalTrack localTrack;

    static boolean isRefreshed = false;

    public onSmallPlayerTouchedListener mCallback;
    public onCompleteListener mCallback2;
    public onPreviousTrackListener mCallback3;
    public onEqualizerClickedListener mCallback4;
    public onQueueClickListener mCallback5;
    public onPreparedLsitener mCallback6;
    public onPlayPauseListener mCallback7;
    public fullScreenListener mCallback8;
    public onSettingsClickedListener mCallback9;

    static ImageView currentAlbumArtHolder;

    public boolean isStart = true;

    SlidingRelativeLayout rootView;

    long startTrack;
    long endTrack;


    private static final String LOG_TAG = PlayerFragment.class.getSimpleName();

    public PlayerFragment() {
        // Required empty public constructor
    }

    public static PlayerFragment newInstance(String feedStreamUrl) {

        mSreamUrl = feedStreamUrl;

        return new PlayerFragment();
    }

    @Override
    public void onCallbackCalled(int i) {

    }

    @Override
    public void togglePLayPauseCallback() {

    }

    @Override
    public boolean getPauseClicked() {
        return false;
    }

    @Override
    public void setPauseClicked(boolean bool) {

    }

    @Override
    public MediaPlayer getMediaPlayer() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.bind(this, rootView);


//        ((PodCastEpisodeActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.title_player));

        return rootView;
    }

//    @Override
//    public void onViewCreated(final View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        rootView = (SlidingRelativeLayout) view.findViewById(R.id.root_view);
//
//
//
//        LogUtils.showInformationLog(LOG_TAG, "@onViewCreated:: Stream Url " + mSreamUrl);
//
//        currentAlbumArtHolder = (ImageView) view.findViewById(R.id.current_album_art_holder);
//
//        fullscreenExtraSpaceOccupier = view.findViewById(R.id.fullscreen_extra_space_occupier);
//
//        bufferingIndicator = view.findViewById(R.id.bufferingIndicator);
//        currTime = (TextView) view.findViewById(R.id.currTime);
//        totalTime = (TextView) view.findViewById(R.id.totalTime);
//
//        repeatIcon = (ImageView) view.findViewById(R.id.repeat_icon);
//        if (PodCastListActivity.shuffleEnabled) {
//            repeatIcon.setImageResource(R.drawable.ic_shuffle_white_48dp);
//        } else if (PodCastListActivity.repeatEnabled) {
//            repeatIcon.setImageResource(R.drawable.ic_repeat_white_48dp);
//        } else if (PodCastListActivity.repeatOnceEnabled) {
//            repeatIcon.setImageResource(R.drawable.ic_repeat_once_white_48dp);
//        }
//        repeatIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (PodCastListActivity.shuffleEnabled) {
//                    PodCastListActivity.shuffleEnabled = false;
//                    PodCastListActivity.repeatEnabled = true;
//                    repeatIcon.setImageResource(R.drawable.ic_repeat_white_48dp);
//                } else if (PodCastListActivity.repeatEnabled) {
//                    PodCastListActivity.repeatEnabled = false;
//                    PodCastListActivity.repeatOnceEnabled = true;
//                    repeatIcon.setImageResource(R.drawable.ic_repeat_once_white_48dp);
//                } else if (PodCastListActivity.repeatOnceEnabled) {
//                    PodCastListActivity.repeatOnceEnabled = false;
//                    PodCastListActivity.shuffleEnabled = true;
//                    repeatIcon.setImageResource(R.drawable.ic_shuffle_white_48dp);
//                }
//            }
//        });
//
//        equalizerIcon = (ImageView) view.findViewById(R.id.equalizer_icon);
//        equalizerIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCallback4.onEqualizerClicked();
//            }
//        });
//        equalizerIcon.setVisibility(View.INVISIBLE);
//
//        saveDNAToggle = (ImageView) view.findViewById(R.id.toggleSaveDNA);
//        if (PodCastListActivity.isSaveDNAEnabled) {
//            saveDNAToggle.setImageResource(R.drawable.ic_save_red_2);
//        } else {
//            saveDNAToggle.setImageResource(R.drawable.ic_save_white_2);
//        }
//
//        saveDNAToggle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (PodCastListActivity.isSaveDNAEnabled) {
//                    PodCastListActivity.isSaveDNAEnabled = false;
//                    saveDNAToggle.setImageResource(R.drawable.ic_save_white_2);
//                } else {
//                    PodCastListActivity.isSaveDNAEnabled = true;
//                    saveDNAToggle.setImageResource(R.drawable.ic_save_red_2);
//                }
//            }
//        });
//
//        mainTrackController = (ImageView) view.findViewById(R.id.controller);
//        nextTrackController = (ImageView) view.findViewById(R.id.next);
//        previousTrackController = (ImageView) view.findViewById(R.id.previous);
//
//        isFav = false;
//
//        favouriteIcon = (ImageView) view.findViewById(R.id.fav_icon);
//        if (PodCastListActivity.isFavourite) {
//            favouriteIcon.setImageResource(R.drawable.ic_heart_filled_1);
//            isFav = true;
//        } else {
//            favouriteIcon.setImageResource(R.drawable.ic_heart_out_1);
//            isFav = false;
//        }
//
//        favouriteIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isFav) {
//                    favouriteIcon.setImageResource(R.drawable.ic_heart_out_1);
//                    isFav = false;
//                    removeFromFavourite();
//                } else {
//                    favouriteIcon.setImageResource(R.drawable.ic_heart_filled_1);
//                    isFav = true;
//
//                }
//                //TODO:: Save Items to favorite DB
//            }
//        });
//
//        queueIcon = (ImageView) view.findViewById(R.id.queue_icon);
//        queueIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCallback5.onQueueClicked();
//            }
//        });
//
//        selected_track_image = (ImageView) view.findViewById(R.id.selected_track_image_sp);
//        selected_track_title = (TextView) view.findViewById(R.id.selected_track_title_sp);
//        player_controller = (ImageView) view.findViewById(R.id.player_control_sp);
//
//        smallPlayer = (Toolbar) view.findViewById(R.id.smallPlayer);
//
//        bottomContainer = (RelativeLayout) view.findViewById(R.id.mainControllerContainer);
//        seekBarContainer = (RelativeLayout) view.findViewById(R.id.seekBarContainer);
//        toggleContainer = (RelativeLayout) view.findViewById(R.id.toggleContainer);
//
//        cpb = (CustomProgressBar) view.findViewById(R.id.customProgress);
//
//        mMediaPlayer = new MediaPlayer();
//        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                completed = false;
//                pauseClicked = false;
//                isPrepared = true;
//                mCallback6.onPrepared();
//                if (PodCastListActivity.isPlayerVisible) {
//                    player_controller.setVisibility(View.VISIBLE);
//                    player_controller.setImageResource(R.drawable.ic_queue_music_white_48dp);
//                } else {
//                    player_controller.setVisibility(View.VISIBLE);
//                    player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
//                }
////                setupVisualizerFxAndUI();
////                mVisualizer.setEnabled(true);
//                togglePlayPause();
//                togglePlayPause();
//                togglePlayPause();
//                bufferingIndicator.setVisibility(View.GONE);
//                equalizerIcon.setVisibility(View.VISIBLE);
//
//                new PodCastListActivity.SaveQueue().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                new PodCastListActivity.SaveRecents().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//            }
//        });
//
//        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                completed = true;
//                if (PodCastListActivity.isPlayerVisible) {
//                    mainTrackController.setImageResource(R.drawable.ic_replay_white_48dp);
//                } else {
//                    player_controller.setImageResource(R.drawable.ic_replay_white_48dp);
//                    mainTrackController.setImageResource(R.drawable.ic_replay_white_48dp);
//                }
//            }
//        });
//
//        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
//            @Override
//            public void onBufferingUpdate(MediaPlayer mp, int percent) {
//                double ratio = percent / 100.0;
//                double bufferingLevel = (int) (mp.getDuration() * ratio);
//                if (progressBar != null) {
//                    progressBar.setSecondaryProgress((int) bufferingLevel);
//                }
//            }
//        });
//
//        mMediaPlayer.setOnErrorListener(
//                new MediaPlayer.OnErrorListener() {
//                    @Override
//                    public boolean onError(MediaPlayer mp, int what, int extra) {
//                        Toast.makeText(getContext(), what + ":" + extra, Toast.LENGTH_SHORT).show();
//                        return true;
//                    }
//                }
//        );
//
//        mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
//            @Override
//            public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                switch (what) {
//                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//                        bufferingIndicator.setVisibility(View.VISIBLE);
//                        isPrepared = false;
//                        break;
//                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
//                        bufferingIndicator.setVisibility(View.GONE);
//                        isPrepared = true;
//                        break;
//                }
//                return true;
//            }
//        });
//
//        smallPlayer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCallback.onSmallPlayerTouched();
//            }
//        });
//
//
//        track = PodCastListActivity.selectedTrack;
//        localTrack = PodCastListActivity.localSelectedTrack;
//
//        if (PodCastListActivity.streamSelected) {
//            try {
//                durationInMilliSec = track.getDuration();
//            } catch (Exception e) {
//
//            }
//            if (track.getArtworkURL() != null) {
//                //TODO:: Load to imageView
//            } else {
//                selected_track_image.setImageResource(R.drawable.ic_default);
//                currentAlbumArtHolder.setImageResource(R.drawable.ic_default);
//            }
//            selected_track_title.setText(track.getTitle());
//        } else {
//            try {
//                durationInMilliSec = (int) localTrack.getDuration();
//            } catch (Exception e) {
//
//            }
//
//            selected_track_title.setText(localTrack.getTitle());
//        }
//
//        temp = getTime(durationInMilliSec);
//        totalTime.setText(temp.first + ":" + temp.second);
//
//        try {
//            if (PodCastListActivity.streamSelected) {
//                isPrepared = false;
//                mMediaPlayer.reset();
//                mMediaPlayer.setDataSource(track.getStreamURL());
//                mMediaPlayer.prepareAsync();
//            } else {
//                isPrepared = false;
//                mMediaPlayer.reset();
//                mMediaPlayer.setDataSource(localTrack.getPath());
//                mMediaPlayer.prepareAsync();
//            }
//            bufferingIndicator.setVisibility(View.VISIBLE);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        player_controller.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!pauseClicked) {
//                    pauseClicked = true;
//                }
//                if (!PodCastListActivity.isPlayerVisible)
//                    togglePlayPause();
//                else
//                    mCallback5.onQueueClicked();
//            }
//        });
//
//
//        mainTrackController.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!PodCastListActivity.hasQueueEnded) {
//                    if (!pauseClicked) {
//                        pauseClicked = true;
//                    }
//                    togglePlayPause();
//                } else {
//                    mCallback2.onComplete();
//                }
//            }
//        });
//
//        nextTrackController.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mMediaPlayer.pause();
//                PodCastListActivity.nextControllerClicked = true;
//                mCallback2.onComplete();
//            }
//        });
//
//        previousTrackController.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mMediaPlayer.pause();
//                mCallback3.onPreviousTrack();
//            }
//        });
//
//        progressBar = (SeekBar) view.findViewById(R.id.progressBar);
//        progressBar.setMax(durationInMilliSec);
//
//        t = new Timer();
//        t.scheduleAtFixedRate(
//                new TimerTask() {
//                    public void run() {
//                        if (isPrepared && !isTracking && getActivity() != null) {
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    float[] hsv = new float[3];
//                                    hsv[1] = (float) 0.8;
//                                    hsv[2] = (float) 0.5;
//                                    progressBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.HSVToColor(hsv), PorterDuff.Mode.SRC_IN));
//                                    cpb.update();
//                                }
//                            });
//                            try {
//                                temp = getTime(mMediaPlayer.getCurrentPosition());
//                                PodCastListActivity.main.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        currTime.setText(temp.first + ":" + temp.second);
//                                    }
//                                });
//                                progressBar.setProgress(mMediaPlayer.getCurrentPosition());
//                            } catch (Exception e) {
//                                Log.e("MEDIA", e.getMessage() + ":");
//                            }
//                        }
//                    }
//                }, 0, 50);
//
//        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                temp = getTime(progress);
//                PodCastListActivity.main.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        currTime.setText(temp.first + ":" + temp.second);
//                    }
//                });
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                isTracking = true;
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                mMediaPlayer.seekTo(seekBar.getProgress());
//                if (mMediaPlayer.isPlaying())
//                    mMediaPlayer.start();
//                isTracking = false;
//            }
//
//        });
//
//        final Button mEndButton = new Button(getContext());
//        mEndButton.setBackgroundColor(Color.parseColor("#B24242"));
//        mEndButton.setTextColor(Color.WHITE);
//
//    }

    public void removeFromFavourite() {

        UnifiedTrack ut;

        if (PodCastListActivity.localSelected)
            ut = new UnifiedTrack(true, localTrack, null);
        else
            ut = new UnifiedTrack(false, null, track);

    }

    public void togglePlayPause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            if (PodCastListActivity.isPlayerVisible) {
                mainTrackController.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                player_controller.setImageResource(R.drawable.ic_queue_music_white_48dp);
            } else {
                player_controller.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                mainTrackController.setImageResource(R.drawable.ic_play_arrow_white_48dp);
            }
            mVisualizer.setEnabled(false);
            if (!isStart && mCallback7 != null)
                mCallback7.onPlayPause();
        } else {
            if (!completed) {
                mVisualizer.setEnabled(true);
                if (PodCastListActivity.isPlayerVisible) {
                    mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
                    player_controller.setImageResource(R.drawable.ic_queue_music_white_48dp);
                } else {
                    mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
                    player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
                }
                mMediaPlayer.start();
                if (!isStart && mCallback7 != null)
                    mCallback7.onPlayPause();
            } else {
                mMediaPlayer.seekTo(0);
                mVisualizer.setEnabled(true);
                mMediaPlayer.start();
                completed = false;
                if (PodCastListActivity.isPlayerVisible) {
                    mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
                } else {
                    mainTrackController.setImageResource(R.drawable.ic_pause_white_48dp);
                    player_controller.setImageResource(R.drawable.ic_pause_white_48dp);
                }
            }
        }
    }


    public Pair<String, String> getTime(int millsec) {
        int min, sec;
        sec = millsec / 1000;
        min = sec / 60;
        sec = sec % 60;
        String minS, secS;
        minS = String.valueOf(min);
        secS = String.valueOf(sec);
        if (sec < 10) {
            secS = "0" + secS;
        }
        return Pair.create(minS, secS);
    }
}
