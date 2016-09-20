package com.thomaskioko.podadddict.app.data.sync.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.thomaskioko.podadddict.app.data.sync.PodAdddictSyncAdapter;


/**
 * Service that invokes {@link PodAdddictSyncAdapter}
 *
 * @author Thomas Kioko
 */
public class PodAdddictSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private PodAdddictSyncAdapter sPodAdddictSyncAdapter = null;


    @Override
    public void onCreate() {
        Log.d("PodAdddictSyncService", "onCreate - PodAdddictSyncService");
        synchronized (sSyncAdapterLock) {
            if (sPodAdddictSyncAdapter == null) {
                sPodAdddictSyncAdapter = new PodAdddictSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sPodAdddictSyncAdapter.getSyncAdapterBinder();
    }
}