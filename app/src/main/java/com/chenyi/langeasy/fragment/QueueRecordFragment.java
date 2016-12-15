package com.chenyi.langeasy.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.chenyi.langeasy.R;
import com.chenyi.langeasy.activity.MainNewActivity;
import com.chenyi.langeasy.list.QueueAdapter;
import com.chenyi.langeasy.list.QueueRecordAdapter;

import java.util.ArrayList;
import java.util.Map;

public class QueueRecordFragment extends ListFragment {

    private QueueRecordAdapter queueRecordAdapter;
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

        // Adding menuItems to ListView
        queueRecordAdapter = new QueueRecordAdapter(activity, new ArrayList<Map<String, Object>>());

        setListAdapter(queueRecordAdapter);

        return listLayout;
    }

    public void load(Integer queueId) {
        final MainNewActivity activity = (MainNewActivity) getActivity();
        ArrayList<Map<String, Object>> recordLlist = activity.getDBHelper().queryQueueRecord(queueId);
        queueRecordAdapter.clear();
        queueRecordAdapter.addAll(recordLlist);
        queueRecordAdapter.notifyDataSetChanged();
    }
}
