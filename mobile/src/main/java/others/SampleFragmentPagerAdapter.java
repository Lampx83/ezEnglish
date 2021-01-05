package others;


import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import fragment.TranslateListFragment;


/**
 * Created by xuanlam on 12/11/15.
 */
public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;

    private Context context;

    public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            TranslateListFragment translateListFragment = new TranslateListFragment();
            translateListFragment.type ="Your";
            return translateListFragment;
        } else if (position == 1) {
            TranslateListFragment translateListFragment = new TranslateListFragment();
            translateListFragment.type ="All";
            return translateListFragment;
            //return new PedometerFragment();
        } else return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
//        if (position == 0)
//            return context.getResources().getString(R.string.translated);
//        else
//            return context.getResources().getString(R.string.pedometer);
        return "";
    }
}
