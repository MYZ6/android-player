package com.chenyi.langeasy.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.chenyi.langeasy.R;
import com.chenyi.langeasy.activity.MainNewActivity;
import com.chenyi.langeasy.list.BookAdapter;
import com.chenyi.langeasy.list.CourseAdapter;
import com.chenyi.langeasy.listener.ButtonPlayListListener;

import java.util.ArrayList;
import java.util.Map;

public class CourseListFragment extends ListFragment {

    private CourseAdapter courseAdapter;
    private EditText search_text;
    private ListView mListView;
    private ButtonPlayListListener btnPlayListListener;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();
        btnPlayListListener = (ButtonPlayListListener) activity;

        // selecting single ListView item
        mListView = getListView();
        // listening to single listitem click
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Map<String, Object> book = courseAdapter.getItem(position);
                Integer index = (Integer) book.get("index");

//                mCallback.onBookSelected(index);

                String courseid = (String) book.get("courseid");
                btnPlayListListener.query("c:" + courseid);
            }
        });
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
                courseAdapter.getFilter().filter(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        final MainNewActivity activity = (MainNewActivity) getActivity();
        // Adding menuItems to ListView
        courseAdapter = new CourseAdapter(activity, new ArrayList<Map<String, Object>>());

        setListAdapter(courseAdapter);

        return listLayout;
    }


    public void query(String condition) {
        search_text.setText(condition);
    }


    public void load(String bookid) {
        final MainNewActivity activity = (MainNewActivity) getActivity();
        ArrayList<Map<String, Object>> courselistData = activity.getDBHelper().listCourse(bookid);
        courseAdapter.clear();
        courseAdapter.addAll(courselistData);
        courseAdapter.notifyDataSetChanged();
    }
}
