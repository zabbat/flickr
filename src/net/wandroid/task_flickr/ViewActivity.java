
package net.wandroid.task_flickr;

import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.people.PeopleInterface;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;

import net.wandroid.task_flickr.ui.UserImageGridFragment;
import net.wandroid.task_flikr.R;

import org.json.JSONException;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

public class ViewActivity extends Activity {

    private static final int MAX_HITS = 300;

    public static final String FLICKR_USER_ID = "flickr_userId";

    private UserImageGridFragment mGridFragment;

    private TextView mInfoText;

    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        // Show the Up button in the action bar.
        setupActionBar();
        mInfoText = (TextView)findViewById(R.id.activity_view_info_text);

        FragmentManager manager = getFragmentManager();
        mGridFragment = (UserImageGridFragment)manager
                .findFragmentById(R.id.activity_view_grid_fragment);

        Intent intent = getIntent();
        if (intent == null || intent.getExtras() == null
                || !intent.getExtras().containsKey(FLICKR_USER_ID)) {
            mInfoText.setText(getResources().getString(R.string.bad_intentions_txt));
            mInfoText.setVisibility(View.VISIBLE);
            return;
        }

        mUserId = intent.getExtras().getString(FLICKR_USER_ID);
        loadResult();
    }

    private void loadResult() {

        if(TextUtils.isEmpty(mUserId)){
            return;
        }
        //Start downloading the images
        new AsyncTask<Void, Void, ArrayList<String>>() {

            protected void onPreExecute() {
                //set searching state
                mInfoText.setVisibility(View.VISIBLE);
                mInfoText.setText(getResources().getString(R.string.loading_txt));
            }

            @Override
            protected ArrayList<String> doInBackground(Void... arg0) {
                PhotoList photos = search(mUserId);

                ArrayList<String> list = new ArrayList<String>();
                for (Photo p : photos) {
                    list.add(p.getThumbnailUrl());
                }
                return list;
            }

            protected void onPostExecute(ArrayList<String> list) {
                //reset state
                if (list.size() == 0) {// no images found
                    mInfoText.setText(getResources().getString(R.string.no_user_images_txt));
                    mInfoText.setVisibility(View.VISIBLE);
                }else{
                    mInfoText.setVisibility(View.GONE);
                }
                mGridFragment.setGridList(list);
            };

        }.execute();
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Search for images by a user
     * Networking, must not be called on the main thread
     * @param userId the id of the user
     * @return a PhotoList of the users images, or an empty list if there was an error
     */
    private PhotoList search(String userId) {
        REST restTransport;
        PhotoList list = new PhotoList();
        try {
            restTransport = new REST();
            String apiKey = getResources().getString(R.string.API_KEY);
            String apiSecret = getResources().getString(R.string.API_SECRET);
            PeopleInterface pi = new PeopleInterface(apiKey, apiSecret, restTransport);
            list = pi.getPublicPhotos(userId, MAX_HITS, 0);

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

}
