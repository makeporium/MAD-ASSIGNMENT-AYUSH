package com.example.gallery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    int a = 21;
    GridView b;
    TextView c;
    TextView d;
    Uri e;
    ArrayList<DocumentFile> f = new ArrayList<>();
    SharedPreferences g;

    @Override
    protected void onCreate(Bundle h) {
        super.onCreate(h);
        setContentView(R.layout.activity_gallery);
        g = getSharedPreferences("p", MODE_PRIVATE);
        b = findViewById(R.id.grid);
        c = findViewById(R.id.tvPath);
        d = findViewById(R.id.tvCount);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnPick).setOnClickListener(v -> i());
        String j = getIntent().getStringExtra("u");
        if (j == null || j.isEmpty()) j = g.getString("u", "");
        if (!j.isEmpty()) {
            e = Uri.parse(j);
            k();
        }
        b.setOnItemClickListener((p, v, pos, id) -> {
            Intent n = new Intent(this, ImageDetailActivity.class);
            n.putExtra("i", f.get(pos).getUri().toString());
            n.putExtra("u", e != null ? e.toString() : "");
            startActivity(n);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (e != null) k();
    }

    void i() {
        Intent h = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(h, a);
    }

    @Override
    protected void onActivityResult(int h, int i, Intent j) {
        super.onActivityResult(h, i, j);
        if (h == a && i == RESULT_OK && j != null) {
            Uri k = j.getData();
            if (k != null) {
                int l = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(k, l);
                e = k;
                g.edit().putString("u", k.toString()).apply();
                k();
            }
        }
    }

    void k() {
        f.clear();
        DocumentFile h = DocumentFile.fromTreeUri(this, e);
        if (h == null) {
            Toast.makeText(this, "Folder unavailable", Toast.LENGTH_SHORT).show();
            return;
        }
        for (DocumentFile i : h.listFiles()) {
            if (!i.isFile()) continue;
            String j = i.getType();
            if (j != null && j.startsWith("image/")) f.add(i);
        }
        c.setText(e.toString());
        d.setText(f.size() + " image" + (f.size() == 1 ? "" : "s"));
        findViewById(R.id.layoutEmpty).setVisibility(f.isEmpty() ? View.VISIBLE : View.GONE);
        b.setVisibility(f.isEmpty() ? View.GONE : View.VISIBLE);
        b.setAdapter(new m());
    }

    class m extends BaseAdapter {
        public int getCount() { return f.size(); }
        public Object getItem(int p) { return f.get(p); }
        public long getItemId(int p) { return p; }
        public View getView(int p, View q, ViewGroup r) {
            if (q == null) q = getLayoutInflater().inflate(R.layout.grid_item, r, false);
            ImageView s = q.findViewById(R.id.img);
            s.setImageURI(f.get(p).getUri());
            return q;
        }
    }
}
