package com.bro.budget.fragment;

import android.app.Fragment;
import android.support.annotation.StringRes;

public abstract class ActionFragment extends Fragment {

    @StringRes
    public abstract int getFragmentName();
}
