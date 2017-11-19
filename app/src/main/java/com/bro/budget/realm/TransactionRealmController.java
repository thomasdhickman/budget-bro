package com.thickman.budget.realm;

import com.thickman.budget.object.Transaction;

import org.joda.time.DateTime;

import io.realm.Realm;
import io.realm.RealmResults;

public class TransactionRealmController {

    private final Realm realm;
    private static TransactionRealmController instance;

    public TransactionRealmController() {
        realm = Realm.getDefaultInstance();
    }

    public static TransactionRealmController getInstance() {
        if (instance == null) {
            instance = new TransactionRealmController();
        }
        return instance;
    }

    public void clearAll() {
        realm.beginTransaction();
        realm.clear(Transaction.class);
        realm.commitTransaction();
    }

    public RealmResults<Transaction> getPostedTransactions() {
        return realm.where(Transaction.class).equalTo("isRecurring", false).findAllSorted("date", false);
    }

    public RealmResults<Transaction> getRecurringTransactions() {
        return getRecurringTransactions(false);
    }

    public RealmResults<Transaction> getRecurringTransactions(boolean today) {
        if (today) {
            DateTime dateTime = new DateTime();
            return realm.where(Transaction.class).equalTo("isRecurring", true).equalTo("dayOfMonth", dateTime.getDayOfMonth()).findAllSorted("date");
        }
        return realm.where(Transaction.class).equalTo("isRecurring", true).findAllSorted("date");
    }

    public void addTransaction(Transaction transaction) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(transaction);
        realm.commitTransaction();
    }

    public void removeTransaction(final Transaction transaction) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Transaction> rows = realm.where(Transaction.class).equalTo("id", transaction.getId()).findAll();
                rows.clear();
            }
        });
    }

    public Transaction getTransaction(String id) {
        return realm.where(Transaction.class).equalTo("id", id).findFirst();
    }

    public RealmResults<Transaction> getTransactionsWithCategory(int spendingCategory) {
        return realm.where(Transaction.class).equalTo("category", spendingCategory).findAll();
    }

    public boolean hasTransactions() {
        return !realm.allObjects(Transaction.class).isEmpty();
    }
}