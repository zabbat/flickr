
package net.wandroid.task_flickr;

import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;
import com.googlecode.flickrjandroid.photos.SearchParameters;

import net.wandroid.task_flickr.ui.ResultFragmentPagerAdapter;
import net.wandroid.task_flickr.ui.SearchFragment.ISearchViewListener;
import net.wandroid.task_flickr.ui.word.SearchResultAdapter;
import net.wandroid.task_flickr.ui.word.SearchResultGridFragment;
import net.wandroid.task_flickr.ui.word.SearchResultGridFragment.ISearchResultGridListener;
import net.wandroid.task_flickr.ui.word.SearchResultListFragment;
import net.wandroid.task_flickr.ui.word.SearchResultListFragment.ISearchResultListFragmentListener;
import net.wandroid.task_flikr.R;

import org.json.JSONException;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends Activity implements ISearchResultListFragmentListener,
        ISearchResultGridListener, ISearchViewListener {

    private static final int FIRST_PAGE = 1;

    private static final int MAX_HITS = 20;

    private TextView mInfoText;

    private SearchResultListFragment mSearchListFragment;

    private SearchResultGridFragment mSearchGridFragment;

    private ViewPager mViewPager;

    private ResultFragmentPagerAdapter mPageAdapter;

    private int mPage = FIRST_PAGE;

    private String mSearchText;

    private boolean mIsLoadingList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInfoText = (TextView)findViewById(R.id.main_info_text);

        mViewPager = (ViewPager)findViewById(R.id.main_pager);

        FragmentManager manager = getFragmentManager();
        mPageAdapter = new ResultFragmentPagerAdapter(manager);
        mViewPager.setAdapter(mPageAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onSearchButtonClick(String searchText) {
        clearSearch();
        mSearchText = searchText;
        mPage = FIRST_PAGE;
        downloadPage(mPage);
    }

    private void clearSearch() {
        if (mSearchListFragment != null) {
            ArrayAdapter<?> adapter = (ArrayAdapter<?>)mSearchListFragment.getListAdapter();
            adapter.clear();
        }
        if (mSearchGridFragment != null) {
            ArrayAdapter<?> adapter = (ArrayAdapter<?>)mSearchGridFragment.getGridView()
                    .getAdapter();
            adapter.clear();
        }
    }

    /**
     * Downloads a search page and add the adapter
     *
     * @param page the page to load
     */
    private void downloadPage(int page) {
        new DownloadPhotoInfoTask(mSearchText).execute(page);
    }

    /**
     * Uses flickr api to search. Must not be executed on main thread
     *
     * @param text The text to search for
     * @return a PhotoList with matches. If any error occurs an empty list will
     *         be returned
     */
    private PhotoList search(String text, int page) {
        REST restTransport;
        PhotoList list = new PhotoList();
        try {
            restTransport = new REST();
            String apiKey = getResources().getString(R.string.API_KEY);
            String apiSecret = getResources().getString(R.string.API_SECRET);
            PhotosInterface pi = new PhotosInterface(apiKey, apiSecret, restTransport);
            SearchParameters params = new SearchParameters();
            params.setText(text);
            list = pi.search(params, MAX_HITS, page);
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FlickrException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void itemClicked(Photo result) {
        // Item in list clicked, start the view activity
        Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
        intent.putExtra(ViewActivity.FLICKR_USER_ID, result.getOwner().getId());
        startActivity(intent);
    }

    private class DownloadPhotoInfoTask extends AsyncTask<Integer, Void, PhotoList> {
        private int mPage;

        private final String mSearchText;

        public DownloadPhotoInfoTask(String searchText) {
            mSearchText = searchText;
        }

        protected void onPreExecute() {
            // set gui to searching state
            setInfoTextVisible(true);
            setInfoText(getResources().getString(R.string.searching_txt));
        }

        @Override
        protected PhotoList doInBackground(Integer... page) {
            mPage = page[0];
            PhotoList photos = search(mSearchText, mPage);
            return photos;
        }

        @Override
        protected void onPostExecute(PhotoList result) {
            super.onPostExecute(result);
            addAllResults(result);

            // reset the gui state
            if (result.isEmpty() && mPage == FIRST_PAGE) { // could not find any
                                                           // result
                setInfoText(getResources().getString(R.string.no_result_txt));
            } else {
                setInfoTextVisible(false);
                // mInfoText.setVisibility(View.GONE);
            }

            mIsLoadingList = false;
        }

        private void addAllResults(PhotoList result) {
            ArrayAdapter<Photo> adapter = (SearchResultAdapter)mSearchListFragment
                    .getListAdapter();
            adapter.addAll(result);

            adapter = (SearchResultAdapter)mSearchGridFragment.getGridView().getAdapter();
            adapter.addAll(result);
        }

    }

    public void setInfoText(String text) {
        mInfoText.setText(text);
    }

    public void setInfoTextVisible(boolean visible) {
        mInfoText.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onScrolledCloseToBottom() {
        if (!mIsLoadingList) {// only load one page at the time
            mIsLoadingList = true;
            mPage++;
            downloadPage(mPage);
        }
    }

    @Override
    public void onSearchResultListFragmentReady(SearchResultListFragment fragment) {
        mSearchListFragment = fragment;
        setInfoTextVisible(mSearchListFragment.getListAdapter().isEmpty());
    }

    @Override
    public void onSearchResultGridFragmentReady(SearchResultGridFragment fragment) {
        mSearchGridFragment = fragment;
        setInfoTextVisible(mSearchGridFragment.getGridView().getAdapter().isEmpty());
    }

}
