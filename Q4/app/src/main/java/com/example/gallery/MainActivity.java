package com.example.gallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static final int RC_CAM = 101;
    static final int RC_PERM = 102;
    Uri photoUri;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnCamera).setOnClickListener(v -> checkAndShoot());
        findViewById(R.id.btnGallery).setOnClickListener(v -> {
            startActivity(new Intent(this, GalleryActivity.class));
        });
    }

    void checkAndShoot() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, RC_PERM);
            } else {
                shoot();
            }
        } else {
            String[] p = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            boolean ok = true;
            for (String x : p) {
                if (ContextCompat.checkSelfPermission(this, x) != PackageManager.PERMISSION_GRANTED) {
                    ok = false;
                    break;
                }
            }
            if (!ok) ActivityCompat.requestPermissions(this, p, RC_PERM);
            else shoot();
        }
    }

    void shoot() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (i.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
            return;
        }
        File f = null;
        try {
            f = makeFile();
        } catch (IOException e) {
            Toast.makeText(this, "Cannot create file", Toast.LENGTH_SHORT).show();
            return;
        }
        photoUri = FileProvider.getUriForFile(this, "com.app.pg.fileprovider", f);
        i.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(i, RC_CAM);
    }

    File makeFile() throws IOException {
        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File dir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Lumina");
        if (!dir.exists()) dir.mkdirs();
        return File.createTempFile("IMG_" + ts, ".jpg", dir);
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req == RC_CAM && res == RESULT_OK) {
            Toast.makeText(this, "Photo saved!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int req, String[] perms, int[] results) {
        super.onRequestPermissionsResult(req, perms, results);
        if (req == RC_PERM) {
            boolean ok = true;
            for (int r : results) if (r != PackageManager.PERMISSION_GRANTED) { ok = false; break; }
            if (ok) shoot();
            else Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}