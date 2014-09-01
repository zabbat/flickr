
package net.wandroid.task_flickr.ui;

import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.people.PeopleInterface;
import com.googlecode.flickrjandroid.people.User;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Class describing name loook up
 */
public class NameLookup {

    //Cache for the names
    private ConcurrentMap<String, String> mUserNames = new ConcurrentHashMap<String, String>();

    private PeopleInterface mPeople;

    /**
     * Constructor
     * @param people flickr people interface to use for the lookup
     */
    public NameLookup(PeopleInterface people) {
        mPeople = people;
    }

    /**
     * returns a user name connected to the id, or null if there was an error or no user name.
     * This method will cache, that means that results can be outdated.
     * @param id the id to look up
     * @return the user name or null if no user name could be found
     * @throws IOException
     * @throws FlickrException
     * @throws JSONException
     */
    public String IdToUserName(String id) throws IOException, FlickrException, JSONException {

        if (mUserNames.containsKey(id)) {
            return mUserNames.get(id);
        }
        User user = mPeople.getInfo(id);
        if (user == null) {
            return null;
        }
        mUserNames.put(id, user.getUsername());
        return user.getUsername();
    }

    public String nameFromCache(String userId) {
        // TODO Auto-generated method stub
        return mUserNames.get(userId);
    }

}
