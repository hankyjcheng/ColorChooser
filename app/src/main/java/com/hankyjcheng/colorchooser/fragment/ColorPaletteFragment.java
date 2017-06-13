package com.hankyjcheng.colorchooser.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;

import com.hankyjcheng.colorchooser.R;
import com.hankyjcheng.colorchooser.databinding.FragmentColorPaletteBinding;
import com.hankyjcheng.colorchooser.listener.ColorValueListener;
import com.hankyjcheng.colorchooser.listener.ColorWheelListener;
import com.hankyjcheng.colorchooser.utility.Pref;
import com.hankyjcheng.colorchooser.utility.Utils;

public class ColorPaletteFragment extends BaseFragment implements ColorValueListener, ColorWheelListener {

    private FragmentColorPaletteBinding binding;
    private int color = -1;

    public static ColorPaletteFragment newInstance() {
        return new ColorPaletteFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_color_palette, container, false);

        binding.colorWheelView.attachListener(this);
        binding.colorWheelView.attachListener(binding.colorValueView);
        binding.colorValueView.attachListener(this);
        binding.redSeekBar.setMax(255);
        binding.redSeekBar.setOnSeekBarChangeListener(redSeekBarListener);
        binding.greenSeekBar.setMax(255);
        binding.greenSeekBar.setOnSeekBarChangeListener(greenSeekBarListener);
        binding.blueSeekBar.setMax(255);
        binding.blueSeekBar.setOnSeekBarChangeListener(blueSeekBarListener);

        color = getMainActivity().getColor();
        ViewTreeObserver colorWheelObserver = binding.colorWheelView.getViewTreeObserver();
        colorWheelObserver.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.colorWheelView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                binding.colorWheelView.setColor(color);
            }
        });
        ViewTreeObserver colorValueObserver = binding.colorValueView.getViewTreeObserver();
        colorValueObserver.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.colorValueView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                binding.colorValueView.setColor(color);
            }
        });
        binding.colorView.setBackgroundColor(color);
        setSeekBarColor(color);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel_light_24dp);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_event:
                save();
                return true;
            case android.R.id.home:
                Utils.hideKeyboard(getContext());
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onColorWheelChange(int color) {
        onColorChange(color);
    }

    @Override
    public void onColorWheelChangeFinish() {
        binding.redSeekBar.setOnSeekBarChangeListener(redSeekBarListener);
        binding.greenSeekBar.setOnSeekBarChangeListener(greenSeekBarListener);
        binding.blueSeekBar.setOnSeekBarChangeListener(blueSeekBarListener);
    }

    @Override
    public void onColorValueChange(int color) {
        onColorChange(color);
    }

    @Override
    public void onColorValueChangeFinish() {
        binding.redSeekBar.setOnSeekBarChangeListener(redSeekBarListener);
        binding.greenSeekBar.setOnSeekBarChangeListener(greenSeekBarListener);
        binding.blueSeekBar.setOnSeekBarChangeListener(blueSeekBarListener);
    }

    private SeekBar.OnSeekBarChangeListener redSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            color = Color.argb(255, progress, binding.greenSeekBar.getProgress(), binding.blueSeekBar.getProgress());
            binding.redTextView.setText(String.valueOf(progress));
            binding.colorView.setBackgroundColor(color);
            binding.colorWheelView.setColor(color);
            binding.colorValueView.setColor(color);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private SeekBar.OnSeekBarChangeListener greenSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            color = Color.argb(255, binding.redSeekBar.getProgress(), progress, binding.blueSeekBar.getProgress());
            binding.greenTextView.setText(String.valueOf(progress));
            binding.colorView.setBackgroundColor(color);
            binding.colorWheelView.setColor(color);
            binding.colorValueView.setColor(color);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private SeekBar.OnSeekBarChangeListener blueSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            color = Color.argb(255, binding.redSeekBar.getProgress(), binding.greenSeekBar.getProgress(), progress);
            binding.blueTextView.setText(String.valueOf(progress));
            binding.colorView.setBackgroundColor(color);
            binding.colorWheelView.setColor(color);
            binding.colorValueView.setColor(color);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private void save() {
        getMainActivity().setColor(color);
        SharedPreferences prefs = getContext().getSharedPreferences(Pref.PREF_APP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Pref.COLOR_HEX, color);
        editor.apply();

        Utils.hideKeyboard(getContext());
        getActivity().onBackPressed();
    }

    private void onColorChange(int color) {
        binding.redSeekBar.setOnSeekBarChangeListener(null);
        binding.greenSeekBar.setOnSeekBarChangeListener(null);
        binding.blueSeekBar.setOnSeekBarChangeListener(null);

        this.color = color;
        binding.colorView.setBackgroundColor(color);
        setSeekBarColor(color);
    }

    private void setSeekBarColor(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        binding.redSeekBar.setProgress(red);
        binding.redTextView.setText(String.valueOf(red));
        binding.greenSeekBar.setProgress(green);
        binding.greenTextView.setText(String.valueOf(green));
        binding.blueSeekBar.setProgress(blue);
        binding.blueTextView.setText(String.valueOf(blue));
    }

}