package com.chenyi.langeasy;

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

import java.util.ArrayList;
import java.util.Map;

public class PlayListFragment extends ListFragment {

    private DBHelper mydb;

    private ArrayList<Map<String, Object>> songsListData;

    private SentenceAdapter sentenceAdapter;

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
        ListView lv = getListView();
        // listening to single listitem click
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Map<String, Object> sentence = sentenceAdapter.getItem(position);
                Integer index = (Integer) sentence.get("index");

                mCallback.onSentenceSelected(index);
            }
        });


        EditText search_text = (EditText) activity.findViewById(R.id.search_text);

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
        final Activity activity = getActivity();

        mydb = new DBHelper(activity);


        SongsManager plm = new SongsManager();
        // get all songs from sdcard
        songsListData = plm.getPlayList(mydb);


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

    // Container Activity must implement this interface
    public interface OnSentenceSelectedListener {
        public void onSentenceSelected(int songIndex);
    }
}
