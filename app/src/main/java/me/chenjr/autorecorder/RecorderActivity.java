package me.chenjr.autorecorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RecorderActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 234;
    MediaRecorder mRecorder = null;
    MediaPlayer mPlayer = null;
    String filename = "record";
    String currentFilePath;
    boolean PermissionAccepted = false;
    boolean isRecording = false;
    Button btn_record = null;
    Button btn_play = null;
    String[] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recoder);
        /*---------*/
        btn_play = findViewById(R.id.btn_paly);
        btn_record = findViewById(R.id.btn_record);

        /*检查是否有权限,api 23 以下默认能够直接获取*/
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            /*没有权限则尝试获取*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(permissions, REQUEST_RECORD_AUDIO_PERMISSION);
            }
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                PermissionAccepted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
        /*无法获取权限则退出*/
        if (!PermissionAccepted) {
            Toast.makeText(this, R.string.permission_granted_denied, Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        /*判断是否为空,非空则创建*/

        if (mRecorder != null) {
            mRecorder.release();
        }
        mRecorder = new MediaRecorder();
        if (mPlayer != null) {
            mPlayer.release();
        }
        mPlayer = new MediaPlayer();


    }

    @Override
    protected void onStop() {
        super.onStop();
        /*非空则释放并设为空*/
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }


    public void StartRecord() {
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd_HH.mm", Locale.getDefault());

        /*-----初始化mRecorder-----*/
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        currentFilePath = getExternalFilesDir("Record") + "/" + filename+df.format(day) + ".acc";
        mRecorder.setOutputFile(currentFilePath);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("[REC_ERR]", "StartRecording: Fail to Start record");
        }
        mRecorder.start();


    }

    public void StopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }

    }

    public void PlayRecord(View view) {

         final  Button btn = findViewById(R.id.btn_paly);


        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            btn.setText(R.string.play_record);
        } else {
            btn.setText(R.string.pause_play_record);
            /*重设状态*/
            mPlayer.reset();
            /*准备*/
            try {
                mPlayer.setDataSource(currentFilePath);
                mPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*设置播放完成回调(设置按键标签)*/
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    btn.setText(R.string.play_record);

                }
            });
            /*开始播放*/
            mPlayer.start();
        }

    }

    public void doRecord(View view) {
        if (isRecording) {
            StopRecording();
            btn_record.setText(R.string.start_record);
            isRecording = false;

        } else {
            StartRecord();
            btn_record.setText(R.string.stop_record);
            isRecording = true;

        }
    }
}
