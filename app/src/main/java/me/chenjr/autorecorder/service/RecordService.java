package me.chenjr.autorecorder.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.chenjr.autorecorder.R;

public class RecordService extends Service {
    public static final String ACTION_INTENT_KEY = "doACTION";
    public static final String STATUS_INTENT_KEY = "Status";
    public static final String BROADCAST_STATUS_ACTION = "mr.recorder.RecordService.BROADCAST_STATUS";
    public static final int NO_ACTION = 0;
    public static final int START_RECORD = 1;
    public static final int STOP_RECORD = 2;
    public static final int START_FOREGROUND = 4;
    public static final int STOP_FOREGROUND = 8;
    public static final int STOP_SERVICE = 16;
    public static final int BROADCAST_STATUS = 32;
    public static final int STATUS_SERVICE_STARTED = 1;
    public static final int STATUS_SERVICE_FOREGROUND = 2;
    public static final int STATUS_RECORDING = 4;

    MediaRecorder mRecorder = null;
    private boolean isRecording = false;
    private boolean isForeground = false;
    public static final int NOTIFICATION_ID = 233;
    Binder mBinder = new RecordBinder();
    NotificationCompat.Builder notificationBuilder;
    SharedPreferences sp;
    private String currentFilePath;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(getString(R.string.sharedpreference_filename), MODE_PRIVATE);
    }

    public boolean isRecording() {
        return isRecording;
    }

    public class RecordBinder extends Binder {
        public RecordService getService() {
            return RecordService.this;
        }
    }

    public void changNotification(String content, String title) {
        if (!isForeground) return;

        if (notificationBuilder == null) {
            if (title == null)
                title = "Notification Service";

            if (content == null)
                content = "Record service started.";

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(content)
                    .setContentText(title)
                    .setSmallIcon(R.mipmap.ic_mic_outline);
        } else {
            if (title != null) {
                notificationBuilder.setContentTitle(title);
            }
            if (content != null)
                notificationBuilder.setContentText(content);
        }
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, notificationBuilder.build());

    }

    public void startForeground() {
        isForeground = true;
        changNotification("Start foreground", null);
        startForeground(NOTIFICATION_ID, notificationBuilder.build());


    }

    public void stopForeground() {
        isForeground = false;
        stopForeground(true);

    }

    public String startRecord() {
        Toast.makeText(getApplicationContext(), "Start record.", Toast.LENGTH_SHORT).show();

        /* 开始录音 */
        if (isRecording) {
            /*若还在录音则先停止*/
            Log.d("@stopRecord", "还在录音则先停止 ");
            stopRecord();

        }

        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("MMdd-HHmmss", Locale.getDefault());

        currentFilePath = getExternalFilesDir("Record") + "/" +
                sp.getString("record_filename_prefix", "mr_recorder_")
                + df.format(day) + ".3gp";
        mRecorder.setOutputFile(currentFilePath);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();

        isRecording = true;
        return currentFilePath;

    }

    public void stopRecord() {
        Log.d("@stopRecord", "stopRecord: ");
        /* 停止当前录音 */
        if (isRecording) {
            Toast.makeText(getApplicationContext(), "Stop record.", Toast.LENGTH_SHORT).show();
            Log.d("@stopRecord", "stopRecord: isRecording ");
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }

        }

        isRecording = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int action;

        action = intent.getIntExtra(ACTION_INTENT_KEY, NO_ACTION);
        Log.d("@RecordService", "onStartCommand: "+action);
        switch (action) {
            case START_RECORD:
                startRecord();
                break;
            case STOP_RECORD:
                stopRecord();
                break;

            case START_FOREGROUND:
                startForeground();
                break;
            case STOP_FOREGROUND:
                stopForeground();
                break;
            case STOP_SERVICE:
                stopSelf();
                break;
            case BROADCAST_STATUS:
                broadcastStatus();
                break;
            default:

                break;
        }
        broadcastStatus();
        return super.onStartCommand(intent, flags, startId);
    }

    private void broadcastStatus() {
        int status = STATUS_SERVICE_STARTED;
        status |= isRecording ? STATUS_RECORDING : 0;
        status |= isForeground ? STATUS_SERVICE_FOREGROUND : 0;

        sendBroadcast(
                new Intent(BROADCAST_STATUS_ACTION).putExtra(STATUS_INTENT_KEY, status));

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("@RecordService", "onDestroy: ");
        stopRecord();
    }
}
