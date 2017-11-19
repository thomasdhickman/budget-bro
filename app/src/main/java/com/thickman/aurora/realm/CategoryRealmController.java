package com.thickman.aurora.realm;

import com.thickman.aurora.object.Category;

import io.realm.Realm;
import io.realm.RealmResults;

public class CategoryRealmController {

    private Realm realm;
    private static CategoryRealmController instance;

    public CategoryRealmController() {
        realm = Realm.getDefaultInstance();
    }

    public static CategoryRealmController getInstance() {
        if (instance == null) {
            instance = new CategoryRealmController();
        }
        return instance;
    }

    public RealmResults<Category> getCategories() {
        return realm.where(Category.class).findAllSorted("name");
    }

    public Category getCategory(int id) {
        return realm.where(Category.class).equalTo("id", id).findFirst();
    }

    public void addCategory(Category category) {
        realm.beginTransaction();
        realm.copyToRealm(category);
        realm.commitTransaction();
    }

    public int getNextId() {
        return realm.where(Category.class).findAll().size() + 1;
    }
}
