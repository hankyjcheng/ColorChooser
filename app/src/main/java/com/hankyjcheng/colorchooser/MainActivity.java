package com.hankyjcheng.colorchooser;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.hankyjcheng.colorchooser.databinding.ActivityMainBinding;
import com.hankyjcheng.colorchooser.fragment.MainFragment;
import com.hankyjcheng.colorchooser.utility.Pref;

public class MainActivity extends AppCompatActivity {

    private final int defaultColor = Color.parseColor("#3F51B5");
    private int color = defaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setHandler(this);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        binding.toolbarTitleTextView.setText(R.string.color_palette);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(Pref.PREF_APP, Context.MODE_PRIVATE);
        if (!prefs.contains(Pref.COLOR_HEX)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(Pref.COLOR_HEX, defaultColor);
            editor.apply();
        }
        else {
            color = prefs.getInt(Pref.COLOR_HEX, defaultColor);
        }

        if (savedInstanceState == null) {
            MainFragment fragment = MainFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}