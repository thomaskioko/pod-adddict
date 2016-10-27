package com.thomaskioko.podadddict.app.data.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.thomaskioko.podadddict.app.api.model.Item;
import com.thomaskioko.podadddict.app.data.db.DbUtils;
import com.thomaskioko.podadddict.app.interfaces.DbTaskCallback;
import com.thomaskioko.podadddict.app.interfaces.InsertEpisodesCallback;

import java.util.List;

/**
 * AsyncTask to insert podcast episode data from the database in the background.
 *
 * @author Thomas Kioko
 */

public class InsertEpisodesAsyncTask extends AsyncTask<String, Void, Integer> {

    private Context mContext;
    private List<Item> mItemList;
    private InsertEpisodesCallback mInsertEpisodesCallback;

    /**
     * Default constructor
     *
     * @param context                {@link Context} Context in which class is called
     * @param insertEpisodesCallback {@link DbTaskCallback} Listener interface used to notify {@link com.thomaskioko.podadddict.app.ui.fragments.PodCastEpisodesFragment}
     *                               when data has been inserted.
     */
    public InsertEpisodesAsyncTask(Context context, InsertEpisodesCallback insertEpisodesCallback, List<Item> itemList) {

        mContext = context;
        mInsertEpisodesCallback = insertEpisodesCallback;
        mItemList = itemList;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... params) {

        String feedId = params[0];

        return DbUtils.insertPodcastEpisode(mContext, Integer.parseInt(feedId), mItemList);
    }

    @Override
    protected void onPostExecute(Integer items) {
        super.onPostExecute(items);

        mInsertEpisodesCallback.CallbackRequest(items);
    }
}
