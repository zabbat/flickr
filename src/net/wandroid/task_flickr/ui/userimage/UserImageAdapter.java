
package net.wandroid.task_flickr.ui.userimage;

import net.wandroid.task_flickr.ui.DownloadThumbnailTask;
import net.wandroid.task_flikr.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter for user image
 */
public class UserImageAdapter extends ArrayAdapter<String> {
    private static final int PART_MEM_FOR_CACHE = 4;

    private static final int IMAGE_NAME_LENGTH = 12;

    private static final int sMaxMemory = (int)(Runtime.getRuntime().maxMemory());

    private final Bitmap DEFAULT_BITMAP;

    private static LruCache<String, Bitmap> sLruCache = new LruCache<String, Bitmap>(sMaxMemory
            / PART_MEM_FOR_CACHE){
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    private LayoutInflater mInflater;

    public UserImageAdapter(Context context, int resource) {
        super(context, resource);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DEFAULT_BITMAP = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.ic_launcher);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.user_image_item, parent, false);
            ImageView image = (ImageView)convertView.findViewById(R.id.user_image_item_image);
            TextView text = (TextView)convertView.findViewById(R.id.user_image_item_text);
            holder = new Holder(image, text, new DownloadThumbnailTask(image, getContext(),
                    sLruCache));
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
            // the view is reused, so there's no point in finishing the download
            holder.downloader.cancel(true);
            // create new image downloader task for this view
            holder.downloader = new DownloadThumbnailTask(holder.image, getContext(), sLruCache);
        }

        holder.image.setImageBitmap(DEFAULT_BITMAP);

        //set the image name
        String address = getItem(position);
        int size = address.length() - IMAGE_NAME_LENGTH;
        if (size > 0) {
            holder.text.setText(address.substring(size));
        } else {
            holder.text.setText(address);
        }

        Bitmap bmp = sLruCache.get(address);
        if (bmp == null) {// nothing cached, download it
            holder.downloader.execute(address);
        } else {
            holder.image.setImageBitmap(bmp);
        }

        return convertView;
    }

    /**
     * Holder class for optimization
     */
    private static class Holder {
        private ImageView image;
        private TextView text;
        private DownloadThumbnailTask downloader;

        public Holder(ImageView image, TextView text, DownloadThumbnailTask downloader) {
            super();
            this.image = image;
            this.text = text;
            this.downloader = downloader;
        }

    }

}
