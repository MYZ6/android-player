package com.chenyi.langeasy.db;

/**
 * Created by liyzh on 2016/9/1.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class PronAudio extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "pron-audio.db";
//    public static final String DATABASE_NAME = "langeasy.db";

    public PronAudio(Context context) {
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/langeasy/sqlite/" + DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.rawQuery("select count(*) from pron_audio", null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public byte[] query(int wordId) {
        String sql = "select audiodata from pron_audio where wordid = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(sql, new String[]{wordId + ""});
        res.moveToFirst();

        byte[] audioData = null;
        while (res.isAfterLast() == false) {
            audioData = res.getBlob(res.getColumnIndex("audiodata"));
            break;
        }
        return audioData;
    }
}