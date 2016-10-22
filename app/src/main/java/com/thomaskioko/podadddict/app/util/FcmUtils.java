package com.thomaskioko.podadddict.app.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.thomaskioko.podadddict.app.service.MyFirebaseInstanceIDService;
import com.thomaskioko.podadddict.app.ui.PodCastListActivity;

/**
 * This class checks whether the device supports FCM and handles FCM Registration
 *
 * @author Thomas Kioko
 * @version Version 1.0
 */
public class FcmUtils {

    private Context mContext;
    private PodCastListActivity mActivationActivity;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String LOG_TAG = FcmUtils.class.getSimpleName();

    /**
     * Default Constructor
     *
     * @param context Application context.
     */
    public FcmUtils(Context context) {
        mContext = context;
    }

    /**
     * Default Constructor
     *
     * @param activationActivity ActivationActivity class
     * @param context            Application context.
     */
    public FcmUtils(PodCastListActivity activationActivity, Context context) {
        mContext = context;
        mActivationActivity = activationActivity;
    }

    /**
     * Invoke {@link com.thomaskioko.podadddict.app.service.MyFirebaseInstanceIDService} to get FCM token
     */
    public void registerFCM() {
        mContext.startService(new Intent(mContext, MyFirebaseInstanceIDService.class));
    }

    /**
     * Check if device supports Play Services
     *
     * @return True/False
     */
    public boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(mActivationActivity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(LOG_TAG,
                        "@checkPlayServices: This device is not supported. Google Play Services not installed!");
            }
            return false;
        }
        return true;
    }
}
