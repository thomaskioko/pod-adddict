package com.thomaskioko.podadddict.app;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.thomaskioko.podadddict.app.api.ApiClient;
import com.thomaskioko.podadddict.app.ui.PodCastEpisodeActivity;
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

    @Override
    public void onCreate() {
        super.onCreate();

        //Only enable Stetho in Debug mode
        if(ApplicationConstants.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }

        //Initialise Player.
        mPodAdddictPlayer = new PodAdddictPlayer.Builder()
                .from(this)
                .notificationActivity(PodCastEpisodeActivity.class)
                .notificationIcon(R.drawable.ic_notification)
                .build();
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
    public static PodAdddictPlayer getPodAdddictPlayer(){
       return mPodAdddictPlayer;
    }
}
