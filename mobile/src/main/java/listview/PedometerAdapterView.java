package listview;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import entity.Pedometer;
import others.MyFunc;

public class PedometerAdapterView extends LinearLayout {


    public PedometerAdapterView(Context context, Pedometer pedometer, int postion) {
        super(context);
        this.setOrientation(HORIZONTAL);

        TextView tvText = new AppCompatTextView(context);
        tvText.setText(pedometer.date);
        tvText.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams lParams = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        lParams.weight = 1;
        tvText.setPadding(0, 10, 0, 10);
        addView(tvText, lParams);

        tvText = new AppCompatTextView(context);
        if (pedometer.steps >= 2)
            tvText.setText(pedometer.steps + " steps");
        else
            tvText.setText(pedometer.steps + " step");
        tvText.setGravity(Gravity.CENTER_VERTICAL);
        lParams = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        tvText.setPadding(0, 10, 0, 10);
        lParams.weight = 1.5f;
        addView(tvText, lParams);

        int distance = MyFunc.getDistance(pedometer.steps);

        tvText = new AppCompatTextView(context);
        if (distance >= 2)
            tvText.setText(distance + " meters");
        else
            tvText.setText(distance + " meter");
        tvText.setGravity(Gravity.CENTER_VERTICAL);
        lParams = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        tvText.setPadding(0, 10, 0, 10);
        lParams.weight = 1.5f;
        addView(tvText, lParams);


        this.setPadding(10, 0, 10, 0);
    }
}