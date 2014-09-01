
package net.wandroid.task_flickr.ui;

import net.wandroid.task_flikr.R;

import org.apache.http.client.HttpResponseException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A class describing an async image downloader
 */
public class DownloadThumbnailTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView mImageView;

    private LruCache<String, Bitmap> mLruCache;

    private Context mContext;

    /**
     * Constructor
     *
     * @param image imageview that will be updated with the downloaded image
     * @param context context
     * @param lruCache cache to look up and store the downloaded result in
     */
    public DownloadThumbnailTask(ImageView image, Context context, LruCache<String, Bitmap> lruCache) {
        super();
        this.mImageView = image;
        this.mContext = context;
        this.mLruCache = lruCache;
    }

    @Override
    protected Bitmap doInBackground(String... address) {
        Bitmap bmp = null;
        try {
            bmp = downloadImage(address[0]);
        } catch (HttpResponseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (bmp == null) {// error, could not load, use error image
            bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.error);
        } else {
            // Put in cache.
            mLruCache.put(address[0], bmp);
        }
        return bmp;
    }

    @Override
    protected void onPostExecute(Bitmap bmp) {
        super.onPostExecute(bmp);
        //update the image view
        mImageView.setImageBitmap(bmp);
    }

    /**
     * Downloads the image.
     * Uses network, must not be called on main thread
     *
     * @param address address to the image
     * @return the downloaded bitmap, or null if image could not be downloaded
     * @throws MalformedURLException
     * @throws HttpResponseException
     * @throws IOException
     */
    private Bitmap downloadImage(String address) throws MalformedURLException,
            HttpResponseException, IOException {
        URL url = new URL(address);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setDoInput(true);
        InputStream is = null;
        try {
            connection.connect();
            int statusCode = connection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();
                Bitmap bmp = BitmapFactory.decodeStream(is);
                return bmp;
            } else {
                throw new HttpResponseException(statusCode,
                        "Response not OK, failed to download thumbnail");
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
