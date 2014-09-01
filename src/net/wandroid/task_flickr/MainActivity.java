
package net.wandroid.task_flickr;

import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;
import com.googlecode.flickrjandroid.photos.SearchParameters;

import net.wandroid.task_flickr.ui.SearchResultListAdapter;
import net.wandroid.task_flickr.ui.SearchResultListFragment.ISearchResultListClickListener;
import net.wandroid.task_flikr.R;

import org.json.JSONException;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends Activity implements ISearchResultListClickListener {

    private static final String SEARCH_TEXT = "squirrel";

    private static final int MAX_HITS = 30;

    private ListFragment mSearchListFragment;

    private TextView mInfoText;

    private Button mSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInfoText = (TextView)findViewById(R.id.activity_main_info_text);
        mSearchButton = (Button)findViewById(R.id.activity_main_search_button);

        FragmentManager manager = getFragmentManager();
        mSearchListFragment = (ListFragment)manager.findFragmentById(R.id.main_list_fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void searchClick(View v) {
        // clear old search results
        SearchResultListAdapter adapter = (SearchResultListAdapter)mSearchListFragment
                .getListAdapter();
        adapter.clear();
        downloadPage(1);
    }

    /**
     * Downloads a search page and add the adapter
     * @param page the page to load
     */
    private  void downloadPage(int page) {
        new DownloadPhotoInfoTask().execute(page);
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


        protected void onPreExecute() {
            // set gui to searching state
            mInfoText.setVisibility(View.VISIBLE);
            mInfoText.setText(getResources().getString(R.string.searching_txt));
            mSearchButton.setEnabled(false);
        }

        @Override
        protected PhotoList doInBackground(Integer... page) {
            mPage = page[0];
            PhotoList photos = search(SEARCH_TEXT, mPage);
            return photos;
        }

        @Override
        protected void onPostExecute(PhotoList result) {
            super.onPostExecute(result);
            SearchResultListAdapter adapter = (SearchResultListAdapter)mSearchListFragment
                    .getListAdapter();
            adapter.addAll(result);

            // reset the gui state
            if (result.size() == 0 && mPage == 0) { // could not find any result at all
                mInfoText.setText(getResources().getString(R.string.no_result_txt));

            } else {
                mInfoText.setVisibility(View.GONE);
            }

            if (result.size() != 0) {//there are more pages
                downloadPage(mPage + 1);
            }else{ // no more pages, enable search button again
                mSearchButton.setEnabled(true);
            }
        }

    }

}
