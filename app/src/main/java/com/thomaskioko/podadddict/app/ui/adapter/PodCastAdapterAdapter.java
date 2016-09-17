package com.thomaskioko.podadddict.app.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.api.model.Entry;
import com.thomaskioko.podadddict.app.api.model.ImImage;
import com.thomaskioko.podadddict.app.ui.util.ForegroundImageView;
import com.thomaskioko.podadddict.app.util.ApplicationConstants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * PodcastFeed adapter class
 *
 * @author Thomas Kioko
 */
public class PodCastAdapterAdapter extends RecyclerView.Adapter<PhotoViewHolder> {

    private Context mContext;
    private final List<Entry> mEntryList;

    /**
     * Constructor
     *
     * @param context   Context in which the class is called.
     * @param entryList List of podcast feeds
     */
    public PodCastAdapterAdapter(Context context, List<Entry> entryList) {
        mEntryList = entryList;
        mContext = context;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoViewHolder(
                LayoutInflater.from(mContext)
                        .inflate(R.layout.podcast_feed_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
        final Entry entry = mEntryList.get(position);

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

        Picasso.with(mContext)
                .load(url)
                .placeholder(R.color.placeholder)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mEntryList.size();
    }


    public Entry getItem(int position) {
        return mEntryList.get(position);
    }

}

class PhotoViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.photo)
    ForegroundImageView imageView;

    public PhotoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
