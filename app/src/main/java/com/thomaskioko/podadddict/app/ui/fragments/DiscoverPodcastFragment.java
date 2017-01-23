package com.thomaskioko.podadddict.app.ui.fragments;


import android.annotation.TargetApi;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.data.PodCastContract;
import com.thomaskioko.podadddict.app.ui.PodCastListActivity;
import com.thomaskioko.podadddict.app.ui.adapter.PodCastAdapterAdapter;
import com.thomaskioko.podadddict.app.ui.views.GridMarginDecoration;
import com.thomaskioko.podadddict.app.util.DeviceUtils;
import com.thomaskioko.podadddict.app.util.GoogleAnalyticsUtil;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.ButterKnife;

/**
 * A {@link Fragment} subclass that displays a list of top rated PodCasts and allows the user to
 * subscribe to a feed.
 *
 * @author Thomas Kioko
 */
public class DiscoverPodcastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.recycler_view_list)
    RecyclerView mRecyclerView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.textViewMessage)
    TextView mTvErrorMessage;
    @BindInt(R.integer.photo_grid_columns)
    int mColumns;
    @BindDimen(R.dimen.grid_item_spacing)
    int mGridSpacing;

    private PodCastAdapterAdapter mPodCastAdapterAdapter;
    private static final int LOADER_ID = 100;
    private int mPosition = RecyclerView.NO_POSITION;
    private int mChoiceMode;
    private boolean mHoldForTransition, mAutoSelectView;
    private static final String SELECTED_KEY = "selected_position";
    private static final String LOG_TAG = DiscoverPodcastFragment.class.getSimpleName();

    public DiscoverPodcastFragment() {
        // Required empty public constructor
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onFeedItemSelected(Uri feedUri, PodCastAdapterAdapter.PhotoViewHolder photoViewHolder);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_discover_podcast, container, false);
        ButterKnife.bind(this, rootView);

        GoogleAnalyticsUtil.trackScreenView(LOG_TAG);

        ((PodCastListActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.title_add_podcast));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), mColumns);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                /* emulating https://material-design.storage.googleapis.com/publish/material_v_4/material_ext_publish/0B6Okdz75tqQsck9lUkgxNVZza1U/style_imagery_integration_scale1.png */
                switch (position % 6) {
                    case 0:
                    case 1:
                    case 2:
                    case 4:
                        return 1;
                    case 3:
                        return 2;
                    default:
                        return 3;
                }
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new GridMarginDecoration(mGridSpacing));
        mRecyclerView.setHasFixedSize(true);

        //Check if the device is connected to the internet
        if(DeviceUtils.isNetworkConnected(getContext())){
            Uri podCastUri = PodCastContract.PodCastFeedEntry.buildPodCastFeedUri();
            final Cursor cursor = getActivity().getContentResolver().query(podCastUri,
                    null, null, null, null);

            if (cursor != null) {
                //Hide the textView
                mTvErrorMessage.setVisibility(View.GONE);
                cursor.close();
            }

            mPodCastAdapterAdapter = new PodCastAdapterAdapter(getActivity(), new PodCastAdapterAdapter.PodCastAdapterAdapterOnClickHandler() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(int feedId, PodCastAdapterAdapter.PhotoViewHolder photoViewHolder) {
                    mPosition = photoViewHolder.getAdapterPosition();

                    ((Callback) getActivity())
                            .onFeedItemSelected(PodCastContract.PodCastFeedEntry.buildPodCastFeedUri(feedId), photoViewHolder
                            );
                }
            }, mChoiceMode);

            mRecyclerView.setAdapter(mPodCastAdapterAdapter);
        }else{
            mProgressBar.setVisibility(View.GONE);
            mTvErrorMessage.setVisibility(View.VISIBLE);
            mTvErrorMessage.setText(getString(R.string.error_no_internet_connection));
        }



        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SELECTED_KEY)) {
                // The Recycler View probably hasn't even been populated yet.  Actually perform the
                // swapout in onLoadFinished.
                mPosition = savedInstanceState.getInt(SELECTED_KEY);
            }
            mPodCastAdapterAdapter.onRestoreInstanceState(savedInstanceState);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setMenuVisibility(false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (mHoldForTransition) {
            getActivity().supportPostponeEnterTransition();
        }
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mRecyclerView) {
            mRecyclerView.clearOnScrollListeners();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        mPodCastAdapterAdapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri podCastUri = PodCastContract.PodCastFeedEntry.buildPodCastFeedUri();

        return new CursorLoader(getActivity(),
                podCastUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mPodCastAdapterAdapter.swapCursor(cursor);
        if (mPosition != RecyclerView.NO_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mRecyclerView.smoothScrollToPosition(mPosition);
        }
        //TODO:: Display a message.. No internet. please wait etc
        if (cursor.getCount() == 0) {
            getActivity().supportStartPostponedEnterTransition();
        } else {
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    // Since we know we're going to get items, we keep the listener around until
                    // we see Children.
                    if (mRecyclerView.getChildCount() > 0) {
                        mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        int itemPosition = mPodCastAdapterAdapter.getSelectedItemPosition();
                        if (RecyclerView.NO_POSITION == itemPosition) itemPosition = 0;
                        RecyclerView.ViewHolder vh = mRecyclerView.findViewHolderForAdapterPosition(itemPosition);
                        if (null != vh && mAutoSelectView) {
                            mPodCastAdapterAdapter.selectView(vh);
                        }
                        if (mHoldForTransition) {
                            getActivity().supportStartPostponedEnterTransition();
                        }
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPodCastAdapterAdapter.swapCursor(null);
    }
}
