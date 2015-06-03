package com.movil.hsaldarriaga.dynamicform;


import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Stack;


public class StepActivity extends AppCompatActivity implements DownloadFinish{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        long pro_id = getIntent().getLongExtra("process_id", 0);
        String pro_name = getIntent().getStringExtra("process_name");
        TextView tv = (TextView) findViewById(R.id.title_step);
        tv.setText(pro_name);
        next_button = (Button)findViewById(R.id.next_step);
        JsonDownloader down = new JsonDownloader(this);
        down.execute("https://dynamicformapi.herokuapp.com/steps/by_procedure/" + pro_id + ".json");
        int col = Step.generateRandomColor(Color.WHITE);
        ((ViewGroup)findViewById(R.id.step_main_container)).setBackgroundColor(col);
        pila_colores = new Stack<>();
        pila_colores.push(col);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_step, menu);
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
        ProgressBar bar = (ProgressBar)findViewById(R.id.progressBar2);
        bar.setVisibility(View.INVISIBLE);
        try {
            JSONArray jsonarray = new JSONArray(obj);
            if (jsonarray.length() > 0) {
                steps = new Step[jsonarray.length()];
                for (int i = 0; i < jsonarray.length(); i++) {
                    steps[i] = Step.getStep(jsonarray.getJSONObject(i));
                }
                StepFragment frag = new StepFragment();
                frag.step = steps[0];
                FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                trans.add(R.id.field_container, frag, null)
                        .commit();
            } else {
                finish();
                Toast.makeText(this, "Este Proceso, no tiene pasos...", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Log.e(e.toString(), e.getMessage());
        }

    }

    public void DisableButton() {
        next_button.setEnabled(false);
    }

    public void EnableButton() {
        next_button.setEnabled(true);
    }

    public void ButtonSetText(String text) {
        next_button.setText(text);
    }

    public void ButtonClick(View v) {
        if (next_step_index == -1) {
            finish();
        } else {
            Step s = null;
            for (Step step : steps) {
                if (step.step_id == next_step_index) {
                    s = step;
                    break;
                }
            }
            if (s != null) {
                StepFragment frag = new StepFragment();
                frag.step = s;
                FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                trans.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.field_container, frag, null)
                        .addToBackStack(null)
                        .commit();
            }
        }
        final ViewGroup group = (ViewGroup)findViewById(R.id.step_main_container);
        int col = Step.generateRandomColor(Color.WHITE);
        int colorFrom = pila_colores.peek();
        int colorTo = col;
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                group.setBackgroundColor((Integer) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
        pila_colores.push(col);
    }

    @Override
    public void onBackPressed() {
        if (pila_colores.size() > 1) {
            int col = pila_colores.pop();
            int col2 = pila_colores.peek();
            final ViewGroup group = (ViewGroup)findViewById(R.id.step_main_container);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), col, col2);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    group.setBackgroundColor((Integer) animator.getAnimatedValue());
                }

            });
            colorAnimation.start();
        }
        super.onBackPressed();
    }

    private Button next_button;
    private Step[] steps;
    public long next_step_index = 0;
    private Stack<Integer> pila_colores;
}
