
package net.wandroid.task_flickr.ui;

import net.wandroid.task_flickr.ui.word.SearchResultGridFragment;
import net.wandroid.task_flickr.ui.word.SearchResultListFragment;
import net.wandroid.task_flikr.R;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.support.v13.app.FragmentPagerAdapter;

public class ResultFragmentPagerAdapter extends FragmentPagerAdapter {
    // TODO: change to statepage

    private static final int NR_PAGES = 2;
    private final String TITLE_0;
    private final String TITLE_1;
    private static final String DEFAULT_TITLE="";

    public ResultFragmentPagerAdapter(FragmentManager fm,Resources resourses) {
        super(fm);
        TITLE_0=resourses.getString(R.string.result_fragment_page_adapter_list_title_txt);
        TITLE_1=resourses.getString(R.string.result_fragment_page_adapter_grid_title_txt);
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
        return fragment;
    }

    @Override
    public int getCount() {
        return NR_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return TITLE_0;
            case 1:
                return TITLE_1;
            default:
                return DEFAULT_TITLE;
        }
    }
}
