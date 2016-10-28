package com.thomaskioko.podadddict.app.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.data.model.Track;
import com.thomaskioko.podadddict.app.ui.adapter.NowPlayingPagerAdapter;
import com.viewpagerindicator.CirclePageIndicator;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Thomas Kioko
 */
public class NowPlayingActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.pager)
    ViewPager mPager;
    @Bind(R.id.page_indicator)
    CirclePageIndicator mCirclePageIndicator;

    private Track mTrack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        ButterKnife.bind(this);

        mTrack = new Track();

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        NowPlayingPagerAdapter pagerAdapter = new NowPlayingPagerAdapter(getSupportFragmentManager(), mTrack);
        mPager.setAdapter(pagerAdapter);

        mCirclePageIndicator.setStrokeColor(getResources().getColor(R.color.white));
        mCirclePageIndicator.setFillColor(getResources().getColor(R.color.white));

        mCirclePageIndicator.setViewPager(mPager);
    }

}
