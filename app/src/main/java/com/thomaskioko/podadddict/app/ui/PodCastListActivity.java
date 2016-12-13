package com.thomaskioko.podadddict.app.ui;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.data.PodCastContract;
import com.thomaskioko.podadddict.app.data.db.DbUtils;
import com.thomaskioko.podadddict.app.data.sync.PodAdddictSyncAdapter;
import com.thomaskioko.podadddict.app.ui.adapter.PodCastAdapterAdapter;
import com.thomaskioko.podadddict.app.ui.adapter.SubscribedPodCastAdapter;
import com.thomaskioko.podadddict.app.ui.fragments.DiscoverPodcastFragment;
import com.thomaskioko.podadddict.app.ui.fragments.PodcastBottomSheetDialogFragment;
import com.thomaskioko.podadddict.app.ui.fragments.SubscriptionFragment;
import com.thomaskioko.podadddict.app.util.GoogleAnalyticsUtil;
import com.thomaskioko.podadddict.app.util.NotificationUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This is the main activity of the app.
 *
 * @author Thomas Kioko
 */
public class PodCastListActivity extends AppCompatActivity implements DiscoverPodcastFragment.Callback,
        SubscriptionFragment.Callback, NavigationView.OnNavigationItemSelectedListener {

    /**
     * Bind View using butter knife.
     */
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.navigation_view)
    NavigationView mSublimeNavigationView;
    @Bind(R.id.coordinated_layout)
    CoordinatorLayout mCoordinatedLayout;
    @Bind(R.id.bottomSheetLayout)
    LinearLayout bottomSheetLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private TextView mTvPodcastCount;

    //Variables
    private BottomSheetBehavior mBottomSheetBehavior;
    private static final String LOG_TAG = PodCastListActivity.class.getSimpleName();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast_list);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());

        //Set up the Drawer
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //Register on clickListener event to the navigation drawer
        mSublimeNavigationView.setNavigationItemSelectedListener(this);

        //Initialize the BottomSheet view
        mBottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));

        mTvPodcastCount = (TextView) MenuItemCompat.getActionView(mSublimeNavigationView.getMenu().
                findItem(R.id.nav_action_poscasts));


        mTvPodcastCount.setTextColor(getResources().getColor(R.color.colorAccent));
        mTvPodcastCount.setGravity(Gravity.CENTER_VERTICAL);
        mTvPodcastCount.setTypeface(null, Typeface.BOLD);

        updatePodcastCount();

        //We set the bottom view state to hidden otherwise it will be displayed be default.
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetLayout.setVisibility(View.GONE);

        Uri podCastSubscribedUri = PodCastContract.PodcastFeedSubscriptionEntry.buildSubscriptionUri();

        Cursor cursor = getContentResolver().query(podCastSubscribedUri, null, null, null, null);

        GoogleAnalyticsUtil.trackScreenView(LOG_TAG);


        /**
         * Check if the user has any subscriptions and decide what fragment to load.
         * If there are no subscriptions load {@link DiscoverPodcastFragment} otherwise we load
         * {@link SubscriptionFragment}
         */
        if (cursor != null) {
            boolean hasObject = false;
            if (cursor.moveToFirst()) {
                hasObject = true;
            }
            if (hasObject) {

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.frameLayout_container, new SubscriptionFragment());
                fragmentTransaction.commit();
                cursor.close();
            } else {
                //Load discover fragment
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.frameLayout_container, new DiscoverPodcastFragment());
                fragmentTransaction.commit();

            }
        }

        //Initialize the sync adapter
        PodAdddictSyncAdapter.syncImmediately(this);


    }


    @Override
    protected void onResume() {
        super.onResume();

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction fragmentTransaction;
        switch (item.getItemId()) {
            case R.id.nav_action_poscasts:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_container, new SubscriptionFragment());
                fragmentTransaction.commit();
                //Close the DrawerLayout when an item is selected
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_action_downloads:
                break;
            case R.id.nav_action_discover:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_container,
                        new DiscoverPodcastFragment()
                );
                fragmentTransaction.commit();
                //Close the DrawerLayout when an item is selected
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;

            default:
                break;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_add_subscription:
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_container,
                        new DiscoverPodcastFragment()
                );
                fragmentTransaction.commit();
                return true;
            case R.id.action_share:
                //TODO:: Add link to playstore once app is published. :)
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message));
                shareIntent.setType("text/plain");
                item.setIntent(shareIntent);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onFeedItemSelected(Uri feedUri, PodCastAdapterAdapter.PhotoViewHolder photoViewHolder) {

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        BottomSheetDialogFragment bottomSheetDialogFragment = PodcastBottomSheetDialogFragment.newInstance(PodCastListActivity.this, feedUri);
        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());


    }

    @Override
    public void onSubscribedFeedItemSelected(Uri feedUri, SubscribedPodCastAdapter.ViewHolder viewHolder) {
        //Pass the uri via the intent
        Intent intent = new Intent(getApplicationContext(), PodCastEpisodeActivity.class).setData(feedUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, viewHolder.imageView,
                    viewHolder.imageView.getTransitionName()).toBundle());
        } else {
            startActivity(intent);
        }
    }

    /**
     * Method called to update podcast count
     */
    private void updatePodcastCount() {
        int episodeCount = DbUtils.getEpisodeCount(getApplicationContext());
        mTvPodcastCount.setText(String.valueOf(episodeCount));
    }

}
