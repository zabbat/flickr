
package net.wandroid.task_flickr.ui.word;

import com.googlecode.flickrjandroid.photos.Photo;

import net.wandroid.task_flikr.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

/**
 * Adapter for a flickr search result list
 */
public abstract class SearchResultAdapter extends ArrayAdapter<Photo> {
    private static final int PART_MEM_FOR_CACHE = 4;

    private static final int sMaxMemory = (int)(Runtime.getRuntime().maxMemory());

    private static LruCache<String, Bitmap> sThumbnailLru = new LruCache<String, Bitmap>(sMaxMemory
            / PART_MEM_FOR_CACHE){
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    protected final Bitmap DEFAULT_BITMAP;

    private LayoutInflater mInflater;

    public SearchResultAdapter(Context context, int resource) {
        super(context, resource);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // note that these resources might not be properly changed on an
        // onConfigurationChange
        Resources res = getContext().getResources();

        DEFAULT_BITMAP = BitmapFactory.decodeResource(res,
                R.drawable.unknown);

    }

    protected LayoutInflater getInflater(){
        return mInflater;
    }

    protected LruCache<String, Bitmap> getCache(){
        return sThumbnailLru;
    }

}
