package com.chenyi.langeasy.fragment;

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

import com.chenyi.langeasy.activity.MainNewActivity;
import com.chenyi.langeasy.R;
import com.chenyi.langeasy.Utilities;
import com.chenyi.langeasy.db.DBHelper;
import com.chenyi.langeasy.db.NumberAudio;
import com.chenyi.langeasy.db.PronAudio;
import com.chenyi.langeasy.db.SentenceAudio;
import com.chenyi.langeasy.listener.FragmentExchangeListener;

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
    private ImageButton btnLearning;
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
    private Utilities utils;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private boolean isShuffle = true;
    private boolean isRepeat = false;
    private boolean isPaused = false;
    /**
     * play pronunciation defaultly
     */
    private boolean isPron = true;
    /**
     * pronuncation played or sentence played
     */
    public String lastPlayedAudioType = "sentence";
    private List<Map<String, Object>> songsList = new ArrayList<>();

    private DBHelper mydb;
    private SentenceAudio sentenceAudio;
    private PronAudio pronAudio;
    private NumberAudio numberAudio;

    private FragmentExchangeListener fragmentExchangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View playerLayout = inflater.inflate(R.layout.player,
                container, false);
        MainNewActivity activity = (MainNewActivity) getActivity();
        songsList = activity.songsList;//songManager.getPlayList(mydb);


        fragmentExchangeListener = (FragmentExchangeListener) activity;

        mydb = new DBHelper(activity);
        sentenceAudio = new SentenceAudio(activity);
        pronAudio = new PronAudio(activity);
        numberAudio = new NumberAudio(activity);

        // All player buttons
        btnPlay = (ImageButton) playerLayout.findViewById(R.id.btnPlay);
        btnForward = (ImageButton) playerLayout.findViewById(R.id.btnForward);
        btnBackward = (ImageButton) playerLayout.findViewById(R.id.btnBackward);
        btnNext = (ImageButton) playerLayout.findViewById(R.id.btnNext);
        btnPrevious = (ImageButton) playerLayout.findViewById(R.id.btnPrevious);
        btnLearning = (ImageButton) playerLayout.findViewById(R.id.btnLearning);
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
        utils = new Utilities();


        IntentFilter actionFilter = new IntentFilter();
        actionFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        activity.registerReceiver(mIntentReceiver, actionFilter);

        // Listeners
        songProgressBar.setOnSeekBarChangeListener(this); // Important
        mp.setOnCompletionListener(this); // Important
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e("MediaPlayer error", mp.toString());
                Log.e("MediaPlayer error", what + "");
                Log.e("MediaPlayer error", extra + "");
                return false;
            }
        });
        audioManage(activity);

        // Getting all songs list
        songsList = activity.songsList;//songManager.getPlayList(mydb);

//        playDefault();
//        playSong(0);
        initButtonEvent();
        initConfig();

        return playerLayout;
    }

    private void playDefault() {
        Integer sentenceid = mydb.queryLastPlayRecord();
        if (sentenceid != null) {
            int index = findIndex(sentenceid);
            playSong(index);
        } else {
            // By default play first song
            playSong(0);
        }
    }

    private int findIndex(Integer sentenceid) {
        for (Map<String, Object> sentence : songsList) {
            int sid = (int) sentence.get("sentenceid");
            if (sid == sentenceid) {
                return (int) sentence.get("index");
            }
        }
        return 0;
    }

    private void initConfig() {
        String svalue = Utilities.getConfig(getActivity(), "isShuffle");
        if ("0".equals(svalue)) {
            isShuffle = false;
            btnShuffle.setImageResource(R.drawable.btn_shuffle);
        }
    }

    private void pausePlayer() {
        mp.pause();
        isPaused = true;
        // Changing button image to play button
        btnPlay.setImageResource(R.drawable.btn_play);

        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    private void resumePlayer() {
        mp.start();
        isPaused = false;
        // Changing button image to pause button
        btnPlay.setImageResource(R.drawable.btn_pause);

        // update timer progress again
        updateProgressBar();
    }

    private void startPlay(File audioFile) {
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

    private void initButtonEvent() {

        /**
         * Play button click event
         * plays a song and changes button to pause image
         * pauses a song and changes button to play image
         * */
        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (mp == null) {
                    playDefault();
                } else {
                    if (mp.isPlaying()) {
                        pausePlayer();
                    } else {
                        // Resume song
                        Log.i("test isPaused", isPaused + "");
                        if (!isPaused) {
                            playDefault();
                        } else {
                            resumePlayer();
                        }
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
                    currentSongIndex = currentSongIndex + 1;
                } else {
                    // play first song
                    currentSongIndex = 0;
                }
                lastPlayedAudioType = "sentence";
                playSong(currentSongIndex);
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
                    currentSongIndex = currentSongIndex - 1;
                } else {
                    // play last song
                    currentSongIndex = songsList.size() - 1;
                }
                lastPlayedAudioType = "sentence";
                playSong(currentSongIndex);

            }
        });
        final Context applicationContext = getActivity().getApplicationContext();
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
                    Utilities.setConfig(getActivity(), "isShuffle", "0");

                } else {
                    // make repeat to true
                    isShuffle = true;
                    Toast.makeText(applicationContext, "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                    Utilities.setConfig(getActivity(), "isShuffle", "1");
                }

                String svalue = Utilities.getConfig(getActivity(), "isShuffle");
                Log.i("isShuffle", svalue + " " + isShuffle);
            }
        });

        /**
         * Button Click event for Play list click event
         * Launches list activity which displays list of songs
         * */
