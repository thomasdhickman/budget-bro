package com.thickman.budget.realm;

import com.thickman.budget.object.Account;
import com.thickman.budget.object.Transaction;

import io.realm.Realm;
import io.realm.RealmResults;

public class AccountRealmController {

    private Realm realm;
    private static AccountRealmController instance;

    public AccountRealmController() {
        realm = Realm.getDefaultInstance();
    }

    public static AccountRealmController getInstance() {
        if (instance == null) {
            instance = new AccountRealmController();
        }
        return instance;
    }

    public void clearAll() {
        realm.beginTransaction();
        realm.clear(Account.class);
        realm.commitTransaction();
    }

    public void addAccount(Account account) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(account);
        realm.commitTransaction();
    }

    public void removeAccount(final Account account) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Account> rows = realm.where(Account.class).equalTo("id", account.getId()).findAll();
                rows.clear();
            }
        });
    }

    public RealmResults<Account> getAccounts() {
        return realm.where(Account.class).findAllSorted("accountName");
    }

    public float getAccountBalance(Account account) {
        float ret = 0.0f;
        RealmResults<Transaction> accountTransactions = realm.where(Transaction.class).equalTo("isRecurring", false ).equalTo("account", account.getId()).findAll();
        for (Transaction t : accountTransactions) {
            ret += t.getAmount();
        }
        return ret;
    }
}
