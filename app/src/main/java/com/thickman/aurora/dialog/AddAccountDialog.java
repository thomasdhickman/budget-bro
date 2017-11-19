package com.thickman.aurora.dialog;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.thickman.aurora.R;
import com.thickman.aurora.enums.AccountType;
import com.thickman.aurora.object.Account;
import com.thickman.aurora.object.Transaction;

import java.util.List;

public class AddAccountDialog extends BaseAddCancelDialog {

    private EditText accountName;
    private EditText startingBalance;
    private Spinner accountType;
    private AddAccountDialogListener listener;

    public AddAccountDialog(Context context, AddAccountDialogListener listener) {
        super(context);
        this.listener = listener;
        setTitle("Add Account");
    }

    private Account getAccount() {
        Account a = new Account();
        a.setId((int) System.currentTimeMillis());
        a.setAccountName(accountName.getText().toString());
        a.setAccountType(accountType.getSelectedItem().toString());
        return a;
    }

    @Nullable
    private Transaction getInitialTransaction(Account account) {
        if (getBalance() <= 0.0f) return null;
        Transaction t = new Transaction();
        t.setId((int) System.currentTimeMillis());
        t.setPayee("Initial balance");
        t.setRecurring(false);
        t.setIncome(true);
        t.setDate(System.currentTimeMillis());
        t.setCategory(10);
        t.setAmount(getBalance());
        t.setAccount(account.getId());
        return t;
    }

    private float getBalance() {
        float ret = 0.0f;
        try {
            ret = Float.valueOf(startingBalance.getText().toString());
        } catch (Exception ex) {
            Log.d("Not a float", ex.toString());
        }
        return ret;
    }

    private void setSpinner(List<String> data, Spinner spinner) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.layout_item, data);
        spinner.setAdapter(spinnerAdapter);
    }

    @Override
    protected void initViews() {
        accountName = (EditText) findViewById(R.id.name);
        startingBalance = (EditText) findViewById(R.id.toBeBudgeted);
        accountType = (Spinner) findViewById(R.id.account_types);
        setSpinner(AccountType.getAccountTypes(), accountType);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_add_account;
    }

    @Override
    protected void onAddButtonClicked() {
        if (listener != null) {
            Account a = getAccount();
            Transaction t = getInitialTransaction(a);
            listener.onAddButtonClicked(a, t);
        }
    }

    @Override
    protected void onCancelButtonClicked() {
        if (listener != null) {
            listener.onCancelButtonClicked();
        }
    }

    public interface AddAccountDialogListener {
        void onAddButtonClicked(Account account, @Nullable Transaction initialTransaction);

        void onCancelButtonClicked();
    }
}
