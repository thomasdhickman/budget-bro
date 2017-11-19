package com.thickman.budget.realm;

import android.content.Context;

import com.thickman.budget.object.Account;

import io.realm.RealmResults;

public class RealmAccountsAdapter extends RealmModelAdapter<Account> {

    public RealmAccountsAdapter(Context context, RealmResults<Account> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}