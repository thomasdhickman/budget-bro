package com.thickman.budget.realm;

import android.content.Context;

import com.thickman.budget.object.Category;

import io.realm.RealmResults;

public class RealmCategoryAdapter extends RealmModelAdapter<Category> {

    public RealmCategoryAdapter(Context context, RealmResults<Category> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}