package com.androidhive.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

/**
 * Created by liyzh on 2016/9/8.
 */
public class MusicIntentReceiver extends android.content.BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (intent.getAction().equals(
                android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            // signal your service to stop playback
            // (via an Intent, for instance)

        }
    }
}
