package com.thomaskioko.podadddict.app.data.provider;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.thomaskioko.podadddict.app.data.PodCastContract;
import com.thomaskioko.podadddict.app.data.db.PodCastFeedDbHelper;

/**
 * Content providers manage access to a structured set of data. They encapsulate the data, and provide
 * mechanisms for defining data security. Content providers are the standard interface that connects
 * data in one process with code running in another process.
 *
 * @author Thomas Kioko
 */

public class PodCastProvider extends ContentProvider {
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PodCastFeedDbHelper mOpenHelper;

    public static final int PLAYLIST_FEED = 100;
    public static final int PLAYLIST_WITH_FEED_ID = 101;
    public static final int PODCAST_FEED = 300;
    public static final int PODCAST_FEED_WITH_FEED_ID = 301;
    public static final int PODCAST_SUBSCRIPTION_FEED = 400;
    public static final int PODCAST_SUBSCRIPTION_FEED_WITH_ID = 401;

    private static final SQLiteQueryBuilder sqLiteQueryBuilder;

    static {
        sqLiteQueryBuilder = new SQLiteQueryBuilder();
    }

    //podCastFeed.feed_id = ?
    private static final String sPodCastFeedSelection =
            PodCastContract.PodCastFeedEntry.TABLE_NAME +
                    "." + PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_ID + " = ? ";

    //podCastFeed.feed_id = ? AND podCastFeedPlaylist.feed_id = ?
    private static final String sLocationSettingWithStartDateSelection =
            PodCastContract.PodCastFeedEntry.TABLE_NAME +
                    "." + PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_ID + " = ? AND " +
                    PodCastContract.PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_ID + " = ? ";


    @Override
    public boolean onCreate() {
        mOpenHelper = new PodCastFeedDbHelper(getContext());
        return true;
    }


