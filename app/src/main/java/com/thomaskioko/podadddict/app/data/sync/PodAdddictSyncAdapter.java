package com.thomaskioko.podadddict.app.data.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.thomaskioko.podadddict.app.PodAddictApplication;
import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.api.ApiClient;
import com.thomaskioko.podadddict.app.api.model.responses.TopPodCastResponse;
import com.thomaskioko.podadddict.app.data.db.DbUtils;
import com.thomaskioko.podadddict.app.util.ApplicationConstants;
import com.thomaskioko.podadddict.app.util.LogUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Thomas Kioko
 */

public class PodAdddictSyncAdapter extends AbstractThreadedSyncAdapter {

    // Interval at which to sync with the weather, in milliseconds.
    // 60 seconds (1 minute)  180 = 3 hours
    private static final int SYNC_INTERVAL = 30;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private Context mContext;
    private final String LOG_TAG = PodAdddictSyncAdapter.class.getSimpleName();


    /**
     * Constructor
     *
     * @param context        Context
     * @param autoInitialize {@link Boolean}
     */
    public PodAdddictSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "@onPerformSync Called.");

        //Invoke API to fetch top rated and save that to the DB
        ApiClient apiClient = PodAddictApplication.getApiClientInstance();
        apiClient.setEndpointUrl(ApplicationConstants.ITUNES_END_POINT);

        Call<TopPodCastResponse> topPodCastResponseCall = apiClient.iTunesServices().getTopRatedPodCasts();
        topPodCastResponseCall.enqueue(new Callback<TopPodCastResponse>() {
            @Override
            public void onResponse(Call<TopPodCastResponse> call, Response<TopPodCastResponse> response) {

               int records = DbUtils.insertPodcastFeeds(mContext, response.body().getFeed().getEntry());
                //TODO:: Notify the user of the number or records updated.
                LogUtils.showDebugLog(LOG_TAG, "@onPerformSync:: Insert PodCast Feeds Complete. " + records + " Inserted");
            }

            @Override
            public void onFailure(Call<TopPodCastResponse> call, Throwable t) {
                LogUtils.showErrorLog(LOG_TAG, "@onFailure:: Error " + t.getMessage());
            }
        });

    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    private static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     *
     * @param context      {@link Context}
     * @param syncInterval Poll frequency. Hourly, days etc
     * @param flexTime     before seconds
     */
    private static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    /**
     * @param newAccount Account
     * @param context    {@link Context}
     */
    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        PodAdddictSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    /**
     * Helper method used to initialize the Sync adapter
     *
     * @param context {@link Context}
     */
    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);

    }


}