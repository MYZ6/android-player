package com.chenyi.langeasy.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.chenyi.langeasy.R;
import com.chenyi.langeasy.Utilities;
import com.chenyi.langeasy.activity.MainActivity;
import com.chenyi.langeasy.activity.PlayListActivity;
import com.chenyi.langeasy.db.DBHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WordLearningFragment extends Fragment {

    private ImageButton btnPlay;
    private ImageButton btnPass;
    private ImageButton btnBackward;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnPlaylist;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private TextView songTitleLabel;
    private TextView pronLabel;
    private TextView meaningLabel;
    private TextView sentenceLabel;
    private TextView chineseLabel;
    private TextView playrecordTotalLabel;
    private TextView playrecordStotalLabel;
    private TextView playrecordWtotalLabel;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    ;
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
    private List<Map<String, Object>> songsList = new ArrayList<>();

    private DBHelper mydb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View playerLayout = inflater.inflate(R.layout.wlearning,
                container, false);
        MainActivity activity = (MainActivity) getActivity();
        songsList = activity.songsList;//songManager.getPlayList(mydb);

        Log.i("init songsList size", songsList.size() + "");

        mydb = new DBHelper(activity);

        // All player buttons
        btnPlay = (ImageButton) playerLayout.findViewById(R.id.btnPlay);
        btnPass = (ImageButton) playerLayout.findViewById(R.id.btnPass);
        btnBackward = (ImageButton) playerLayout.findViewById(R.id.btnBackward);
        btnNext = (ImageButton) playerLayout.findViewById(R.id.btnNext);
        btnPrevious = (ImageButton) playerLayout.findViewById(R.id.btnPrevious);
        btnPlaylist = (ImageButton) playerLayout.findViewById(R.id.btnPlaylist);
        btnRepeat = (ImageButton) playerLayout.findViewById(R.id.btnRepeat);
        btnShuffle = (ImageButton) playerLayout.findViewById(R.id.btnShuffle);
        songTitleLabel = (TextView) playerLayout.findViewById(R.id.songTitle);
        pronLabel = (TextView) playerLayout.findViewById(R.id.pron);
        meaningLabel = (TextView) playerLayout.findViewById(R.id.meaning);
        sentenceLabel = (TextView) playerLayout.findViewById(R.id.sentence);
        chineseLabel = (TextView) playerLayout.findViewById(R.id.chinese);
        playrecordTotalLabel = (TextView) playerLayout.findViewById(R.id.playrecord_total);
        playrecordWtotalLabel = (TextView) playerLayout.findViewById(R.id.playrecord_wtotal);
        playrecordStotalLabel = (TextView) playerLayout.findViewById(R.id.playrecord_stotal);


        IntentFilter actionFilter = new IntentFilter();
        actionFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);


        playDefault();

        initConfig();
        initButtonEvent();

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
        int svalue = Utilities.getConfig(getActivity(), "isShuffle2");
        if (svalue == 0) {
            isShuffle = false;
            btnShuffle.setImageResource(R.drawable.btn_shuffle);
        }
    }

    private void initButtonEvent() {
        final Context applicationContext = getActivity().getApplicationContext();

        /**
         * Play button click event
         * plays a song and changes button to pause image
         * pauses a song and changes button to play image
         * */
        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                mCallback.onPlay(currentSongIndex);
            }
        });

        /**
         * Forward button click event
         * Forwards song specified seconds
         * */
        btnPass.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Map<String, Object> song = songsList.get(currentSongIndex);
                Integer wordid = (Integer) song.get("wordid");
                mydb.passWord(wordid);

                String word = (String) song.get("word");
                Toast.makeText(applicationContext, "Pass " + word, Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * Backward button click event
         * Backward song to specified seconds
         * */
//        btnBackward.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                // get current song position
//
//            }
//        });

        /**
         * Next button click event
         * Plays next song by taking currentSongIndex + 1
         * */
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isShuffle) {
                    // shuffle is on - play a random song
                    Random rand = new Random();
                    currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
                } else {
                    // no repeat or shuffle ON - play next song
                    if (currentSongIndex < (songsList.size() - 1)) {
                        currentSongIndex = currentSongIndex + 1;
                    } else {
                        // play first song
                        currentSongIndex = 0;
                    }
                }
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
                    playSong(currentSongIndex - 1);
                    currentSongIndex = currentSongIndex - 1;
                } else {
                    // play last song
                    playSong(songsList.size() - 1);
                    currentSongIndex = songsList.size() - 1;
                }

            }
        });
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
                    Utilities.setConfig(getActivity(), "isShuffle2", 0);
                } else {
                    // make repeat to true
                    isShuffle = true;
                    Toast.makeText(applicationContext, "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                    Utilities.setConfig(getActivity(), "isShuffle2", 1);
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
    }

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
        if (songIndex > -1) {
//            return;
        }
        Log.i("songsList size", songsList.size() + "");
        Log.i("songIndex", songIndex + "");
        Map<String, Object> song = songsList.get(songIndex);
        currentSongIndex = songIndex;

        Log.i("song", song.toString() + "");

        setPlayInfo(song);
    }

    private void setPlayInfo(Map<String, Object> song) {
        Integer wordid = (Integer) song.get("wordid");
        Integer sentenceid = (Integer) song.get("sentenceid");
        String word = (String) song.get("word");
//        mydb.addPlayRecord(wordid, word, sentenceid);

        Map<String, Integer> precord = mydb.queryPlayRecord(wordid, sentenceid);
        playrecordTotalLabel.setText("" + precord.get("total"));
        playrecordWtotalLabel.setText("w" + precord.get("wtotal"));
        playrecordStotalLabel.setText("s" + precord.get("stotal"));

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
    }


    OnPlayListener mCallback;

    // Container Activity must implement this interface
    public interface OnPlayListener {
        public void onPlay(int songIndex);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnPlayListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPlayListener");
        }

    }
}