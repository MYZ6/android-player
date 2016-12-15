package com.chenyi.langeasy.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.chenyi.langeasy.R;
import com.chenyi.langeasy.activity.MainNewActivity;
import com.chenyi.langeasy.list.QueueAdapter;
import com.chenyi.langeasy.list.QueueRecordAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class QueueRecordFragment extends ListFragment {

    private QueueRecordAdapter queueRecordAdapter;
    private EditText search_text;
    private ListView mListView;
    private View listLayout;
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
        listLayout = inflater.inflate(R.layout.queue_record,
                container, false);
        final MainNewActivity activity = (MainNewActivity) getActivity();

        // Adding menuItems to ListView
        queueRecordAdapter = new QueueRecordAdapter(activity, new ArrayList<Map<String, Object>>());

        Button bSort = (Button) listLayout.findViewById(R.id.btn_sort);
        bSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queueRecordAdapter.sort(new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> record1, Map<String, Object> record2) {
                        Integer i1 = (Integer) record1.get("index");
                        Integer i2 = (Integer) record2.get("index");
                        int result = i1.compareTo(i2);
                        if (sortType == 1) {
                            return result;
                        } else {
                            return -result;
                        }
                    }
                });
                if (sortType == 1) {
                    sortType = 2;
                } else {
                    sortType = 1;
                }
            }
        });

        setListAdapter(queueRecordAdapter);

        return listLayout;
    }

    public void load(Integer queueId) {
        final MainNewActivity activity = (MainNewActivity) getActivity();
        ArrayList<Map<String, Object>> recordLlist = activity.getDBHelper().queryQueueRecord(queueId);
        queueRecordAdapter.clear();
        queueRecordAdapter.addAll(recordLlist);
        queueRecordAdapter.notifyDataSetChanged();

        TextView vSize = (TextView) listLayout.findViewById(R.id.history_size_val);
        vSize.setText(recordLlist.size() + "");
    }
}
