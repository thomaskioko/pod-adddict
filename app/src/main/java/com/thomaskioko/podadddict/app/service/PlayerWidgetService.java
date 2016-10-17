package com.thomaskioko.podadddict.app.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.receiver.PlayerWidgetProvider;
import com.thomaskioko.podadddict.app.ui.PodCastListActivity;
import com.thomaskioko.podadddict.musicplayerlib.model.Track;
import com.thomaskioko.podadddict.musicplayerlib.player.PlaybackService;

/**
 * Helper class that updates the state of the player widget
 *
 * @author kioko.
 */

public class PlayerWidgetService extends IntentService {

    private static final String TAG = PlayerWidgetService.class.getSimpleName();

    /**
     * Default constructor
     */
    public PlayerWidgetService() {
        super("PlayerWidgetService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                PlayerWidgetProvider.class));

        for (int appWidgetId : appWidgetIds) {

            final RemoteViews views = new RemoteViews(getPackageName(), R.layout.player_widget);
            final AppWidgetTarget appWidgetTarget = new AppWidgetTarget(PlayerWidgetService.this, views, R.id.widget_art_work, appWidgetIds);

            //TODO:: Navigate to now playing screen
            Intent startApp = new Intent(getBaseContext(), PodCastListActivity.class);
            startApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent startAppPending = PendingIntent.getActivity(getBaseContext(), 0,
                    startApp, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setOnClickPendingIntent(R.id.widget, startAppPending);

            final Track track = PlaybackService.getCurrentTrack();

            //If the Track is not null update the widget.
            if (track != null) {
                views.setTextViewText(R.id.widget_track_title, track.getTitle());
                views.setTextViewText(R.id.widget_artist, track.getArtist());
                views.setImageViewResource(R.id.widget_playback_view_toggle_play, R.drawable.ic_pause_white);

                //We load the image in a handler since we are using glide in a service.
                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(PlayerWidgetService.this)
                                .load(track.getArtworkUrl())
                                .asBitmap()
                                .into(appWidgetTarget);
                    }
                });

            } else {
                views.setTextViewText(R.id.widget_track_title, "Nothing playing");
                views.setTextViewText(R.id.widget_artist, "--");
            }

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "@onDestroy:: Service is about to be destroyed");
    }

}
