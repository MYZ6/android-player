package com.chenyi.langeasy.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chenyi.langeasy.R;
import com.chenyi.langeasy.activity.MainNewActivity;
import com.chenyi.langeasy.db.SentenceAudio;

public class SettingFragment extends Fragment {

    private SentenceAudio sentenceAudio;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View settingLayout = inflater.inflate(R.layout.setting, container,
                false);

        Button sync_saudio = (Button) settingLayout.findViewById(R.id.btn_sync_saudio);
        Button btnShare = (Button) settingLayout.findViewById(R.id.btn_share_bak);

        final MainNewActivity activity = (MainNewActivity) getActivity();
        sentenceAudio = new SentenceAudio(activity);
        final Context applicationContext = activity.getApplicationContext();
        sync_saudio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sentenceAudio.syncList();
                Toast.makeText(applicationContext, "sync_saudio test", Toast.LENGTH_SHORT).show();
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                activity.showShare();
            }
        });
        return settingLayout;
    }

}
