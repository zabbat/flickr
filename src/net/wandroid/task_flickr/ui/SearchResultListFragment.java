
package net.wandroid.task_flickr.ui;

import net.wandroid.task_flikr.R;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * ListFragment for search results
 */
public class SearchResultListFragment extends ListFragment {

    private ISearchResultListClickListener mSearchResultListClickListener = ISearchResultListClickListener.NO_LISTENER;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ArrayAdapter<SearchResult> adapter = new SearchResultListAdapter(getActivity(),
                R.layout.word_search_item);
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ISearchResultListClickListener) {
            mSearchResultListClickListener = (ISearchResultListClickListener)activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSearchResultListClickListener = ISearchResultListClickListener.NO_LISTENER;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        SearchResult result = (SearchResult)getListAdapter().getItem(position);
        mSearchResultListClickListener.itemClicked(result);
    }

    /**
     * Interface for listening to a list click.
     */
    public interface ISearchResultListClickListener {
        public void itemClicked(SearchResult result);

        //Null object pattern, to avoid pesky null checks.
        public static ISearchResultListClickListener NO_LISTENER = new ISearchResultListClickListener() {
            @Override
            public void itemClicked(SearchResult result) {
            }
        };
    }

}
