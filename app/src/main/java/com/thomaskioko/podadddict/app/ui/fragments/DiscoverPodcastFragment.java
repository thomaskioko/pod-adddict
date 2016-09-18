package com.thomaskioko.podadddict.app.ui.fragments;


import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.thomaskioko.podadddict.app.PodAddictApplication;
import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.api.ApiClient;
import com.thomaskioko.podadddict.app.api.model.Entry;
import com.thomaskioko.podadddict.app.api.model.ImImage;
import com.thomaskioko.podadddict.app.api.model.responses.TopPodCastResponse;
import com.thomaskioko.podadddict.app.ui.PodCastDetailActivity;
import com.thomaskioko.podadddict.app.ui.PodCastListActivity;
import com.thomaskioko.podadddict.app.ui.adapter.PodCastAdapterAdapter;
import com.thomaskioko.podadddict.app.ui.util.GridMarginDecoration;
import com.thomaskioko.podadddict.app.ui.util.ItemClickSupport;
import com.thomaskioko.podadddict.app.util.ApplicationConstants;

import java.util.List;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A {@link Fragment} subclass that displays a list of top rated PodCasts and allows the user to
 * subscribe to a feed.
 *
 * @author Thomas Kioko
 */
public class DiscoverPodcastFragment extends Fragment {

    @Bind(R.id.recycler_view_list)
    RecyclerView mRecyclerView;
    @Bind(android.R.id.empty)
    ProgressBar mProgressBar;
    @BindInt(R.integer.photo_grid_columns)
    int mColumns;
    @BindDimen(R.dimen.grid_item_spacing)
    int mGridSpacing;

    private List<Entry> mEntryList;
    private static PodCastListActivity mPodCastListActivity;
    private static Context mContext;

    public DiscoverPodcastFragment() {
        // Required empty public constructor
    }

    public static DiscoverPodcastFragment newInstance(Context context, PodCastListActivity activity) {
        mContext = context;
        mPodCastListActivity = activity;

        return new DiscoverPodcastFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_discover_podcast, container, false);
        ButterKnife.bind(this, rootView);

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


        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View view) {

                        List<ImImage> imImages = mEntryList.get(position).getImImage();

                        String url = null;
                        for (ImImage imImage : imImages) {
                            url = imImage.getLabel();
                        }

                        if (url != null) {
                            url = url.replace(ApplicationConstants.IMAGE_SIZE_170x170,
                                    ApplicationConstants.IMAGE_SIZE_600x600
                            );
                        }
                        Intent intent = new Intent(mContext, PodCastDetailActivity.class);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        //We need this because we are calling startActivity outside an Activity
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        mPodCastListActivity.startActivity(intent,
                                ActivityOptions.makeSceneTransitionAnimation(mPodCastListActivity, view,
                                        view.getTransitionName()).toBundle());
                    }
                });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ApiClient apiClient = PodAddictApplication.getApiClientInstance();
        apiClient.setEndpointUrl(ApplicationConstants.ITUNES_END_POINT);

        Call<TopPodCastResponse> topPodCastResponseCall = apiClient.iTunesServices().getTopRatedPodCasts();
        topPodCastResponseCall.enqueue(new Callback<TopPodCastResponse>() {
            @Override
            public void onResponse(Call<TopPodCastResponse> call, Response<TopPodCastResponse> response) {
                mProgressBar.setVisibility(View.GONE);

                //Get the list for the response.
                mEntryList = response.body().getFeed().getEntry();
                mRecyclerView.setAdapter(new PodCastAdapterAdapter(getActivity(), mEntryList));
            }

            @Override
            public void onFailure(Call<TopPodCastResponse> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

}
