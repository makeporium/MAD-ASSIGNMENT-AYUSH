package com.example.gallery;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageDetailActivity extends AppCompatActivity {

    Uri a;
    Uri b;
    DocumentFile c;

    @Override
    protected void onCreate(Bundle d) {
        super.onCreate(d);
        setContentView(R.layout.activity_image_detail);
        String e = getIntent().getStringExtra("i");
        String f = getIntent().getStringExtra("u");
        if (e == null || f == null || e.isEmpty() || f.isEmpty()) {
            finish();
            return;
        }
        a = Uri.parse(e);
        b = Uri.parse(f);
        DocumentFile g = DocumentFile.fromTreeUri(this, b);
        if (g == null) {
            finish();
            return;
        }
        for (DocumentFile h : g.listFiles()) {
            if (h.getUri().toString().equals(a.toString())) {
                c = h;
                break;
            }
        }
        if (c == null) {
            finish();
            return;
        }
        ((ImageView) findViewById(R.id.imgDetail)).setImageURI(c.getUri());
        ((TextView) findViewById(R.id.tvName)).setText(c.getName() == null ? "-" : c.getName());
        ((TextView) findViewById(R.id.tvPath)).setText(c.getUri().toString());
        long i = c.length();
        String j = i > 1024 * 1024 ? String.format(Locale.US, "%.2f MB", i / (1024.0 * 1024.0)) : (i / 1024) + " KB";
        ((TextView) findViewById(R.id.tvSize)).setText(j);
        String k = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(new Date(c.lastModified()));
        ((TextView) findViewById(R.id.tvDate)).setText(k);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnDelete).setOnClickListener(v -> l());
    }

    void l() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Image")
                .setMessage("Delete this image?")
                .setPositiveButton("Delete", (d, w) -> {
                    if (c != null && c.delete()) finish();
                    else Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
