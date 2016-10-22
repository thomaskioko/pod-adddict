package com.thomaskioko.podadddict.app.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.thomaskioko.podadddict.app.data.PodCastContract.PodCastFeedEntry;
import com.thomaskioko.podadddict.app.data.PodCastContract.PodCastFeedPlaylistEntry;
import com.thomaskioko.podadddict.app.data.PodCastContract.PodcastFeedSubscriptionEntry;
import com.thomaskioko.podadddict.app.data.PodCastContract.PodCastEpisodeEntry;

/**
 * @author Thomas Kioko
 */

public class PodCastFeedDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "podAdddict.db";

    /**
     * Default constructor.
     *
     * @param context Context the class is called
     */
    public PodCastFeedDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_PODCAST_FEED_TABLE = "CREATE TABLE " + PodCastFeedEntry.TABLE_NAME + " (" +
                PodCastFeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                /**
                 * Make the Feed Id setting unique
                 */
                PodCastFeedEntry.COLUMN_PODCAST_FEED_ID + " INTEGER UNIQUE ON CONFLICT REPLACE NOT NULL, " +
                PodCastFeedEntry.COLUMN_PODCAST_FEED_TITLE + " TEXT NOT NULL, " +
                PodCastFeedEntry.COLUMN_PODCAST_FEED_IMAGE_URL + " TEXT NOT NULL, " +
                PodCastFeedEntry.COLUMN_PODCAST_FEED_SUMMARY + " TEXT NOT NULL, " +
                PodCastFeedEntry.COLUMN_PODCAST_FEED_ARTIST + " TEXT NOT NULL, " +
                PodCastFeedEntry.COLUMN_PODCAST_FEED_CATEGORY + " TEXT NOT NULL " +
                ");";

        final String SQL_CREATE_PODCAST_PLAYLIST_TABLE = "CREATE TABLE " + PodCastFeedPlaylistEntry.TABLE_NAME + " (" +
                PodCastFeedPlaylistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the PodCastFeedEntry entry associated with the feed Id
                PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_ID + " INTEGER NOT NULL, " +
                PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_TRACK_ID + " TEXT NOT NULL, " +
                PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_TRACK_NAME + " TEXT NOT NULL, " +
                PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_ARTIST_NAME + " TEXT NOT NULL, " +
                PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_TRACK_COUNT + " INTEGER NOT NULL," +
                PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_URL + " TEXT NOT NULL, " +
                PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_ART_WORK_URL_100 + " TEXT NOT NULL, " +
                PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_ART_WORK_URL_600 + " TEXT NOT NULL, " +
                PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_GENRE + " TEXT NOT NULL, " +


                // Set up the PodCastFeedEntry column as a foreign key to PodCastFeedEntry table.
                " FOREIGN KEY (" + PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_ID + ") REFERENCES " +
                PodCastFeedEntry.TABLE_NAME + " (" + PodCastFeedEntry._ID + "), " +

                // To assure the application have just one feed ID entry
                // per PodCastFeedEntry, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_ID + ", " +
                PodCastFeedPlaylistEntry.COLUMN_PODCAST_FEED_PLAYLIST_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_PODCAST_SUBSCRIPTION_TABLE = "CREATE TABLE " + PodcastFeedSubscriptionEntry.TABLE_NAME + " (" +
                PodcastFeedSubscriptionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the PodCastFeedEntry entry associated with this PodcastFeedSubscriptionEntry
                PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_FEED_ID + " INTEGER UNIQUE ON CONFLICT REPLACE NOT NULL, " +
                PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_TRACK_ID + " TEXT NOT NULL, " +
                PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_ARTIST_NAME + " TEXT NOT NULL, " +
                PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_TRACK_NAME + " TEXT NOT NULL, " +
                PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_URL + " TEXT NOT NULL," +
                PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_ART_WORK_URL_100 + " TEXT NOT NULL, " +
                PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_ART_WORK_URL_600 + " TEXT NOT NULL, " +
                PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_TRACK_COUNT + " INTEGER NOT NULL, " +
                PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_GENRE + " TEXT NOT NULL, " +

                // Set up the PodCastFeedEntry column as a foreign key to PodcastFeedSubscriptionEntry table.
                " FOREIGN KEY (" + PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_FEED_ID + ") REFERENCES " +
                PodCastFeedEntry.TABLE_NAME + " (" + PodCastFeedEntry.COLUMN_PODCAST_FEED_ID + ")); ";

        final String SQL_CREATE_PODCAST_EPISODE_TABLE = "CREATE TABLE " + PodCastEpisodeEntry.TABLE_NAME + " (" +
                PodcastFeedSubscriptionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the PodCastFeedEntry entry associated with this PodcastFeedSubscriptionEntry
                PodCastEpisodeEntry.COLUMN_PODCAST_FEED_ID + " INTEGER NOT NULL, " +
                PodCastEpisodeEntry.COLUMN_PODCAST_EPISODE_TITLE + " TEXT NOT NULL, " +
                PodCastEpisodeEntry.COLUMN_PODCAST_EPISODE_AUTHOR + " TEXT , " +
                PodCastEpisodeEntry.COLUMN_PODCAST_EPISODE_SUMMARY + " TEXT NOT NULL, " +
                PodCastEpisodeEntry.COLUMN_PODCAST_EPISODE_DURATION + " TEXT NOT NULL," +
                PodCastEpisodeEntry.COLUMN_PODCAST_EPISODE_PUBLISH_DATE + " TEXT NOT NULL, " +
                PodCastEpisodeEntry.COLUMN_PODCAST_EPISODE_STREAM_URL + " TEXT NOT NULL, " +

                // Set up the PodCastFeedEntry column as a foreign key to PodcastFeedSubscriptionEntry table.
                " FOREIGN KEY (" + PodCastEpisodeEntry.COLUMN_PODCAST_FEED_ID + ") REFERENCES " +
                PodcastFeedSubscriptionEntry.TABLE_NAME + " (" + PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_FEED_ID + ")); ";


        sqLiteDatabase.execSQL(SQL_CREATE_PODCAST_PLAYLIST_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PODCAST_FEED_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PODCAST_SUBSCRIPTION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PODCAST_EPISODE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PodCastFeedEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PodCastFeedPlaylistEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PodcastFeedSubscriptionEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PodCastEpisodeEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
