package com.thomaskioko.podadddict.app.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.service.PlayerWidgetService;

/**
 * Helper class to aid in implementing an AppWidget provider.
 *
 * @author kioko.
 */

public class PlayerWidgetProvider extends AppWidgetProvider {
    public static String ACTION_WIDGET_NEXT = "ActionReceiverNext";
    public static String ACTION_WIDGET_PREVIOUS = "ActionReceiverPrevious";
    public static String ACTION_WIDGET_PLAY = "ActionReceiverPlay";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.player_widget);

        //Update the widget view.
        remoteViews.setTextViewText(R.id.widget_track_title, "Nothing playing");
        remoteViews.setTextViewText(R.id.widget_artist, "--");

        Intent active = new Intent(context, PlayerWidgetProvider.class);
        active.setAction(ACTION_WIDGET_NEXT);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_playback_view_next, actionPendingIntent);

        active = new Intent(context, PlayerWidgetProvider.class);
        active.setAction(ACTION_WIDGET_PREVIOUS);
        actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_playback_view_previous, actionPendingIntent);

        active = new Intent(context, PlayerWidgetProvider.class);
        active.setAction(ACTION_WIDGET_PLAY);
        actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_playback_view_toggle_play, actionPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, PlayerWidgetService.class));
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(ACTION_WIDGET_NEXT)) {
            Log.i("onReceive", ACTION_WIDGET_NEXT);
        } else if (intent.getAction().equals(ACTION_WIDGET_PREVIOUS)) {
            Log.i("onReceive", ACTION_WIDGET_PREVIOUS);
        } else if (intent.getAction().equals(ACTION_WIDGET_PLAY)) {
            Log.i("onReceive", ACTION_WIDGET_PLAY);
        } else {
            super.onReceive(context, intent);
        }
    }

}
