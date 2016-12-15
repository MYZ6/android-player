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
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class QueueHelper {

    public static ArrayList<Map<String, Object>> queryQueue(SQLiteDatabase db) {
        ArrayList<Map<String, Object>> array_list = new ArrayList<>();
//        db.execSQL("drop TABLE queue;");
//        db.execSQL("CREATE TABLE queue ( id INTEGER, name TEXT, ctime TEXT, PRIMARY KEY (id) )");
//        db.execSQL("insert into queue(id, name, ctime) values(1,'test', " + new Date().getTime() +
//                ")");
//        int a = 1;
//        if (a > 0) {
//            return array_list;
//        }

        String sql = "select * from queue";
        Cursor res = db.rawQuery(sql, null);
        res.moveToFirst();

        int count = 0;
        while (res.isAfterLast() == false) {
            Map<String, Object> map = new HashMap<>();
            map.put("index", count++);
            Integer id = res.getInt(res.getColumnIndex("id"));
            String name = res.getString(res.getColumnIndex("name"));
            map.put("id", id);
            map.put("name", name);

            Long ctime = res.getLong(res.getColumnIndex("ctime"));
            map.put("ctime", new Date(ctime));

            array_list.add(map);
            res.moveToNext();
        }
        return array_list;
    }

    public static ArrayList<Map<String, Object>> queryQueueRecord(SQLiteDatabase db, Integer queueId) {
        ArrayList<Map<String, Object>> array_list = new ArrayList<>();

        String sql = "select s.* from queue_record r inner join sentence s on s.sentenceid = r.sid where r.qid = ?";
        Cursor res = db.rawQuery(sql, new String[]{Integer.toString(queueId)});
        res.moveToFirst();

        int count = 0;
        while (res.isAfterLast() == false) {
            Map<String, Object> map = new HashMap<>();
            map.put("index", count++);
            Integer wordid = res.getInt(res.getColumnIndex("wordid"));
            Integer sentenceid = res.getInt(res.getColumnIndex("sentenceid"));
            String word = res.getString(res.getColumnIndex("word"));
            map.put("sentenceid", sentenceid);
            map.put("wordid", res.getInt(res.getColumnIndex("wordid")));
            map.put("booktype", res.getString(res.getColumnIndex("booktype")));
            map.put("bookname", res.getString(res.getColumnIndex("bookname")));
            map.put("word", word);
            map.put("wordunique", wordid + word + sentenceid);

            array_list.add(map);
            res.moveToNext();
        }
        return array_list;
    }

    public static void addQueue(SQLiteDatabase db, ArrayList<Integer> sentenceidList) {
//        db.execSQL("drop TABLE queue_record;");
//        db.execSQL("CREATE TABLE queue_record ( id INTEGER, qid INTEGER, sid INTEGER, PRIMARY KEY (id) )");
//                ")");
//        int a = 1;
//        if (a > 0) {
//            return array_list;
//        }
        String INSERT_QUERY = "insert into queue_record(qid, sid) values(?, ?)";
        SQLiteStatement statement = db.compileStatement(INSERT_QUERY);
        db.beginTransaction();

        ContentValues contentValues = new ContentValues();
        Date now = new Date();
        contentValues.put("name", DateFormat.format("yyyy-MM-dd HH:mm:ss", now) + "");
        contentValues.put("ctime", now.getTime());
        long qid = db.insert("queue", null, contentValues);
        try {
            for (Integer sid : sentenceidList) {
                statement.clearBindings();
                statement.bindLong(1, qid);
                statement.bindString(2, sid + "");
                // rest of bindings
                statement.execute(); //or executeInsert() if id is needed
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public static void editQueueName(SQLiteDatabase db, Integer queueId, String queueName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", queueName);
        Log.i("queueId", queueId + "");
        Log.i("queue name", queueName);
        db.update("queue", contentValues, "id = ? ", new String[]{Integer.toString(queueId)});
    }
}