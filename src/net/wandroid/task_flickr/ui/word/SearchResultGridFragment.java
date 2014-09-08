
package net.wandroid.task_flickr.ui.word;

import com.googlecode.flickrjandroid.photos.Photo;

import net.wandroid.task_flikr.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.AbsListView.OnScrollListener;

public class SearchResultGridFragment extends Fragment implements OnScrollListener,
        OnItemClickListener {
    private static final int CLOSE_TO_BOTTOM_OFFSET = 10;

    private ArrayAdapter<Photo> mAdapter;

    private GridView mGridView;

    private ISearchResultGridListener mSearchResultGridListener = ISearchResultGridListener.NO_LISTENER;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mGridView == null) {
            mGridView = (GridView)inflater.inflate(R.layout.word_search_grid, container, false);
        }

        if (mGridView.getAdapter() == null) {
            mAdapter = new SearchGridAdapter(getActivity(), R.layout.word_search_item);
            mGridView.setAdapter(mAdapter);
        }
        setRetainInstance(true);
        mGridView.setOnItemClickListener(this);
        return mGridView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGridView.setOnScrollListener(this);
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

    @Override
    public void onScroll(AbsListView list, int arg1, int arg2, int arg3) {

        if (list.getCount() == 0) {
            return;
        }
        // scrolled to last item
        if (list.getLastVisiblePosition() > list.getAdapter().getCount() - CLOSE_TO_BOTTOM_OFFSET) {
            mSearchResultGridListener.onScrolledCloseToBottom();
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView arg0, int arg1) {
        // TODO Auto-generated method stub
    }

    public interface ISearchResultGridListener extends ISearchResultListener {
        void onSearchResultGridFragmentReady(SearchResultGridFragment fragment);

        public static final ISearchResultGridListener NO_LISTENER = new ISearchResultGridListener() {
            @Override
            public void onSearchResultGridFragmentReady(SearchResultGridFragment fragment) {
            }

            @Override
            public void itemClicked(Photo result) {
            }

            @Override
            public void onScrolledCloseToBottom() {

            }
        };
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Photo result = mAdapter.getItem(position);
        mSearchResultGridListener.itemClicked(result);
    }

}
