package com.chenyi.langeasy.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chenyi.langeasy.R;
import com.chenyi.langeasy.db.SentenceAudio;

public class MoreFragment extends Fragment {

    private SentenceAudio sentenceAudio;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View settingLayout = inflater.inflate(R.layout.setting, container,
                false);

        TextView btnFilterByBook = (TextView) settingLayout.findViewById(R.id.btn_filter_by_book);
        TextView sync_saudio = (TextView) settingLayout.findViewById(R.id.sync_saudio);

        Activity activity = getActivity();
        sentenceAudio = new SentenceAudio(activity);
        final Context applicationContext = activity.getApplicationContext();
        btnFilterByBook.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
            }
        });
        sync_saudio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sentenceAudio.syncList();
                Toast.makeText(applicationContext, "sync_saudio test", Toast.LENGTH_SHORT).show();

            }
        });
        return settingLayout;
    }

}
