package listview;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jquiz.project2.R;

import java.util.Date;

import controlvariable.MyGlobal;
import entity.Translate;
import others.MyFunc;

public class TranslateAdapterView extends LinearLayout {


    public TranslateAdapterView(final Context context, final Translate translate, int postion) {
        super(context);
        this.setOrientation(HORIZONTAL);

//        if(!translate.user_id.equals(MyGlobal.user_id)){
//            this.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//            getResources().getColor(R.color.colorAccent);
//        }

        TextView tvText = new AppCompatTextView(context);
        if (!translate.user_id.equals(MyGlobal.user_id)) {
            tvText.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        Date d1 = new Date();
        d1.setTime(translate.time * 1000);
        String time_ago = (String) DateUtils.getRelativeTimeSpanString(d1.getTime(), new Date().getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
        tvText.setText(time_ago);
        tvText.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams lParams = new LayoutParams(0, LayoutParams.MATCH_PARENT);

        lParams.weight = 1f;
        addView(tvText, lParams);


        tvText = new AppCompatTextView(context);
        if (!translate.user_id.equals(MyGlobal.user_id)) {
            tvText.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        tvText.setText(translate.text);
        tvText.setGravity(Gravity.CENTER_VERTICAL);
        lParams = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        lParams.weight = 1;
        addView(tvText, lParams);

        if (translate.device != 4) { //No add syntax
            tvText = new AppCompatTextView(context);
            if (!translate.user_id.equals(MyGlobal.user_id)) {
                tvText.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            tvText.setText(translate.result);
            tvText.setGravity(Gravity.CENTER_VERTICAL);
            lParams = new LayoutParams(0, LayoutParams.MATCH_PARENT);

            lParams.weight = 1f;
            addView(tvText, lParams);
        }

        final ImageView imMark = new ImageView(context);
        lParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT);

        if (translate.mark == 1)
            imMark.setImageResource(R.drawable.mark_done);
        else
            imMark.setImageResource(R.drawable.mark);

        imMark.setId(R.id.imgmark);

        imMark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                translate.mark = new MyFunc(context).changeMarkTranslate(translate, imMark, true);
            }
        });
        lParams.weight = 0.5f;
        lParams.gravity = Gravity.CENTER_VERTICAL;
        imMark.setPadding(MyGlobal.fivedp, MyGlobal.fivedp, MyGlobal.fivedp, MyGlobal.fivedp);
        addView(imMark, lParams);

        this.setPadding(MyGlobal.fivedp, MyGlobal.fivedp, MyGlobal.fivedp, MyGlobal.fivedp);
    }
}