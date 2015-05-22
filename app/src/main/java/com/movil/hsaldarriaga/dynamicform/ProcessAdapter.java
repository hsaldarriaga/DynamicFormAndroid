package com.movil.hsaldarriaga.dynamicform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hass-pc on 21/05/2015.
 */
public class ProcessAdapter extends BaseAdapter {

    List<Process> Processes;
    LayoutInflater inflater;
    public ProcessAdapter(List<Process> Processes, Context c) {
        this.Processes = Processes;
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return Processes.size();
    }

    @Override
    public Object getItem(int position) {
        return Processes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Processes.get(position).process_id;
    }

    public String getItemName(int position) {
        return Processes.get(position).name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlaceHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_process_item, null);
            holder = new PlaceHolder();
            holder.title = (TextView) convertView.findViewById(R.id.process_title);
            holder.description = (TextView) convertView.findViewById(R.id.process_details);
            convertView.setTag(holder);
        } else {
            holder = (PlaceHolder)convertView.getTag();
        }
        holder.title.setText(Processes.get(position).name);
        holder.description.setText(Processes.get(position).description);
        return convertView;
    }
    private static class PlaceHolder {
        public TextView title, description;
    }
}
