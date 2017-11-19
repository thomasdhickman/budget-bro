package com.thickman.budget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thickman.budget.R;

public abstract class BaseAddCancelDialog extends Dialog implements View.OnClickListener {

    private Button add;
    private Button cancel;
    private TextView title;
    private LinearLayout dialogContent;

    public BaseAddCancelDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_add_cancel);
        dialogContent = (LinearLayout) findViewById(R.id.dialog_content);
        initButtons();
        inflateChildLayout();
        initViews();
    }

    private void initButtons() {
        add = (Button) findViewById(R.id.add);
        cancel = (Button) findViewById(R.id.cancel);
        add.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    private void inflateChildLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View inflatedLayout = inflater.inflate(getLayoutResId(), null, false);
        dialogContent.addView(inflatedLayout);
    }

    protected void setTitle(String text) {
        title = (TextView) findViewById(R.id.title);
        title.setText(text);
    }

    protected void setAddButtonText(String text) {
        add.setText(text);
    }

    protected abstract void initViews();

    @LayoutRes
    protected abstract int getLayoutResId();

    protected abstract void onAddButtonClicked();

    protected abstract void onCancelButtonClicked();

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.add:
                onAddButtonClicked();
                break;
            case R.id.cancel:
                onCancelButtonClicked();
                break;
        }
        dismiss();
    }
}
