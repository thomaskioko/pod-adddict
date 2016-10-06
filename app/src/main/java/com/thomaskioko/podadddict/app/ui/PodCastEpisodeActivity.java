package com.thomaskioko.podadddict.app.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.api.model.Item;
import com.thomaskioko.podadddict.app.ui.fragments.PodCastEpisodesFragment;
import com.thomaskioko.podadddict.app.util.ApplicationConstants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * An activity representing a single PodCast detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link PodCastListActivity}.
 */
public class PodCastEpisodeActivity extends AppCompatActivity implements PodCastEpisodesFragment.EpisodeCallback {

    @Bind(R.id.bottomMargin)
    FrameLayout mRelativeLayout;
    @Bind(R.id.selected_track_image_sp_home)
    ImageView mThumbNail;
    @Bind(R.id.selected_track_title_sp_home)
    TextView mTrackTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast_detail);
        ButterKnife.bind(this);


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable(PodCastEpisodesFragment.DETAIL_URI, getIntent().getData());
            PodCastEpisodesFragment fragment = new PodCastEpisodesFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.podcast_detail_container, fragment)
                    .commit();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, PodCastListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEpisodeSelected(Uri uri, Item item) {

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            //Display the MiniPlayer
            mRelativeLayout.setVisibility(View.VISIBLE);

            while (cursor.moveToNext()) {

                //Create a blur effect
                mTrackTitle.setText(item.getTitle());

                String imageUrl = cursor.getString(ApplicationConstants.COLUMN_SUBSCRIBED_PODCAST_FEED_IMAGE_URL);
                mThumbNail.setColorFilter(new LightingColorFilter(0xff828282, 0x000000));
                Glide.with(PodCastEpisodeActivity.this)
                        .load(imageUrl)
                        .asBitmap()
                        .placeholder(R.color.placeholder)
                        .into(new BitmapImageViewTarget(mThumbNail) {
                            @Override
                            public void onResourceReady(Bitmap bitmap, final GlideAnimation glideAnimation) {
                                super.onResourceReady(bitmap, glideAnimation);
                                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(Palette palette) {
                                        //Set the background of the relative layout.
                                        if (palette.getDarkVibrantSwatch() != null) {
                                            mRelativeLayout.setBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
                                        } else if (palette.getMutedSwatch() != null) {
                                            mRelativeLayout.setBackgroundColor(palette.getMutedSwatch().getRgb());
                                        }

                                    }
                                });
                            }
                        });
            }

            cursor.close();
        }

    }
}
