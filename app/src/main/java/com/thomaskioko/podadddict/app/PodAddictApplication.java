package com.thomaskioko.podadddict.app;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.thomaskioko.podadddict.app.api.ApiClient;
import com.thomaskioko.podadddict.app.ui.NowPlayingActivity;
import com.thomaskioko.podadddict.app.util.ApplicationConstants;
import com.thomaskioko.podadddict.musicplayerlib.player.PodAdddictPlayer;

/**
 * Application class.
 *
 * @author Thomas Kioko
 */
public class PodAddictApplication extends Application {

    private static ApiClient mApiClientInstance = new ApiClient();
    private static PodAdddictPlayer mPodAdddictPlayer;
    private static GoogleAnalytics mGoogleAnalytics;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        //Only enable Stetho in Debug mode
        if (ApplicationConstants.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }

        mContext = this;

        mPodAdddictPlayer = new PodAdddictPlayer.Builder()
                .from(mContext)
                .notificationActivity(NowPlayingActivity.class)
                .notificationIcon(R.drawable.ic_notification)
                .build();


        mGoogleAnalytics = GoogleAnalytics.getInstance(this);

    }

    /**
     * Helper method that instantiates the API instance.
     *
     * @return {@link ApiClient} instance
     */
    public static ApiClient getApiClientInstance() {
        mApiClientInstance.setIsDebug(ApplicationConstants.DEBUG);
        return mApiClientInstance;
    }

    /**
     * Method to return the instance of {@link PodAdddictPlayer}
     *
     * @return {@link PodAdddictPlayer} instance
     */
    public static PodAdddictPlayer getPodAdddictPlayer() {

        if (mPodAdddictPlayer == null) {

            return mPodAdddictPlayer = new PodAdddictPlayer.Builder()
                    .from(mContext)
                    .notificationActivity(NowPlayingActivity.class)
                    .notificationIcon(R.drawable.ic_notification)
                    .build();
        } else {
            return mPodAdddictPlayer;
        }
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public static Tracker getDefaultTracker() {

        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        return mGoogleAnalytics.newTracker(R.xml.global_tracker);
    }
}
