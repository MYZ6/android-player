package com.androidhive.musicplayer;

/**
 * Created by liyzh on 2016/9/1.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SentenceAudio extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "sentence-audio.db";
//    public static final String DATABASE_NAME = "langeasy.db";

    public SentenceAudio(Context context) {
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/langeasy/sqlite/" + DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.rawQuery("select count(*) from sentence_audio", null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public byte[] query(int sentenceId) {
        String sql = "select audiodata from sentence_audio where sentenceid = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(sql, new String[]{sentenceId + ""});
        res.moveToFirst();

        byte[] audioData = null;
        while (res.isAfterLast() == false) {
            audioData = res.getBlob(res.getColumnIndex("audiodata"));
            break;
        }
        return audioData;
    }
}