package com.thickman.budget.object;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BudgetItem extends RealmObject {

    @PrimaryKey
    private int id;
    private int category;
    private float amountBudgeted;
    private int monthOfYear;
    private int year;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public float getAmountBudgeted() {
        return amountBudgeted;
    }

    public void setAmountBudgeted(float amountBudgeted) {
        this.amountBudgeted = amountBudgeted;
    }

    public int getMonthOfYear() {
        return monthOfYear;
    }

    public void setMonthOfYear(int monthOfYear) {
        this.monthOfYear = monthOfYear;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
