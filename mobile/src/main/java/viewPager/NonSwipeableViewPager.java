package viewPager;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity_display.MTerms;
import fragment.CardFragment;

public class NonSwipeableViewPager extends ViewPager {

    public NonSwipeableViewPager(Context context) {
        super(context);
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        // Never allow swiping to switch between pages
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }


    public static class CardSliderAdapter extends FragmentStatePagerAdapter {
        private final List<MTerms> list_terms;

        public CardSliderAdapter(Context context, FragmentManager fm, List<MTerms> list_terms) {
            super(fm);
            this.list_terms = list_terms;
            mFragmentTags = new HashMap<String, Fragment>();
        }

        @Override
        public Fragment getItem(int position) {
            CardFragment f = CardFragment.create(position);
            mFragmentTags.put("lpxFragment" + position,f);
            return f;
        }

        @Override
        public int getCount() {
            return list_terms.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "" + (position + 1);
        }


        private Map<String, Fragment> mFragmentTags;

        public Fragment getFragment(int position) {
            Fragment currentFragment = mFragmentTags.get("lpxFragment" + position);

            return currentFragment;
        }
    }

}
