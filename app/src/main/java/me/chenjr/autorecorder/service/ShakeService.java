package me.chenjr.autorecorder.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import me.chenjr.autorecorder.R;

public class ShakeService extends Service implements SensorEventListener {
    public static final long DEFAULT_GAP = 350;
    public static final int STATUS_SERVICE_STARTED = 1;
    public static final String BROADCAST_STATUS_ACTION = "mr.recorder.RecordService.BROADCAST_STATUS";
    public static final String STATUS_INTENT_KEY = "ShakeServiceStatus";
    public static final String TAG = "@ShakeService";
    long last_shake_time = 0;
    SensorManager sensorManager;
    boolean enabled = false;
    boolean isRunning = false;
    float threshold;

    public ShakeService() {
        super();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Log.d(TAG, "onStartCommand: ");
        SharedPreferences sp = getSharedPreferences(getString(R.string.sharedpreference_filename), MODE_PRIVATE);
        if (sp.getBoolean("enable_shake_auto_record", false)) {
            isRunning = true;

            threshold = Float.valueOf(sp.getString("shake_threshold", "16.0"));
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL
            );
            enabled = true;
            broadcastStatus();
            return super.onStartCommand(intent, flags, startId);

        }
        else{
            stopSelf();
            return START_STICKY;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        boolean isShake = false;
        long current_time = System.currentTimeMillis();
//        Log.d(TAG, "onSensorChanged: ");
//
//        Log.d(TAG, "values" +values[0]+" "+values[1]+" "+values[2]);
        /*遍历每个方向上的加速度*/
        for (float val : values) {
            /*任意一个方向上的值大于阈值,且上次触发摇动时间的间隔大于设定值 便将标志位设为true*/
            if (val > threshold && current_time - last_shake_time > DEFAULT_GAP) {
                isShake = true;
                last_shake_time = current_time;
            }

        }
        if (isShake) {
            Toast.makeText(this, "It shakes!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), RecordService.class);
            intent.putExtra(RecordService.ACTION_INTENT_KEY, RecordService.TOGGLE_RECORD);
            Log.d(TAG, "onSensorChanged:isShake ");
            startService(intent);

        }

    }

    private void broadcastStatus() {
        int status = isRunning? STATUS_SERVICE_STARTED:0;
        status |= enabled ? 0b10 : 0b00;
        Log.d(TAG, "broadcastStatus: "+status);

        sendBroadcast(
                new Intent(BROADCAST_STATUS_ACTION)
                        .putExtra(STATUS_INTENT_KEY, status)
                        .putExtra("FROM_SHAKE",true));
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        sensorManager.unregisterListener(this);
        isRunning = false;
        Intent intent = new Intent(getApplicationContext(), RecordService.class);
        intent.putExtra(RecordService.ACTION_INTENT_KEY, RecordService.STOP_RECORD);
        Log.d(TAG, "onDestroy: ");
        broadcastStatus();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged: ");
    }
}
