package com.example.q2;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mp;
    VideoView vv;
    TextView tvStatus, tvTime;
    SeekBar sb;
    Handler h = new Handler();
    boolean isVideo = false;

    Runnable upd = new Runnable() {
        public void run() {
            if (mp != null && mp.isPlaying()) {
                sb.setProgress(mp.getCurrentPosition());
                tvTime.setText(fmt(mp.getCurrentPosition()) + " / " + fmt(mp.getDuration()));
            } else if (vv != null && vv.isPlaying()) {
                sb.setProgress(vv.getCurrentPosition());
                tvTime.setText(fmt(vv.getCurrentPosition()) + " / " + fmt(vv.getDuration()));
            }
            h.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        vv = findViewById(R.id.vv);
        tvStatus = findViewById(R.id.tvStatus);
        tvTime = findViewById(R.id.tvTime);
        sb = findViewById(R.id.sb);

        findViewById(R.id.btnOpenFile).setOnClickListener(v -> openFile());
        findViewById(R.id.btnOpenUrl).setOnClickListener(v -> openUrl());
        findViewById(R.id.btnPlay).setOnClickListener(v -> play());
        findViewById(R.id.btnPause).setOnClickListener(v -> pause());
        findViewById(R.id.btnStop).setOnClickListener(v -> stop());
        findViewById(R.id.btnRestart).setOnClickListener(v -> restart());
    }

    void openFile() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("audio/*");
        startActivityForResult(i, 1);
    }

    void openUrl() {
        EditText et = new EditText(this);
        et.setHint("https://...");
        et.setText("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
        new AlertDialog.Builder(this)
                .setTitle("Enter Video URL")
                .setView(et)
                .setPositiveButton("Load", (d, w) -> loadVideo(et.getText().toString().trim()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req == 1 && res == RESULT_OK && data != null) {
            releaseAll();
            isVideo = false;
            vv.setVisibility(android.view.View.GONE);
            Uri uri = data.getData();
            mp = MediaPlayer.create(this, uri);
            sb.setMax(mp.getDuration());
            tvStatus.setText("Audio loaded ✔");
            h.post(upd);
        }
    }

    void loadVideo(String url) {
        releaseAll();
        isVideo = true;
        vv.setVisibility(android.view.View.VISIBLE);
        vv.setVideoPath(url);
        vv.setOnPreparedListener(p -> {
            sb.setMax(vv.getDuration());
            tvStatus.setText("Video ready ✔");
            h.post(upd);
        });
        vv.requestFocus();
    }

    void play() {
        if (isVideo) { vv.start(); }
        else if (mp != null) { mp.start(); }
        tvStatus.setText("▶ Playing");
    }

    void pause() {
        if (isVideo) { vv.pause(); }
        else if (mp != null && mp.isPlaying()) { mp.pause(); }
        tvStatus.setText("⏸ Paused");
    }

    void stop() {
        if (isVideo) { vv.stopPlayback(); }
        else if (mp != null) { mp.stop(); mp.prepareAsync(); }
        sb.setProgress(0);
        tvStatus.setText("⏹ Stopped");
    }

    void restart() {
        if (isVideo) { vv.seekTo(0); vv.start(); }
        else if (mp != null) { mp.seekTo(0); mp.start(); }
        tvStatus.setText("⏮ Restarted");
    }

    void releaseAll() {
        h.removeCallbacks(upd);
        if (mp != null) { mp.release(); mp = null; }
    }

    String fmt(int ms) {
        int s = ms / 1000;
        return String.format("%d:%02d", s / 60, s % 60);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseAll();
    }
}