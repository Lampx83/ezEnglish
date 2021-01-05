package waveview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by John on 2014/10/15.
 */
class Solid extends View {

    private Paint aboveWavePaint;

    public Solid(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Solid(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        setLayoutParams(params);
    }

    public void setAboveWavePaint(Paint aboveWavePaint) {
        this.aboveWavePaint = aboveWavePaint;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(getLeft(), 0, getRight(), getBottom(), aboveWavePaint);
    }
}
