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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "langeasy.db";
    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_EMAIL = "email";
    public static final String CONTACTS_COLUMN_STREET = "street";
    public static final String CONTACTS_COLUMN_CITY = "place";
    public static final String CONTACTS_COLUMN_PHONE = "phone";
    private HashMap hp;

    public DBHelper(Context context) {
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

    public boolean insertContact(String name, String phone, String email, String street, String place) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.insert("contacts", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from contacts where id=" + id + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact(Integer id, String name, String phone, String email, String street, String place) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.update("contacts", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteContact(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public ArrayList<Map<String, Object>> listSentence() {
        ArrayList<Map<String, Object>> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from sentence", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            Map<String, Object> map = new HashMap<>();
            //sentenceid, wordid, word, pron, mtype, meaning, sentence, chinese
            Integer sentenceid = res.getInt(res.getColumnIndex("sentenceid"));
            String word = res.getString(res.getColumnIndex("word"));
            map.put("sentenceid", sentenceid);
            map.put("wordid", res.getInt(res.getColumnIndex("wordid")));
            map.put("word", word);
            map.put("wordunique", word + sentenceid);
            map.put("pron", res.getString(res.getColumnIndex("pron")));
            map.put("mtype", res.getString(res.getColumnIndex("mtype")));
            map.put("meaning", res.getString(res.getColumnIndex("meaning")));
            map.put("sentence", res.getString(res.getColumnIndex("sentence")));
            map.put("chinese", res.getString(res.getColumnIndex("chinese")));
            array_list.add(map);
            res.moveToNext();
        }
        return array_list;
    }


    public byte[] queryPronAudio(int wordId) {
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

    public byte[] querySentenceAudio(int sentenceId) {
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