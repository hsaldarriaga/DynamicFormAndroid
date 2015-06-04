package com.movil.hsaldarriaga.dynamicform;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.HashMap;

/**
 * Created by hass-pc on 21/05/2015.
 */
public class StepFragment extends Fragment {
    public Step step;
    private StepActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_step, container, false);
        Configure(v);
        return v;
    }

    private void Configure(View v) {
        LinearLayout linear = (LinearLayout)v;
        fields_map = new HashMap<>();
        View vv = null;
        for (Step.Field field : step.content.fields) {
            switch (field.field_type) {
                case QUESTION:
                    vv = Step.getQuestion(this.getActivity(), field, this);
                    fields_map.put(field.field_id, vv);
                    linear.addView(vv);
                    break;
                case BOOLEAN:
                    vv = Step.getBoolean(this.getActivity(), field, this);
                    linear.addView(vv);
                    fields_map.put(field.field_id, vv);
                    break;
                case NUMERIC:
                    vv = Step.getNumeric(this.getActivity(), field, this);
                    linear.addView(vv);
                    fields_map.put(field.field_id, vv);
                    break;
                case LABEL:
                    vv = Step.getLabel(this.getActivity(), field, this);
                    linear.addView(vv);
                    fields_map.put(field.field_id, vv);
                    break;
            }
        }
        ValidateDecisions();
    }

    public void ValidateDecisions() {
        for (Step.Decision dec : step.content.Decisions) {
            boolean can_continue = true;
            for (Step.Branch br : dec.Branches) {
                Step.Field field = null;
                for (Step.Field f : step.content.fields) {
                    if (f.field_id == br.field_id) {
                        field = f;
                        break;
                    }
                }
                if (field != null) {
                    switch (field.field_type) {
                        case QUESTION:
                            Spinner sp = (Spinner)((ViewGroup)fields_map.get(field.field_id)).getChildAt(2);
                            if (!br.Value.equals(sp.getSelectedItem().toString()))
                                can_continue = false;
                            break;
                        case BOOLEAN:
                            CheckBox box = (CheckBox)((ViewGroup)fields_map.get(field.field_id)).getChildAt(1);
                            if (!(box.isChecked() && br.Value.equals("True")))
                                if (!(!box.isChecked() && br.Value.equals("False")))
                                    can_continue = false;
                            break;
                        case NUMERIC:
                            EditText et = (EditText)((ViewGroup)fields_map.get(field.field_id)).getChildAt(1);
                            if (et.getText().length() > 0) {
                                int val = Integer.parseInt(et.getText().toString());
                                int val_comp = Integer.parseInt(br.Value);
                                switch (br.comparision_type) {
                                    case GREATER:
                                        if (!(val_comp < val))
                                            can_continue = false;
                                        break;
                                    case LESS:
                                        if (!(val_comp > val))
                                            can_continue = false;
                                        break;
                                    case EQUAL:
                                        if (!(val_comp == val))
                                            can_continue = false;
                                        break;
                                }
                            } else {
                                can_continue = false;
                            }
                            break;
                    }
                }
                if (!can_continue)
                    break;
            }
            if (can_continue) {
                if (dec.go_to_step == -1)
                    activity.ButtonSetText("Terminar");
                else {
                    activity.ButtonSetText("Siguiente");
                }
                activity.next_step_index = dec.go_to_step;
                activity.EnableButton();
                break;
            } else {
                activity.DisableButton();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        this.activity = (StepActivity)activity;
        super.onAttach(activity);
    }

    private HashMap<Long, View> fields_map;
}
