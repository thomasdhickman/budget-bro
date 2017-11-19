package com.thickman.aurora.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.thickman.aurora.R;
import com.thickman.aurora.object.Account;

import java.util.List;

public class AccountArrayAdapter extends ArrayAdapter<Account> {

    private TextView textView;
    private int resource;

    public AccountArrayAdapter(Context context, int resource, List<Account> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getAccountView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getAccountView(position, convertView, parent);
    }

    @NonNull
    private View getAccountView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
        }
        textView = (TextView) convertView.findViewById(R.id.text1);
        textView.setText(String.format("%s (%s)", getItem(position).getAccountName(), getItem(position).getAccountType()));
        return convertView;
    }
}