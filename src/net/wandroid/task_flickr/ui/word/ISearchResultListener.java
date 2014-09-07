package net.wandroid.task_flickr.ui.word;

import com.googlecode.flickrjandroid.photos.Photo;

/**
 * Interface for listening to a list click.
 */
public interface ISearchResultListener {
    public void itemClicked(Photo result);

    public void onScrolledCloseToBottom();
}