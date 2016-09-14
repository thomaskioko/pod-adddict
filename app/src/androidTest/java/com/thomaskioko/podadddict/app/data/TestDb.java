package com.thomaskioko.podadddict.app.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.thomaskioko.podadddict.app.data.utils.TestUtilities;

import java.util.HashSet;

/**
 * This class contains DB Test cases.
 *
 * @author Thomas Kioko
 */
public class TestDb extends AndroidTestCase {

    /**
     * Since we want each test to start with a clean slate
     */
    private void deleteTheDatabase() {
        mContext.deleteDatabase(PodCastFeedDbHelper.DATABASE_NAME);
    }

    /**
     * This function gets called before each test is executed to delete the database.  This makes
     * sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }


    /**
     * Function to test database creation.
     *
     * @throws Throwable
     */
    public void testCreateDb() throws Throwable {
        /**
         * Build a HashSet of all of the table names we wish to look for. Note that there will be
         * another table in the DB that stores the Android metadata (db version information)
         */
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(PodCastContract.PodCastFeedEntry.TABLE_NAME);
        tableNameHashSet.add(PodCastContract.PodCastFeedPlaylistEntry.TABLE_NAME);

        mContext.deleteDatabase(PodCastFeedDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new PodCastFeedDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        //Check if we have created the tables.
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                cursor.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(cursor.getString(0));
        } while (cursor.moveToNext());


        /**
         * If this fails, it means that your database doesn't contain both the location entry and
         * weather entry tables
         */
        assertTrue("Error: Your database was created without both the podcastFeed entry and podcastPlaylist entry tables",
                tableNameHashSet.isEmpty());

        // Check if the tables contain the correct columns?
        cursor = db.rawQuery("PRAGMA table_info(" + PodCastContract.PodCastFeedEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                cursor.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> podCastFeedColumnHashSet = new HashSet<>();
        podCastFeedColumnHashSet.add(PodCastContract.PodCastFeedEntry._ID);
        podCastFeedColumnHashSet.add(PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_ID);
        podCastFeedColumnHashSet.add(PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_TITLE);
        podCastFeedColumnHashSet.add(PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_IMAGE_URL);
        podCastFeedColumnHashSet.add(PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_SUMMARY);
        podCastFeedColumnHashSet.add(PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_ARTIST);
        podCastFeedColumnHashSet.add(PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_CATEGORY);
        podCastFeedColumnHashSet.add(PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_SUBSCRIBE_STATE);

        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            podCastFeedColumnHashSet.remove(columnName);
        } while (cursor.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required podcast feed
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                podCastFeedColumnHashSet.isEmpty());
        cursor.close();
        db.close();
    }

    /**
     * Helper method to test inserting data in the PodCastFeed table.
     *
     * @return {@link long} Id of the record that was last inserted.
     */
    public long testPodCastFeedTable() {
        // First step: Get reference to writable database
        SQLiteDatabase db = new PodCastFeedDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // Create ContentValues of what you want to insert
        ContentValues contentValues = TestUtilities.createPodCastFeedValues();

        // Insert ContentValues into database and get a row ID back
        long insertResult = db.insert(PodCastContract.PodCastFeedEntry.TABLE_NAME, null, contentValues);

        assertTrue(insertResult != -1);
        // Move the cursor to a valid database row
        Cursor cursor = db.query(
                PodCastContract.PodCastFeedEntry.TABLE_NAME, //Table name
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        assertTrue("Error: No Records returned from location query", cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        TestUtilities.validateCursor("Error: Location Query Validation Failed", cursor, contentValues);

        assertFalse("Error: More than one record returned from location query", cursor.moveToNext());
        // Finally, close the cursor and database
        cursor.close();

        return insertResult;
    }

    /**
     * Helper method to insert data to PodCastPlaylist table.
     */
    public void testPodcastPlaylistTable() {
        long rowId = insertPodcastFeed();

        // Make sure we have a valid row ID.
        assertFalse("Error: PodCast Feed Not Inserted Correctly", rowId == -1L);

        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        PodCastFeedDbHelper dbHelper = new PodCastFeedDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step (PodCastPlaylist): Create PodCastPlaylist values
        ContentValues podcastPlaylistValues = TestUtilities.createPodcastPlaylistValues(rowId);

        // Third Step (PodCastPlaylist): Insert ContentValues into database and get a row ID back
        long weatherRowId = db.insert(PodCastContract.PodCastFeedPlaylistEntry.TABLE_NAME, null, podcastPlaylistValues);
        assertTrue(weatherRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor playlistCursor = db.query(
                PodCastContract.PodCastFeedPlaylistEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue("Error: No Records returned from PodcastFeed query", playlistCursor.moveToFirst());

        // Fifth Step: Validate the PodCastFeed Query
        TestUtilities.validateCurrentRecord("testInsertReadDb PodCastPlaylist failed to validate",
                playlistCursor, podcastPlaylistValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from PodCastPlaylist query",
                playlistCursor.moveToNext());

        // Sixth Step: Close cursor and database
        playlistCursor.close();
        dbHelper.close();
    }


    /**
     * Helper method to test data insertion in PodCastFeed table
     *
     * @return Location row Id
     */
    private long insertPodcastFeed() {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        PodCastFeedDbHelper dbHelper = new PodCastFeedDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        ContentValues testValues = TestUtilities.createPodCastFeedValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long locationRowId = db.insert(PodCastContract.PodCastFeedEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                PodCastContract.PodCastFeedEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue("Error: No Records returned from PodcastFeed query", cursor.moveToFirst());

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: PodcastFeed Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from PodcastFeed query",
                cursor.moveToNext());

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return locationRowId;
    }
}
