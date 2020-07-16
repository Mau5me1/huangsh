package com.hsh.record;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    Button record;
    Button stop;
    Button play;
    File soundFile;
    String playFileName;
    MediaRecorder mRecorder;
    MediaPlayer mediaPlayer;
    AudioManager mAudioManager;
    private static final int GET_RECODE_AUDIO = 1;
    private static String[] PERMISSION_AUDIO = {Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        verifyAudioPermissions(this);
        playFileName = null;
        record = (Button) findViewById(R.id.record);
        stop = (Button) findViewById(R.id.stop);
        play = (Button) findViewById(R.id.play);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String path = getFilesDir().getPath();

                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                Date curDate = new Date(System.currentTimeMillis());   //获取当前时间
                String time = formatter.format(curDate);
                String recordFile = "MIC1_" + time + ".m4a";
                playFileName = path + recordFile;

                Toast.makeText(getApplicationContext(), "start recording",
                        Toast.LENGTH_SHORT).show();


                soundFile = new File(path + recordFile);
                if (!soundFile.exists()) {
                    try {
                        soundFile.createNewFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.i("create file", "path: " + path);


                if (mRecorder == null) {
                    mRecorder = new MediaRecorder();
                }

                try {
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    mRecorder.setAudioSamplingRate(48000);
                    mRecorder.setAudioChannels(2);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);

                    mRecorder.setOutputFile(soundFile.getAbsolutePath());

                    Log.i("Recorder", "init finish");
                    Log.i("Recorder", "record  start");

                    mRecorder.prepare();
                    mRecorder.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    Log.i("Recorder", "record already start");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "stop recording",
                        Toast.LENGTH_SHORT).show();
                if (mRecorder != null) {
                    try {
                        mRecorder.stop();
                    } catch (IllegalStateException e) {
                        // TODO 如果当前java状态和jni里面的状态不一致，
                        //e.printStackTrace();
                        mRecorder = null;
                        mRecorder = new MediaRecorder();
                    }
                    mRecorder.release();
                    Log.i("Recorder", "record stop and release");
                    mRecorder = null;
                }
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("record", "record file name ========" + playFileName);
                if (playFileName == null) {
                    Toast.makeText(getApplicationContext(), "need record first",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "play record files",
                            Toast.LENGTH_SHORT).show();
                    mediaPlayer = null;
                    mediaPlayer = new MediaPlayer();
                    mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    mAudioManager.setParameters("noise_suppression=off");
                    int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, 0);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                    try {
                        mediaPlayer.setDataSource(playFileName);
                        Log.i("MusicAty", "setDataSource ");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("MusicAty", "setDataSource fail");
                    }

                    try {
                        mediaPlayer.prepare();
                        Log.i("MusicAty", "prepare ");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                    try {
                        mediaPlayer.start();
                        Log.i("MusicAty", "start ");
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        super.onDestroy();
    }

    public static void verifyAudioPermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSION_AUDIO,
                    GET_RECODE_AUDIO);
        }
    }
}
