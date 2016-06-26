package com.example.yinhui.myapplication;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Yinhui on 2016/6/21.
 */
public class UserRest extends AsyncTask<String, Integer, String> {

    private TextView pass_v;

    public UserRest(TextView tv) {
        pass_v = tv;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "Not Found";
        try {
            String url = "http://192.168.1.105:8080/DBWebService/rest/pass/" + params[0] + "/" + params[1];
            HttpURLConnection httpConn = null;
            OutputStream out = null;
            httpConn = (HttpURLConnection) new URL(url).openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            result = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        pass_v.setText(s);
    }
}