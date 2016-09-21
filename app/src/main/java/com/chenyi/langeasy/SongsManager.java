package com.chenyi.langeasy;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Map;

public class SongsManager {
    // SDCard Path
    final String MEDIA_PATH = new String("/sdcard/qqmusic/song/");
    private ArrayList<Map<String,Object>> songsList = new ArrayList<>();

    // Constructor
    public SongsManager() {
    }

    /**
     * Function to read all mp3 files from sdcard
     * and store the details in ArrayList
     */
    public ArrayList<Map<String, Object>> getPlayList(DBHelper mydb) {
//        File home = new File(MEDIA_PATH);
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();

        songsList = mydb.listSentence();


        // Not sure if the / is on the path or not
        File home = new File(baseDir + File.separator + "qqmusic/song/");
        Log.i("home path", home.getAbsolutePath());
        if (home.exists()) {
            Log.i("home exsists", home.getAbsolutePath());
        }
//        if (home.listFiles(new FileExtensionFilter()).length > 0) {
//            for (File file : home.listFiles(new FileExtensionFilter())) {
//                HashMap<String, String> song = new HashMap<String, String>();
//                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
//                song.put("songPath", file.getPath());
//
//                // Adding each song to SongList
//                songsList.add(song);
//            }
//        }
        // return songs list array
        return songsList;
    }

    /**
     * Class to filter files which are having .mp3 extension
     */
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
}
