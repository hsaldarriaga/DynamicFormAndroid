package com.movil.hsaldarriaga.dynamicform;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements DownloadFinish{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = (ListView)findViewById(R.id.listView);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        JsonDownloader down = new JsonDownloader(this);
        down.execute("https://dynamicformapi.herokuapp.com/groups.json");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnFinishDownload(String obj) {
        try {
            List<Category> categories = new ArrayList<>();
            JSONArray cats = new JSONArray(obj);
            for (int i = 0; i < cats.length(); i++) {
                JSONObject item = cats.getJSONObject(i);
                Category category = new Category(item.getLong("group_id"), item.getString("name"));
                categories.add(category);
            }
            adapter = new CategoryAdapter(categories, this);
            listview.setAdapter(adapter);
            SetListView();
            progressBar.setVisibility(View.INVISIBLE);
        } catch (JSONException e) {
            Log.e(e.toString(), e.getMessage());
        }
    }

    private void SetListView() {
        final Context c = this;
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(c, ProcessActivity.class);
                intent.putExtra("category", adapter.getStringName(position));
                intent.putExtra("category_id", adapter.getItemId(position));
                c.startActivity(intent);
            }
        });
    }

    private ListView listview;
    private ProgressBar progressBar;
    private CategoryAdapter adapter;
}
