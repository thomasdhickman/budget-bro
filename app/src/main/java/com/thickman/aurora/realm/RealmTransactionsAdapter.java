package com.thickman.aurora.realm;

import android.content.Context;

import com.thickman.aurora.object.Transaction;

import io.realm.RealmResults;

public class RealmTransactionsAdapter extends RealmModelAdapter<Transaction> {

    public RealmTransactionsAdapter(Context context, RealmResults<Transaction> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}