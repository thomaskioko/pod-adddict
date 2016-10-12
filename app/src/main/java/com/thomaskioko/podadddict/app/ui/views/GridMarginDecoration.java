package com.thomaskioko.podadddict.app.ui.views;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Helper class to implement line decoration on the recycler view
 *
 * @author Thomas Kioko
 */
public class GridMarginDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public GridMarginDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.top = space;
        outRect.right = space;
        outRect.bottom = space;
    }
}
