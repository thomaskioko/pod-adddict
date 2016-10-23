package com.thomaskioko.podadddict.app.data.tasks;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.thomaskioko.podadddict.app.api.model.Enclosure;
import com.thomaskioko.podadddict.app.api.model.Item;
import com.thomaskioko.podadddict.app.interfaces.DbTaskCallback;
import com.thomaskioko.podadddict.app.util.ApplicationConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * AsyncTask to fetch episode data from the database in the background.
 *
 * @author Thomas Kioko
 */

public class DatabaseAsyncTask extends AsyncTask<Uri, Void, List<Item>> {

    private Context mContext;
    private DbTaskCallback mDbTaskCallback;

    /**
     * Default constructor
     *
     * @param context        {@link Context} Context in which class is called
     * @param dbTaskCallback {@link DbTaskCallback} Listener interface used to notify {@link com.thomaskioko.podadddict.app.ui.fragments.PodCastEpisodesFragment}
     *                       when data has been loaded.
     */
    public DatabaseAsyncTask(Context context, DbTaskCallback dbTaskCallback) {

        mContext = context;
        mDbTaskCallback = dbTaskCallback;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Item> doInBackground(Uri... uris) {
        List<Item> itemList = new ArrayList<>();
        Uri podcastEpisodeUri = uris[0];
        Cursor cursor = mContext.getContentResolver().query(podcastEpisodeUri, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Item item = new Item();
                Enclosure enclosure = new Enclosure();
                enclosure.setUrl(cursor.getString(ApplicationConstants.COLUMN_PODCAST_EPISODE_STREAM_URL));

                item.setTitle(cursor.getString(ApplicationConstants.COLUMN_PODCAST_EPISODE_TITLE));
                item.setItunesAuthor(cursor.getString(ApplicationConstants.COLUMN_PODCAST_EPISODE_AUTHOR));
                item.setPubDate(cursor.getString(ApplicationConstants.COLUMN_PODCAST_EPISODE_PUBLISH_DATE));
                item.setItunesSummary(cursor.getString(ApplicationConstants.COLUMN_PODCAST_EPISODE_SUMMARY));
                item.setItunesDuration(cursor.getString(ApplicationConstants.COLUMN_PODCAST_EPISODE_DURATION));
                item.setEnclosure(enclosure);

                itemList.add(item);
            }

            //close the cursor when done to prevent leakage.
            cursor.close();
        }


        return itemList;
    }

    @Override
    protected void onPostExecute(List<Item> items) {
        super.onPostExecute(items);

        mDbTaskCallback.CallbackRequest(items);
    }
}
