package com.bro.budget.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bro.budget.R;
import com.bro.budget.object.Category;
import com.bro.budget.object.Transaction;
import com.bro.budget.realm.CategoryRealmController;
import com.bro.budget.realm.RealmRecyclerViewAdapter;
import com.bro.budget.util.TransactionUtils;

public class TransactionRecyclerViewAdapter extends RealmRecyclerViewAdapter<Transaction> {

    private final Context context;
    private final OnTransactionClickListener listener;
    private boolean showRecurringFrequency;

    public TransactionRecyclerViewAdapter(Context context, OnTransactionClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Transaction transaction = getItem(position);

        TransactionViewHolder viewHolder = (TransactionViewHolder) holder;
        viewHolder.mItem = transaction;
        viewHolder.mPayeeView.setText(transaction.getPayee());
        viewHolder.mDueDateView.setText(getDateText(transaction));
        viewHolder.mAmountDueView.setText(TransactionUtils.getAmountFormat(transaction.getAmount()));
        viewHolder.mAmountDueView.setTextColor(context.getColor(TransactionUtils.getAmountColor(transaction.getAmount())));
        Category category = CategoryRealmController.getInstance().getCategory(transaction.getCategory());
        viewHolder.mCategoryView.setText(category != null ? category.getName() : "Income");

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onTransactionClicked(transaction);
                }
            }
        });

        viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != listener) {
                    listener.onTransactionLongClicked(transaction);

                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }

    private String getDateText(Transaction t) {
        return showRecurringFrequency ? TransactionUtils.getRecurringDateText(t.getDate()) : TransactionUtils.getDate(t.getDate());
    }

    public void setShowRecurringFrequency(boolean showRecurringFrequency) {
        this.showRecurringFrequency = showRecurringFrequency;
    }

    public interface OnTransactionClickListener {
        void onTransactionClicked(Transaction transaction);

        void onTransactionLongClicked(Transaction transaction);
    }

    private class TransactionViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mPayeeView;
        final TextView mAmountDueView;
        final TextView mDueDateView;
        final TextView mCategoryView;
        Transaction mItem;

        TransactionViewHolder(View view) {
            super(view);
            mView = view;
            mPayeeView = view.findViewById(R.id.payee);
            mAmountDueView = view.findViewById(R.id.amount_due);
            mDueDateView = view.findViewById(R.id.due_date);
            mCategoryView = view.findViewById(R.id.category);
        }
    }
}