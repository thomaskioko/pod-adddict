package com.thomaskioko.podadddict.app.data.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * The service which allows the sync adapter framework to access {@link PodAdddictAuthenticator}.
 *
 * @author Thomas Kioko
 */
public class PodAddditctAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private PodAdddictAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new PodAdddictAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
