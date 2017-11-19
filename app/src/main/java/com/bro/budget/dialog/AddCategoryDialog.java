package com.thickman.budget.dialog;

import android.content.Context;
import android.widget.EditText;

import com.thickman.budget.R;
import com.thickman.budget.event.RefreshEvent;
import com.thickman.budget.object.Category;
import com.thickman.budget.realm.CategoryRealmController;

import org.greenrobot.eventbus.EventBus;

public class AddCategoryDialog extends BaseAddCancelDialog {

    private EditText categoryName;

    public AddCategoryDialog(Context context) {
        super(context);
        setTitle("New Category");
    }

    private Category getCategory() {
        int addedCategoryId = CategoryRealmController.getInstance().getNextId();
        Category c = new Category();
        c.setId(addedCategoryId);
        c.setName(categoryName.getText().toString());
        return c;
    }

    @Override
    protected void initViews() {
        categoryName = findViewById(R.id.category_name);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_add_category;
    }

    @Override
    protected void onAddButtonClicked() {
        Category category = getCategory();
        CategoryRealmController.getInstance().addCategory(category);
        EventBus.getDefault().post(new RefreshEvent());
    }

    @Override
    protected void onCancelButtonClicked() {

    }
}