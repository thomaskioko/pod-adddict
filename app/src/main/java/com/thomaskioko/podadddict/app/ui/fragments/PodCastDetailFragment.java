package com.thomaskioko.podadddict.app.ui.fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thomaskioko.podadddict.app.R;
import com.thomaskioko.podadddict.app.ui.PodCastDetailActivity;
import com.thomaskioko.podadddict.app.ui.PodCastListActivity;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.ButterKnife;

/**
 * A fragment representing a single PodCast detail screen.
 * This fragment is either contained in a {@link PodCastListActivity}
 * in two-pane mode (on tablets) or a {@link PodCastDetailActivity}
 * on handsets.
 */
public class PodCastDetailFragment extends Fragment {

    @Bind(R.id.toolbar)
    android.widget.Toolbar toolbar;
    @Bind(R.id.photo)
    ImageView imageView;
    @Bind(R.id.author)
    TextView author;
    @BindInt(R.integer.detail_desc_slide_duration) int slideDuration;

    public static final String EXTRA_TITLE = "EXTRA_TITLE";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PodCastDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.podcast_detail, container, false);
        ButterKnife.bind(this, rootView);

        Glide.with(getActivity())
                .load(getActivity().getIntent().getData())
                .crossFade()
                .placeholder(R.color.placeholder)
                .into(imageView);
        author.setText(getActivity().getIntent().getStringExtra(EXTRA_TITLE));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    getActivity().finishAfterTransition();
                }
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide(Gravity.BOTTOM);
            slide.addTarget(R.id.description);
            slide.setInterpolator(AnimationUtils.loadInterpolator(getActivity(), android.R.interpolator
                    .linear_out_slow_in));
            slide.setDuration(slideDuration);
            getActivity().getWindow().setEnterTransition(slide);
        }

        return rootView;
    }
}
