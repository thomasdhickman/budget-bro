package com.thickman.budget.enums;

import java.util.ArrayList;
import java.util.List;

public enum AccountType {

    CHECKING("Checking"),
    SAVINGS("Savings"),
    CREDIT("Credit Card");

    private String name;

    AccountType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static List<String> getAccountTypes() {
        List<String> accountTypes = new ArrayList<>();
        accountTypes.add(CHECKING.getName());
        accountTypes.add(SAVINGS.getName());
        accountTypes.add(CREDIT.getName());
        return accountTypes;
    }
}