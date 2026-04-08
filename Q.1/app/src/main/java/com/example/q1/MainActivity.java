package com.example.q1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    String[] cur = {"INR", "USD", "JPY", "EUR"};
    double[][] rate = {
            {1, 0.012, 1.77, 0.011},
            {83.5, 1, 147.8, 0.92},
            {0.565, 0.00677, 1, 0.00623},
            {90.6, 1.085, 160.4, 1}
    };

    Spinner s1, s2;
    EditText et;
    TextView tv;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle b) {
        sp = getSharedPreferences("pref", MODE_PRIVATE);
        if (sp.getBoolean("dark", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        s1 = findViewById(R.id.s1);
        s2 = findViewById(R.id.s2);
        et = findViewById(R.id.et);
        tv = findViewById(R.id.tv);

        ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cur);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(ad);
        s2.setAdapter(ad);
        s2.setSelection(1);

        findViewById(R.id.btn).setOnClickListener(v -> convert());
        findViewById(R.id.btnSwap).setOnClickListener(v -> swap());
        findViewById(R.id.btnSettings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    void convert() {
        String inp = et.getText().toString();
        if (inp.isEmpty()) { tv.setText("Enter amount"); return; }
        double amt = Double.parseDouble(inp);
        int f = s1.getSelectedItemPosition();
        int t = s2.getSelectedItemPosition();
        double res = amt * rate[f][t];
        tv.setText(String.format("%.4f %s", res, cur[t]));
    }

    void swap() {
        int p = s1.getSelectedItemPosition();
        s1.setSelection(s2.getSelectedItemPosition());
        s2.setSelection(p);
        convert();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sp.getBoolean("dark", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}