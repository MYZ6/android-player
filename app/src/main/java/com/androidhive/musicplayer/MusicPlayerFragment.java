package com.androidhive.musicplayer;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MusicPlayerFragment extends Fragment implements OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnPlaylist;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private SeekBar songProgressBar;
    private TextView songTitleLabel;
    private TextView pronLabel;
    private TextView meaningLabel;
    private TextView sentenceLabel;
    private TextView chineseLabel;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    // Media Player
    private MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    ;
    private SongsManager songManager;
    private Utilities utils;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private boolean isShuffle = true;
    private boolean isRepeat = false;
    /**
     * play pronunciation defaultly
     */
    private boolean isPron = true;
    /**
     * pronuncation played or sentence played
     */
    private String lastPlayedAudioType = "sentence";
    private List<Map<String, Object>> songsList = new ArrayList<>();

    private DBHelper mydb;
    private SentenceAudio sentenceAudio;
    private PronAudio pronAudio;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View playerLayout = inflater.inflate(R.layout.player,
                container, false);
        Activity activity = getActivity();
        mydb = new DBHelper(activity);
        sentenceAudio = new SentenceAudio(activity);
        pronAudio = new PronAudio(activity);

        // All player buttons
        btnPlay = (ImageButton) playerLayout.findViewById(R.id.btnPlay);
        btnForward = (ImageButton) playerLayout.findViewById(R.id.btnForward);
        btnBackward = (ImageButton) playerLayout.findViewById(R.id.btnBackward);
        btnNext = (ImageButton) playerLayout.findViewById(R.id.btnNext);
        btnPrevious = (ImageButton) playerLayout.findViewById(R.id.btnPrevious);
        btnPlaylist = (ImageButton) playerLayout.findViewById(R.id.btnPlaylist);
        btnRepeat = (ImageButton) playerLayout.findViewById(R.id.btnRepeat);
        btnShuffle = (ImageButton) playerLayout.findViewById(R.id.btnShuffle);
        songProgressBar = (SeekBar) playerLayout.findViewById(R.id.songProgressBar);
        songTitleLabel = (TextView) playerLayout.findViewById(R.id.songTitle);
        pronLabel = (TextView) playerLayout.findViewById(R.id.pron);
        meaningLabel = (TextView) playerLayout.findViewById(R.id.meaning);
        sentenceLabel = (TextView) playerLayout.findViewById(R.id.sentence);
        chineseLabel = (TextView) playerLayout.findViewById(R.id.chinese);
        songCurrentDurationLabel = (TextView) playerLayout.findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) playerLayout.findViewById(R.id.songTotalDurationLabel);

        // Mediaplayer
        mp = new MediaPlayer();
        songManager = new SongsManager();
        utils = new Utilities();


        IntentFilter actionFilter = new IntentFilter();
        actionFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        activity.registerReceiver(mIntentReceiver, actionFilter);

        // Listeners
        songProgressBar.setOnSeekBarChangeListener(this); // Important
        mp.setOnCompletionListener(this); // Important
        audioManage(activity);

        // Getting all songs list
        songsList = songManager.getPlayList(mydb);

        // By default play first song
        playSong(0);

        /**
         * Play button click event
         * plays a song and changes button to pause image
         * pauses a song and changes button to play image
         * */
        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (mp.isPlaying()) {
                    if (mp != null) {
                        mp.pause();
                        // Changing button image to play button
                        btnPlay.setImageResource(R.drawable.btn_play);
                    }
                } else {
                    // Resume song
                    if (mp != null) {
                        mp.start();
                        // Changing button image to pause button
                        btnPlay.setImageResource(R.drawable.btn_pause);
                    }
                }

            }
        });

        /**
         * Forward button click event
         * Forwards song specified seconds
         * */
        btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if (currentPosition + seekForwardTime <= mp.getDuration()) {
                    // forward song
                    mp.seekTo(currentPosition + seekForwardTime);
                } else {
                    // forward to end position
                    mp.seekTo(mp.getDuration());
                }
            }
        });

        /**
         * Backward button click event
         * Backward song to specified seconds
         * */
        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if (currentPosition - seekBackwardTime >= 0) {
                    // forward song
                    mp.seekTo(currentPosition - seekBackwardTime);
                } else {
                    // backward to starting position
                    mp.seekTo(0);
                }

            }
        });

        /**
         * Next button click event
         * Plays next song by taking currentSongIndex + 1
         * */
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check if next song is there or not
                if (currentSongIndex < (songsList.size() - 1)) {
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                } else {
                    // play first song
                    playSong(0);
                    currentSongIndex = 0;
                }

            }
        });

        /**
         * Back button click event
         * Plays previous song by currentSongIndex - 1
         * */
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (currentSongIndex > 0) {
                    playSong(currentSongIndex - 1);
                    currentSongIndex = currentSongIndex - 1;
                } else {
                    // play last song
                    playSong(songsList.size() - 1);
                    currentSongIndex = songsList.size() - 1;
                }

            }
        });
       final Context applicationContext = activity.getApplicationContext();
        /**
         * Button Click event for Repeat button
         * Enables repeat flag to true
         * */
        btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isRepeat) {
                    isRepeat = false;
                    Toast.makeText(applicationContext, "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                } else {
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(applicationContext, "Repeat is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isShuffle = false;
                    btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }
            }
        });

        /**
         * Button Click event for Shuffle button
         * Enables shuffle flag to true
         * */
        btnShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isShuffle) {
                    isShuffle = false;
                    Toast.makeText(applicationContext, "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                } else {
                    // make repeat to true
                    isShuffle = true;
                    Toast.makeText(applicationContext, "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }
            }
        });

        /**
         * Button Click event for Play list click event
         * Launches list activity which displays list of songs
         * */
        btnPlaylist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(applicationContext, PlayListActivity.class);
                startActivityForResult(i, 100);
            }
        });

        return playerLayout;
    }

    private void audioManage(Context mContext) {
        AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                String TAG = "AudioManager focusChange type";
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        Log.i(TAG, "AUDIOFOCUS_GAIN");
                        // Set volume level to desired levels
                        mp.start();
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                        Log.i(TAG, "AUDIOFOCUS_GAIN_TRANSIENT");
                        // You have audio focus for a short time

                        mp.start();
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                        Log.i(TAG, "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK");
                        // Play over existing audio
                        mp.start();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        Log.e(TAG, "AUDIOFOCUS_LOSS");
                        mp.pause();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        Log.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                        // Temporary loss of audio focus - expect to get it back - you can keep your resources around
                        mp.pause();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        Log.e(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                        // Lower the volume
                        break;
                }
            }
        };

        AudioManager am = (AudioManager) mContext
                .getSystemService(Context.AUDIO_SERVICE);
        // Request audio focus for play back
        int result = am.requestAudioFocus(mOnAudioFocusChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//            mAudioFocusGranted = true;
        } else if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
            // take appropriate action
        }
    }

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(
                    AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                // signal your service to stop playback
                // (via an Intent, for instance)
                Log.i("action", "ACTION_AUDIO_BECOMING_NOISY--lyz");
                mp.pause();
            }
        }
    };

    /**
     * Receiving song index from playlist view
     * and play the song
     */
    @Override
    public void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            currentSongIndex = data.getExtras().getInt("songIndex");
            // play selected song
            playSong(currentSongIndex);
        }

    }

    /**
     * Function to play a song
     *
     * @param songIndex - index of song
     */
    public void playSong(int songIndex) {
        Map<String, Object> song = songsList.get(songIndex);
        currentSongIndex = songIndex;

        Log.i("song", song.toString() + "");
        Log.i("isPron", isPron + "");
        Log.i("lastPlayedAudioType", lastPlayedAudioType + "");
        if (isPron) {
            if ("sentence".equals(lastPlayedAudioType)) {
                setPlayInfo(song);
                playPron(song);
                lastPlayedAudioType = "pron";
            } else {
                playSentence(song);
                lastPlayedAudioType = "sentence";
            }
        } else {
            setPlayInfo(song);
            playSentence(song);
        }
    }

    private void setPlayInfo(Map<String, Object> song) {
        // Displaying Song title
        String word = (String) song.get("word");
        songTitleLabel.setText(word);
        pronLabel.setText((String) song.get("pron"));
        meaningLabel.setText((String) song.get("meaning"));
        String sentence = (String) song.get("sentence");
        int start = sentence.toLowerCase().indexOf(word);
        Spannable sentenceSpan = new SpannableString(sentence);
        int wordColor = getResources().getColor(R.color.word_pink);
        sentenceSpan.setSpan(new ForegroundColorSpan(wordColor), start, start + word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sentenceSpan.setSpan(new RelativeSizeSpan(1.8f), start, start + word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sentenceLabel.setText(sentenceSpan);
        chineseLabel.setText((String) song.get("chinese"));
    }

    private void playPron(Map<String, Object> song) {
        Integer wordid = (Integer) song.get("wordid");
        byte[] audioData = pronAudio.query(wordid);
        if (audioData == null) {
            playSentence(song);
            lastPlayedAudioType = "sentence";
            return;
        }
        File audioFile = null;
        try {
            audioFile = File.createTempFile("langeasy", "pron_audio");

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(audioFile));
            bos.write(audioData);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Play song
        try {
            mp.reset();
            mp.setDataSource(audioFile.getAbsolutePath());
            mp.prepare();
            mp.start();

            // Changing Button Image to pause image
            btnPlay.setImageResource(R.drawable.btn_pause);

            // set Progress bar values
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playSentence(Map<String, Object> song) {
        Integer sentenceid = (Integer) song.get("sentenceid");
        File audioFile = null;
        try {
            audioFile = File.createTempFile("langeasy", "sentence_audio");

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(audioFile));
            bos.write(sentenceAudio.query(sentenceid));
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Play song
        try {
            mp.reset();
            mp.setDataSource(audioFile.getAbsolutePath());
            mp.prepare();
            mp.start();

            // Changing Button Image to pause image
            btnPlay.setImageResource(R.drawable.btn_pause);

            // set Progress bar values
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update timer on seekbar
     */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    /**
     *
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    /**
     * On Song Playing completed
     * if repeat is ON play same song again
     * if shuffle is ON play random song
     */
    @Override
    public void onCompletion(MediaPlayer arg0) {
//        System.out.println(1 / 0);

        if (isPron & "pron".equals(lastPlayedAudioType)) {
            playSong(currentSongIndex);// keep playing sentence next
            return;
        }
        // check for repeat is ON or OFF
        if (isRepeat) {
            // repeat is on play same song again
            playSong(currentSongIndex);
        } else if (isShuffle) {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        } else {
            // no repeat or shuffle ON - play next song
            if (currentSongIndex < (songsList.size() - 1)) {
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            } else {
                // play first song
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
    }

}