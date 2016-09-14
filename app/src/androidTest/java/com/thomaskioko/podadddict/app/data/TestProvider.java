package com.thomaskioko.podadddict.app.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.thomaskioko.podadddict.app.data.PodCastContract.PodCastFeedEntry;
import com.thomaskioko.podadddict.app.data.PodCastContract.PodCastFeedPlaylistEntry;
import com.thomaskioko.podadddict.app.data.provider.PodCastProvider;
import com.thomaskioko.podadddict.app.data.utils.TestUtilities;

/**
 * This class contains basic test classes to test that at least the
 * basic functionality has been implemented correctly.
 *
 * @author Thomas Kioko
 */
public class TestProvider extends AndroidTestCase {

    private static final String LOG_TAG = TestProvider.class.getSimpleName();


    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /**
     * Method to test if provider has correctly been set up
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // PodCastProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                PodCastProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: PodCastProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + PodCastContract.CONTENT_AUTHORITY,
                    providerInfo.authority, PodCastContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: PodCastProvider not registered at " + mContext.getPackageName(), false);
        }
    }

    /**
     * This test doesn't touch the database.  It verifies that the ContentProvider returns
     * the correct type for each type of URI that it can handle.
     */
    public void testGetType() {
        // content://com.example.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(PodCastFeedPlaylistEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the PodCastFeedPlaylistEntry CONTENT_URI should return PodCastFeedPlaylistEntry.CONTENT_TYPE",
                PodCastFeedPlaylistEntry.CONTENT_TYPE, type);

