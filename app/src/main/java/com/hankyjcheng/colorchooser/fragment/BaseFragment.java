package com.hankyjcheng.colorchooser.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.hankyjcheng.colorchooser.MainActivity;

public class BaseFragment extends Fragment {

    protected MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    protected ActionBar getSupportActionBar() {
        return getMainActivity().getSupportActionBar();
    }

}