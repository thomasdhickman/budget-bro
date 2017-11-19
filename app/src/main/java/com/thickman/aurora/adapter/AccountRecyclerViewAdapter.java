package com.thickman.aurora.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thickman.aurora.R;
import com.thickman.aurora.object.Account;
import com.thickman.aurora.realm.AccountRealmController;
import com.thickman.aurora.realm.RealmRecyclerViewAdapter;
import com.thickman.aurora.util.TransactionUtils;

public class AccountRecyclerViewAdapter extends RealmRecyclerViewAdapter<Account> {

    private Context context;
    private OnAccountClickListener listener;

    public AccountRecyclerViewAdapter(Context context, AccountRecyclerViewAdapter.OnAccountClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_account, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Account account = getItem(position);
        float accountBalance = AccountRealmController.getInstance().getAccountBalance(account);

        AccountViewHolder viewHolder = (AccountViewHolder) holder;
        viewHolder.mItem = account;
        viewHolder.mNameView.setText(account.getAccountName());
        viewHolder.mTypeView.setText(account.getAccountType());
        viewHolder.mBalanceView.setText(TransactionUtils.getAmountFormat(accountBalance));
        viewHolder.mBalanceView.setTextColor(context.getColor(TransactionUtils.getAmountColor(accountBalance)));

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAccountClicked(account);
                }
            }
        });

        viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onAccountLongClicked(account);
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

    private class AccountViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mNameView;
        final TextView mTypeView;
        final TextView mBalanceView;
        Account mItem;

        AccountViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name);
            mTypeView = (TextView) view.findViewById(R.id.type);
            mBalanceView = (TextView) view.findViewById(R.id.toBeBudgeted);
        }
    }

    public interface OnAccountClickListener {
        void onAccountClicked(Account account);

        void onAccountLongClicked(Account account);
    }
}