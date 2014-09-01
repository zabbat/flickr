
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

    private String mUserName;

    public SearchResult(final Photo photo, String userName) {
        mTitle = photo.getTitle();
        mUserId = photo.getOwner().getId();
        mThumbnailAddress = photo.getThumbnailUrl();
        mUserName = userName;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getThumbnailAddress() {
        return mThumbnailAddress;
    }

}
