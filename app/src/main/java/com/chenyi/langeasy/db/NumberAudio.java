package com.chenyi.langeasy.db;

/**
 * Created by liyzh on 2016/10/5.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class NumberAudio extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "number-audio.db";
//    public static final String DATABASE_NAME = "langeasy.db";

    public NumberAudio(Context context) {
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/langeasy/sqlite/" + DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.rawQuery("select count(*) from number_audio", null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public byte[] query(int number) {
        String sql = "select audiodata from number_audio where num = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(sql, new String[]{number + ""});
        res.moveToFirst();

        byte[] audioData = null;
        while (res.isAfterLast() == false) {
            audioData = res.getBlob(res.getColumnIndex("audiodata"));
            break;
        }
        return audioData;
    }
}