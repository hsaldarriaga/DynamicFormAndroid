package com.movil.hsaldarriaga.dynamicform;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

/**
 * Created by hass-pc on 19/05/2015.
 */
public class CategoryAdapter extends BaseAdapter {

    List<Category> Categories;
    LayoutInflater inflater;
    public CategoryAdapter(List<Category> categories, Context c) {
        Categories = categories;
        inflater = LayoutInflater.from(c);
    }

    public String getStringName(int position) {
        return Categories.get(position).name;
    }

    @Override
    public int getCount() {
        return Categories.size();
    }

    @Override
    public Object getItem(int position) {
        return Categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Categories.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textview = null;
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
            textview = (TextView) convertView.findViewById(android.R.id.text1);
            textview.setTextColor(Color.WHITE);
            convertView = textview;
        } else {
            textview = (TextView) convertView.findViewById(android.R.id.text1);
        }
        textview.setText(Categories.get(position).name);
        return convertView;
    }
}
