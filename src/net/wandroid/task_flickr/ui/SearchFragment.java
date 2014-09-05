
package net.wandroid.task_flickr.ui;

import net.wandroid.task_flikr.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class SearchFragment extends Fragment implements OnClickListener {
    private ISearchViewListener mSearchViewListener=ISearchViewListener.NO_LISTENER;

    private EditText mSearchEditText;

    private Button mSearchButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_view, container, false);
        mSearchButton = (Button)view.findViewById(R.id.search_view_button);
        mSearchButton.setOnClickListener(this);
        mSearchEditText = (EditText)view.findViewById(R.id.search_edit_text);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof ISearchViewListener){
            mSearchViewListener=(ISearchViewListener)activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSearchViewListener=ISearchViewListener.NO_LISTENER;
    }

    @Override
    public void onClick(View view) {
        if(TextUtils.isEmpty(mSearchEditText.getText())) {// no empty strings
            return;
        }
        // hide keyboard
        InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        mSearchViewListener.onSearchButtonClick(mSearchEditText.getText().toString());
    }

    /**
     * Interface for searchview listeners
     */
    public interface ISearchViewListener{
        /**
         * Called when search button is clicked and there is search text
         * @param searchText the search text, cannot be empty or null
         */
        void onSearchButtonClick(String searchText);
        // null object pattern to avoid flood of null checks
        public static final ISearchViewListener NO_LISTENER=new ISearchViewListener() {
            @Override
            public void onSearchButtonClick(String searchText) {
            }
        };
    }

}
