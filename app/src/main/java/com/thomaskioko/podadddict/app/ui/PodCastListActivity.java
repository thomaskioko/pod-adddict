package com.thomaskioko.podadddict.app.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.appeaser.sublimenavigationviewlibrary.OnNavigationMenuEventListener;
import com.appeaser.sublimenavigationviewlibrary.SublimeBaseMenuItem;
import com.appeaser.sublimenavigationviewlibrary.SublimeNavigationView;
import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.ui.fragments.DiscoverPodcastFragment;
import com.thomaskioko.podadddict.app.ui.fragments.SubscriptionFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This is the main activity of the app.
 *
 * @author Thomas Kioko
 */
public class PodCastListActivity extends AppCompatActivity implements OnNavigationMenuEventListener {

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.navigation_view)
    SublimeNavigationView mSublimeNavigationView;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast_list);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        mSublimeNavigationView.setNavigationMenuEventListener(this);

        /**
         * TODO::Check if the user has any subscriptions and decide what fragment to load.
         * If there are no subscriptions load {@link DiscoverPodcastFragment} otherwise we load
         * {@link SubscriptionFragment}
         */
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frameLayout_container,
                DiscoverPodcastFragment.newInstance(getApplicationContext(), PodCastListActivity.this)
        );
        fragmentTransaction.commit();

    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
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
                        DiscoverPodcastFragment.newInstance(getApplicationContext(), PodCastListActivity.this)
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
                    DiscoverPodcastFragment.newInstance(getApplicationContext(), PodCastListActivity.this)
            );
            fragmentTransaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
