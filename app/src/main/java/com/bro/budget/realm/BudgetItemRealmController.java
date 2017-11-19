package com.bro.budget.realm;

import com.bro.budget.object.BudgetItem;
import com.bro.budget.object.Transaction;

import org.joda.time.DateTime;

import io.realm.Realm;
import io.realm.RealmResults;

public class BudgetItemRealmController {

    private Realm realm;
    private static BudgetItemRealmController instance;

    public BudgetItemRealmController() {
        realm = Realm.getDefaultInstance();
    }

    public static BudgetItemRealmController getInstance() {
        if (instance == null) {
            instance = new BudgetItemRealmController();
        }
        return instance;
    }

    public BudgetItem getBudgetItemForCategory(int categoryId) {
        BudgetItem item = realm.where(BudgetItem.class).equalTo("category", categoryId).findFirst();
        if (item == null) {
            DateTime dateTime = new DateTime();
            BudgetItem budgetItem = new BudgetItem();
            budgetItem.setId((int) System.nanoTime());
            budgetItem.setAmountBudgeted(0);
            budgetItem.setCategory(categoryId);
            budgetItem.setMonthOfYear(dateTime.getYear());
            budgetItem.setMonthOfYear(dateTime.getMonthOfYear());
            addBudgetItem(budgetItem);
            return realm.where(BudgetItem.class).equalTo("category", categoryId).findFirst();
        }
        return item;
    }

    public RealmResults<BudgetItem> getBudgetItems() {
        return realm.where(BudgetItem.class).findAllSorted("id");
    }

    public BudgetItem getCategory(int id) {
        return realm.where(BudgetItem.class).equalTo("id", id).findFirst();
    }

    public void addBudgetItem(BudgetItem budgetItem) {
        realm.beginTransaction();
        realm.copyToRealm(budgetItem);
        realm.commitTransaction();
    }

    public void updateBudgetItem(int id, float amount) {
        BudgetItem item = realm.where(BudgetItem.class).equalTo("id", id).findFirst();
        realm.beginTransaction();
        item.setAmountBudgeted(amount);
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
    }

    public float getToBeBudgetedAmount() {
        float amount = 0;
        RealmResults<BudgetItem> budgetItems = getBudgetItems();
        RealmResults<Transaction> transactions = TransactionRealmController.getInstance().getPostedTransactions();
        for (Transaction transaction : transactions) {
            if (transaction.isIncome()) {
                amount += transaction.getAmount();
            }
        }
        for (BudgetItem budgetItem : budgetItems) {
            amount -= budgetItem.getAmountBudgeted();
        }
        return amount;
    }
}
