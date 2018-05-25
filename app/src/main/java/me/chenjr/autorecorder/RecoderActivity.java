package me.chenjr.autorecorder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.TimedText;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.sql.Time;

public class RecoderActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 234;
    MediaRecorder mRecorder =null;
    MediaPlayer mPlayer =null;
    String filename = "record";
    String currentFilePath;
    boolean PermissionAccepted = false;
    String[] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recoder);
        /*检查是否有权限,api 23 以下默认能够直接获取*/
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            /*没有权限则尝试获取*/
            this.requestPermissions(permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        }
        Log.d("__FILE_PATH__", "Environment.getExternalStorageState: "+ Environment.getExternalStorageState());
        Log.d("__FILE_PATH__", "Environment.getExternalStorageDirectory: "+ Environment.getExternalStorageDirectory());
        Log.d("__FILE_PATH__", "Environment.getRootDirectory: "+ Environment.getRootDirectory());
        Log.d("__FILE_PATH__", "Environment.getDataDirectory: "+ Environment.getDataDirectory());
        Log.d("__FILE_PATH__", "Environment.getDownloadCacheDirectory: "+ Environment.getDownloadCacheDirectory());
        Log.d("__FILE_PATH__", "Environment.getExternalStoragePublicDirectory: "+ Environment.getExternalStoragePublicDirectory(""));

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
        if (mPlayer !=null){
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
            mRecorder=null;
        }
        if (mPlayer !=null){
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void StartRecording(View view) {
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        currentFilePath = getExternalFilesDir("Record")+"/"+filename+".acc";
        mRecorder.setOutputFile(currentFilePath);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("[REC_ERR]", "StartRecording: Fail to Start record");
        }
        mRecorder.start();


    }

    public void StopRecording(View view) {
        if(mRecorder!=null){
            mRecorder.stop();
        }

    }

    public void PlayRecord(View view) {
        try {
            mPlayer.setDataSource(currentFilePath);
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Button btn = (Button) view;
        mPlayer.start();
        if (mPlayer.isPlaying()){

            btn.setText("Pause");

        }
        else {
            btn.setText(R.string.play_record);
        }


    }
}
