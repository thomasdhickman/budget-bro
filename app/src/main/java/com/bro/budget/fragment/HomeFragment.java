package com.thickman.budget.fragment;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thickman.budget.R;
import com.thickman.budget.adapter.CategoryViewAdapter;
import com.thickman.budget.dialog.AddTransactionDialog;
import com.thickman.budget.event.RefreshEvent;
import com.thickman.budget.object.BudgetItem;
import com.thickman.budget.realm.BudgetItemRealmController;
import com.thickman.budget.realm.CategoryRealmController;
import com.thickman.budget.realm.RealmCategoryAdapter;
import com.thickman.budget.util.TransactionUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class HomeFragment extends ActionFragment implements CategoryViewAdapter.OnCategoryClickedListener {

    private View rootView;
    private TextView balance;
    private RecyclerView recyclerView;
    private View balanceLayout;
    private CategoryViewAdapter budgetItemAdapter;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        balance = rootView.findViewById(R.id.toBeBudgeted);
        balanceLayout = rootView.findViewById(R.id.balance_layout);
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

    public void refresh() {
        float amount = getToBeBudgetedAmount();
        balanceLayout.setBackgroundColor(getResources().getColor(amount >= 0 ? R.color.nephritis : R.color.pomegranate));
        balance.setText(TransactionUtils.getAmountFormat(amount));
        setupAdapter();
        setupRecycler();
    }

    private float getToBeBudgetedAmount() {
        return BudgetItemRealmController.getInstance().getToBeBudgetedAmount();
    }

    @Override
    public void onAction() {
        AddTransactionDialog dialog = new AddTransactionDialog(getContext());
        dialog.show();
    }

    @Override
    public String getFragmentName() {
        return "Home";
    }

    private void setupRecycler() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = rootView.findViewById(R.id.budget);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(budgetItemAdapter);
    }

    private void setupAdapter() {
        RealmCategoryAdapter realmAdapter = new RealmCategoryAdapter(getContext(), CategoryRealmController.getInstance().getCategories(), true);
        budgetItemAdapter = new CategoryViewAdapter(getContext(), this);
        budgetItemAdapter.setRealmAdapter(realmAdapter);
        budgetItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBudgetItemClicked(BudgetItem budgetItem) {
        refresh();
    }

    @Override
    public void onBudgetItemLongClicked(BudgetItem budgetItem) {
        refresh();
    }
}