package com.bro.budget.realm;

import android.content.Context;

import com.bro.budget.object.Transaction;

import io.realm.RealmResults;

public class RealmTransactionsAdapter extends RealmModelAdapter<Transaction> {

    public RealmTransactionsAdapter(Context context, RealmResults<Transaction> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}