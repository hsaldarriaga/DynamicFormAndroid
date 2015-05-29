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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ProcessActivity extends AppCompatActivity implements DownloadFinish {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        TextView tv = (TextView) findViewById(R.id.process_title);
        listview = (ListView)findViewById(R.id.listView2);
        progressBar = (ProgressBar)findViewById(R.id.process_progressBar);
        tv.setText(tv.getText() + " " + getIntent().getStringExtra("category"));
        String url = "https://dynamicformapi.herokuapp.com/procedures/by_group/"+ getIntent().getLongExtra("category_id", 0) +".json";
        JsonDownloader down = new JsonDownloader(this);
        down.execute(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_process, menu);
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
            progressBar.setVisibility(View.INVISIBLE);
            List<Process> pro_list = new ArrayList<>();
            JSONArray processes = new JSONArray(obj);
            for (int i = 0; i < processes.length(); i++) {
                JSONObject item = processes.getJSONObject(i);
                Process pro = new Process(item.getLong("procedure_id"), item.getString("name"), item.getString("description"));
                pro_list.add(pro);
            }
            adapter = new ProcessAdapter(pro_list, this);
            listview.setAdapter(adapter);
            SetListView();
        } catch (JSONException e) {
            Log.e(e.toString(), e.getMessage());
        }
    }

    private void SetListView() {
        final Context c = this;
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intento = new Intent(c, StepActivity.class);
                intento.putExtra("process_id", adapter.getItemId(position));
                intento.putExtra("process_name", adapter.getItemName(position));
                c.startActivity(intento);
            }
        });
    }

    private ProcessAdapter adapter;
    private ListView listview;
    private ProgressBar progressBar;
}
