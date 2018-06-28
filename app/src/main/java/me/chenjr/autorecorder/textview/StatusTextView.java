package me.chenjr.autorecorder.textview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import android.widget.Toast;

public class StatusTextView extends TextView {
    boolean servicesStatus = false;


    public void updateStatus(boolean status){
        servicesStatus = status;

        if (status){
            this.setText("✔️");
            // Toast.makeText(getContext(),"Running",Toast.LENGTH_SHORT).show();
        }else {
            this.setText("❌");
            // Toast.makeText(getContext(),"Not Running",Toast.LENGTH_SHORT).show();
        }
    }

    public boolean getServicesStatus() {
        return servicesStatus;
    }



    public StatusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        updateStatus(servicesStatus);
    }

    public StatusTextView(Context context) {
        super(context);
        updateStatus(servicesStatus);

    }
}
