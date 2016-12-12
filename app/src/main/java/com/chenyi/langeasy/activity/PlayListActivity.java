package com.chenyi.langeasy.activity;

import java.util.ArrayList;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.chenyi.langeasy.R;
import com.chenyi.langeasy.SentenceAdapter;
import com.chenyi.langeasy.SongsManager;
import com.chenyi.langeasy.db.DBHelper;

public class PlayListActivity extends ListActivity {

    private DBHelper mydb;

    private ArrayList<Map<String, Object>> songsListData;

    private SentenceAdapter sentenceAdapter;

    private EditText search_text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);


        mydb = new DBHelper(this);


        SongsManager plm = new SongsManager();
        // get all songs from sdcard
        songsListData = plm.getPlayList(mydb);


        // Adding menuItems to ListView
        sentenceAdapter = new SentenceAdapter(this, songsListData);
//        ListAdapter adapter = new SimpleAdapter(this, songsListData,
//                R.layout.playlist_item, new String[]{"wordunique"}, new int[]{
//                R.id.songTitle});

        setListAdapter(sentenceAdapter);
//        adapter.no

        // selecting single ListView item
        ListView lv = getListView();
        // listening to single listitem click
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Map<String, Object> sentence = sentenceAdapter.getItem(position);
                Integer index = (Integer)sentence.get("index");

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        MusicPlayerOldActivity.class);
                // Sending songIndex to PlayerActivity
                in.putExtra("songIndex", index);
                setResult(100, in);
                // Closing PlayListView
                finish();
            }
        });

         search_text = (EditText) findViewById(R.id.search_text);

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

    public void jump(int index) {
        search_text.setText("");
        getListView().smoothScrollToPosition(index);
    }
}
