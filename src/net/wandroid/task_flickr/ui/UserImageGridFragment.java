
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_images_grid, container, false);
        mGridView = (GridView)view.findViewById(R.id.user_images_gridview);

        ArrayList<String> list = new ArrayList<String>();
        ArrayAdapter<String> adapter = new UserImageAdapter(getActivity(), R.layout.user_image_item);
        adapter.addAll(list);
        mGridView.setAdapter(adapter);

        // do not kill the fragment onConfigurationChanged, just relayout
        // this view will contain a lot of images
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
        UserImageAdapter adapter = (UserImageAdapter)mGridView.getAdapter();
        adapter.addAll(list);
    }

}
