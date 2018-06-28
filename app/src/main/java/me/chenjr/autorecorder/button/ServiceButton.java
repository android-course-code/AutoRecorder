package me.chenjr.autorecorder.button;


import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;

public class ServiceButton extends AppCompatButton {
    boolean servicesStatus = false;
    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            updateStatus(servicesStatus);
        }
    };
    public ServiceButton(Context context) {
        super(context);
        updateStatus(servicesStatus);
    }

    public ServiceButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        updateStatus(servicesStatus);

    }

    public boolean getServicesStatus() {
        return servicesStatus;
    }

    public void updateStatus(boolean status){
        servicesStatus = status;
        if (status){
            setText("Stop");

        }else {
            setText("Start");

        }
    }

}
