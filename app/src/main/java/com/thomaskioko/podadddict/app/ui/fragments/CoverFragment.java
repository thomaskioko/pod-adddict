package com.thomaskioko.podadddict.app.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.musicplayerlib.model.Track;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Fragment class to display track artwork.
 *
 * @author kioko
 */
public class CoverFragment extends Fragment {


    @Bind(R.id.player_album_art)
    ImageView mPlayerArtWork;

    private static Track mTrack;

    public CoverFragment() {
        // Required empty public constructor
    }

    /**
     * Constructor
     *
     * @param track {@link Track} Instance
     * @return {@link CoverFragment} instance
     */
    public static CoverFragment newInstance(Track track) {
        mTrack = track;
        return new CoverFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_cover, container, false);
        ButterKnife.bind(this, rootView);

        Picasso.with(getActivity())
                .load(mTrack.getArtworkUrl())
                .fit()
                .centerCrop()
                .placeholder(R.color.placeholder)
                .into(mPlayerArtWork);

        return rootView;
    }

}
