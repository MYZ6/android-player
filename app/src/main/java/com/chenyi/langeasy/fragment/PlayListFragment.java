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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.chenyi.langeasy.R;
import com.chenyi.langeasy.Utilities;
import com.chenyi.langeasy.list.SentenceAdapter;
import com.chenyi.langeasy.activity.MainNewActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;

public class PlayListFragment extends ListFragment {

    private ArrayList<Map<String, Object>> songsListData;
    private ArrayList<Map<String, Object>> originalData;

    private SentenceAdapter sentenceAdapter;
    private EditText search_text;
    private ListView mListView;
    private int sortType = 1;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnSentenceSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSentenceSelectedListener");
        }


        final Context applicationContext = activity.getApplicationContext();
        // selecting single ListView item
        mListView = getListView();
        // listening to single listitem click
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Map<String, Object> sentence = sentenceAdapter.getItem(position);
                Integer index = (Integer) sentence.get("index");

                mCallback.onSentenceSelected(index);
            }
        });

    }

    MainNewActivity.PlayListResetCallback prCallback;
    boolean reset = false;

    public void reset(MainNewActivity.PlayListResetCallback prCallback) {
        this.prCallback = prCallback;
        reset = true;
        search_text.setText("");
    }

    public interface AdapterCallback {
        void filterFinished();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View listLayout = inflater.inflate(R.layout.playlist,
                container, false);

        search_text = (EditText) listLayout.findViewById(R.id.search_text);

        search_text.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                // you can call or do what you want with your EditText here
//                s.toString();
                sentenceAdapter.getFilter().filter(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        final MainNewActivity activity = (MainNewActivity) getActivity();
//        songsListData = new ArrayList<Map<String, Object>>(activity.songsList);
//        originalData = activity.songsList;
        songsListData = activity.songsList;
        ;//songManager.getPlayList(mydb);

        final boolean[] init = {false};
        AdapterCallback adapterCallback = new AdapterCallback() {
            @Override
            public void filterFinished() {
                if (init[0]) {
                    activity.setTabSelection(3);// waiting playlist filter finished
                    init[0] = false;
                } else if (reset) {
                    prCallback.afterReset();
                    reset = false;
                }
            }
        };


        TextView vSize = (TextView) listLayout.findViewById(R.id.history_size_val);
        vSize.setText(songsListData.size() + "");
        // Adding menuItems to ListView
        sentenceAdapter = new SentenceAdapter(activity, listLayout, adapterCallback, songsListData);
//        ListAdapter adapter = new SimpleAdapter(this, songsListData,
//                R.layout.playlist_item, new String[]{"wordunique"}, new int[]{
//                R.id.songTitle});

        setListAdapter(sentenceAdapter);
//        adapter.no

        String filter = Utilities.getConfig(getActivity(), "playlist-filter");
        if (!"0".equals(filter)) {
            init[0] = true;
            if (filter.startsWith("qid:")) {// filter by queue
                String queueId = filter.substring(4);
                loadQueueRecord(Integer.parseInt(queueId));
            }
            search_text.setText(filter);
            Log.i("filter", filter + " ");
        }

        Button bSort = (Button) listLayout.findViewById(R.id.btn_sort);
        bSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentenceAdapter.sort(new Comparator<Map<String, Object>>() {
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

        CheckBox bCritical50 = (CheckBox) listLayout.findViewById(R.id.btn_critical50);
        bCritical50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByCount(((CheckBox) v).isChecked(), "50");
            }
        });
        CheckBox bCritical30 = (CheckBox) listLayout.findViewById(R.id.btn_critical30);
        bCritical30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByCount(((CheckBox) v).isChecked(), "30");
            }
        });
        CheckBox bCritical20 = (CheckBox) listLayout.findViewById(R.id.btn_critical20);
        bCritical20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByCount(((CheckBox) v).isChecked(), "20");
            }
        });
        CheckBox bCritical10 = (CheckBox) listLayout.findViewById(R.id.btn_critical10);
        bCritical10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByCount(((CheckBox) v).isChecked(), "10");
            }
        });
        CheckBox bCritical0 = (CheckBox) listLayout.findViewById(R.id.btn_critical0);
        bCritical0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByCount(((CheckBox) v).isChecked(), "0");
            }
        });
        return listLayout;
    }

    private void filterByCount(boolean checked, String type) {
        if (checked) {
            search_text.setText("[critical" + type +
                    "]");
        } else {
            String oldValue = search_text.getText() + "";
            if (!"".equals(oldValue)) {
                search_text.setText("");
            }
        }
    }

    private void loadQueueRecord(Integer queueId) {
        MainNewActivity activity = (MainNewActivity) getActivity();
        ArrayList<Map<String, Object>> recordLlist = activity.getDBHelper().queryQueueRecord(queueId);
        ArrayList<Integer> sidList = new ArrayList<>();
        for (Map<String, Object> record : recordLlist) {
            sidList.add((Integer) record.get("sentenceid"));
        }
        sentenceAdapter.sentenceIdList = sidList;
    }

    OnSentenceSelectedListener mCallback;

    public void query(String condition) {
        if (condition.startsWith("qid:")) {// filter by queue
            String queueId = condition.substring(4);
            loadQueueRecord(Integer.parseInt(queueId));
        }
        search_text.setText(condition);
    }

    public void remember() {
        Utilities.setConfig(getActivity(), "playlist-filter", search_text.getText() + "");
    }

    // Container Activity must implement this interface
    public interface OnSentenceSelectedListener {
        public void onSentenceSelected(int songIndex);
    }

    public void jump(int index) {
        Map<String, Object> map = songsListData.get(index);
//        int sid = (int) map.get("sentenceid");

//        search_text.setText(sid + "");
//        search_text.setText("");
//        if (!search_text.getText().toString().isEmpty()) {
//            search_text.setText("");
//            final int findex = index;
//            mListView.post(new Runnable() {
//                public void run() {
//                    mListView.setSelection(findex);
//                }
//            });
//        } else {
        mListView.setSelection(index);
//        }
//        sentenceAdapter.getFilter().filter(sid+"");
//        mListView.clearFocus();
//        sentenceAdapter.notifyDataSetChanged();
//        mListView.requestFocus();
//        mListView.setItemChecked(index, true);

        Random generator = new Random();
        int d = generator.nextInt(6) + 6;
//        if(d<8){
//            mListView.setSelection(698);
//        }else{
//            mListView.setSelection(1698);
//        }
        Log.i("index d", d + "");
//        mListView.scrollTo(30, index * 30);
        Log.i("index", index + "");
//        getListView().smoothScrollToPosition(index);
    }
}
