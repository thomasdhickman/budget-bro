package com.thickman.budget.realm;

import android.content.Context;

import com.thickman.budget.object.BudgetItem;

import io.realm.RealmResults;

public class RealmBudgetItemAdapter extends RealmModelAdapter<BudgetItem> {

    public RealmBudgetItemAdapter(Context context, RealmResults<BudgetItem> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}