package com.bro.budget.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bro.budget.R;
import com.bro.budget.adapter.AccountRecyclerViewAdapter;
import com.bro.budget.object.Account;
import com.bro.budget.realm.AccountRealmController;
import com.bro.budget.realm.RealmAccountsAdapter;

import io.realm.RealmResults;

public class MyAccountsFragment extends ActionFragment implements AccountRecyclerViewAdapter.OnAccountClickListener {

    private View rootView;
    private AccountRecyclerViewAdapter accountsAdapter;

    public MyAccountsFragment() {

    }

    public static MyAccountsFragment newInstance() {
        return new MyAccountsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        refresh();
        return rootView;
    }

    private void refresh() {
        setupAdapter();
        setupRecycler();
    }

    private void setupRecycler() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView recyclerView = rootView.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(accountsAdapter);
    }

    private void setupAdapter() {
        RealmAccountsAdapter realmAdapter = new RealmAccountsAdapter(getContext(), getItems(), true);
        accountsAdapter = new AccountRecyclerViewAdapter(getContext(), this);
        accountsAdapter.setRealmAdapter(realmAdapter);
        accountsAdapter.notifyDataSetChanged();
    }

    private RealmResults<Account> getItems() {
        return AccountRealmController.getInstance().getAccounts();
    }

    @Override
    public void onAccountClicked(Account account) {
        refresh();
    }

    @Override
    public void onAccountLongClicked(Account account) {
        AccountRealmController.getInstance().removeAccount(account);
        Snackbar.make(rootView, "Account removed", Snackbar.LENGTH_LONG).show();
        refresh();
    }

    @Override
    public int getFragmentName() {
        return R.string.my_accounts;
    }
}