package activity;

import android.os.Bundle;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.jquiz.project2.R;

import java.util.ArrayList;

import fragment.Page1Fragment;
import fragment.Page2Fragment;
import others.Row;
import others.SampleGridPagerAdapter;


public class MainActivity1 extends ParentActivity {
    public RelativeLayout rlDialog;
    GridViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        rlDialog = (RelativeLayout) findViewById(R.id.rlDialog);


        CircledImageView btnCancel = (CircledImageView) findViewById(R.id.btnCancel);
        CircledImageView btnOk = (CircledImageView) findViewById(R.id.btnOk);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlDialog.setVisibility(View.GONE);
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pager = (GridViewPager) findViewById(R.id.pager);

        myAdapter = new SampleGridPagerAdapter(getFragmentManager());
        myAdapter.mRows = new ArrayList<Row>();


        myAdapter.mRows.add(new Row(0));
        myAdapter.mRows.add(new Row(1));

        myAdapter.notifyDataSetChanged();
        pager.setAdapter(myAdapter);
        DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        dotsPageIndicator.setPager(pager);
    }

    public void updateView(final String translate) {
        ((Page1Fragment) myAdapter.mRows.get(0).getColumn(0)).updateView(translate);
        ((Page2Fragment) myAdapter.mRows.get(1).getColumn(0)).updateView(translate);
    }


    public boolean inApp = false;

    @Override
    protected void onPause() {
        inApp = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        inApp = true;
        super.onResume();
    }


    SampleGridPagerAdapter myAdapter;


}
