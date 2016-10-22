package com.thomaskioko.podadddict.app.util;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;

import com.thomaskioko.podadddict.app.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Class to handle to displaying of notifications.
 *
 * @author Thomas Kioko
 */

public class NotificationUtils {

    private static String LOG_TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;

    /**
     * Default constructor.
     *
     * @param mContext {@link Context} Context in which application is called
     */
    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Helper method to show notification.
     *
     * @param title   Podcast title
     * @param summary Podcast summary
     * @param author  Podcast author
     * @param intent  {@link Intent}
     */
    public void showNotificationMessage(String title, String summary, String author, Intent intent) {
        showNotificationMessage(title, summary, author, intent, null);
    }

    /**
     * Helper method to show notification.
     *
     * @param title    Podcast title
     * @param summary  Podcast summary
     * @param author   Podcast author
     * @param intent   {@link Intent}
     * @param imageUrl Podcast artwork url
     */
    public void showNotificationMessage(final String title, final String summary, final String author,
                                        Intent intent, String imageUrl) {
        // Check for empty push message
        if (TextUtils.isEmpty(summary))
            return;


        // notification icon
        final int icon = R.mipmap.ic_launcher;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getPackageName() + "/raw/notification");

        if (!TextUtils.isEmpty(imageUrl)) {

            if (imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                Bitmap bitmap = getBitmapFromURL(imageUrl);

                if (bitmap != null) {
                    showBigNotification(bitmap, mBuilder, icon, title, summary, author, resultPendingIntent, alarmSound);
                } else {
                    showSmallNotification(mBuilder, icon, title, summary, author, resultPendingIntent, alarmSound);
                }
            }
        } else {
            showSmallNotification(mBuilder, icon, title, summary, author, resultPendingIntent, alarmSound);
            playNotificationSound();
        }
    }

    /**
     * Helper method to show big notification.
     *
     * @param mBuilder            Notification builder instance.
     * @param icon                Icon
     * @param title               Podcast title
     * @param summary             Podcast summary
     * @param author              Podcast author
     * @param resultPendingIntent {@link PendingIntent}
     * @param alarmSound          Alarm sound to play.
     */
    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title,
                                       String summary, String author, PendingIntent resultPendingIntent, Uri alarmSound) {

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(summary);

        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(inboxStyle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(summary)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ApplicationConstants.NOTIFICATION_ID, notification);
    }

    /**
     * Helper method to show big notification.
     *
     * @param bitmap              {@link Bitmap}
     * @param mBuilder            Notification builder instance.
     * @param icon                Icon
     * @param title               Podcast title
     * @param summary             Podcast summary
     * @param author              Podcast author
     * @param resultPendingIntent {@link PendingIntent}
     * @param alarmSound          Alarm sound to play.
     */
    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon,
                                     String title, String summary, String author,
                                     PendingIntent resultPendingIntent, Uri alarmSound) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(summary).toString());
        bigPictureStyle.bigPicture(bitmap);
        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(bigPictureStyle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(summary)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ApplicationConstants.NOTIFICATION_ID_BIG_IMAGE, notification);
    }

    /**
     * Downloading push notification image before displaying it in he notification tray
     *
     * @param strURL Podcast image url
     * @return Bitmap
     */
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            GoogleAnalyticsUtil.trackException(mContext, e);
            return null;
        }
    }

    /**
     * Helper method to play notification..
     */
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            GoogleAnalyticsUtil.trackException(mContext, e);
        }
    }

    /**
     * Method checks if the app is in background or not
     *
     * @param context {@link Context}
     * @return {@link Boolean}
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    /**
     * Clears notification tray messages
     *
     * @param context {@link}
     */
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
