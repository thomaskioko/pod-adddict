package com.thomaskioko.podadddict.app.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.thomaskioko.podadddict.app.ui.PodCastListActivity;
import com.thomaskioko.podadddict.app.util.ApplicationConstants;
import com.thomaskioko.podadddict.app.util.GoogleAnalyticsUtil;
import com.thomaskioko.podadddict.app.util.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class receives the firebase messages and shows notifications.
 *
 * @author Thomas Kioko
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils mNotificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                GoogleAnalyticsUtil.trackException(getApplicationContext(), e);
            }
        }
    }

    /**
     * Helper method to send a received message via a broadcast receiver.
     *
     * @param message Message received via notification.
     */
    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(ApplicationConstants.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    /**
     * Helper method to find parse JSON data
     *
     * @param json Json Payload
     */
    private void handleDataMessage(JSONObject json) {

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            String imageUrl = data.getString("imageUrl");
            String author = data.getString("author");

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(ApplicationConstants.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), PodCastListActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, author, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, author, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            GoogleAnalyticsUtil.trackException(getApplicationContext(), e);
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            GoogleAnalyticsUtil.trackException(getApplicationContext(), e);
        }
    }

    /**
     * Showing notification with text only
     *
     * @param context {@link Context}
     * @param title   Podcast title
     * @param summary Podcast summary
     * @param author  Podcast author
     * @param intent  {@link Intent}
     */
    private void showNotificationMessage(Context context, String title, String summary, String author, Intent intent) {
        mNotificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mNotificationUtils.showNotificationMessage(title, summary, author, intent);
    }

    /**
     * Showing notification with text and image.
     *
     * @param title    Podcast title
     * @param summary  Podcast summary
     * @param author   Podcast author
     * @param intent   {@link Intent}
     * @param imageUrl Podcast artwork url
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String summary,
                                                     String author, Intent intent, String imageUrl) {
        mNotificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mNotificationUtils.showNotificationMessage(title, summary, author, intent, imageUrl);
    }
}

