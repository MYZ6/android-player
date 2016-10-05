package com.chenyi.langeasy;

import android.app.Activity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chenyi.langeasy.activity.TestActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

/**
 * Created by liyzh on 2016/9/22.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class TestVolley {

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Test
    public void emailValidator_CorrectEmailSimple_ReturnsTrue() {
        String url = "http://www.baidu.com";
        Activity activity = Robolectric.setupActivity(TestActivity.class);

        RequestQueue queue = Volley.newRequestQueue(activity);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
//                        Log.i("volley test", response);

                        System.out.println(response);
                        assertNotSame("hello world", systemOutRule.getLog());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println(error);
                assertNotSame("hello world", systemOutRule.getLog());
                Log.i("volley test error", error.toString());
            }
        });
        queue.add(stringRequest);

        System.out.println("lskjflkdslfks");
        assertNotSame("hello world", systemOutRule.getLog());
        try {
            Thread.sleep(300000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
// Add the request to the RequestQueue.
    }
}
