package com.thomaskioko.podadddict.app.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.thomaskioko.podadddict.app.api.model.Entry;
import com.thomaskioko.podadddict.app.api.model.ImImage;
import com.thomaskioko.podadddict.app.api.model.Item;
import com.thomaskioko.podadddict.app.api.model.Result;
import com.thomaskioko.podadddict.app.data.PodCastContract;
import com.thomaskioko.podadddict.app.util.ApplicationConstants;
import com.thomaskioko.podadddict.app.util.LogUtils;

import java.util.List;
import java.util.Vector;

import static android.content.ContentValues.TAG;

/**
 * Database helper class
 *
 * @author Thomas Kioko
 */

public class DbUtils {

    /**
     * This method inserts FeedIds into {@link com.thomaskioko.podadddict.app.data.PodCastContract.PodcastFeedSubscriptionEntry}
     * table
     *
     * @param context    {@link Context}
     * @param resultList List of result object
     * @return Row id of the record
     */
    public static long insertSubscriptionFeed(Context context, List<Result> resultList, int rowId) {

        Vector<ContentValues> contentValuesVector = new Vector<>(resultList.size());

        for (Result result : resultList) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(PodCastContract.PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_FEED_ID, rowId);
            contentValues.put(PodCastContract.PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_TRACK_ID, result.getTrackId());
            contentValues.put(PodCastContract.PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_ARTIST_NAME, result.getArtistName());
            contentValues.put(PodCastContract.PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_TRACK_NAME, result.getTrackName());
            contentValues.put(PodCastContract.PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_URL, result.getFeedUrl());
            contentValues.put(PodCastContract.PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_ART_WORK_URL_100, result.getArtworkUrl100());
            contentValues.put(PodCastContract.PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_ART_WORK_URL_600, result.getArtworkUrl600());
            contentValues.put(PodCastContract.PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_TRACK_COUNT, result.getTrackCount());
            contentValues.put(PodCastContract.PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_GENRE, result.getPrimaryGenreName());

            contentValuesVector.add(contentValues);

        }

        // add to database
        if (contentValuesVector.size() > 0) {
            ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
            contentValuesVector.toArray(contentValuesArray);
            context.getContentResolver().bulkInsert(PodCastContract.PodcastFeedSubscriptionEntry.CONTENT_URI, contentValuesArray);
        }

        Uri podCastUri = PodCastContract.PodcastFeedSubscriptionEntry.buildSubscriptionUri();

        // Display what what you stored in the bulkInsert
        Cursor cursor = context.getContentResolver().query(
                podCastUri, null, null, null, null);

