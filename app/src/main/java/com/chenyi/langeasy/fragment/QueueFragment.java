package com.chenyi.langeasy.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.chenyi.langeasy.R;
import com.chenyi.langeasy.activity.MainNewActivity;
import com.chenyi.langeasy.list.HistoryAdapter;
import com.chenyi.langeasy.list.QueueAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class QueueFragment extends ListFragment {

    private ArrayList<Map<String, Object>> queueListData;

    private QueueAdapter queueAdapter;
    private EditText search_text;
    private ListView mListView;
    private int dataType = 1;
    private int sortType = 1;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // selecting single ListView item
        mListView = getListView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View listLayout = inflater.inflate(R.layout.queue,
                container, false);
        final MainNewActivity activity = (MainNewActivity) getActivity();

        queueListData = activity.getDBHelper().queryQueue();

        // Adding menuItems to ListView
        queueAdapter = new QueueAdapter(activity, listLayout, queueListData);

        setListAdapter(queueAdapter);

        Button bRefresh = (Button) listLayout.findViewById(R.id.btn_refresh);
        bRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queueListData = activity.getDBHelper().queryQueue();
                queueAdapter.clear();
                queueAdapter.addAll(queueListData);
                queueAdapter.notifyDataSetChanged();
            }
        });

        return listLayout;
    }

}
