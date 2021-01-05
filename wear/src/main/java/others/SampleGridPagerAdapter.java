
package others;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.wearable.view.FragmentGridPagerAdapter;

import java.util.List;


public class SampleGridPagerAdapter extends FragmentGridPagerAdapter {


    public List<Row> mRows;


    public SampleGridPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getFragment(int row, int col) {
        Row adapterRow = mRows.get(row);
        return adapterRow.getColumn(col);
    }


    @Override
    public int getRowCount() {
        return mRows.size();
    }

    @Override
    public int getColumnCount(int rowNum) {
        return mRows.get(rowNum).getColumnCount();
    }


}
