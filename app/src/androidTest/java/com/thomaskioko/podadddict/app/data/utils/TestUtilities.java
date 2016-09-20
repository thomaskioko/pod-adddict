package com.thomaskioko.podadddict.app.data.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

import com.thomaskioko.podadddict.app.data.PodCastContract;
import com.thomaskioko.podadddict.app.data.db.PodCastFeedDbHelper;

import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


/**
 * @author Thomas Kioko
 */
public class TestUtilities {
    public static final int TEST_FEED_ID = 523121474;
    public static final String TEST_FEED_PLAYLIST_TRACK_ID = "523121474";
    public static final String TEST_FEED_PLAYLIST_ARTIST_NAME = "NPR";
    public static final String TEST_FEED_PLAYLIST_TRACK_NAME = "TED Radio Hour";
    public static final String TEST_FEED_PLAYLIST_URL = "http://www.npr.org/rss/podcast.php?id=510298";
    public static final String TEST_FEED_PLAYLIST_ART_WORK_URL_100 = "http://is5.mzstatic.com/image/thumb/Music71/v4/21/3a/3e/213a3e55-3264-653c-7fda-b8ef4dc39bbf/source/100x100bb.jpg";
    public static final String TEST_FEED_PLAYLIST_ART_WORK_URL_600 = "http://is5.mzstatic.com/image/thumb/Music71/v4/21/3a/3e/213a3e55-3264-653c-7fda-b8ef4dc39bbf/source/600x600bb.jpg";
    public static final int TEST_FEED_PLAYLIST_TRACK_COUNT = 147;
    public static final String TEST_FEED_PLAYLIST_GENRE = "Technology";
    private static final String TEST_FEED_CATEGORY = "Technology";
    private static final String TEST_FEED_RIGHTS = "© Copyright 2016 NPR - For Personal Use Only";
    private static final String TEST_FEED_SUMMARY = "Child abductions are rare crimes. And they're typically solved. For 27 years, the investigation into the abduction of Jacob Wetterling in rural Minnesota yielded no answers. In the most comprehensive reporting on this case, APM Reports and reporter Madeleine Baran reveal how law enforcement mishandled one of the most notorious child abductions in the country and how those failures fueled national anxiety about stranger danger, changed how adults parent their kids and led to the nation's sex-offender registries.";
    private static final String TEST_FEED_STATUS = "1";

    /**
     * @param error          Error Message
     * @param valueCursor    {@link Cursor}
     * @param expectedValues {@link ContentValues}
     */
    public static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    /**
     * @param error          Error Message
     * @param valueCursor    {@link Cursor}
     * @param expectedValues {@link ContentValues}
     */
    public static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /**
     * Helper method to populate content values with data.
     *
     * @param feedRowId Row Id
     * @return {@link ContentValues}
     */
    public static ContentValues createPodcastPlaylistValues(long feedRowId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PodCastContract.PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_ID, feedRowId);
        contentValues.put(PodCastContract.PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_TRACK_ID, TEST_FEED_PLAYLIST_TRACK_ID);
        contentValues.put(PodCastContract.PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_ARTIST_NAME, TEST_FEED_PLAYLIST_ARTIST_NAME);
        contentValues.put(PodCastContract.PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_TRACK_NAME, TEST_FEED_PLAYLIST_TRACK_NAME);
        contentValues.put(PodCastContract.PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_URL, TEST_FEED_PLAYLIST_URL);
        contentValues.put(PodCastContract.PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_ART_WORK_URL_100, TEST_FEED_PLAYLIST_ART_WORK_URL_100);
        contentValues.put(PodCastContract.PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_ART_WORK_URL_600, TEST_FEED_PLAYLIST_ART_WORK_URL_600);
        contentValues.put(PodCastContract.PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_TRACK_COUNT, TEST_FEED_PLAYLIST_TRACK_COUNT);
        contentValues.put(PodCastContract.PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_GENRE, TEST_FEED_PLAYLIST_GENRE);

        return contentValues;
    }

    /**
     * Helper method to populate content values with data.
     *
     * @return {@link ContentValues}
     */
    public static ContentValues createPodCastFeedValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_ID, TEST_FEED_ID);
        testValues.put(PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_TITLE, TEST_FEED_PLAYLIST_TRACK_NAME);
        testValues.put(PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_IMAGE_URL, TEST_FEED_PLAYLIST_ART_WORK_URL_600);
        testValues.put(PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_SUMMARY, TEST_FEED_SUMMARY);
        testValues.put(PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_ARTIST, TEST_FEED_PLAYLIST_ARTIST_NAME);
        testValues.put(PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_CATEGORY, TEST_FEED_CATEGORY);

        return testValues;
    }

    /**
     * Helper method to create podcast subscription content values
     *
     * @param feedRowId podcat feed id
     * @return PodcastFeedSubscriptionEntry {@link ContentValues}
     */
    public static ContentValues createPodcastSubscriptionValues(long feedRowId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PodCastContract.PodcastFeedSubscriptionEntry.COLUMN_PODCAST_FEED_ID, feedRowId);

        return contentValues;
    }

    /**
     * Helper method to insert data in PodcastFeed Table
     *
     * @param context Application Context
     * @return Row ID
     */
    public static long insertPodcastFeedValues(Context context) {
        // insert our test records into the database
        PodCastFeedDbHelper dbHelper = new PodCastFeedDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createPodCastFeedValues();

        long podcastFeedRowId = db.insert(PodCastContract.PodCastFeedEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Podcast Feed Values", podcastFeedRowId != -1);

        return podcastFeedRowId;
    }

    /**
     * Helper class used to test the ContentObserver callbacks using the PollingCheck class
     * that we grabbed from the Android.
     * <p>
     * Note that this only tests that the onChange function is called; it does not test that the
     * correct Uri is returned.
     */
    public static class TestContentObserver extends ContentObserver {
        final HandlerThread mHandlerThread;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread handlerThread = new HandlerThread("ContentObserverThread");
            handlerThread.start();
            return new TestContentObserver(handlerThread);
        }

        /**
         * Constructor.
         *
         * @param handlerThread {@link HandlerThread}
         */
        private TestContentObserver(HandlerThread handlerThread) {
            super(new Handler(handlerThread.getLooper()));
            mHandlerThread = handlerThread;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHandlerThread.quit();
        }
    }

    /**
     * @return {@link TestContentObserver}
     */
    public static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

}
