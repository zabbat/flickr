
package net.wandroid.task_flickr.ui;

import com.googlecode.flickrjandroid.photos.Photo;

/**
 * Class describing a search result with flickr api
 *
 */
public class SearchResult {

    private String mTitle;

    private String mUserId;

    private String mThumbnailAddress;

    public SearchResult(final Photo photo) {
        mTitle = photo.getTitle();
        mUserId = photo.getOwner().getId();
        mThumbnailAddress = photo.getThumbnailUrl();
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getThumbnailAddress() {
        return mThumbnailAddress;
    }

}
