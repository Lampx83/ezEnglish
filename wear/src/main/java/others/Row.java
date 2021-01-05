package others;


import android.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import fragment.Page1Fragment;
import fragment.Page2Fragment;

/**
 * Created by xuanlam on 9/23/15.
 */

public class Row {
    final List<Fragment> columns = new ArrayList<Fragment>();


    public Row(int id) {
        Fragment fragment;
        if (id == 0) {
             fragment = new Page1Fragment();
            add(fragment);
        } else {
            fragment = new Page2Fragment();
            add(fragment);
        }
    }

    public void add(Fragment f) {
        columns.add(f);
    }

    public Fragment getColumn(int i) {
        return columns.get(i);
    }

    public int getColumnCount() {
        return columns.size();
    }
}