
package net.wandroid.task_flickr.ui;

import net.wandroid.task_flikr.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter for a flickr search result list
 */
public class SearchResultListAdapter extends ArrayAdapter<SearchResult> {
    private static final int PART_MEM_FOR_CACHE = 4;

    private static final int sMaxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);

    private static LruCache<String, Bitmap> sThumbnailLru = new LruCache<String, Bitmap>(sMaxMemory
            / PART_MEM_FOR_CACHE);

    private final Bitmap DEFAULT_BITMAP;

    private LayoutInflater mInflater;

    private String mAuthorStr;

    private String mNoTitleStr;

    private String mTitleStr;

    public SearchResultListAdapter(Context context, int resource) {
        super(context, resource);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // note that these string might not be properly changed on an
        // onConfigurationChange
        Resources res = getContext().getResources();
        mAuthorStr = res.getString(R.string.author_txt);
        mNoTitleStr = res.getString(R.string.no_title_txt);
        mTitleStr = res.getString(R.string.title_txt);
        DEFAULT_BITMAP = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.ic_launcher);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchResult result = getItem(position);
        Holder holder = null;

        if (convertView == null) {// not reusing, create a new view
            convertView = mInflater.inflate(R.layout.word_search_item, parent, false);
            TextView author = (TextView)convertView.findViewById(R.id.word_search_author_text);
            TextView title = (TextView)convertView.findViewById(R.id.word_search_title_text);
            ImageView thumbnail = (ImageView)convertView
                    .findViewById(R.id.word_search_thumbnail_image);

            holder = new Holder(title, author, thumbnail, new DownloadThumbnailTask(thumbnail,
                    getContext(), sThumbnailLru));
            convertView.setTag(holder);

        } else {// reusing view
            holder = (Holder)convertView.getTag();

            // the view is reused, so there's no point in finishing the download
            holder.downloader.cancel(true);
            // create new image downloader task for this view
            holder.downloader = new DownloadThumbnailTask(holder.thumbnail, getContext(),
                    sThumbnailLru);
        }

        // set author
        holder.author.setText(mAuthorStr + result.getUserName());

        // set title
        if (TextUtils.isEmpty(result.getTitle())) {
            holder.title.setText(mNoTitleStr);
        } else {
            holder.title.setText(mTitleStr + result.getTitle());
        }

        // set image to default image in case there is no cached version
        holder.thumbnail.setImageBitmap(DEFAULT_BITMAP);

        Bitmap bmp = sThumbnailLru.get(result.getThumbnailAddress());
        if (bmp == null) {// nothing cached, start downloading
            holder.downloader.execute(result.getThumbnailAddress());
        } else {
            holder.thumbnail.setImageBitmap(bmp);
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

        private DownloadThumbnailTask downloader;

        public Holder(TextView title, TextView author, ImageView thumbnail,
                DownloadThumbnailTask downloader) {
            super();
            this.title = title;
            this.author = author;
            this.thumbnail = thumbnail;
            this.downloader = downloader;

        }
    }

}
