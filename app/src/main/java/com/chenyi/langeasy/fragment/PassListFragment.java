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
import com.chenyi.langeasy.Utilities;
import com.chenyi.langeasy.activity.MainNewActivity;
import com.chenyi.langeasy.list.SentenceAdapter;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class PassListFragment extends ListFragment {

    private ArrayList<Map<String, Object>> songsListData;

    private SentenceAdapter sentenceAdapter;
    private EditText search_text;
    private ListView mListView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();

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

            }
        });

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
        songsListData = activity.getDBHelper().listSentence("passed");

        // Adding menuItems to ListView
        sentenceAdapter = new SentenceAdapter(activity, listLayout, songsListData);

        setListAdapter(sentenceAdapter);
//        adapter.no

        return listLayout;
    }
}
