package com.chenyi.langeasy.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.chenyi.langeasy.R;
import com.chenyi.langeasy.list.BookAdapter;
import com.chenyi.langeasy.list.SentenceAdapter;
import com.chenyi.langeasy.activity.MainNewActivity;
import com.chenyi.langeasy.listener.ButtonPlayListListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class BookListFragment extends ListFragment {

    private ArrayList<Map<String, Object>> booklistData;

    private BookAdapter bookAdapter;
    private EditText search_text;
    private ListView mListView;
//    private ButtonPlayListListener btnPlayListListener;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        final Activity activity = getActivity();
//        btnPlayListListener = (ButtonPlayListListener) activity;

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
//        try {
//            mCallback = (OnItemSelectedListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement BookListFragment OnItemSelectedListener");
//        }
//
//
//        final Context applicationContext = activity.getApplicationContext();

//        mListView = (ListView) activity.findViewById(R.id.blist);

        // selecting single ListView item
        mListView = getListView();
        // listening to single listitem click
//        mListView.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                Map<String, Object> book = bookAdapter.getItem(position);
//                Integer index = (Integer) book.get("index");
//                String bookid = (String) book.get("bookid");
//
////                mCallback.onBookSelected(bookid);
//
//                btnPlayListListener.query("b:" + bookid);
//            }
//        });
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
                bookAdapter.getFilter().filter(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        final MainNewActivity activity = (MainNewActivity) getActivity();
        booklistData = activity.getDBHelper().listBook();

        // Adding menuItems to ListView
        bookAdapter = new BookAdapter(activity, booklistData);

        setListAdapter(bookAdapter);

        return listLayout;
    }


//    OnItemSelectedListener mCallback;

    public void query(String condition) {
        search_text.setText(condition);
    }

    // Container Activity must implement this interface
    public interface OnItemSelectedListener {
        public void onBookSelected(String bookid);
    }

    public void jump(int index) {
        Map<String, Object> map = booklistData.get(index);
        mListView.setSelection(index);
        Log.i("index", index + "");
    }
}
