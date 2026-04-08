package com.example.gallery;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    int a = 11;
    int b = 12;
    int c = 13;
    Uri d;
    Uri e;
    File f;
    SharedPreferences g;

    @Override
    protected void onCreate(Bundle h) {
        super.onCreate(h);
        setContentView(R.layout.activity_main);
        g = getSharedPreferences("p", MODE_PRIVATE);
        String i = g.getString("u", "");
        if (!i.isEmpty()) d = Uri.parse(i);

        findViewById(R.id.btnFolder).setOnClickListener(v -> j());
        findViewById(R.id.btnCamera).setOnClickListener(v -> k());
        findViewById(R.id.btnGallery).setOnClickListener(v -> {
            Intent i1 = new Intent(this, GalleryActivity.class);
            if (d != null) i1.putExtra("u", d.toString());
            startActivity(i1);
        });
    }

    void j() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(i, a);
    }

    void k() {
        if (d == null) {
            Toast.makeText(this, "Pick folder first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, c);
            return;
        }
        l();
    }

    void l() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (i.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            f = File.createTempFile("img_", ".jpg", getCacheDir());
        } catch (IOException e) {
            Toast.makeText(this, "Cannot create file", Toast.LENGTH_SHORT).show();
            return;
        }
        e = FileProvider.getUriForFile(this, "com.example.gallery.fileprovider", f);
        i.putExtra(MediaStore.EXTRA_OUTPUT, e);
        startActivityForResult(i, b);
    }

    @Override
    protected void onActivityResult(int i, int j, Intent k) {
        super.onActivityResult(i, j, k);
        if (i == a && j == RESULT_OK && k != null) {
            Uri l = k.getData();
            if (l != null) {
                int m = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(l, m);
                d = l;
                g.edit().putString("u", l.toString()).apply();
                Toast.makeText(this, "Folder selected", Toast.LENGTH_SHORT).show();
            }
        }
        if (i == b && j == RESULT_OK) {
            m();
        }
    }

    void m() {
        if (d == null || f == null || !f.exists()) return;
        DocumentFile n = DocumentFile.fromTreeUri(this, d);
        if (n == null || !n.canWrite()) {
            Toast.makeText(this, "Folder not writable", Toast.LENGTH_SHORT).show();
            return;
        }
        String o = "IMG_" + System.currentTimeMillis() + ".jpg";
        DocumentFile p = n.createFile("image/jpeg", o);
        if (p == null) {
            Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
            return;
        }
        try (InputStream q = getContentResolver().openInputStream(e);
             OutputStream r = getContentResolver().openOutputStream(p.getUri())) {
            if (q == null || r == null) throw new IOException();
            byte[] s = new byte[8192];
            int t;
            while ((t = q.read(s)) > 0) r.write(s, 0, t);
            r.flush();
            Toast.makeText(this, "Photo saved", Toast.LENGTH_SHORT).show();
        } catch (Exception x) {
            Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int i, String[] j, int[] k) {
        super.onRequestPermissionsResult(i, j, k);
        if (i == c) {
            if (k.length > 0 && k[0] == PackageManager.PERMISSION_GRANTED) l();
            else Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}