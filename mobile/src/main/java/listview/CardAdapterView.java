package listview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jquiz.project2.R;

import controlvariable.MyGlobal;
import entity_display.MTranslate;
import others.MyFunc;

public class CardAdapterView extends LinearLayout {

    Context context;

    public CardAdapterView(final Context context, final MTranslate translate) {
        super(context);
        this.context = context;
        LayoutParams Params;
        this.setOrientation(HORIZONTAL);
        if (translate.status == -1) { // Group
            TextView tvTerm = new TextView(context);
            tvTerm = new TextView(context);
            Params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
            Params.setMargins(MyGlobal.fivedp, 0, MyGlobal.fivedp, 0);
            Params.weight = 1;
            tvTerm.setText(translate.text + " (" + translate.mark + ")");
            tvTerm.setTypeface(null, Typeface.BOLD);
            tvTerm.setTextSize(1.2f * MyGlobal.font_size * tvTerm.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
            addView(tvTerm, Params);
            if (translate.box == 0) {
                tvTerm.setTextColor(Color.WHITE);
                this.setBackgroundColor(getResources().getColor(R.color.mau_wrong)); // Often
            } else if (translate.box == 1) {
                tvTerm.setTextColor(Color.WHITE);
                this.setBackgroundColor(getResources().getColor(R.color.mau_wrong)); // Sometimes
            } else if (translate.box == 2) {
                tvTerm.setTextColor(Color.WHITE);
                this.setBackgroundColor(getResources().getColor(R.color.mau_xanhlo)); // Seldom
            } else if (translate.box == 3) {
                tvTerm.setTextColor(Color.WHITE);
                this.setBackgroundColor(getResources().getColor(R.color.mau_xanhlo)); // Never
            } else if (translate.box == -1) {
                tvTerm.setTextColor(Color.WHITE);
                this.setBackgroundColor(getResources().getColor(R.color.mau_xam5)); // Not Yet
            }
        } else {
            // TERM
            TextView tvTerm = new TextView(context);
            tvTerm = new TextView(context);
            Params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
            Params.setMargins(MyGlobal.fivedp, 0, MyGlobal.fivedp, 0);
            Params.weight = 1;
            String term = translate.text;
            if (term.length() > 50) {
                term = term.substring(0, 45) + "...";
            }
            tvTerm.setText(term);
            tvTerm.setTextSize(1.1f * MyGlobal.font_size * tvTerm.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
            addView(tvTerm, Params);

            // DEF
            TextView tvDef = new TextView(context);
            tvDef = new TextView(context);
            Params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
            Params.setMargins(MyGlobal.fivedp, 0, MyGlobal.fivedp, 0);
            Params.weight = 1;
            tvDef.setGravity(Gravity.CENTER_HORIZONTAL);
            if (translate.result.trim().equals("")) {
                tvDef.setText("[Image]");
            } else {
                String def = translate.result;
                if (def.length() > 100) {
                    def = def.substring(0, 95) + "...";
                }
                if (translate.image != null) {
                    tvDef.setText(Html.fromHtml(def + "<br>[image]"));
                } else {
                    tvDef.setText(def);
                }
            }
            tvDef.setTextSize(MyGlobal.font_size * tvDef.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
            addView(tvDef, Params);

            // MARK
            final ImageView imMark = new ImageView(context);
            Params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            imMark.setPadding(MyGlobal.fivedp, 0, 2 * MyGlobal.fivedp, 0);
            if (translate.mark == 1) {
                imMark.setImageResource(R.drawable.mark_done);
            } else {
                imMark.setImageResource(R.drawable.mark);
            }


            imMark.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //new AppDialog(context).mark_click_dialog(item, imMark);
                    translate.mark = new MyFunc(context).changeMarkTranslate(translate, imMark, true);

                }
            });
            addView(imMark, Params);
        }
        this.setPadding(0, MyGlobal.fivedp, 0, MyGlobal.fivedp);
    }
}