package com.thomaskioko.podadddict.app.ui.adapter;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.interfaces.ItemTouchHelperAdapter;
import com.thomaskioko.podadddict.app.interfaces.ItemTouchHelperViewHolder;
import com.thomaskioko.podadddict.app.interfaces.OnStartDragListener;
import com.thomaskioko.podadddict.app.interfaces.TrackListener;
import com.thomaskioko.podadddict.musicplayerlib.model.Track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Simple RecyclerView.Adapter that implements {@link ItemTouchHelperAdapter} to respond to move and
 * dismiss events from a {@link android.support.v7.widget.helper.ItemTouchHelper}.
 *
 * @author Paul Burke (ipaulpro)
 */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private static List<Track> mItemList = new ArrayList<>();
    private Context mContext;
    private TrackListener mTrackListener;

    private final OnStartDragListener mDragStartListener;

    /**
     * Constructor.
     *  @param context           {@link Context}
     * @param dragStartListener
     * @param mPlaylistTracks
     * @param listener
     */
    public PlaylistAdapter(Context context, OnStartDragListener dragStartListener,
                           ArrayList<Track> mPlaylistTracks, TrackListener listener) {
        mDragStartListener = dragStartListener;
        mItemList = mPlaylistTracks;
        mContext = context;
        mTrackListener = listener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item_main, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {

        final Track item = mItemList.get(position);

        holder.textView.setText(item.getTitle());
        Picasso.with(mContext)
                .load(item.getArtworkUrl())
                .fit()
                .centerCrop()
                .placeholder(R.color.placeholder)
                .into(holder.mArtWork);

        // Start a drag whenever the handle view it touched
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTrackListener.onTrackClicked(item);
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
        mItemList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItemList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        final TextView textView;
        final ImageView handleView;
        final ImageView mArtWork;
        final View mView;

        ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
            handleView = (ImageView) itemView.findViewById(R.id.handle);
            mArtWork = (ImageView) itemView.findViewById(R.id.episode_artwork);
            mView = itemView;
        }

        @Override
        public void onItemSelected() {
//            itemView.setBackgroundColor(Color.LTGRAY);
        }
    }
}
