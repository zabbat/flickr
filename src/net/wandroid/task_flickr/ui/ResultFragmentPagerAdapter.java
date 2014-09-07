
package net.wandroid.task_flickr.ui;

import net.wandroid.task_flickr.ui.word.SearchResultGridFragment;
import net.wandroid.task_flickr.ui.word.SearchResultListFragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ResultFragmentPagerAdapter extends FragmentPagerAdapter {
    // TODO: change to statepage
    ArrayList<WeakReference<Fragment>> mPages = new ArrayList<WeakReference<Fragment>>();

    private static final int NR_PAGES = 2;

    public ResultFragmentPagerAdapter(FragmentManager fm) {
        super(fm);

        for (int i = 0; i < NR_PAGES; i++) {
            mPages.add(new WeakReference<Fragment>(null));
        }
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new SearchResultListFragment();
                break;
            case 1:
                fragment = new SearchResultGridFragment();
                break;
            default:
        }
        mPages.set(position, new WeakReference<Fragment>(fragment));
        return fragment;
    }

    @Override
    public int getCount() {
        return NR_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "List";
            case 1:
                return "Grid";
                default:
                return "";
        }

    }

}
