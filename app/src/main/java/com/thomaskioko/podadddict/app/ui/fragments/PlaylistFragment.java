package com.thomaskioko.podadddict.app.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thomaskioko.podadddict.app.PodAddictApplication;
import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.interfaces.OnStartDragListener;
import com.thomaskioko.podadddict.app.interfaces.SimpleItemTouchHelperCallback;
import com.thomaskioko.podadddict.app.ui.adapter.PlaylistAdapter;
import com.thomaskioko.podadddict.musicplayerlib.model.Track;
import com.thomaskioko.podadddict.musicplayerlib.player.PodAdddictPlayer;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.View.INVISIBLE;

/**
 * Fragment to display a list of tracks that have been added to the playlist.
 *
 * @author kioko
 */
public class PlaylistFragment extends Fragment implements OnStartDragListener {

    @Bind(R.id.recycler_view_list)
    RecyclerView mRecyclerView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.textViewMessage)
    TextView mTvErrorMessage;

    private ItemTouchHelper mItemTouchHelper;
    private ArrayList<Track> mPlaylistTracks;

    /**
     * Required empty public constructor
     */
    public PlaylistFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_playlist, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PodAdddictPlayer podAdddictPlayer = PodAddictApplication.getPodAdddictPlayer();

        mPlaylistTracks = new ArrayList<>();

        // check if tracks are already loaded into the player.
        ArrayList<Track> currentsTracks = podAdddictPlayer.getTracks();
        if (currentsTracks != null) {
            mPlaylistTracks.addAll(currentsTracks);
        }

        PlaylistAdapter adapter = new PlaylistAdapter(getActivity(), this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mTvErrorMessage.setVisibility(INVISIBLE);
        mProgressBar.setVisibility(INVISIBLE);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
