package com.hankyjcheng.colorchooser.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hankyjcheng.colorchooser.R;
import com.hankyjcheng.colorchooser.databinding.FragmentMainBinding;

public class MainFragment extends BaseFragment {

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentMainBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_main, container, false);
        binding.chooseColorButton.setOnClickListener(chooseColorListener);
        binding.colorView.setBackgroundColor(getMainActivity().getColor());
        return binding.getRoot();
    }

    private View.OnClickListener chooseColorListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ColorPaletteFragment fragment = ColorPaletteFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(MainFragment.class.toString())
                    .commit();
        }
    };

}