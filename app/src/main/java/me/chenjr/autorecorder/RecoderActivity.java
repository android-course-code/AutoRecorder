package me.chenjr.autorecorder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class RecoderActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 234;
    MediaRecorder mRecorder;
    String filepath = "record.acc";
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
        if (mRecorder != null) {
            mRecorder.release();
        }
        mRecorder = new MediaRecorder();


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
        }
    }

    public void StartRecording(View view) {
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setOutputFile(filepath);


    }

    public void StopRecording(View view) {

    }

    public void PlayRecord(View view) {

    }
}
