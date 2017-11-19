package com.bro.budget.dialog;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.bro.budget.R;
import com.bro.budget.enums.AccountType;
import com.bro.budget.event.RefreshEvent;
import com.bro.budget.object.Account;
import com.bro.budget.object.Transaction;
import com.bro.budget.realm.AccountRealmController;
import com.bro.budget.realm.TransactionRealmController;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class AddAccountDialog extends BaseAddCancelDialog {

    private EditText accountName;
    private EditText startingBalance;
    private Spinner accountType;

    public AddAccountDialog(Context context) {
        super(context);
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
        t.setCategory(0);
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
        accountName = findViewById(R.id.name);
        startingBalance = findViewById(R.id.toBeBudgeted);
        accountType = findViewById(R.id.account_types);
        setSpinner(AccountType.getAccountTypes(), accountType);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_add_account;
    }

    @Override
    protected void onAddButtonClicked() {
        Account account = getAccount();
        Transaction initialTransaction = getInitialTransaction(account);
        AccountRealmController.getInstance().addAccount(account);
        if (initialTransaction != null) {
            TransactionRealmController.getInstance().addTransaction(initialTransaction);
        }
        EventBus.getDefault().post(new RefreshEvent());
    }

    @Override
    protected void onCancelButtonClicked() {

    }
}
