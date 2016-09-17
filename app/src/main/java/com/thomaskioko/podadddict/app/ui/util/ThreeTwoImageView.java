package com.thomaskioko.podadddict.app.ui.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom ImageView class used to set the size of the image.
 *
 * @author nickbutcher
 */
public class ThreeTwoImageView extends ForegroundImageView {

    public ThreeTwoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int desiredHeight = width * 2 / 3;
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(desiredHeight, View.MeasureSpec.EXACTLY));
    }
}