        if (cursor != null) {
            contentValuesVector = new Vector<>(cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cursor, cv);
                    contentValuesVector.add(cv);
                } while (cursor.moveToNext());
            }
            return contentValuesVector.size();
        }

        return 0;
    }

    /**
     * @param context
     * @param entryList
     * @return
     */
    public static int insertPodcastFeeds(Context context, List<Entry> entryList) {
        //Vector to hold content values.
        Vector<ContentValues> contentValuesVector = new Vector<>(entryList.size());

        //Loop through the response object and get the data
        for (Entry entry : entryList) {

            /**
             * Entry contains a list of images. So we loop thought the list and get the last
             * item which is the largest image size.
             */
            List<ImImage> imImages = entry.getImImage();
            String url = null;
            for (ImImage imImage : imImages) {
                url = imImage.getLabel();
            }

            /**
             * The Image from the feed are not clear. The largest size from the JSON Object is
             * {@link ApplicationConstants.IMAGE_SIZE_170x170} which is not clear. So we replace the
             * default dimensions with a larger one making the image clear. {@link ApplicationConstants.IMAGE_SIZE_600x600}
             *
             * Sample Url:
             * {@see <href "http://is1.mzstatic.com/image/thumb/Podcasts71/v4/03/62/51/036251e2-e2b5-b462-41ea-e2c2d29458fa/mza_1057278831507231273.png/170x170bb-85.jpg"}
             */
            if (url != null) {
                url = url.replace(ApplicationConstants.IMAGE_SIZE_170x170, ApplicationConstants.IMAGE_SIZE_600x600);
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_ID, entry.getId().getAttributes().getImId());
            contentValues.put(PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_TITLE, entry.getImName().getLabel());
            contentValues.put(PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_IMAGE_URL, url);
            contentValues.put(PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_SUMMARY, entry.getSummary().getLabel());
            contentValues.put(PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_ARTIST, entry.getImArtist().getLabel());
            contentValues.put(PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_CATEGORY, entry.getCategory().getAttributes().getLabel());

            contentValuesVector.add(contentValues);
        }


        // add to database
        if (contentValuesVector.size() > 0) {
            ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
            contentValuesVector.toArray(contentValuesArray);
            context.getContentResolver().bulkInsert(PodCastContract.PodCastFeedEntry.CONTENT_URI, contentValuesArray);
        }

        Uri podCastUri = PodCastContract.PodCastFeedEntry.buildPodCastFeedUri();

        // Display what what you stored in the bulkInsert
        Cursor cursor = context.getContentResolver().query(
                podCastUri, null, null, null, null);

        if (cursor != null) {
            contentValuesVector = new Vector<>(cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cursor, cv);
                    contentValuesVector.add(cv);
                } while (cursor.moveToNext());
            }
            return contentValuesVector.size();
        }

        return 0;
    }

    /**
     * Helper method to insert podcast episode records to the DB.
     *
     * @param context {@link Context} Context in which method is called
     * @param podcastFeedId Podcast Feed ID
     * @param itemList list of Feed items
     * @return Id. if 0 no records have been inserted.
     */
    public static int insertPodcastEpisode(Context context, int podcastFeedId, List<Item> itemList) {
        //Vector to hold content values.
        Vector<ContentValues> contentValuesVector = new Vector<>(itemList.size());

        //Loop through the response object and get the data
        for (Item item : itemList) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(PodCastContract.PodCastEpisodeEntry.COLUMN_PODCAST_FEED_ID, podcastFeedId);
            contentValues.put(PodCastContract.PodCastEpisodeEntry.COLUMN_PODCAST_EPISODE_TITLE, item.getTitle());
            contentValues.put(PodCastContract.PodCastEpisodeEntry.COLUMN_PODCAST_EPISODE_STREAM_URL, item.getEnclosure().getUrl());
            contentValues.put(PodCastContract.PodCastEpisodeEntry.COLUMN_PODCAST_EPISODE_SUMMARY, item.getItunesSummary());
            contentValues.put(PodCastContract.PodCastEpisodeEntry.COLUMN_PODCAST_EPISODE_AUTHOR, item.getItunesAuthor());
            contentValues.put(PodCastContract.PodCastEpisodeEntry.COLUMN_PODCAST_EPISODE_DURATION, item.getItunesDuration());
            contentValues.put(PodCastContract.PodCastEpisodeEntry.COLUMN_PODCAST_EPISODE_PUBLISH_DATE, item.getPubDate());

            contentValuesVector.add(contentValues);
        }


        // add to database
        if (contentValuesVector.size() > 0) {
            ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
            contentValuesVector.toArray(contentValuesArray);
            context.getContentResolver().bulkInsert(PodCastContract.PodCastEpisodeEntry.CONTENT_URI, contentValuesArray);
        }

        Uri podCastUri = PodCastContract.PodCastEpisodeEntry.buildPodCastEpisodeUri();

        // Display what what you stored in the bulkInsert
        Cursor cursor = context.getContentResolver().query(
                podCastUri, null, null, null, null);

        if (cursor != null) {
            contentValuesVector = new Vector<>(cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cursor, cv);
                    contentValuesVector.add(cv);
                } while (cursor.moveToNext());
            }
            return contentValuesVector.size();
        }

        return 0;
    }


    /**
     *
     * @param context
     * @param feedId
     * @return
     */
    public static boolean dbHasRecord(Context context, String feedId) {
        PodCastFeedDbHelper dbHelper = new PodCastFeedDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selectString = "SELECT * FROM " + PodCastContract.PodcastFeedSubscriptionEntry.TABLE_NAME
                + " WHERE " + PodCastContract.PodcastFeedSubscriptionEntry.COLUMN_SUBSCRIBED_PODCAST_TRACK_ID + " =?";

        Cursor cursor = db.rawQuery(selectString, new String[]{feedId});

        boolean hasObject = false;
        if (cursor.moveToFirst()) {
            hasObject = true;
            //region if you had multiple records to check for, use this region.

            int count = 0;
            while (cursor.moveToNext()) {
                count++;
            }
            //here, count is records found
            LogUtils.showInformationLog(TAG, "@dbHasRecord Records found" +  count);

        }

        cursor.close();          // Dont forget to close your cursor
        db.close();              //AND your Database!
        return hasObject;
    }

    /**
     * Helper method to check if podcast episode db has data.
     *
     * @param context {@link Context}
     * @param feedId Podcast Feed Id
     * @return {@link Boolean}
     */
    public static boolean episodeDbHasRecords(Context context, String feedId) {
        PodCastFeedDbHelper dbHelper = new PodCastFeedDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selectString = "SELECT * FROM " + PodCastContract.PodCastEpisodeEntry.TABLE_NAME
                + " WHERE " + PodCastContract.PodCastEpisodeEntry.COLUMN_PODCAST_FEED_ID + " =?";

        Cursor cursor = db.rawQuery(selectString, new String[]{feedId});

        boolean hasObject = false;
        if (cursor.moveToFirst()) {
            hasObject = true;
            //region if you had multiple records to check for, use this region.

            int count = 0;
            while (cursor.moveToNext()) {
                count++;
            }
            //here, count is records found
            LogUtils.showInformationLog(TAG, "@episodeDbHasRecords Records found" +  count);

        }

        cursor.close();          // Dont forget to close your cursor
        db.close();              //AND your Database!
        return hasObject;
    }
}
