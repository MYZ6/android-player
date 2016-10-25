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
import com.chenyi.langeasy.SentenceAdapter;
import com.chenyi.langeasy.SongsManager;
import com.chenyi.langeasy.activity.MainActivity;
import com.chenyi.langeasy.db.DBHelper;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class PlayListFragment extends ListFragment {

    private ArrayList<Map<String, Object>> songsListData;
    private ArrayList<Map<String, Object>> originalData;

    private SentenceAdapter sentenceAdapter;
    private EditText search_text;
    private ListView mListView;

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


        search_text = (EditText) activity.findViewById(R.id.search_text);

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View listLayout = inflater.inflate(R.layout.playlist,
                container, false);

        final MainActivity activity = (MainActivity) getActivity();
//        songsListData = new ArrayList<Map<String, Object>>(activity.songsList);
//        originalData = activity.songsList;
        songsListData = activity.songsList;
        ;//songManager.getPlayList(mydb);


        // Adding menuItems to ListView
        sentenceAdapter = new SentenceAdapter(activity, songsListData);
//        ListAdapter adapter = new SimpleAdapter(this, songsListData,
//                R.layout.playlist_item, new String[]{"wordunique"}, new int[]{
//                R.id.songTitle});

        setListAdapter(sentenceAdapter);
//        adapter.no

        return listLayout;
    }


    OnSentenceSelectedListener mCallback;

    public void query(String condition) {
        search_text.setText(condition);
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
