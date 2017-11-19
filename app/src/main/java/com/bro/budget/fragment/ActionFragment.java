package com.thickman.budget.fragment;

import android.app.Fragment;

public abstract class ActionFragment extends Fragment {
    public abstract void onAction();

    public abstract String getFragmentName();
}
