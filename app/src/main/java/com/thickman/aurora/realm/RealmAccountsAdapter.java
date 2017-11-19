package com.thickman.aurora.realm;

import android.content.Context;

import com.thickman.aurora.object.Account;

import io.realm.RealmResults;

public class RealmAccountsAdapter extends RealmModelAdapter<Account> {

    public RealmAccountsAdapter(Context context, RealmResults<Account> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}