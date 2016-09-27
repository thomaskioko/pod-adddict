package com.thomaskioko.podadddict.app.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.api.model.Item;
import com.thomaskioko.podadddict.app.ui.fragments.PodcastEpisodeBottomSheetFragment;
import com.thomaskioko.podadddict.app.ui.util.RecyclerItemChoiceManager;
import com.thomaskioko.podadddict.app.util.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.R.attr.choiceMode;

/**
 * PodcastFeed adapter class
 *
 * @author Thomas Kioko
 */
public class PodcastEpisodeListAdapter extends RecyclerView.Adapter<PodcastEpisodeListAdapter.ViewHolder> {

    private List<Item> mItemList;
    private Context mContext;
    private Uri mUri;
    private final RecyclerItemChoiceManager mRecyclerItemChoiceManager;
    private static final String LOG_TAG = PodcastEpisodeListAdapter.class.getSimpleName();

    /**
     * Constructor
     *
     * @param context  Context in which the class is called.
     * @param itemList List of podcast feeds
     * @param uri
     */
    public PodcastEpisodeListAdapter(Context context, List<Item> itemList, Uri uri) {
        mContext = context;
        mItemList = itemList;
        mUri = uri;
        mRecyclerItemChoiceManager = new RecyclerItemChoiceManager(this);
        mRecyclerItemChoiceManager.setChoiceMode(choiceMode);
    }

    /**
     *
     */
    public interface PodCastEpisodeAdapterOnClickHandler {
        void onClick(int feedId, ViewHolder vh);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.statusUnread)
        TextView statusUnread;
        @Bind(R.id.txtvItemname)
        TextView mTvPodcastTitle;
        @Bind(R.id.txtvLenSize)
        TextView mTvPodcastDuration;
        @Bind(R.id.txtMonth)
        TextView mTvMonth;
        @Bind(R.id.txtDate)
        TextView mTvDate;
        @Bind(R.id.btnPlay)
        ImageView mPlayImageButton;
        @Bind(R.id.episodeRelativeLayout)
        RelativeLayout mEpisodeRl;
        public View view;

        /**
         * @param itemView View item
         */
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            view = itemView;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(mContext)
                        .inflate(R.layout.episode_list_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Item item = mItemList.get(position);

        String dateStr = item.getPubDate();
        DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.getDefault());
        try {
            Date date = formatter.parse(dateStr);
            String pubDateStr = DateUtils.formatAbbrev(mContext, date);

            String[] dateMonth = pubDateStr.split(" ");

            holder.mTvMonth.setText(dateMonth[0]);
            holder.mTvDate.setText(dateMonth[1]);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (null != item.getItunesDuration()) {
            //Check if the duration is already formatted
            if (item.getItunesDuration().contains(":")) {
                holder.mTvPodcastDuration.setText(item.getItunesDuration());
            } else {
                long min = Integer.parseInt(item.getItunesDuration()) / 60000;
                long sec = Integer.parseInt(item.getItunesDuration()) % 60000 / 1000;
                holder.mTvPodcastDuration.setText(String.format(mContext.getResources().getString(R.string.duration), min, sec));
            }
        }

        holder.mTvPodcastTitle.setText(item.getTitle());
        holder.mPlayImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, item.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.mEpisodeRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayBottomSheet(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == mItemList) return 0;
        return mItemList.size();
    }

    /**
     *
     */
    private void displayBottomSheet(Item item) {

        View view = ((FragmentActivity) mContext).getLayoutInflater().inflate(R.layout.podcast_detail_bottom_sheet, null);
        BottomSheetDialogFragment bottomSheetDialogFragment = PodcastEpisodeBottomSheetFragment.newInstance(item, mUri);
        bottomSheetDialogFragment.show(((FragmentActivity) mContext).getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

    }

}


