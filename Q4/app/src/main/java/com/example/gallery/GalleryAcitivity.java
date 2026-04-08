package com.example.gallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    static final int RC_PICK = 201;
    static final int RC_PERM = 202;

    GridView gv;
    LinearLayout emptyLayout;
    TextView tvPath, tvCount;
    ArrayList<File> imgs = new ArrayList<>();
    String curPath = "";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_gallery);

        gv = findViewById(R.id.grid);
        emptyLayout = findViewById(R.id.layoutEmpty);
        tvPath = findViewById(R.id.tvPath);
        tvCount = findViewById(R.id.tvCount);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnPick).setOnClickListener(v -> checkPerm());

        gv.setOnItemClickListener((p, v, pos, id) -> {
            Intent i = new Intent(this, ImageDetailActivity.class);
            i.putExtra("path", imgs.get(pos).getAbsolutePath());
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!curPath.isEmpty()) loadImgs(new File(curPath));
    }

    void checkPerm() {
        String p = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{p}, RC_PERM);
        } else {
            pickFolder();
        }
    }

    void pickFolder() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(i, RC_PICK);
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req == RC_PICK && res == RESULT_OK && data != null) {
            Uri u = data.getData();
            String raw = u.getPath();
            String actual = raw.contains(":") ? raw.split(":")[1] : raw;
            File f = new File("/storage/emulated/0/" + actual);
            if (!f.exists()) f = new File(actual);
            if (f.exists() && f.isDirectory()) {
                curPath = f.getAbsolutePath();
                loadImgs(f);
            } else {
                Toast.makeText(this, "Could not access folder", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void loadImgs(File dir) {
        imgs.clear();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                String n = f.getName().toLowerCase();
                if (n.endsWith(".jpg") || n.endsWith(".jpeg")
                        || n.endsWith(".png") || n.endsWith(".webp")) {
                    imgs.add(f);
                }
            }
        }
        tvPath.setText(dir.getAbsolutePath());
        tvCount.setText(imgs.size() + " image" + (imgs.size() == 1 ? "" : "s"));

        if (imgs.isEmpty()) {
            emptyLayout.setVisibility(View.VISIBLE);
            gv.setVisibility(View.GONE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            gv.setVisibility(View.VISIBLE);
            gv.setAdapter(new Adp());
        }
    }

    @Override
    public void onRequestPermissionsResult(int req, String[] perms, int[] results) {
        super.onRequestPermissionsResult(req, perms, results);
        if (req == RC_PERM && results.length > 0
                && results[0] == PackageManager.PERMISSION_GRANTED) {
            pickFolder();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    class Adp extends BaseAdapter {
        public int getCount() { return imgs.size(); }
        public Object getItem(int p) { return imgs.get(p); }
        public long getItemId(int p) { return p; }

        public View getView(int pos, View cv, ViewGroup parent) {
            if (cv == null) {
                cv = getLayoutInflater().inflate(R.layout.grid_item, parent, false);
            }
            ImageView iv = cv.findViewById(R.id.img);
            iv.setImageURI(Uri.fromFile(imgs.get(pos)));
            return cv;
        }
    }
}