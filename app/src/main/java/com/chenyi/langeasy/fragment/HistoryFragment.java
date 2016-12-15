package com.chenyi.langeasy.fragment;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.chenyi.langeasy.R;
import com.chenyi.langeasy.activity.MainNewActivity;
import com.chenyi.langeasy.list.HistoryAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class HistoryFragment extends ListFragment {

    private ArrayList<Map<String, Object>> historyListData;

    private HistoryAdapter historyAdapter;
    private EditText search_text;
    private ListView mListView;
    private int dataType = 1;
    private int sortType = 1;
//    private boolean filterCritical = false;
//    private FragmentExchangeListener btnPlayListListener;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // selecting single ListView item
        mListView = getListView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View listLayout = inflater.inflate(R.layout.history,
                container, false);
        final MainNewActivity activity = (MainNewActivity) getActivity();

        CheckBox bWord = (CheckBox) listLayout.findViewById(R.id.btn_word);
        bWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    dataType = 2;
                    Log.i("checked", "true" + "");
                } else {
                    dataType = 1;
                }
            }
        });


        Button bRefresh = (Button) listLayout.findViewById(R.id.btn_refresh);
        bRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyListData = activity.getDBHelper().history(dataType);
                historyAdapter.clear();
                historyAdapter.addAll(historyListData);
                historyAdapter.notifyDataSetChanged();
            }
        });
        Button bSort = (Button) listLayout.findViewById(R.id.btn_sort);
        bSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyAdapter.sort(new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> record1, Map<String, Object> record2) {
                        Integer scount1 = (Integer) record1.get("scount");
                        Integer scount2 = (Integer) record2.get("scount");
                        int result = scount1.compareTo(scount2);
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

//        search_text = (EditText) listLayout.findViewById(R.id.book_search_text);
        search_text = (EditText) listLayout.findViewById(R.id.search_text);

        search_text.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                // you can call or do what you want with your EditText here
//                s.toString();
                historyAdapter.getFilter().filter(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        CheckBox bCritical = (CheckBox) listLayout.findViewById(R.id.btn_critical);
        bCritical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    search_text.setText("[critical30]");
                } else {
                    search_text.setText("");
                }
            }
        });
        CheckBox bCritical20 = (CheckBox) listLayout.findViewById(R.id.btn_critical20);
        bCritical20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    search_text.setText("[critical20]");
                } else {
                    search_text.setText("");
                }
            }
        });
        CheckBox bCritical10 = (CheckBox) listLayout.findViewById(R.id.btn_critical10);
        bCritical10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    search_text.setText("[critical10]");
                } else {
                    search_text.setText("");
                }
            }
        });
        Button bQueue = (Button) listLayout.findViewById(R.id.btn_addqueue);

        final Context applicationContext = activity.getApplicationContext();
        bQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> sidList = new ArrayList<Integer>();
                for (Map<String, Object> record : historyListData) {
                    sidList.add((Integer) record.get("sentenceid"));
                }
                activity.getDBHelper().addQueue(sidList);

                Toast.makeText(applicationContext, "Add Queue Success!", Toast.LENGTH_SHORT).show();
            }
        });


        historyListData = activity.getDBHelper().history(dataType);

        // Adding menuItems to ListView
        historyAdapter = new HistoryAdapter(activity, historyListData);

        setListAdapter(historyAdapter);

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
        Map<String, Object> map = historyListData.get(index);
        mListView.setSelection(index);
        Log.i("index", index + "");
    }
}
