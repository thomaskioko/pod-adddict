package com.thomaskioko.podadddict.app.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Visualizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
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
import com.thomaskioko.podadddict.app.service.MediaPlayerService;
import com.thomaskioko.podadddict.app.ui.PodCastEpisodeActivity;
import com.thomaskioko.podadddict.app.ui.PodCastListActivity;
import com.thomaskioko.podadddict.app.ui.util.CustomProgressBar;
import com.thomaskioko.podadddict.app.ui.util.SlidingRelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

/**
 * @author kioko
 */
public class PlayerFragment extends Fragment  {


    public PlayerFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_player, null);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
