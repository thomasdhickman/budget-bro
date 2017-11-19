package com.bro.budget.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.bro.budget.R;
import com.bro.budget.event.RefreshEvent;
import com.bro.budget.object.BudgetItem;
import com.bro.budget.object.Category;
import com.bro.budget.object.Transaction;
import com.bro.budget.realm.BudgetItemRealmController;
import com.bro.budget.realm.RealmRecyclerViewAdapter;
import com.bro.budget.realm.TransactionRealmController;
import com.bro.budget.util.TransactionUtils;

import org.greenrobot.eventbus.EventBus;

import io.realm.RealmResults;

public class CategoryViewAdapter extends RealmRecyclerViewAdapter<Category> {

    private Context context;
    private View rootView;
    private OnCategoryClickedListener listener;
    private int mSelected = -1;

    public CategoryViewAdapter(Context context, OnCategoryClickedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_budget_item, parent, false);
        return new AccountViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Category category = getItem(position);
        final BudgetItem budgetItem = BudgetItemRealmController.getInstance().getBudgetItemForCategory(category.getId());

        final AccountViewHolder viewHolder = (AccountViewHolder) holder;
        viewHolder.mItem = budgetItem;
        viewHolder.mCategory.setText(category.getName());
        viewHolder.setBudgeted(budgetItem == null ? 0 : budgetItem.getAmountBudgeted());

        float balance = getAmountSpent(budgetItem);
        viewHolder.mBalance.setTextColor(context.getResources().getColor(TransactionUtils.getAmountColor(balance)));
        viewHolder.mBalance.setText(TransactionUtils.getAmountFormat(budgetItem == null ? 0 : getAmountSpent(budgetItem)));

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.toggleEditMode();
                mSelected = holder.getAdapterPosition();
            }
        });

        viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onBudgetItemLongClicked(budgetItem);
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

    private float getAmountSpent(BudgetItem budgetItem) {
        RealmResults<Transaction> transactions
                = TransactionRealmController.getInstance().getTransactionsWithCategory(budgetItem.getCategory());

        float amountSpent = 0.0f;
        for (Transaction t : transactions) {
            amountSpent += t.getAmount();
        }
        return budgetItem.getAmountBudgeted() + amountSpent;
    }

    public interface OnCategoryClickedListener {
        void onBudgetItemClicked(BudgetItem budgetItem);

        void onBudgetItemLongClicked(BudgetItem budgetItem);
    }

    private class AccountViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mCategory;
        final TextView mBalance;
        final TextView mBudgeted;
        final EditText mBudgetedEditText;
        BudgetItem mItem;

        AccountViewHolder(View view) {
            super(view);
            mView = view;
            mCategory = view.findViewById(R.id.category);
            mBalance = view.findViewById(R.id.balance);
            mBudgeted = view.findViewById(R.id.budgeted);
            mBudgetedEditText = view.findViewById(R.id.budgetedEditText);
            mBudgetedEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            mBudgetedEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        exitEditMode();
                    }
                    return true;
                }
            });
        }

        void setBudgeted(float amount) {
            String amountText = TransactionUtils.getAmountFormat(amount);
            mBudgeted.setText(amountText);
            mBudgetedEditText.setText(amountText.replace("$", ""));
        }

        void enterEditMode() {
            mBudgetedEditText.setVisibility(View.VISIBLE);
            mBudgeted.setVisibility(View.GONE);
            mBudgetedEditText.requestFocus();
        }

        void exitEditMode() {
            mBudgetedEditText.setVisibility(View.GONE);
            mBudgeted.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mBudgetedEditText.getWindowToken(), 0);
            String amountString = mBudgetedEditText.getText().toString().replace("$", "");
            float amount = amountString.isEmpty() ? mItem.getAmountBudgeted() : Float.valueOf(amountString);
            setBudgeted(amount);
            BudgetItemRealmController.getInstance().updateBudgetItem(mItem.getId(), amount);
            EventBus.getDefault().post(new RefreshEvent());
        }

        void toggleEditMode() {
            if (mBudgetedEditText.getVisibility() == View.VISIBLE) {
                exitEditMode();
            } else {
                enterEditMode();
            }
        }
    }
}