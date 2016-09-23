package com.thomaskioko.podadddict.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Helper class that handles device functionality.
 *
 * @author Thomas Kioko
 */
public class DeviceUtils {

    /**
     * Method to check if device is connected to the internet.
     *
     * @param context Application context.
     * @return {@link Boolean} True/False. Whether the device is connected.
     */
    public static boolean isNetworkConnected(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}