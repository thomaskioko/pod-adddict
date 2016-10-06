package com.thomaskioko.podadddict.app.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.appeaser.sublimenavigationviewlibrary.OnNavigationMenuEventListener;
import com.appeaser.sublimenavigationviewlibrary.SublimeBaseMenuItem;
import com.appeaser.sublimenavigationviewlibrary.SublimeNavigationView;
import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.data.PodCastContract;
import com.thomaskioko.podadddict.app.data.model.LocalTrack;
import com.thomaskioko.podadddict.app.data.model.Queue;
import com.thomaskioko.podadddict.app.data.model.Track;
import com.thomaskioko.podadddict.app.data.sync.PodAdddictSyncAdapter;
import com.thomaskioko.podadddict.app.service.MediaPlayerService;
import com.thomaskioko.podadddict.app.ui.adapter.PodCastAdapterAdapter;
import com.thomaskioko.podadddict.app.ui.adapter.SubscribedPodCastAdapter;
import com.thomaskioko.podadddict.app.ui.fragments.DiscoverPodcastFragment;
import com.thomaskioko.podadddict.app.ui.fragments.PlayerFragment;
import com.thomaskioko.podadddict.app.ui.fragments.PodcastBottomSheetDialogFragment;
import com.thomaskioko.podadddict.app.ui.fragments.SubscriptionFragment;
import com.thomaskioko.podadddict.app.util.ApplicationConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This is the main activity of the app.
 *
 * @author Thomas Kioko
 */
