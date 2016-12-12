package com.chenyi.langeasy.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.chenyi.langeasy.R;
import com.chenyi.langeasy.activity.MainNewActivity;
import com.chenyi.langeasy.list.BookAdapter;
import com.chenyi.langeasy.list.BooktypeAdapter;

import java.util.ArrayList;
import java.util.Map;

public class BookTypeListFragment extends ListFragment {

    private ArrayList<Map<String, Object>> booktypeListData;

    private BooktypeAdapter booktypeAdapter;
    private EditText search_text;
    private ListView mListView;
//    private ButtonPlayListListener btnPlayListListener;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // selecting single ListView item
        mListView = getListView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View listLayout = inflater.inflate(R.layout.playlist,
                container, false);

//        search_text = (EditText) listLayout.findViewById(R.id.book_search_text);
        search_text = (EditText) listLayout.findViewById(R.id.search_text);

        search_text.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                // you can call or do what you want with your EditText here
//                s.toString();
                booktypeAdapter.getFilter().filter(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        final MainNewActivity activity = (MainNewActivity) getActivity();
        booktypeListData = activity.getDBHelper().listBooktype();

        // Adding menuItems to ListView
        booktypeAdapter = new BooktypeAdapter(activity, booktypeListData);

        setListAdapter(booktypeAdapter);

        return listLayout;
    }


//    OnItemSelectedListener mCallback;

    public void query(String condition) {
        search_text.setText(condition);
    }

    // Container Activity must implement this interface
    public interface OnItemSelectedListener {
        public void onBooktypeSelected(String booktype);
    }

    public void jump(int index) {
        Map<String, Object> map = booktypeListData.get(index);
        mListView.setSelection(index);
        Log.i("index", index + "");
    }
}
