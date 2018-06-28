package me.chenjr.autorecorder;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final float DEFAULT_THRESHOLD = 16;
    public static final long DEFAULT_GAP = 350;
    public static MainHandler mHandler;
    private SensorManager mSensorManager;
    private float threshold = DEFAULT_THRESHOLD;

    long last_shake_time = 0;
    private TextView et_x;
    private TextView et_y;
    private TextView et_z;
    private EditText et_threshold;

    private SensorEventListener sensorEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_x = findViewById(R.id.Main_et_x);
        et_y = findViewById(R.id.Main_et_y);
        et_z = findViewById(R.id.Main_et_z);
        et_threshold = findViewById(R.id.Main_et_threshold);
        et_threshold.setText(""+threshold);
        /*创建handler*/
        mHandler = new MainHandler();
        /*获取SensorService*/
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorEventListener = new ShakeSensorListener();

    }

    @Override
    protected void onResume() {
        super.onResume();

        StartListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
//        StopListening();
    }

    void StartListening() {

        /*设置加速度传感器的监听器*/
        mSensorManager.registerListener(sensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void StartListening(View view) {
        StartListening();
    }

    void StopListening() {
        mSensorManager.unregisterListener(sensorEventListener);

    }

    public void StopListening(View view) {
        StopListening();
    }

    /*在listener触发之后由handler调用,就是在传感器的值发生变化的时候会执行的功能模块*/
    public void onUpdate(Object obj) {
        float[] values = (float[]) obj;
        boolean isShake = false;
        long current_time = System.currentTimeMillis();

        et_x.setText("x: " + values[0]);
        et_y.setText("y: " + values[1]);
        et_z.setText("z: " + values[2]);
        /*遍历每个方向上的加速度*/
        for (float val : values) {
            /*任意一个方向上的值大于阈值,且上次触发摇动时间的间隔大于设定值 便将标志位设为true*/
            if (val > threshold && current_time - last_shake_time > DEFAULT_GAP) {
                isShake = true;
                last_shake_time = current_time;
            }

        }
        if (isShake)
            Toast.makeText(this, "It shakes!", Toast.LENGTH_SHORT).show();
    }

    public void setThresholdListener(View view) {
        threshold = Float.valueOf(et_threshold.getText().toString());
    }

    public void GoToRecord(View view) {
        Intent intent = new Intent(this,RecorderActivity.class);
        startActivity(intent);
    }


    /*Handler类*/
    public class MainHandler extends Handler {
        public static final int AT_SHAKE = 0x01;
        public static final int AT_CHANGE = 0x02;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
//                case AT_SHAKE:
//                    onShake();
//                    break;
                case AT_CHANGE:
                    onUpdate(msg.obj);
                    break;
            }

        }
    }

    /*加速度计监听器*/
    class ShakeSensorListener implements SensorEventListener {


        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
//            boolean isShake = false;
//            /*遍历每个方向上的加速度*/
//            for (float value : values) {
//                /*任意一个方向上的值大于阈值便将标志位设为true*/
//                if (Math.abs(value) > threshold) {
//                    isShake = true;
//                }
//            }
//
//            if (isShake) mHandler.sendEmptyMessage(MainHandler.AT_SHAKE);
            /*将数据用消息传到handler里*/
            Message msg = Message.obtain();
            msg.obj = values;
            msg.what = MainHandler.AT_CHANGE;
            mHandler.sendMessage(msg);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d("[Unfinished]", "onAccuracyChanged: ");
        }
    }


}
