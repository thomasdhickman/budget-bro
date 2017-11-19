package com.thickman.budget.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thickman.budget.R;
import com.thickman.budget.adapter.AccountRecyclerViewAdapter;
import com.thickman.budget.dialog.AddAccountDialog;
import com.thickman.budget.object.Account;
import com.thickman.budget.object.Transaction;
import com.thickman.budget.realm.AccountRealmController;
import com.thickman.budget.realm.RealmAccountsAdapter;
import com.thickman.budget.realm.TransactionRealmController;

import io.realm.RealmResults;

public class MyAccountsFragment extends ActionFragment implements AccountRecyclerViewAdapter.OnAccountClickListener,
        AddAccountDialog.AddAccountDialogListener {

    private View rootView;
    private RecyclerView recyclerView;
    private AccountRecyclerViewAdapter accountsAdapter;

    public MyAccountsFragment() {

    }

    public static MyAccountsFragment newInstance() {
        MyAccountsFragment fragment = new MyAccountsFragment();
        return fragment;
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
        recyclerView = (RecyclerView) rootView.findViewById(R.id.list);
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
    public void onAction() {
        AddAccountDialog dialog = new AddAccountDialog(getContext(), this);
        dialog.show();
    }

    @Override
    public String getFragmentName() {
        return "My Accounts";
    }

    @Override
    public void onAddButtonClicked(Account account, Transaction initialTransaction) {
        AccountRealmController.getInstance().addAccount(account);
        if (initialTransaction != null) {
            TransactionRealmController.getInstance().addTransaction(initialTransaction);
        }
        refresh();
    }

    @Override
    public void onCancelButtonClicked() {
        refresh();
    }
}
