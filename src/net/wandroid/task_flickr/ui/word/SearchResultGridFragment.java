
package net.wandroid.task_flickr.ui.word;

import com.googlecode.flickrjandroid.photos.Photo;

import net.wandroid.task_flikr.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

public class SearchResultGridFragment extends Fragment {

    private ArrayAdapter<Photo> mAdapter;

    private GridView mGridView;

    private ISearchResultGridListener mSearchResultGridListener = ISearchResultGridListener.NO_LISTENER;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(mGridView==null){
            mGridView=(GridView)inflater.inflate(R.layout.word_search_grid, container, false);
        }

        if (mGridView.getAdapter() == null) {
            mAdapter = new SearchResultListAdapter(getActivity(), R.layout.word_search_item);
            mGridView.setAdapter(mAdapter);
        }
        setRetainInstance(true);
        return mGridView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSearchResultGridListener.onSearchResultGridFragmentReady(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ISearchResultGridListener) {
            mSearchResultGridListener = (ISearchResultGridListener)activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSearchResultGridListener = ISearchResultGridListener.NO_LISTENER;
    }

    public GridView getGridView() {
        return mGridView;
    }

    public interface ISearchResultGridListener {
        void onSearchResultGridFragmentReady(SearchResultGridFragment fragment);

        public static final ISearchResultGridListener NO_LISTENER = new ISearchResultGridListener() {
            @Override
            public void onSearchResultGridFragmentReady(SearchResultGridFragment fragment) {
            }
        };
    }

}
