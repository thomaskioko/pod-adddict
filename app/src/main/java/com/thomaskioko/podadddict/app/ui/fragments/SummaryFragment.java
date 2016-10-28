package com.thomaskioko.podadddict.app.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.data.model.Track;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Fragment class to display track details. ie. Author, Title and Description.
 *
 * @author kioko
 */
public class SummaryFragment extends Fragment {

    @Bind(R.id.description_title)
    TextView mPodcastTitle;
    @Bind(R.id.description_artist)
    TextView mPodcastArtistName;
    @Bind(R.id.description_date)
    TextView mPodcastPublishDate;

    /**
     * Required empty public constructor
     */
    public SummaryFragment() {
    }

    /**
     * @param track {@link Track} instance
     * @return {@link SummaryFragment} instance
     */
    public static SummaryFragment newInstance(Track track) {
        return new SummaryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_summary, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

}
