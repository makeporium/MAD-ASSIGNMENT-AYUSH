package com.example.q1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    Switch sw;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_settings);

        sp = getSharedPreferences("pref", MODE_PRIVATE);
        sw = findViewById(R.id.sw);
        sw.setChecked(sp.getBoolean("dark", false));

        sw.setOnCheckedChangeListener((v, on) -> {
            sp.edit().putBoolean("dark", on).apply();
            AppCompatDelegate.setDefaultNightMode(
                    on ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
            recreate();
        });
    }
}