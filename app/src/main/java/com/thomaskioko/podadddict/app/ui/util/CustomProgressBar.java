package com.thomaskioko.podadddict.app.ui.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.thomaskioko.podadddict.app.ui.fragments.PlayerFragment;

/**
 * Custom helper class used to style the progressBar.
 *
 * @author kioko
 */
public class CustomProgressBar extends View {

    Paint forePaint;

    public CustomProgressBar(Context context) {
        super(context);
        init();
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        forePaint = new Paint();
        forePaint.setStrokeWidth(1.0f);
        forePaint.setAntiAlias(true);
        forePaint.setColor(Color.rgb(0, 128, 255));
    }

    public void update() {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float[] hsv = new float[3];
        hsv[1] = (float) 0.8;
        hsv[2] = (float) 0.5;
        forePaint.setColor(Color.parseColor("#B24242"));
        forePaint.setAlpha(248);
        float right = ((float) canvas.getWidth() / (float) PlayerFragment.durationInMilliSec) * (float) PlayerFragment.mMediaPlayer.getCurrentPosition();
        canvas.drawRect(0, 0, right, canvas.getHeight(), forePaint);
    }
}
