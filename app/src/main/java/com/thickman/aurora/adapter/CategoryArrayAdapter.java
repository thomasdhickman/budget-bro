package com.thickman.aurora.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.thickman.aurora.R;
import com.thickman.aurora.object.Category;

import java.util.List;

public class CategoryArrayAdapter extends ArrayAdapter<Category> {

    private TextView textView;
    private int resource;

    public CategoryArrayAdapter(Context context, int resource, List<Category> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCategoryView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCategoryView(position, convertView, parent);
    }

    @NonNull
    private View getCategoryView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
        }
        textView = (TextView) convertView.findViewById(R.id.text1);
        textView.setText(getItem(position).getName());
        return convertView;
    }
}