    /**
     * Here is where you need to create the UriMatcher. This UriMatcher will
     * match each URI to the PLAYLIST_FEED, PLAYLIST_WITH_FEED_ID, PLAYLIST_WITH_FEED_AND_DATE,
     * and PODCAST_FEED integer constants defined above.  You can test this by uncommenting the
     * testUriMatcher test within TestUriMatcher.
     */
    public static UriMatcher buildUriMatcher() {

        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PodCastContract.CONTENT_AUTHORITY;

        //Create a corresponding code for each URI
        uriMatcher.addURI(authority, PodCastContract.PATH_PODCAST_FEED_PLAYLIST, PLAYLIST_FEED);
        uriMatcher.addURI(authority, PodCastContract.PATH_PODCAST_FEED_PLAYLIST + "/*", PLAYLIST_WITH_FEED_ID);


        uriMatcher.addURI(authority, PodCastContract.PATH_PODCAST_FEED, PODCAST_FEED);
        uriMatcher.addURI(authority, PodCastContract.PATH_PODCAST_FEED + "/*", PODCAST_FEED_WITH_FEED_ID);

        uriMatcher.addURI(authority, PodCastContract.PATH_PODCAST_FEED_SUBSCRIBED, PODCAST_SUBSCRIPTION_FEED);
        uriMatcher.addURI(authority, PodCastContract.PATH_PODCAST_FEED_SUBSCRIBED + "/*", PODCAST_SUBSCRIPTION_FEED_WITH_ID);
        return uriMatcher;
    }


    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PLAYLIST_FEED:
                return PodCastContract.PodCastFeedPlaylistEntry.CONTENT_TYPE;
            case PLAYLIST_WITH_FEED_ID:
                return PodCastContract.PodCastFeedPlaylistEntry.CONTENT_TYPE;
            case PODCAST_FEED:
                return PodCastContract.PodCastFeedEntry.CONTENT_TYPE;
            case PODCAST_FEED_WITH_FEED_ID:
                return PodCastContract.PodCastFeedEntry.CONTENT_TYPE;
            case PODCAST_SUBSCRIPTION_FEED:
                return PodCastContract.PodcastFeedSubscriptionEntry.CONTENT_TYPE;
            case PODCAST_SUBSCRIPTION_FEED_WITH_ID:
                return PodCastContract.PodcastFeedSubscriptionEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "podCastFeedPlaylist"
            case PLAYLIST_FEED: {
                retCursor = getPodCastFeedPlaylist(projection, selection, selectionArgs, sortOrder);
                break;
            }
            // "podCastFeedPlaylist/325404506"
            case PLAYLIST_WITH_FEED_ID: {
                retCursor = getPodCastFeedPlaylistWithId(uri, projection, sortOrder);
                break;
            }
            // "podCastFeed"
            case PODCAST_FEED: {
                retCursor = podCastFeed(projection, selection, selectionArgs, sortOrder);
                break;
            }
            // "podCastFeed/325404506"
            case PODCAST_FEED_WITH_FEED_ID: {
                //Set the tables
                sqLiteQueryBuilder.setTables(PodCastContract.PodCastFeedEntry.TABLE_NAME);
                retCursor = getPodcastFeedWithId(uri);
                break;
            }
            // "podCastSubscriptionFeed"
            case PODCAST_SUBSCRIPTION_FEED: {
                retCursor = podCastFeedSubscription(projection, selection, selectionArgs, sortOrder);
                break;
            }
            // "podCastSubscriptionFeed/325404506"
            case PODCAST_SUBSCRIPTION_FEED_WITH_ID: {
                //Set the tables
                sqLiteQueryBuilder.setTables(
                        PodCastContract.PodcastFeedSubscriptionEntry.TABLE_NAME + " INNER JOIN " +
                                PodCastContract.PodCastFeedEntry.TABLE_NAME +
                                " ON " + PodCastContract.PodcastFeedSubscriptionEntry.TABLE_NAME +
                                "." + PodCastContract.PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_FEED_ID +
                                " = " + PodCastContract.PodCastFeedEntry.TABLE_NAME +
                                "." + PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_ID
                );

                retCursor = getPodcastSubscriptionFeedWithId(uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (retCursor != null) {
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return retCursor;
    }

    /**
     * Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PLAYLIST_FEED: {
                long _id = db.insert(PodCastContract.PodCastFeedPlaylistEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = PodCastContract.PodCastFeedPlaylistEntry.buildPodCastFeedPlaylistUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case PODCAST_FEED: {
                long _id = db.insert(PodCastContract.PodCastFeedEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = PodCastContract.PodCastFeedEntry.buildPodCastFeedUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case PODCAST_SUBSCRIPTION_FEED: {
                long _id = db.insert(PodCastContract.PodcastFeedSubscriptionEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = PodCastContract.PodcastFeedSubscriptionEntry.buildSubscriptionUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //Register a content observer to watch for changes on the uri and notify the UI when th cursor changes
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case PLAYLIST_FEED: {
                rowsDeleted = db.delete(
                        PodCastContract.PodCastFeedPlaylistEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            }
            case PODCAST_FEED: {
                rowsDeleted = db.delete(
                        PodCastContract.PodCastFeedEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            //Register a content observer to watch for changes on the uri and notify the UI when th cursor changes
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case PLAYLIST_FEED: {
                rowsUpdated = db.update(
                        PodCastContract.PodCastFeedPlaylistEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            case PODCAST_FEED: {
                rowsUpdated = db.update(
                        PodCastContract.PodCastFeedEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //Register a content observer to watch for changes on the uri and notify the UI when th cursor changes
        if (rowsUpdated != 0) {
            //Register a content observer to watch for changes on the uri and notify the UI when th cursor changes
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase sqLiteDatabase = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PLAYLIST_FEED:
                sqLiteDatabase.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = sqLiteDatabase.insert(PodCastContract.PodCastFeedPlaylistEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    sqLiteDatabase.setTransactionSuccessful();
                } finally {
                    sqLiteDatabase.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }


    /**
     * @param projection    A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param selection     A filter declaring which rows to return
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values from selectionArgs
     * @param sortOrder     How to order the rows
     * @return {@link Cursor} with result
     */
    private Cursor getPodCastFeedPlaylist(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return mOpenHelper.getReadableDatabase().query(
                PodCastContract.PodCastFeedPlaylistEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    /**
     * @param projection    A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param selection     A filter declaring which rows to return
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values from selectionArgs
     * @param sortOrder     How to order the rows
     * @return {@link Cursor} with result
     */
    private Cursor podCastFeed(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return mOpenHelper.getReadableDatabase().query(
                PodCastContract.PodCastFeedEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    /**
     * @param uri        {@link Uri}
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder  How to order the rows
     * @return {@link Cursor} with result
     */
    private Cursor getPodCastFeedPlaylistWithId(Uri uri, String[] projection, String sortOrder) {
        String feedId = PodCastContract.PodCastFeedPlaylistEntry.getFeedIdFromUri(uri);
        long startDate = PodCastContract.PodCastFeedPlaylistEntry.getPlaylistFeedIdFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == 0) {
            selection = sPodCastFeedSelection;
            selectionArgs = new String[]{feedId};
        } else {
            selectionArgs = new String[]{feedId, Long.toString(startDate)};
            selection = sLocationSettingWithStartDateSelection;
        }

        return sqLiteQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    /**
     * Helper method to get specific Podcast feed using the select feed id
     *
     * @param uri Uri with selected podcast Id
     * @return Cursor
     */
    private Cursor getPodcastFeedWithId(Uri uri) {
        String feedId = PodCastContract.PodCastFeedEntry.getPodcastFeedIdFromUri(uri);

        return sqLiteQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                null,
                sPodCastFeedSelection,
                new String[]{feedId},
                null,
                null,
                null
        );
    }

    /**
     * @param projection    A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param selection     A filter declaring which rows to return
     * @param selectionArgs You may includes in selection, which will be replaced by the values from selectionArgs
     * @param sortOrder     How to order the rows
     * @return {@link Cursor} with result
     */
    private Cursor podCastFeedSubscription(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return mOpenHelper.getReadableDatabase().query(
                PodCastContract.PodcastFeedSubscriptionEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    /**
     * Helper method to get specific Podcast subscription feed using the select feed id
     *
     * @param uri Uri with selected podcast Id
     * @return Cursor
     */
    private Cursor getPodcastSubscriptionFeedWithId(Uri uri) {
        String locationSetting = PodCastContract.PodcastFeedSubscriptionEntry.getPodcastFeedIdFromUri(uri);

        return sqLiteQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                null,
                sPodCastFeedSelection,
                new String[]{locationSetting},
                null,
                null,
                null
        );
    }


}
