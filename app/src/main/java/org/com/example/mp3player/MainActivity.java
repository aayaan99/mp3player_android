package org.com.example.mp3player;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MusicAdapter adapter;
    private List<MusicFile> musicFiles;
    private MediaPlayer mediaPlayer;
    private Button playButton;
    private Button logButton;
    private SeekBar seekBar;
    private Handler timeUpdateHandler = new Handler();
    private Runnable timeUpdateRunnable;

    public static List<String> playLog = new ArrayList<>();

    private String currentSongName = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAndRequestPermissions();

        recyclerView = findViewById(R.id.recyclerView);
        playButton = findViewById(R.id.button2);
        logButton = findViewById(R.id.button);
        seekBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        musicFiles = fetchMusicFiles();
        adapter = new MusicAdapter(musicFiles, this::onMusicFileClicked);
        recyclerView.setAdapter(adapter);


        playButton.setOnClickListener(v -> {
            if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
                if (mediaPlayer == null) {
                    if (!musicFiles.isEmpty()) {
                        onMusicFileClicked(musicFiles.get(0));
                    }
                } else {
                    mediaPlayer.start();
                    playButton.setText("Pause");
                    timeUpdateHandler.post(timeUpdateRunnable);
                    startSeekBarThread();
                }
            } else {
                logPlaytime();
                mediaPlayer.pause();
                playButton.setText("Continue");
                timeUpdateHandler.removeCallbacks(timeUpdateRunnable);
            }
        });


        logButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LogActivity.class);
            startActivity(intent);
        });



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        timeUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    TextView timeTextView = findViewById(R.id.textView2);
                    timeTextView.setText("Time: " + formatTime(mediaPlayer.getCurrentPosition()));
                    timeUpdateHandler.postDelayed(this, 1000);
                }
            }
        };
    }

    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        int hours = minutes / 60;
        minutes = minutes % 60;

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }


    private void onMusicFileClicked(MusicFile file) {
        TextView selectedMusicTextView = findViewById(R.id.textView);
        selectedMusicTextView.setText(file.getName());
        int position = musicFiles.indexOf(file);
        adapter.setSelectedItem(position);
        playMusic(file.getPath(), file.getName());
        playButton.setText("Pause");
    }



    private void checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), 1);
        }
    }


    private void playMusic(String path, String songName) {
        logPlaytime();

        currentSongName = songName;

        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            } else {
                mediaPlayer.reset();
            }

            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();

            seekBar.setMax(mediaPlayer.getDuration());
            startSeekBarThread();
            timeUpdateHandler.post(timeUpdateRunnable);
            playButton.setText("Pause");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error playing the file", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        logPlaytime();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            playButton.setText("Continue");
        }
    }


    private void startSeekBarThread() {
        Thread seekBarThread = new Thread(() -> {
            while (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    runOnUiThread(() -> seekBar.setProgress(mediaPlayer.getCurrentPosition()));
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        seekBarThread.start();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        logPlaytime();
        timeUpdateHandler.removeCallbacks(timeUpdateRunnable);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private List<MusicFile> fetchMusicFiles() {
        List<MusicFile> fileList = new ArrayList<>();

        fileList.add(new MusicFile("Baby Mandala", "/sdcard/Download/baby-mandala-169039.mp3"));
        fileList.add(new MusicFile("Drive Breakbeat", "/sdcard/Download/drive-breakbeat-173062.mp3"));
        fileList.add(new MusicFile("Once in Paris", "/sdcard/Download/once-in-paris-168895.mp3"));
        fileList.add(new MusicFile("Science Documentary", "/sdcard/Download/science-documentary-169621.mp3"));
        fileList.add(new MusicFile("Titanium", "/sdcard/Download/titanium-170190.mp3"));
        fileList.add(new MusicFile("Trap Future Bass", "/sdcard/Download/trap-future-bass-royalty-free-music-167020.mp3"));
        fileList.add(new MusicFile("Baby", "/sdcard/Download/baby-mandala-169039.mp3"));
        fileList.add(new MusicFile("Breakbeat", "/sdcard/Download/drive-breakbeat-173062.mp3"));
        fileList.add(new MusicFile("Paris", "/sdcard/Download/once-in-paris-168895.mp3"));
        fileList.add(new MusicFile("Science", "/sdcard/Download/science-documentary-169621.mp3"));
        fileList.add(new MusicFile("Titanium", "/sdcard/Download/titanium-170190.mp3"));
        fileList.add(new MusicFile("Trap", "/sdcard/Download/trap-future-bass-royalty-free-music-167020.mp3"));


        return fileList;
    }

    private void logPlaytime() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            String logEntry = "<b>" + currentSongName + ".mp3</b><br/>" +
                    "Time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            playLog.add(logEntry);
        }
    }


}
