
package net.wandroid.task_flickr.ui;

import net.wandroid.task_flikr.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * fragment for a user image displaying grid
 */
public class UserImageGridFragment extends Fragment {

    private GridView mGridView;
    private ArrayAdapter<String> mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_images_grid, container, false);
        mGridView = (GridView)view.findViewById(R.id.user_images_gridview);

        if(mAdapter==null){
            mAdapter = new UserImageAdapter(getActivity(), R.layout.user_image_item);
        }
        mGridView.setAdapter(mAdapter);

        // do not kill the fragment onConfigurationChanged
        setRetainInstance(true);
        return view;
    }

    /**
     * Sets the GridView's list
     *
     * @param list the list to set the adapter with. Should contains strings to
     *            thumbnail urls
     */
    public void setGridList(ArrayList<String> list) {
        mAdapter.addAll(list);
    }

    public boolean isEmpty(){
        return mAdapter.isEmpty();
    }

}
