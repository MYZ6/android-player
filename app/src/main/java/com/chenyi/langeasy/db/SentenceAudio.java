package com.chenyi.langeasy.db;

/**
 * Created by liyzh on 2016/9/1.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class SentenceAudio extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "sentence-audio.db";
//    public static final String DATABASE_NAME = "langeasy.db";

    private Context scontext;

    public SentenceAudio(Context context) {
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/langeasy/sqlite/" + DATABASE_NAME, null, 2);
        scontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.rawQuery("select count(*) from sentence_audio", null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("alter table sentence_audio add column mtime text");
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

    public Long queryLastSyncTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select max(mtime) as mtime from sentence_audio", null);
        res.moveToFirst();

        Long mtime = null;
        while (res.isAfterLast() == false) {
            mtime = res.getLong(res.getColumnIndex("mtime"));
            break;
        }
        return mtime;
    }

    public void syncList() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(scontext);
        Long mtime = queryLastSyncTime();
        Log.i("volley mtime", mtime.toString());
        String url = "http://192.168.1.123:83/langeasy/api?t=listModifiedSentence&mtime=" + mtime;

// Request a string response from the provided URL.
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject json = response.getJSONObject(i);
                        int sentenceid = json.getInt("sentenceid");
                        String mtime = json.getString("mtime");
                        if (sentenceid == 412125) {
                            updateSentence(sentenceid, mtime);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                Log.i("volley test", response.toString());
                Toast.makeText(scontext, response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("volley test", response);
                        Toast.makeText(scontext, response, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("volley test error", error.toString());
            }
        });
// Add the request to the RequestQueue.
        queue.add(arrayRequest);
//        queue.add(stringRequest);
    }


    public void updateSentence(int sentenceid, String mtime) throws IOException {
        String[] params = new String[]{sentenceid + "", mtime};
        new RetrieveMediaTask().execute(params);
    }

    public void updateAudio(int sentenceId, byte[] audiodata, String mtime) {
//        String isql = "update sentence_audio set audiodata = ?, mtime = ? where sentenceid = ?";
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("audiodata", audiodata);
        contentValues.put("mtime", mtime);
        db.update("sentence_audio", contentValues, "sentenceid = ? ", new String[]{Integer.toString(sentenceId)});
    }

    class RetrieveMediaTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;

        @Override
        protected Integer doInBackground(String... params) {
            int sentenceid = Integer.parseInt(params[0]);
            try {
                String surl = "http://192.168.1.123:83/langeasy/api?t=m&id=" + sentenceid;
                Log.i("volley test surl", surl);
                if (sentenceid != 0) {
//                    return null;
                }
                URL url = new URL(surl);

                URLConnection connection = url.openConnection();
//        IOUtils.toByteArray(connection.getInputStream());
                updateAudio(sentenceid, IOUtils.toByteArray(connection.getInputStream()), params[1]);
//                OutputStream out = new FileOutputStream("/sdcard/langeasy/sqlite/" + sentenceid + "mp3");
//                IOUtils.copy(connection.getInputStream(), out);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}