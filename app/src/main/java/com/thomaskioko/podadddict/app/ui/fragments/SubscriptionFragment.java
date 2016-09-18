package com.thomaskioko.podadddict.app.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thomaskioko.podadddict.app.R;

/**
 * This {@link Fragment} loads users subscribed PodCast feeds from the SQLite
 * via {@link com.thomaskioko.podadddict.app.data.provider.PodCastProvider}
 *
 * @author Thomas Kioko
 */
public class SubscriptionFragment extends Fragment {

    /**
     * Required empty public constructor
     */
    public SubscriptionFragment() {
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Enable menu option
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subscription, container, false);
    }


}
