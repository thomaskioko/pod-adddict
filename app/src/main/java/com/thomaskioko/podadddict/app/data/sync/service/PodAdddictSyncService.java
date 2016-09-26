package com.thomaskioko.podadddict.app.data.sync.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.thomaskioko.podadddict.app.data.sync.PodAdddictSyncAdapter;
import com.thomaskioko.podadddict.app.util.LogUtils;


/**
 * Service that invokes {@link PodAdddictSyncAdapter}
 *
 * @author Thomas Kioko
 */
public class PodAdddictSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private PodAdddictSyncAdapter sPodAdddictSyncAdapter = null;
    private static final String LOG_TAG = PodAdddictSyncService.class.getSimpleName();

    @Override
    public void onCreate() {
        LogUtils.showDebugLog(LOG_TAG, "@onCreate - PodAdddictSyncService");
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