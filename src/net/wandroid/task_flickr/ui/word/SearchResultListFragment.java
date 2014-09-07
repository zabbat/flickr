
package net.wandroid.task_flickr.ui.word;

import com.googlecode.flickrjandroid.photos.Photo;

import net.wandroid.task_flikr.R;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * ListFragment for search results
 */
public class SearchResultListFragment extends ListFragment implements OnScrollListener {

    private static final int CLOSE_TO_BOTTOM_OFFSET = 10;

    private ISearchResultListFragmentListener mSearchResultListListener = ISearchResultListFragmentListener.NO_LISTENER;

    private ArrayAdapter<Photo> mAdapter;

    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mAdapter == null) {
            mAdapter = new SearchListAdapter(getActivity(), R.layout.word_search_item);
            setListAdapter(mAdapter);
        }
        if (mListView == null) {
            mListView = (ListView)inflater.inflate(R.layout.word_search_list, container, false);
        }
        // don't kill fragment on configuration change
        setRetainInstance(true);
        return mListView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ListView list = getListView();
        list.setOnScrollListener(this);
        mSearchResultListListener.onSearchResultListFragmentReady(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ISearchResultListFragmentListener) {
            mSearchResultListListener = (ISearchResultListFragmentListener)activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSearchResultListListener = ISearchResultListFragmentListener.NO_LISTENER;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Photo result = (Photo)getListAdapter().getItem(position);
        mSearchResultListListener.itemClicked(result);
    }

    @Override
    public void onScroll(AbsListView list, int arg1, int arg2, int arg3) {

        if (list.getCount() == 0) {
            return;
        }
        // scrolled to last item
        if (list.getLastVisiblePosition() > list.getAdapter().getCount() - CLOSE_TO_BOTTOM_OFFSET) {
            mSearchResultListListener.onScrolledCloseToBottom();
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView arg0, int arg1) {
        // TODO Auto-generated method stub
    }

    public interface ISearchResultListFragmentListener extends ISearchResultListener {
        public void onSearchResultListFragmentReady(SearchResultListFragment fragment);

        // Null object pattern, to avoid pesky null checks.
        public static ISearchResultListFragmentListener NO_LISTENER = new ISearchResultListFragmentListener() {
            @Override
            public void itemClicked(Photo result) {
            }

            @Override
            public void onScrolledCloseToBottom() {
            }

            @Override
            public void onSearchResultListFragmentReady(SearchResultListFragment fragment) {
                // TODO Auto-generated method stub

            }

        };
    }

}
