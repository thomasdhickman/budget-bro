package com.thickman.budget.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thickman.budget.R;
import com.thickman.budget.adapter.TransactionRecyclerViewAdapter;
import com.thickman.budget.dialog.AddTransactionDialog;
import com.thickman.budget.event.RefreshEvent;
import com.thickman.budget.object.Transaction;
import com.thickman.budget.realm.RealmTransactionsAdapter;
import com.thickman.budget.realm.TransactionRealmController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.realm.RealmResults;

public class TransactionListFragment extends ActionFragment implements TransactionRecyclerViewAdapter.OnTransactionClickListener {

    private TransactionRecyclerViewAdapter transactionsAdapter;
    private RecyclerView recyclerView;
    private View rootView;

    private static String KEY_LIST_TYPE = "KEY_LIST_TYPE";
    private static int LIST_TYPE_POSTED = 0;
    private static int LIST_TYPE_RECURRING = 1;

    public TransactionListFragment() {
        Log.d("TransactionList", "");
    }

    public static TransactionListFragment newPostedTransactionInstance() {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, LIST_TYPE_POSTED);
        return newInstance(args);
    }

    public static TransactionListFragment newRecurringTransactionInstance() {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, LIST_TYPE_RECURRING);
        return newInstance(args);
    }

    private static TransactionListFragment newInstance(Bundle args) {
        TransactionListFragment fragment = new TransactionListFragment();
        fragment.setArguments(args);
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
        EventBus.getDefault().register(this);
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {
        refresh();
    }

    private void refresh() {
        setupAdapter();
        setupRecycler();
    }

    private void setupRecycler() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = rootView.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(transactionsAdapter);
    }

    private void setupAdapter() {
        RealmTransactionsAdapter realmAdapter = new RealmTransactionsAdapter(getContext(), getItems(), true);
        transactionsAdapter = new TransactionRecyclerViewAdapter(getContext(), this);
        transactionsAdapter.setRealmAdapter(realmAdapter);
        transactionsAdapter.setShowRecurringFrequency(isRecurringTransactionList());
        transactionsAdapter.notifyDataSetChanged();
    }

    private RealmResults<Transaction> getItems() {
        return isRecurringTransactionList() ? getRecurringTransactions() : getPostedTransactions();
    }

    private RealmResults<Transaction> getRecurringTransactions() {
        return TransactionRealmController.getInstance().getRecurringTransactions();
    }

    private RealmResults<Transaction> getPostedTransactions() {
        return TransactionRealmController.getInstance().getPostedTransactions();
    }

    @Override
    public void onTransactionClicked(Transaction transaction) {
        if (isRecurringTransactionList()) {
            showPostRecurringDialog(transaction);
        } else {
            showAddTransactionDialog(transaction, false);
        }
    }

    private void showAddTransactionDialog(Transaction transaction, boolean postRecurring) {
        AddTransactionDialog dialog = new AddTransactionDialog(getContext(), transaction, postRecurring);
        dialog.show();
    }

    @Override
    public void onTransactionLongClicked(final Transaction transaction) {
        TransactionRealmController.getInstance().removeTransaction(transaction);
        Snackbar.make(rootView, "Transaction deleted", Snackbar.LENGTH_LONG).show();
        refresh();
    }

    private void showPostRecurringDialog(final Transaction transaction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Do you want to post this transaction?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showAddTransactionDialog(transaction, true);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onAction() {
        AddTransactionDialog dialog = new AddTransactionDialog(getContext());
        dialog.show();
    }

    @Override
    public String getFragmentName() {
        return isRecurringTransactionList() ? "Recurring Transactions" : "Posted Transactions";
    }

    private boolean isRecurringTransactionList() {
        return LIST_TYPE_RECURRING == getArguments().getInt(KEY_LIST_TYPE);
    }
}