package com.movil.hsaldarriaga.dynamicform;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hass-pc on 19/05/2015.
 */
public class JsonDownloader extends AsyncTask<String, Integer, String> {

    DownloadFinish finish;

    public JsonDownloader(DownloadFinish finish) {
        this.finish = finish;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection con = null;
        BufferedReader reader = null;
        String content = "";
        try {
            URL url = new URL(params[0]);
            con = (HttpURLConnection) url.openConnection();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                content += line;
            }
            reader.close();
        } catch (IOException e) {
            Log.e(e.toString(), e.getMessage());
        } finally {
            if (con!= null)
                con.disconnect();
        }
        if (content != "") {
            return content;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String jsonObject) {
        finish.OnFinishDownload(jsonObject);
    }
}
