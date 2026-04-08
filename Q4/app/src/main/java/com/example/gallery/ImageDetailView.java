package com.example.gallery;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageDetailActivity extends AppCompatActivity {

    File img;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_image_detail);

        String p = getIntent().getStringExtra("path");
        img = new File(p);

        ImageView iv = findViewById(R.id.imgDetail);
        iv.setImageURI(Uri.fromFile(img));

        ((TextView) findViewById(R.id.tvName)).setText(img.getName());
        ((TextView) findViewById(R.id.tvPath)).setText(img.getAbsolutePath());

        long kb = img.length() / 1024;
        String sz = kb > 1024 ? String.format("%.1f MB", kb / 1024.0) : kb + " KB";
        ((TextView) findViewById(R.id.tvSize)).setText(sz);

        String dt = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)
                .format(new Date(img.lastModified()));
        ((TextView) findViewById(R.id.tvDate)).setText(dt);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnDelete).setOnClickListener(v -> confirmDelete());
    }

    void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Image")
                .setMessage("Delete \"" + img.getName() + "\"? This cannot be undone.")
                .setPositiveButton("Delete", (d, w) -> {
                    if (img.delete()) finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}