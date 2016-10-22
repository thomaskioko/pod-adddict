package com.thomaskioko.podadddict.app.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.data.PodCastContract;
import com.thomaskioko.podadddict.app.ui.util.RecyclerItemChoiceManager;
import com.thomaskioko.podadddict.app.ui.views.ForegroundImageView;
import com.thomaskioko.podadddict.app.util.ApplicationConstants;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.shts.android.library.TriangleLabelView;

/**
 * PodcastFeed adapter class
 *
 * @author Thomas Kioko
 */
public class PodCastAdapterAdapter extends RecyclerView.Adapter<PodCastAdapterAdapter.PhotoViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    final private PodCastAdapterAdapterOnClickHandler mClickHandler;
    final private RecyclerItemChoiceManager mRecyclerItemChoiceManager;

    /**
     * Constructor
     *
     * @param context Context in which the class is called.
     * @param dh      List of podcast feeds
     */
    public PodCastAdapterAdapter(Context context, PodCastAdapterAdapterOnClickHandler dh, int choiceMode) {
        mContext = context;
        mClickHandler = dh;
        mRecyclerItemChoiceManager = new RecyclerItemChoiceManager(this);
        mRecyclerItemChoiceManager.setChoiceMode(choiceMode);
    }

    public interface PodCastAdapterAdapterOnClickHandler {
        void onClick(int feedId, PhotoViewHolder vh);
    }


    public class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.photo)
        public ForegroundImageView imageView;
        @Bind(R.id.triangleCountView)
        TriangleLabelView triangleLabelView;
        public View view;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            view = itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);

            int podcastIndex = mCursor.getColumnIndex(PodCastContract.PodCastFeedEntry.COLUMN_PODCAST_FEED_ID);
            mClickHandler.onClick(mCursor.getInt(podcastIndex), this);
            mRecyclerItemChoiceManager.onClick(this);
        }
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoViewHolder(
                LayoutInflater.from(mContext)
                        .inflate(R.layout.podcast_feed_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
        mCursor.moveToPosition(position);

        holder.triangleLabelView.setVisibility(View.GONE);
        String url = mCursor.getString(ApplicationConstants.COLUMN_PODCAST_FEED_IMAGE_URL);
        Picasso.with(mContext)
                .load(url)
                .placeholder(R.color.placeholder)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    /**
     * Method to swap cursor with a new one
     *
     * @param cursor Cursor
     */
    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    /**
     * Method to get the instance of the cursor.
     *
     * @return Cursor
     */
    public Cursor getCursor() {
        return mCursor;
    }

    /**
     * Method to get the selected item position in the recyclerView
     *
     * @return Position
     */
    public int getSelectedItemPosition() {
        return mRecyclerItemChoiceManager.getSelectedItemPosition();
    }

    /**
     * Method to restore saved data.
     *
     * @param savedInstanceState {@link Bundle}
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mRecyclerItemChoiceManager.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Method to save data in a bundle. This is called is specific cases. When the screen orientation
     * changes. and other cases
     *
     * @param outState {@link Bundle}
     */
    public void onSaveInstanceState(Bundle outState) {
        mRecyclerItemChoiceManager.onSaveInstanceState(outState);
    }

    /**
     * @param viewHolder
     */
    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof PhotoViewHolder) {
            PhotoViewHolder forecastAdapterViewHolder = (PhotoViewHolder) viewHolder;
            forecastAdapterViewHolder.onClick(forecastAdapterViewHolder.itemView);
        }
    }

}


