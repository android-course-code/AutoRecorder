package me.chenjr.autorecorder;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import me.chenjr.autorecorder.service.RecordService;

public class PhoneReceiver extends BroadcastReceiver {
    Context mContext ;
    SharedPreferences sp;
    PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if(sp.getBoolean("enable_call_auto_record",false))
                return;
            Intent intent = new Intent(mContext,RecordService.class);
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {

                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d("[RECEIVER]","挂断");
                    intent.putExtra(RecordService.ACTION_INTENT_KEY,RecordService.STOP_RECORD);
                    mContext.startService(intent);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d("[RECEIVER]","接听"+ incomingNumber + " is null " + (incomingNumber==null) +"length "+incomingNumber.length());
//                    Log.d("[RECEIVER]", );
                    intent.putExtra(RecordService.ACTION_INTENT_KEY,RecordService.START_RECORD);
                    intent.putExtra(RecordService.EXTRA_PHONE_NUMBER,""+incomingNumber);
                    if (incomingNumber.length()>1)
                        mContext.startService(intent);

                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d("[RECEIVER]","响铃:来电号码" + incomingNumber);
                    // 输出来电号码
                    break;
            }


        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        mContext = context;
        Log.d("[RECEIVER]","action" + intent.getAction());


        sp = context.getSharedPreferences(
                context.getString(R.string.sharedpreference_filename), Context.MODE_PRIVATE);

        if(sp.getBoolean("enable_call_auto_record",false))
            return;
        // 如果是去电
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.d("[RECEIVER]", "call OUT:" + phoneNumber);
            if (phoneNumber.length()>1){
                Intent intent_start = new Intent(mContext,RecordService.class);
                intent_start.putExtra(RecordService.ACTION_INTENT_KEY,RecordService.START_RECORD);
                intent_start.putExtra(RecordService.EXTRA_PHONE_NUMBER,phoneNumber);
                context.startService(intent_start);
            }
        } else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        }

    }
}
