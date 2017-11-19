package com.thickman.budget.dialog;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.thickman.budget.R;
import com.thickman.budget.enums.RecurringFrequency;
import com.thickman.budget.event.RefreshEvent;
import com.thickman.budget.object.Account;
import com.thickman.budget.object.Category;
import com.thickman.budget.object.Transaction;
import com.thickman.budget.realm.AccountRealmController;
import com.thickman.budget.realm.CategoryRealmController;
import com.thickman.budget.realm.TransactionRealmController;
import com.thickman.budget.util.TransactionUtils;
import com.thickman.budget.adapter.AccountArrayAdapter;
import com.thickman.budget.adapter.CategoryArrayAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;

public class AddTransactionDialog extends BaseAddCancelDialog {

    private Transaction transaction;
    private boolean editMode;
    private AccountArrayAdapter accountsAdapter;
    private CategoryArrayAdapter categoriesAdapter;
    private boolean postRecurring;

    private EditText amount;
    private EditText payee;
    private Spinner accounts;
    private DatePicker datePicker;
    private Spinner categories;
    private RadioButton income;
    private CheckBox recurring;
    private RadioGroup recurringRadioGroup;
    private RadioButton week;
    private RadioButton biWeekly;
    private RadioButton month;

    public AddTransactionDialog(Context context) {
        this(context, null, false);
    }

    public AddTransactionDialog(Context context, Transaction transaction, boolean postRecurring) {
        super(context);
        this.transaction = transaction;
        this.editMode = transaction != null;
        this.postRecurring = postRecurring;
        setTransaction();
        setTitle(editMode ? "Edit Transaction" : "Add Transaction");
        setAddButtonText(editMode ? "Submit" : "Add");
    }

    @Override
    protected void initViews() {
        amount = findViewById(R.id.amount);
        payee = findViewById(R.id.payee);
        accounts = findViewById(R.id.accounts);
        datePicker = findViewById(R.id.date_picker);
        categories = findViewById(R.id.categories);
        income = findViewById(R.id.income);
        recurring = findViewById(R.id.recurring);
        recurringRadioGroup = findViewById(R.id.recurring_radio_group);
        week = findViewById(R.id.week);
        biWeekly = findViewById(R.id.bi_weekly);
        month = findViewById(R.id.month);

        updateAmountTextColor(false);

        income.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                categories.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                updateAmountTextColor(isChecked);
            }
        });
        setAccountSpinner(AccountRealmController.getInstance().getAccounts(), accounts);
        setCategorySpinner(CategoryRealmController.getInstance().getCategories(), categories);

        recurring.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_add_transaction;
    }

    @Override
    protected void onAddButtonClicked() {
        TransactionRealmController.getInstance().addTransaction(getTransaction());
        EventBus.getDefault().post(new RefreshEvent());
    }

    @Override
    protected void onCancelButtonClicked() {
        dismiss();
    }

    private void updateAmountTextColor(boolean isIncome) {
        amount.setTextColor(getContext().getColor(isIncome ? R.color.transaction_green : R.color.transaction_red));
    }

    private void setTransaction() {
        if (transaction == null) return;
        amount.setText(TransactionUtils.getAmountFormat(transaction.getAmount(), false));
        payee.setText(transaction.getPayee());
        income.setChecked(transaction.isIncome());
        setDate();
        setRecurring();
    }

    private Transaction getTransaction() {
        Transaction t = new Transaction();
        t.setId(getId());
        t.setAmount(getAmount());
        t.setPayee(payee.getText().toString());
        t.setDate(getDate());
        t.setDayOfMonth(datePicker.getDayOfMonth());
        t.setMonthOfYear(datePicker.getMonth());
        t.setYear(datePicker.getYear());
        t.setCategory(getCategory());
        t.setIncome(income.isChecked());
        t.setRecurring(!postRecurring && recurring.isChecked());
        t.setRecurringFrequency(getRecurringFrequency());
        t.setAccount(getAccount());
        return t;
    }

    private int getCategory() {
        return income.isChecked() ? 10 : ((Category) categories.getSelectedItem()).getId();
    }

    private void setAccountSpinner(List<Account> data, Spinner spinner) {
        accountsAdapter = new AccountArrayAdapter(getContext(), R.layout.layout_item, data);
        spinner.setAdapter(accountsAdapter);
    }

    private void setCategorySpinner(List<Category> data, Spinner spinner) {
        categoriesAdapter = new CategoryArrayAdapter(getContext(), R.layout.layout_item, data);
        spinner.setAdapter(categoriesAdapter);
        spinner.setSelection(getSelectedSpinnerItemPosition(data));
    }

    private int getSelectedSpinnerItemPosition(List<Category> data) {
        if (transaction == null) return 0;
        int index = 0;
        for (int i = 0; i < data.size(); i++) {
            if (transaction.getCategory() == data.get(i).getId()) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void setDate() {
        int[] dateSplit = TransactionUtils.splitDateInt(transaction.getDate());
        datePicker.init(dateSplit[2], dateSplit[0]-1, dateSplit[1], null);
    }

    private void setRecurring() {
        recurring.setChecked(!postRecurring && transaction.isRecurring());
        recurring.setVisibility(postRecurring ? View.GONE : View.VISIBLE);
        if (postRecurring) return;
        if (transaction.getRecurringFrequency().equals(RecurringFrequency.WEEK.name())) {
            week.setChecked(true);
        } else if (transaction.getRecurringFrequency().equals(RecurringFrequency.BI_WEEKLY.name())) {
            biWeekly.setChecked(true);
        } else if (transaction.getRecurringFrequency().equals(RecurringFrequency.MONTH.name())) {
            month.setChecked(true);
        }
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.recurring) {
                // recurringRadioGroup.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        }
    };

    private int getId() {
        if (postRecurring) return (int) System.currentTimeMillis();
        return (transaction == null)? (int) System.currentTimeMillis() : transaction.getId();
    }

    private float getAmount() {
        float amt = 0f;
        try {
            amt = Float.valueOf(amount.getText().toString());
        } catch (Exception ex) {
            Log.d("Not a float", ex.toString());
        }

        if (!income.isChecked()) amt = amt * -1;

        return amt;
    }

    private long getDate() {
        Date date = new Date(datePicker.getYear() - 1900, datePicker.getMonth(), datePicker.getDayOfMonth());
        return date.getTime();
    }

    private String getRecurringFrequency() {
        if (postRecurring) {
            RecurringFrequency.NONE.name();
        } else if (week.isChecked()) {
            return RecurringFrequency.WEEK.name();
        } else if (biWeekly.isChecked()) {
            return RecurringFrequency.BI_WEEKLY.name();
        } else if (month.isChecked()) {
            return RecurringFrequency.MONTH.name();
        }
        return RecurringFrequency.NONE.name();
    }

    private int getAccount() {
        return accountsAdapter.getItem(accounts.getSelectedItemPosition()).getId();
    }
}
