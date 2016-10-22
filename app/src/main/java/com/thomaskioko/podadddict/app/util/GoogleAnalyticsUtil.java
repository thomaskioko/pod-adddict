package com.thomaskioko.podadddict.app.util;

import android.content.Context;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.thomaskioko.podadddict.app.PodAddictApplication;

/**
 * This class contains methods used to log events and actions to Google Analytics
 *
 * @author Thomas Kioko
 */

public class GoogleAnalyticsUtil {

    /**
     * Helper method to log the name of the screen being
     *
     * @param screenName Name of the screen.
     */
    public static void trackScreenView(String screenName) {

        Tracker tracker = PodAddictApplication.getDefaultTracker();
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    /**
     * Helper method to track events that happen.
     *
     * @param category event category
     * @param action   action of the event e.g Download, Streaming
     * @param label    label
     */
    public static void trackEvent(String category, String action, String label) {
        Tracker tracker = PodAddictApplication.getDefaultTracker();

        // Build and send an Event.
        tracker.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }

    /**
     * Helper method log Exceptions.
     *
     * @param context   {@link Context} Context in which the method is called.
     * @param exception {@link Exception} Error
     */
    public static void trackException(Context context, Exception exception) {

        Tracker tracker = PodAddictApplication.getDefaultTracker();
        tracker.send(new HitBuilders.ExceptionBuilder()
                .setDescription(
                        new StandardExceptionParser(context, null)
                                .getDescription(Thread.currentThread().getName(), exception))
                .setFatal(false)
                .build()
        );
    }


}
