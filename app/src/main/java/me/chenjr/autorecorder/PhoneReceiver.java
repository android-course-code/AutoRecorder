package me.chenjr.autorecorder;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneReceiver extends BroadcastReceiver {
    PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d("[RECEIVER]","挂断");

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d("[RECEIVER]","接听");
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
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("[RECEIVER]","action" + intent.getAction());
        // 如果是去电
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.d("[RECEIVER]", "call OUT:" + phoneNumber);
        } else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        }

    }
}
