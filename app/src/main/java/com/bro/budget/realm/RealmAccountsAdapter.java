package com.bro.budget.realm;

import android.content.Context;

import com.bro.budget.object.Account;

import io.realm.RealmResults;

public class RealmAccountsAdapter extends RealmModelAdapter<Account> {

    public RealmAccountsAdapter(Context context, RealmResults<Account> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}