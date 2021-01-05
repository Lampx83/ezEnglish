package waveview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.jquiz.project2.R;


// y=Asin(ωx+φ)+k
class Wave extends View {
    private final int WAVE_HEIGHT_LARGE = 20;
    private final int WAVE_HEIGHT_MIDDLE = 8;
    private final int WAVE_HEIGHT_LITTLE = 5;


    private final float WAVE_LENGTH_MULTIPLE_LARGE = 1.5f;
    private final float WAVE_LENGTH_MULTIPLE_MIDDLE = 1f;
    private final float WAVE_LENGTH_MULTIPLE_LITTLE = 0.5f;

    private final float WAVE_HZ_FAST = 0.3f;
    private final float WAVE_HZ_NORMAL = 0.2f;
    private final float WAVE_HZ_SLOW = 0.1f;

    private final float X_SPACE = 20;
    private final double PI2 = 2 * Math.PI;
    private Path mAboveWavePath = new Path();
    private Paint mAboveWavePaint = new Paint();
    private int mAboveWaveColor;
    private float mWaveMultiple;
    private float mWaveLength;
    private int mWaveHeight;
    private float mMaxRight;
    private float mWaveHz;

    // wave animation
    private float mAboveOffset = 0.0f;

    private RefreshProgressRunnable mRefreshProgressRunnable;

    private int left, right, bottom;
    // ω
    private double omega;

    public Wave(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.waveViewStyle);
    }

    public Wave(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mAboveWavePath, mAboveWavePaint);
    }

    public void setAboveWaveColor(int aboveWaveColor) {
        this.mAboveWaveColor = aboveWaveColor;
    }


    public Paint getAboveWavePaint() {
        return mAboveWavePaint;
    }


    public void initializeWaveSize(int waveMultiple, int waveHeight, int waveHz) {
        mWaveMultiple = getWaveMultiple(waveMultiple);
        mWaveHeight = getWaveHeight(waveHeight);
        mWaveHz = getWaveHz(waveHz);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mWaveHeight * 2);
        setLayoutParams(params);
        mWaveHeight = 0;
    }

    public void initializePainters() {
        mAboveWavePaint.setColor(mAboveWaveColor);
        mAboveWavePaint.setStyle(Paint.Style.FILL);
        mAboveWavePaint.setAntiAlias(true);


    }

    private float getWaveMultiple(int size) {
        switch (size) {
            case WaveView.LARGE:
                return WAVE_LENGTH_MULTIPLE_LARGE;
            case WaveView.MIDDLE:
                return WAVE_LENGTH_MULTIPLE_MIDDLE;
            case WaveView.LITTLE:
                return WAVE_LENGTH_MULTIPLE_LITTLE;
        }
        return 0;
    }

    public void setmWaveHz(int waveHz) {
        mWaveHz = getWaveHz(waveHz);
    }

    int heightMode = -1; //1  go to max, //0  go to min //-1 do nothing

    public void changeHeightMode(int heightMode) {
        this.heightMode = heightMode;
    }

    private int getWaveHeight(int size) {
        switch (size) {
            case WaveView.LARGE:
                return WAVE_HEIGHT_LARGE;
            case WaveView.MIDDLE:
                return WAVE_HEIGHT_MIDDLE;
            case WaveView.LITTLE:
                return WAVE_HEIGHT_LITTLE;
        }
        return 0;
    }

    private float getWaveHz(int size) {
        switch (size) {
            case WaveView.LARGE:
                return WAVE_HZ_FAST;
            case WaveView.MIDDLE:
                return WAVE_HZ_NORMAL;
            case WaveView.LITTLE:
                return WAVE_HZ_SLOW;
        }
        return 0;
    }

    private void calculatePath() {
        if (heightMode == 1) {
            if (mWaveHeight < WAVE_HEIGHT_LARGE)
                mWaveHeight++;
            else
                heightMode = -1;
        } else if (heightMode == 0) {
            if (mWaveHeight > 0)
                mWaveHeight--;
        }


        mAboveWavePath.reset();
        getWaveOffset();
        float y;
        mAboveWavePath.moveTo(left, bottom);
        for (float x = 0; x <= mMaxRight; x += X_SPACE) {
            y = (float) (mWaveHeight * Math.sin(omega * x + mAboveOffset) + mWaveHeight);
            mAboveWavePath.lineTo(x, y);
        }
        mAboveWavePath.lineTo(right, bottom);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (View.GONE == visibility) {
            removeCallbacks(mRefreshProgressRunnable);
        } else {
            removeCallbacks(mRefreshProgressRunnable);
            mRefreshProgressRunnable = new RefreshProgressRunnable();
            post(mRefreshProgressRunnable);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            if (mWaveLength == 0) {
                startWave();
            }
        }
    }

    private void startWave() {
        if (getWidth() != 0) {
            int width = getWidth();
            mWaveLength = width * mWaveMultiple;
            left = getLeft();
            right = getRight();
            bottom = getBottom() + 2;
            mMaxRight = right + X_SPACE;
            omega = PI2 / mWaveLength;
        }
    }

    private void getWaveOffset() {

        if (mAboveOffset > Float.MAX_VALUE - 100) {
            mAboveOffset = 0;
        } else {
            mAboveOffset += mWaveHz;
        }
    }

    private class RefreshProgressRunnable implements Runnable {
        public void run() {
            synchronized (Wave.this) {
                long start = System.currentTimeMillis();

                calculatePath();

                invalidate();

                long gap = 16 - (System.currentTimeMillis() - start);
                postDelayed(this, gap < 0 ? 0 : gap);
            }
        }
    }

}
