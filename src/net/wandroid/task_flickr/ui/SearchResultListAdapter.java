
package net.wandroid.task_flickr.ui;

import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.people.PeopleInterface;
import com.googlecode.flickrjandroid.photos.Photo;

import org.json.JSONException;

import net.wandroid.task_flikr.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Adapter for a flickr search result list
 */
public class SearchResultListAdapter extends ArrayAdapter<Photo> {
    private static final int PART_MEM_FOR_CACHE = 4;

    private static final int sMaxMemory = (int)(Runtime.getRuntime().maxMemory());

    private static LruCache<String, Bitmap> sThumbnailLru = new LruCache<String, Bitmap>(sMaxMemory
            / PART_MEM_FOR_CACHE){
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    private final Bitmap DEFAULT_BITMAP;

    private LayoutInflater mInflater;

    private String mNoTitleStr;

    private NameLookup mNameLookup;

    public SearchResultListAdapter(Context context, int resource) {
        super(context, resource);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // note that these resources might not be properly changed on an
        // onConfigurationChange
        Resources res = getContext().getResources();
        mNoTitleStr = res.getString(R.string.no_title_txt);
        DEFAULT_BITMAP = BitmapFactory.decodeResource(res,
                R.drawable.ic_launcher);

        try {
            REST rest = new REST();
            PeopleInterface pi = new PeopleInterface(context.getResources().getString(
                    R.string.API_KEY), context.getResources().getString(R.string.API_SECRET), rest);
            mNameLookup = new NameLookup(pi);
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Photo result = getItem(position);
        Holder holder = null;

        if (convertView == null) {// not reusing, create a new view
            convertView = mInflater.inflate(R.layout.word_search_item, parent, false);
            TextView author = (TextView)convertView.findViewById(R.id.word_search_author_text);
            TextView title = (TextView)convertView.findViewById(R.id.word_search_title_text);
            ImageView thumbnail = (ImageView)convertView
                    .findViewById(R.id.word_search_thumbnail_image);

            holder = new Holder(title, author, thumbnail, new DownloadThumbnailTask(thumbnail,
                    getContext(), sThumbnailLru),
                    new DownloadUserNameTask(author, mNameLookup));
            convertView.setTag(holder);

        } else {// reusing view
            holder = (Holder)convertView.getTag();

            // the view is reused, so there's no point in finishing the download
            holder.imgDownloader.cancel(true);
            // create new image downloader task for this view
            holder.imgDownloader = new DownloadThumbnailTask(holder.thumbnail, getContext(),
                    sThumbnailLru);

            // the view is reused, so there's no point in finishing the download
            holder.nameDownloader.cancel(true);
         // create new name downloader task for this view
            holder.nameDownloader=new DownloadUserNameTask(holder.author, mNameLookup);
        }

        // set title
        if (TextUtils.isEmpty(result.getTitle())) {
            holder.title.setText(mNoTitleStr);
        } else {
            holder.title.setText(result.getTitle());
        }

        // set image to default image in case there is no cached version
        holder.thumbnail.setImageBitmap(DEFAULT_BITMAP);

        Bitmap bmp = sThumbnailLru.get(result.getThumbnailUrl());
        String userId=result.getOwner().getId();
        if (bmp == null) {// nothing cached, start downloading
            holder.imgDownloader.execute(result.getThumbnailUrl(), userId);
        } else {
            holder.thumbnail.setImageBitmap(bmp);
        }

        holder.author.setText("");
        String name=mNameLookup.nameFromCache(userId);
        if(name==null){
            holder.nameDownloader.execute(userId);
        }else{
            holder.author.setText(name);
        }
        return convertView;
    }

    /**
     * Holder class for optimization
     */
    private class Holder {
        private TextView title;

        private TextView author;

        private ImageView thumbnail;

        private DownloadThumbnailTask imgDownloader;

        private DownloadUserNameTask nameDownloader;

        public Holder(TextView title, TextView author, ImageView thumbnail,
                DownloadThumbnailTask imgDownloader,DownloadUserNameTask nameDownloader) {
            super();
            this.title = title;
            this.author = author;
            this.thumbnail = thumbnail;
            this.imgDownloader = imgDownloader;
            this.nameDownloader=nameDownloader;
        }
    }

    /**
     * Class to download user name from id
     */
    private class DownloadUserNameTask extends AsyncTask<String, Void, String>{

        private TextView mText;
        private NameLookup mNameLookup;

        public DownloadUserNameTask(TextView text, NameLookup nameLookup) {
            super();
            mText = text;
            mNameLookup = nameLookup;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //clear text while downloading
            mText.setText("");
        }

        @Override
        protected String doInBackground(String... id) {
            String name=null;
            try {
                name = mNameLookup.IdToUserName(id[0]);
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
            return name;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(TextUtils.isEmpty(result)){
                mText.setText("");
            }else{//set author text
                mText.setText(result);
            }
        }

    }

}
