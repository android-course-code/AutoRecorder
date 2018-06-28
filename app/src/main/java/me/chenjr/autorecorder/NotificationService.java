package me.chenjr.autorecorder;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Random;

public class NotificationService extends Service {

    /* binder对象*/
    private  final  Binder mBinder  = new LocalBinder();
    boolean started = false;
    private final int  mNotificationID = 233;

    public NotificationService() {
        super();
    }
    /* 内部binder类 */
   public  class LocalBinder extends Binder{
       NotificationService getService(){
           return  NotificationService.this;
       }

    }
    public void StartForeground(){
        Notification notification  = new NotificationCompat.Builder(this)
                .setContentTitle("Notification Service")
                .setContentText("Foreground service started.")
                .setSmallIcon(R.mipmap.ic_mic_outline)
                .build();
        startForeground(mNotificationID,notification);
        started  = true;

    }
    public void StopForegroud(){
       stopForeground(true);
       started = false;
    }



    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this,"Service destroyed.",Toast.LENGTH_SHORT).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,"Service get cmd.",Toast.LENGTH_SHORT).show();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this,"Service bound.",Toast.LENGTH_SHORT).show();

        return  mBinder;
    }


}