//        btnPlaylist.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                btnPlayListListener.toList(currentSongIndex);
//            }
//        });

        btnLearning.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                fragmentExchangeListener.toLearning(currentSongIndex);
            }
        });
    }


    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener;
    private boolean transientPause = false;
    private boolean volumeDuck = false;

    private boolean isPlaying() {
        try {
            if (mp.isPlaying()) {
                return true;
            }
        } catch (IllegalStateException ex) {
            Log.e("MediaPlayer ", "IllegalState", ex);
        }
        return false;
    }

    private void audioManage(Context mContext) {

        final AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                    // Pause playback
                    if (mp != null && mp.isPlaying()) {
                        pausePlayer();
                        transientPause = true;
                    }
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    // Resume playback
                    if (transientPause) {
                        transientPause = false;
                        resumePlayer();
                    } else if (volumeDuck) { // Raise it back to normal
                        volumeDuck = false;
                        mp.setVolume(1f, 1f);
                    }
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    // Stop playback
                    if (mp != null && isPlaying()) {
                        pausePlayer();
                    }
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                    // Lower the volume
                    if (mp != null && mp.isPlaying()) {
                        volumeDuck = true;
                        mp.setVolume(0.1f, 0.1f);
                    }
                }
            }
        };
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
            if (intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                // signal your service to stop playback
                // (via an Intent, for instance)
                Log.i("action", "ACTION_AUDIO_BECOMING_NOISY--lyz");
                if (mp != null && mp.isPlaying()) {
                    pausePlayer();
                }
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

    private boolean songPaused = false;
    private boolean songStarted;

    /**
     * Function to play a song
     *
     * @param songIndex - index of song
     */
    public void playSong(int songIndex) {
        if (songIndex > -1) {
//            return;
        }
        Map<String, Object> song = songsList.get(songIndex);
        currentSongIndex = songIndex;

        Log.i("song", song.toString() + "");
        Log.i("isPron", isPron + "");
        Log.i("lastPlayedAudioType", lastPlayedAudioType + "");
        if (isPron) {
            if ("sentence".equals(lastPlayedAudioType)) {
                setPlayInfo(song);
                playNumber(song);
                lastPlayedAudioType = "number";
            } else if ("number".equals(lastPlayedAudioType)) {
//                setPlayInfo(song);
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
        Integer wordid = (Integer) song.get("wordid");
        Integer sentenceid = (Integer) song.get("sentenceid");
        String word = (String) song.get("word");
        mydb.addPlayRecord(wordid, word, sentenceid);
        ((MainNewActivity) getActivity()).remember();

        int wordCount = mydb.queryWordCount(wordid);

        // Displaying Song title
        songTitleLabel.setText(wordid + word + " " + wordCount);
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

        Integer scount = (Integer) song.get("scount");
        song.put("scount", scount + 1);
    }

    private Integer lastWordId = -1;
    private File lastWordAudio = null;

    private void playNumber(Map<String, Object> song) {
        Integer wordid = (Integer) song.get("wordid");

        File audioFile = null;

        if (lastWordId == wordid) {
            audioFile = lastWordAudio;
        } else {
            byte[] audioData = numberAudio.query(wordid);
            if (audioData == null) {
                lastPlayedAudioType = "number";
                playPron(song);
                return;
            }

            audioFile = getAudioFile(audioData, "number");
            lastWordAudio = audioFile;
            lastWordId = wordid;
        }
        startPlay(audioFile);
    }

    private File getAudioFile(byte[] audioData, String type) {
        File audioFile = null;
        try {
            audioFile = File.createTempFile("langeasy", type + "_audio");

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(audioFile));
            bos.write(audioData);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return audioFile;
    }

    private Integer lastPwordId = -1;
    private File lastPronAudio = null;

    private void playPron(Map<String, Object> song) {
        Integer wordid = (Integer) song.get("wordid");

        File audioFile = null;

        if (lastPwordId == wordid) {
            audioFile = lastPronAudio;
        } else {
            byte[] audioData = pronAudio.query(wordid);
            if (audioData == null) {
                lastPlayedAudioType = "pron";
                playSentence(song);
                return;
            }

            audioFile = getAudioFile(audioData, "pron");
            lastPronAudio = audioFile;
            lastPwordId = wordid;
        }

        Log.i("lastWordId", lastWordId + "");
        Log.i("wordid", wordid + "");
        Log.i("audioFile", audioFile.getName() + "");
        startPlay(audioFile);
    }

    private Integer lastSentenceId = -1;
    private File lastSentenceAudio = null;

    private void playSentence(Map<String, Object> song) {
        Integer sentenceid = (Integer) song.get("sentenceid");

        File audioFile = null;

        if (lastSentenceId == sentenceid) {
            audioFile = lastSentenceAudio;
        } else {
            byte[] audioData = sentenceAudio.query(sentenceid);

            audioFile = getAudioFile(audioData, "sentence");
            lastSentenceAudio = audioFile;
            lastSentenceId = sentenceid;
        }
        startPlay(audioFile);
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
            long totalDuration = 0;
            try {
                totalDuration = mp.getDuration();
            } catch (IllegalStateException ex) {
                Log.e("MediaPlayer ", "IllegalState", ex);
                return;
            }
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

        if ((isPron & "pron".equals(lastPlayedAudioType)) || "number".equals(lastPlayedAudioType)) {
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
                currentSongIndex = currentSongIndex + 1;
            } else {
                // play first song
                currentSongIndex = 0;
            }
            playSong(currentSongIndex);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
    }

}