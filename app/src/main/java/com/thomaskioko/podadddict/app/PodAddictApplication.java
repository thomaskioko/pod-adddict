package com.thomaskioko.podadddict.app;

import android.app.Application;

import com.thomaskioko.podadddict.app.api.ApiClient;
import com.thomaskioko.podadddict.app.util.ApplicationConstants;

/**
 * @author Thomas Kioko
 */
public class PodAddictApplication extends Application {

    private static ApiClient mApiClientInstance = new ApiClient();

    @Override
    public void onCreate() {
        super.onCreate();
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
}