public class PodCastListActivity extends AppCompatActivity implements OnNavigationMenuEventListener,
        DiscoverPodcastFragment.Callback, SubscriptionFragment.Callback {

    /**
     * Bind View using butter knife.
     */
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.navigation_view)
    SublimeNavigationView mSublimeNavigationView;
    @Bind(R.id.coordinated_layout)
    CoordinatorLayout mCoordinatedLayout;
    @Bind(R.id.bottomSheetLayout)
    LinearLayout bottomSheetLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    //Variables
    private BottomSheetBehavior mBottomSheetBehavior;
    private static final String LOG_TAG = PodCastListActivity.class.getSimpleName();

    public static boolean isPlayerVisible = false;
    public static boolean isLocalVisible = false;
    public static boolean isStreamVisible = false;
    public static boolean isQueueVisible = false;
    public static boolean isPlaylistVisible = false;
    public static boolean isEqualizerVisible = false;
    public static boolean isFavouriteVisible = false;
    public static boolean isAllPlaylistVisible = false;
    public static boolean isAllFolderVisible = false;
    public static boolean isFolderContentVisible = false;
    public static boolean isAllSavedDnaVisisble = false;
    public static boolean isSavedDNAVisible = false;
    public static boolean isAlbumVisible = false;
    public static boolean isArtistVisible = false;
    public static boolean isRecentVisible = false;
    public static boolean isFullScreenEnabled = false;
    public static boolean isSettingsVisible = false;
    public static boolean shuffleEnabled = false;
    public static boolean repeatEnabled = true;
    public static boolean repeatOnceEnabled = false;
    public static boolean nextControllerClicked = false;
    public static boolean isFavourite = false;
    public static boolean isReloaded = true;
    public static int queueCurrentIndex = 0;
    public static boolean isSaveDNAEnabled = false;
    public static Track selectedTrack;
    public static LocalTrack localSelectedTrack;
    public static boolean localSelected = false;
    public static boolean streamSelected = false;
    public static boolean hasQueueEnded = false;
    boolean isNotificationVisible = false;
    static boolean queueCall = false;
    boolean wasMediaPlayerPlaying = false;

    public static Queue queue;
    public static Activity main;
    public static android.support.v4.app.FragmentManager fragMan2;

    public static List<LocalTrack> localTrackList = new ArrayList<>();
    public static List<LocalTrack> finalLocalSearchResultList = new ArrayList<>();
    public static List<Track> streamingTrackList = new ArrayList<>();

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
        mSublimeNavigationView.setNavigationMenuEventListener(this);

        //Initialize the BottomSheet view
        mBottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));

        //We set the bottom view state to hidden otherwise it will be displayed be default.
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetLayout.setVisibility(View.GONE);

        Uri podCastSubscribedUri = PodCastContract.PodcastFeedSubscriptionEntry.buildSubscriptionUri();

        Cursor cursor = getContentResolver().query(podCastSubscribedUri, null, null, null, null);


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
    public boolean onNavigationMenuEvent(Event event, SublimeBaseMenuItem menuItem) {
        FragmentTransaction fragmentTransaction;
        switch (menuItem.getItemId()) {
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
        int id = item.getItemId();

        if (id == R.id.action_add_subscription) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout_container,
                    new DiscoverPodcastFragment()
            );
            fragmentTransaction.commit();
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


    public static class SaveFavourites extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {


            return null;
        }
    }

    public static class SavePlaylists extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }
    }

    public static class SaveQueue extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }
    }

    public static class SaveTheDNAs extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }

    public static class SaveRecents extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {


            return null;
        }
    }

    public static PlayerFragment getPlayerFragment() {
        try {
            return (PlayerFragment) fragMan2.findFragmentByTag("player");
        } catch (Exception e) {

        }
        return null;
    }

    public void showNotification() {

        if (Build.VERSION.SDK_INT >= 21) {
            if (!isNotificationVisible) {
                Intent intent = new Intent(this, MediaPlayerService.class);
                intent.setAction(ApplicationConstants.ACTION_PLAY);
                startService(intent);
                isNotificationVisible = true;
            }
        } else {
            setNotification();
        }

    }

    public void setNotification() {
        Notification notification;
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) getSystemService(ns);
        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.notification_view);
        RemoteViews notificationViewSmall = new RemoteViews(getPackageName(), R.layout.notification_view_small);
        Intent notificationIntent = new Intent(this, getClass());
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Intent switchIntent = new Intent("com.sdsmdg.harjot.MusicDNA.ACTION_PLAY_PAUSE");
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 100, switchIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.btn_pause_play_in_notification, pendingSwitchIntent);
        try {
            if (PlayerFragment.mMediaPlayer.isPlaying()) {
                notificationView.setImageViewResource(R.id.btn_pause_play_in_notification, R.drawable.ic_pause_white_48dp);
            } else {
                notificationView.setImageViewResource(R.id.btn_pause_play_in_notification, R.drawable.ic_play_arrow_white_48dp);
            }
        } catch (Exception e) {
        }
        Intent switchIntent2 = new Intent("com.sdsmdg.harjot.MusicDNA.ACTION_NEXT");
        PendingIntent pendingSwitchIntent2 = PendingIntent.getBroadcast(this, 100, switchIntent2, 0);
        notificationView.setOnClickPendingIntent(R.id.btn_next_in_notification, pendingSwitchIntent2);
        Intent switchIntent3 = new Intent("com.sdsmdg.harjot.MusicDNA.ACTION_PREV");
        PendingIntent pendingSwitchIntent3 = PendingIntent.getBroadcast(this, 100, switchIntent3, 0);
        notificationView.setOnClickPendingIntent(R.id.btn_prev_in_notification, pendingSwitchIntent3);

        notificationViewSmall.setOnClickPendingIntent(R.id.btn_pause_play_in_notification, pendingSwitchIntent);
        try {
            if (PlayerFragment.mMediaPlayer.isPlaying()) {
                notificationViewSmall.setImageViewResource(R.id.btn_pause_play_in_notification, R.drawable.ic_pause_white_48dp);
            } else {
                notificationViewSmall.setImageViewResource(R.id.btn_pause_play_in_notification, R.drawable.ic_play_arrow_white_48dp);
            }
        } catch (Exception e) {
        }
        notificationViewSmall.setOnClickPendingIntent(R.id.btn_next_in_notification, pendingSwitchIntent2);
        notificationViewSmall.setOnClickPendingIntent(R.id.btn_prev_in_notification, pendingSwitchIntent3);

        Notification.Builder builder = new Notification.Builder(this);
        notification = builder.setContentTitle("MusicDNA")
                .setContentText("Slide down on note to expand")
                .setSmallIcon(R.drawable.ic_default)
                .setContentTitle("Title")
                .setContentText("Artist")
                .addAction(R.drawable.ic_skip_previous_white_48dp, "Prev", pendingSwitchIntent3)
                .addAction(R.drawable.ic_play_arrow_white_48dp, "Play", pendingSwitchIntent)
                .addAction(R.drawable.ic_skip_next_white_48dp, "Next", pendingSwitchIntent2)
                .setLargeIcon(((BitmapDrawable) PlayerFragment.selected_track_image.getDrawable()).getBitmap())
                .build();
        notification.priority = Notification.PRIORITY_MAX;
        notification.bigContentView = notificationView;
        notification.contentView = notificationViewSmall;
        notification.contentIntent = pendingNotificationIntent;
        if (PlayerFragment.mMediaPlayer.isPlaying()) {
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
        }
        notificationView.setImageViewBitmap(R.id.image_in_notification, ((BitmapDrawable) PlayerFragment.selected_track_image.getDrawable()).getBitmap());
        if (PlayerFragment.localIsPlaying) {
            notificationView.setTextViewText(R.id.title_in_notification, PlayerFragment.localTrack.getTitle());
            notificationView.setTextViewText(R.id.artist_in_notification, PlayerFragment.localTrack.getArtist());
        } else {
            notificationView.setTextViewText(R.id.title_in_notification, PlayerFragment.track.getTitle());
            notificationView.setTextViewText(R.id.artist_in_notification, "");
        }
        notificationViewSmall.setImageViewBitmap(R.id.image_in_notification, ((BitmapDrawable) PlayerFragment.selected_track_image.getDrawable()).getBitmap());
        if (PlayerFragment.localIsPlaying) {
            notificationViewSmall.setTextViewText(R.id.title_in_notification, PlayerFragment.localTrack.getTitle());
            notificationViewSmall.setTextViewText(R.id.artist_in_notification, PlayerFragment.localTrack.getArtist());
        } else {
            notificationViewSmall.setTextViewText(R.id.title_in_notification, PlayerFragment.track.getTitle());
            notificationViewSmall.setTextViewText(R.id.artist_in_notification, "");
        }
        getPlayerFragment().isStart = false;
        notificationManager.notify(1, notification);
    }

    /**
     * This method replaces the current fragment with {@link PlayerFragment}
     *
     * @param feedStreamUrl Episode stream url
     */
    public void opePlayerFragment(String feedStreamUrl) {


    }
}
