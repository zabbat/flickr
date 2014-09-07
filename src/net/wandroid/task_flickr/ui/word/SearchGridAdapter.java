
package net.wandroid.task_flickr.ui.word;

import com.googlecode.flickrjandroid.photos.Photo;

import net.wandroid.task_flickr.ui.DownloadThumbnailTask;
import net.wandroid.task_flikr.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SearchGridAdapter extends SearchResultAdapter {

    public SearchGridAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Photo result = getItem(position);
        Holder holder = null;

        if (convertView == null) {// not reusing, create a new view
            convertView = getInflater().inflate(R.layout.word_search_item, parent, false);

            ImageView thumbnail = (ImageView)convertView
                    .findViewById(R.id.word_search_thumbnail_image);

            holder = new Holder(thumbnail, new DownloadThumbnailTask(thumbnail, getContext(),
                    getCache()));
            convertView.setTag(holder);

        } else {// reusing view
            holder = (Holder)convertView.getTag();

            // the view is reused, so there's no point in finishing the download
            holder.imgDownloader.cancel(true);
            // create new image downloader task for this view
            holder.imgDownloader = new DownloadThumbnailTask(holder.thumbnail, getContext(),
                    getCache());
        }

        // set image to default image in case there is no cached version
        holder.thumbnail.setImageBitmap(DEFAULT_BITMAP);

        Bitmap bmp = getCache().get(result.getThumbnailUrl());
        String userId = result.getOwner().getId();
        if (bmp == null) {// nothing cached, start downloading
            holder.imgDownloader.execute(result.getThumbnailUrl(), userId);
        } else {
            holder.thumbnail.setImageBitmap(bmp);
        }

        return convertView;
    }

    /**
     * Holder class for optimization
     */
    private class Holder {

        private ImageView thumbnail;

        private DownloadThumbnailTask imgDownloader;

        public Holder(ImageView thumbnail, DownloadThumbnailTask imgDownloader) {
            super();

            this.thumbnail = thumbnail;
            this.imgDownloader = imgDownloader;

        }
    }
}