        int testFeedId = 94074;
        // content://com.example.android.sunshine.app/weather/94074
        type = mContext.getContentResolver().getType(
                PodCastFeedPlaylistEntry.buildPodCastFeedPlaylist(testFeedId));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the PodCastFeedPlaylistEntry CONTENT_URI with location should return PodCastFeedPlaylistEntry.CONTENT_TYPE",
                PodCastFeedPlaylistEntry.CONTENT_TYPE, type);

        // content://com.thomaskioko.podadddict.app.data/podCastFeed/
        type = mContext.getContentResolver().getType(PodCastFeedEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.thomaskioko.podadddict.app.data/podCastFeed
        assertEquals("Error: the PodCastFeedEntry CONTENT_URI should return PodCastFeedEntry.CONTENT_TYPE",
                PodCastFeedEntry.CONTENT_TYPE, type);
    }


    /**
     * This test uses the database directly to insert and then uses the ContentProvider to
     * read out the data. It test to see if the basic PodCastFeed query functionality
     * given in the ContentProvider is working correctly.
     */
    public void testBasicPlaylistQuery() {
        // insert our test records into the database
        PodCastFeedDbHelper dbHelper = new PodCastFeedDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long locationRowId = TestUtilities.insertPodcastFeedValues(mContext);

        // Fantastic.  Now that we have a feed, add some playlist data!
        ContentValues podcastPlaylistValues = TestUtilities.createPodcastPlaylistValues(locationRowId);

        long weatherRowId = db.insert(PodCastFeedPlaylistEntry.TABLE_NAME, null, podcastPlaylistValues);
        assertTrue("Unable to Insert PodCastFeedPlaylistEntry into the Database", weatherRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor cursor = mContext.getContentResolver().query(
                PodCastFeedPlaylistEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicPlaylistQuery", cursor, podcastPlaylistValues);
    }

    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if your location queries are
        performing correctly.
     */
    public void testBasicFeedQueries() {
        // insert our test records into the database
        PodCastFeedDbHelper dbHelper = new PodCastFeedDbHelper(mContext);

        ContentValues testValues = TestUtilities.createPodCastFeedValues();

        // Test the basic content provider query
        Cursor cursor = mContext.getContentResolver().query(
                PodCastFeedEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicFeedQueries, location query", cursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: PodCastFeed Query did not properly set NotificationUri",
                    cursor.getNotificationUri(), PodCastFeedEntry.CONTENT_URI);
        }
    }


    /**
     * Make sure we can still delete after adding/updating stuff. It relies on insertions with
     * testInsertReadProvider, so insert and  query functionality must also be
     * complete before this test can be used.
     */
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createPodCastFeedValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver testContentObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(PodCastFeedEntry.CONTENT_URI, true, testContentObserver);
        Uri locationUri = mContext.getContentResolver().insert(PodCastFeedEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        testContentObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(testContentObserver);

        long rowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(rowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                PodCastFeedEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating PodCastFeedEntry.",
                cursor, testValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = TestUtilities.createPodcastPlaylistValues(rowId);
        // The TestContentObserver is a one-shot class
        testContentObserver = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(PodCastFeedPlaylistEntry.CONTENT_URI, true, testContentObserver);

        Uri weatherInsertUri = mContext.getContentResolver()
                .insert(PodCastFeedPlaylistEntry.CONTENT_URI, weatherValues);
        assertTrue(weatherInsertUri != null);

        // Did our content observer get called?  Students:  If this fails, your insert weather
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        testContentObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(testContentObserver);

        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = mContext.getContentResolver().query(
                PodCastFeedPlaylistEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating PodCastFeedPlaylistEntry insert.",
                weatherCursor, weatherValues);

        // Add the location values in with the weather data so that we can make
        // sure that the join worked and we actually get all the values back
        weatherValues.putAll(testValues);

        // Get the joined Weather and Location data
        weatherCursor = mContext.getContentResolver().query(
                PodCastFeedPlaylistEntry.buildPodCastFeedPlaylistUri(TestUtilities.TEST_FEED_ID),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Weather and Location Data.",
                weatherCursor, weatherValues);

    }

    /**
     * Make sure we can still delete after adding/updating stuff
     */
    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our location delete.
        TestUtilities.TestContentObserver locationObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(PodCastFeedEntry.CONTENT_URI, true, locationObserver);

        // Register a content observer for our weather delete.
        TestUtilities.TestContentObserver weatherObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(PodCastFeedPlaylistEntry.CONTENT_URI, true, weatherObserver);

        deleteAllRecordsFromProvider();

        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        locationObserver.waitForNotificationOrFail();
        weatherObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(locationObserver);
        mContext.getContentResolver().unregisterContentObserver(weatherObserver);
    }

    /**
     *
     */
    public void testUpdatePodCastFeed() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createPodCastFeedValues();

        Uri uri = mContext.getContentResolver().
                insert(PodCastFeedEntry.CONTENT_URI, values);
        long feedRowId = ContentUris.parseId(uri);

        // Verify we got a row back.
        assertTrue(feedRowId != -1);
        Log.d(LOG_TAG, "New row id: " + feedRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(PodCastFeedEntry._ID, feedRowId);
        updatedValues.put(PodCastFeedEntry.COLUMN_PODCAST_FEED_TITLE, "Santa's Village");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor feedCursor = mContext.getContentResolver().query(PodCastFeedEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver testContentObserver = TestUtilities.getTestContentObserver();
        if (feedCursor != null) {
            feedCursor.registerContentObserver(testContentObserver);
        }

        int count = mContext.getContentResolver().update(
                PodCastFeedEntry.CONTENT_URI, updatedValues, PodCastFeedEntry._ID + "= ?",
                new String[]{Long.toString(feedRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        testContentObserver.waitForNotificationOrFail();

        if (feedCursor != null) {
            feedCursor.unregisterContentObserver(testContentObserver);
            feedCursor.close();
        }

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                PodCastFeedEntry.CONTENT_URI,
                null,   // projection
                PodCastFeedEntry._ID + " = " + feedRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdatePodCastFeed.  Error validating location entry update.",
                cursor, updatedValues);

        if (cursor != null) {
            cursor.close();
        }
    }


    /**
     * This helper function deletes all records from both database tables using the ContentProvider.
     * It also queries the ContentProvider to make sure that the database has been successfully
     * deleted, so it cannot be used until the Query and Delete functions have been written
     * in the ContentProvider.
     * <p>
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                PodCastFeedPlaylistEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                PodCastFeedEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                PodCastFeedPlaylistEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        if (cursor != null) {
            assertEquals("Error: Records not deleted from PodCastFeedPlaylist table during delete", 0, cursor.getCount());

            cursor = mContext.getContentResolver().query(
                    PodCastFeedEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                assertEquals("Error: Records not deleted from PodCastFeed table during delete", 0, cursor.getCount());
            }
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    /**
     * This helper function deletes all records from both database tables using the database
     * functions only.  This is designed to be used to reset the state of the database until the
     * delete functionality is available in the ContentProvider.
     */
    public void deleteAllRecordsFromDB() {
        PodCastFeedDbHelper dbHelper = new PodCastFeedDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(PodCastFeedPlaylistEntry.TABLE_NAME, null, null);
        db.delete(PodCastFeedEntry.TABLE_NAME, null, null);
        db.close();
    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }
}
