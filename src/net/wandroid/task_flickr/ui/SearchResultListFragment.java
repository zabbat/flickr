
package net.wandroid.task_flickr.ui;

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
    private ISearchResultListListener mSearchResultListListener = ISearchResultListListener.NO_LISTENER;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ArrayAdapter<Photo> adapter = new SearchResultListAdapter(getActivity(),
                R.layout.word_search_item);
        setListAdapter(adapter);

        return inflater.inflate(R.layout.word_search_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ListView listi = getListView();
        listi.setOnScrollListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ISearchResultListListener) {
            mSearchResultListListener = (ISearchResultListListener)activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSearchResultListListener = ISearchResultListListener.NO_LISTENER;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Photo result = (Photo)getListAdapter().getItem(position);
        mSearchResultListListener.itemClicked(result);
    }

    /**
     * Interface for listening to a list click.
     */
    public interface ISearchResultListListener {
        public void itemClicked(Photo result);

        public void onScrolledCloseToBottom();

        // Null object pattern, to avoid pesky null checks.
        public static ISearchResultListListener NO_LISTENER = new ISearchResultListListener() {
            @Override
            public void itemClicked(Photo result) {
            }

            @Override
            public void onScrolledCloseToBottom() {
            }
        };
    }

    @Override
    public void onScroll(AbsListView list, int arg1, int arg2, int arg3) {

        if(list.getCount()==0){
            return;
        }
        //scrolled to last item
        if (list.getLastVisiblePosition() > list.getAdapter().getCount() - CLOSE_TO_BOTTOM_OFFSET) {
            mSearchResultListListener.onScrolledCloseToBottom();
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView arg0, int arg1) {
        // TODO Auto-generated method stub
    }

}
