package com.example.sensors;

import android.hardware.*;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sm;
    TextView tvAcc, tvLight, tvProx;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        tvAcc = findViewById(R.id.tvAcc);
        tvLight = findViewById(R.id.tvLight);
        tvProx = findViewById(R.id.tvProx);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reg(Sensor.TYPE_ACCELEROMETER);
        reg(Sensor.TYPE_LIGHT);
        reg(Sensor.TYPE_PROXIMITY);
    }

    void reg(int t) {
        Sensor s = sm.getDefaultSensor(t);
        if (s != null) sm.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent e) {
        int t = e.sensor.getType();
        if (t == Sensor.TYPE_ACCELEROMETER) {
            tvAcc.setText(String.format("X: %.2f\nY: %.2f\nZ: %.2f", e.values[0], e.values[1], e.values[2]));
        } else if (t == Sensor.TYPE_LIGHT) {
            tvLight.setText(String.format("%.1f lx", e.values[0]));
        } else if (t == Sensor.TYPE_PROXIMITY) {
            tvProx.setText(e.values[0] == 0 ? "Near" : "Far");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor s, int a) {}
}