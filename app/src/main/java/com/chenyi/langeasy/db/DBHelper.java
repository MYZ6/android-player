package com.chenyi.langeasy.db;

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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "data.db";

    public DBHelper(Context context) {
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/langeasy/sqlite/" + DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.rawQuery("select count(*) from sentence", null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS contacts");
//        onCreate(db);

        db.execSQL("CREATE TABLE play_record ( id INTEGER, wordid INT, word TEXT, sentenceid INT, playtime TEXT, PRIMARY KEY (id) )");
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
        int numRows = (int) DatabaseUtils.queryNumEntries(db, "test");
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

    public boolean addPlayRecord(int wordid, String word, int sentenceid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("wordid", wordid);
        contentValues.put("word", word);
        contentValues.put("sentenceid", sentenceid);
        contentValues.put("playtime", new Date().getTime());
        db.insert("play_record", null, contentValues);
        return true;
    }

    public Integer queryLastPlayRecord() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select sentenceid from play_record order by playtime desc limit 1", null);
        res.moveToFirst();

        Integer sentenceid = null;
        while (res.isAfterLast() == false) {
            sentenceid = res.getInt(res.getColumnIndex("sentenceid"));
            break;
        }
        return sentenceid;
    }

    public Integer queryWordCount(int wordid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select count(*) from sentence where wordid = " + wordid, null);
        res.moveToFirst();

        Integer count = null;
        while (res.isAfterLast() == false) {
            count = res.getInt(0);
            break;
        }
        return count;
    }
    public ArrayList<Map<String, Object>> listBook() {
        ArrayList<Map<String, Object>> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select s.booktype, s.bookid, s.bookname, count(*) as scount " +
                "from sentence s inner join vocabulary v on v.wordid = s.wordid and ifnull(v.pass, 0) !=1 " +
                "group by s.bookid order by s.booktype, s.bookname";
        Cursor res = db.rawQuery(sql, null);
        res.moveToFirst();

        int count = 0;
        while (res.isAfterLast() == false) {
            Map<String, Object> map = new HashMap<>();
            map.put("index", count++);
            Integer scount = res.getInt(res.getColumnIndex("scount"));
            map.put("bookid", res.getString(res.getColumnIndex("bookid")));
            map.put("bookname", res.getString(res.getColumnIndex("bookname")));
            map.put("booktype", res.getString(res.getColumnIndex("booktype")));
            map.put("scount",scount);
            array_list.add(map);
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<Map<String, Object>> listSentence() {
        ArrayList<Map<String, Object>> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select s.* from sentence s inner join vocabulary v on v.wordid = s.wordid and ifnull(v.pass, 0) !=1", null);
        res.moveToFirst();

        int count = 0;
        while (res.isAfterLast() == false) {
            Map<String, Object> map = new HashMap<>();
            map.put("index", count++);
            //sentenceid, wordid, word, pron, mtype, meaning, sentence, chinese
            Integer wordid = res.getInt(res.getColumnIndex("wordid"));
            Integer sentenceid = res.getInt(res.getColumnIndex("sentenceid"));
            String word = res.getString(res.getColumnIndex("word"));
            map.put("sentenceid", sentenceid);
            map.put("wordid", res.getInt(res.getColumnIndex("wordid")));
            map.put("word", word);
            map.put("wordunique", wordid + word + sentenceid);
            map.put("pron", res.getString(res.getColumnIndex("pron")));
            map.put("mtype", res.getString(res.getColumnIndex("mtype")));
            map.put("meaning", res.getString(res.getColumnIndex("meaning")));
            map.put("sentence", res.getString(res.getColumnIndex("sentence")));
            map.put("chinese", res.getString(res.getColumnIndex("chinese")));
            map.put("bookid", res.getString(res.getColumnIndex("bookid")));
            map.put("bookname", res.getString(res.getColumnIndex("bookname")));
            map.put("booktype", res.getString(res.getColumnIndex("booktype")));
            map.put("courseid", res.getString(res.getColumnIndex("courseid")));
            map.put("coursename", res.getString(res.getColumnIndex("coursename")));
            array_list.add(map);
            res.moveToNext();
        }
        return array_list;
    }


    public Map<String, Integer> queryPlayRecord(Integer wordid, Integer sentenceid) {
        ArrayList<Map<String, Object>> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select count(*) from play_record", null);
        res.moveToFirst();

        Integer total = null;
        while (res.isAfterLast() == false) {
            total = res.getInt(0);
            break;
        }
        res = db.rawQuery("select count(*) from play_record where sentenceid = ?", new String[]{sentenceid + ""});
        res.moveToFirst();

        Integer stotal = null;
        while (res.isAfterLast() == false) {
            stotal = res.getInt(0);
            break;
        }
        res = db.rawQuery("select count(*) from play_record where wordid = ?", new String[]{wordid + ""});
        res.moveToFirst();

        Integer wtotal = null;
        while (res.isAfterLast() == false) {
            wtotal = res.getInt(0);
            break;
        }

        Map<String, Integer> map = new HashMap<>();
        map.put("total", total);
        map.put("stotal", stotal);
        map.put("wtotal", wtotal);
        return map;
    }


    public boolean passWord(Integer wordid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pass", 1);
        db.update("vocabulary", contentValues, "wordid = ? ", new String[]{Integer.toString(wordid)});
        return true;
    }

//
//    public byte[] queryPronAudio(int wordId) {
//        String sql = "select audiodata from pron_audio where wordid = ?";
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res = db.rawQuery(sql, new String[]{wordId + ""});
//        res.moveToFirst();
//
//        byte[] audioData = null;
//        while (res.isAfterLast() == false) {
//            audioData = res.getBlob(res.getColumnIndex("audiodata"));
//            break;
//        }
//        return audioData;
//    }
//
//    public byte[] querySentenceAudio(int sentenceId) {
//        String sql = "select audiodata from sentence_audio where sentenceid = ?";
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res = db.rawQuery(sql, new String[]{sentenceId + ""});
//        res.moveToFirst();
//
//        byte[] audioData = null;
//        while (res.isAfterLast() == false) {
//            audioData = res.getBlob(res.getColumnIndex("audiodata"));
//            break;
//        }
//        return audioData;
//    }
}