package com.movil.hsaldarriaga.dynamicform;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by hass-pc on 21/05/2015.
 */
public class Step {
    public long step_id;
    public long procedure_id;
    public Content content;

    private Step() {
    }

    public static class Content {
        Field[] fields;
        Decision[] Decisions;

        public Content(Field[] fields, Step.Decision[] decisions) {
            this.fields = fields;
            Decisions = decisions;
        }
    }

    public static class Field {
        public long field_id;
        public String caption;
        public TYPES field_type;
        public String[] Possible_Values;

        public Field(long field_id, String caption, TYPES field_type, String[] possible_Values) {
            this.field_id = field_id;
            this.caption = caption;
            this.field_type = field_type;
            Possible_Values = possible_Values;
        }
    }

    public static class Decision {
        Branch[] Branches;
        public long go_to_step;
        public Decision(Branch[] branches, long go_to_step) {
            Branches = branches;
            this.go_to_step = go_to_step;
        }
    }

    public static class Branch {
        public long field_id;
        public COMPARISION comparision_type;
        public String Value;
        public Branch(long field_id, COMPARISION comparision_type, String value) {
            this.field_id = field_id;
            this.comparision_type = comparision_type;
            Value = value;
        }
    }

    public enum TYPES {
        QUESTION,
        BOOLEAN,
        NUMERIC,
        LABEL
    }

    public enum COMPARISION {
        GREATER, LESS, EQUAL
    }

    public static Step getStep(JSONObject step_json) {
        Step step = new Step();
        try {
            step.step_id = step_json.getLong("step_id");
            step.procedure_id = step_json.getLong("procedure_id");
            String content = step_json.getString("content");
            JSONObject content_json = new JSONObject(content);
            JSONArray fields = content_json.getJSONArray("Fields");
            Field[] fields_array = new Field[fields.length()];
            for (int i = 0; i < fields.length(); i++) {
                JSONObject obj = fields.getJSONObject(i);
                String values[] = null;
                try {
                    JSONArray values_array = obj.getJSONArray("possible_values");
                    values = new String[values_array.length()];
                    for (int j = 0; j < values_array.length(); j++) {
                        values[j] = values_array.getString(j);
                    }
                } catch (JSONException ex) {

                }
                fields_array[i] = new Field(obj.getLong("id"), obj.getString("caption"), TYPES.values()[obj.getInt("field_type")],values);
            }
            JSONArray decision_json = content_json.getJSONArray("Decisions");
            Decision Decisions[] = new Decision[decision_json.length()];
            for (int i = 0; i < decision_json.length(); i++) {
                JSONObject obj = decision_json.getJSONObject(i);
                JSONArray branches_array = obj.getJSONArray("branch");
                Branch branches[] = new Branch[branches_array.length()];
                for (int j = 0; j< branches_array.length(); j++) {
                    JSONObject obj1 = branches_array.getJSONObject(j);
                    branches[j] = new Branch(obj1.getLong("field_id"), getType(obj1.getString("comparison_type")), obj1.getString("value"));
                }
                Decisions[i] = new Decision(branches, obj.getLong("go_to_step"));
            }
            step.content = new Content(fields_array, Decisions);
        } catch (JSONException e) {
            Log.e(e.toString(), e.getMessage());
        }
        return step;
    }

    public static COMPARISION getType(String data) {
        if (data.equals("="))
            return COMPARISION.EQUAL;
        if (data.equals("<"))
            return COMPARISION.LESS;
        return COMPARISION.GREATER;
    }

    public static View getQuestion(Context c, Field field, final StepFragment frag) {
        View v = View.inflate(c, R.layout.field_question, null);
        TextView tv = (TextView) v.findViewById(R.id.question_caption);
        tv.setText(field.caption);
        Spinner spinner = (Spinner) v.findViewById(R.id.numeric_edittext);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(c, R.layout.spinner_item_layout, field.Possible_Values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                frag.ValidateDecisions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                frag.ValidateDecisions();
            }
        });
        return v;
    }
    public static View getBoolean(Context c, Field field, final StepFragment frag) {
        View v = View.inflate(c, R.layout.field_boolean, null);
        TextView tv = (TextView) v.findViewById(R.id.boolean_caption);
        tv.setText(field.caption);
        CheckBox box = (CheckBox) v.findViewById(R.id.numeric_edittext);
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                frag.ValidateDecisions();
            }
        });
        return v;
    }
    public static View getNumeric(Context c, Field field, final StepFragment frag) {
        View v = View.inflate(c, R.layout.field_numeric, null);
        TextView tv = (TextView) v.findViewById(R.id.numeric_caption);
        tv.setText(field.caption);
        EditText tv_num = (EditText) v.findViewById(R.id.numeric_edittext);
        tv_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                frag.ValidateDecisions();
            }

            @Override
            public void afterTextChanged(Editable s) {
                frag.ValidateDecisions();
            }
        });
        return v;
    }
    public static View getLabel(Context c, Field field, final StepFragment frag) {
        View v = View.inflate(c, R.layout.field_text, null);
        TextView tv = (TextView) v.findViewById(R.id.text_textview);
        tv.setText(field.caption);
        return v;
    }

    public static int generateRandomColor(int mix) {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        red = (red + Color.red(mix)) / 2;
        green = (green + Color.green(mix)) / 2;
        blue = (blue + Color.blue(mix)) / 2;
        int color = Color.rgb(red, green, blue);
        return color;
    }
}
