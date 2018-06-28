package me.chenjr.autorecorder.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import me.chenjr.autorecorder.NotificationService;

import me.chenjr.autorecorder.R;
import me.chenjr.autorecorder.button.ServiceButton;
import me.chenjr.autorecorder.service.RecordService;
import me.chenjr.autorecorder.textview.StatusTextView;

public class HomePageFragment extends Fragment {
    ServiceButton startServiceBtn;
    Button startRecordBtn;
    StatusTextView textView;
    RecordService recordService;
    ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            recordService = ((RecordService.RecordBinder) service).getService();
            recordService.startForeground();
            Toast.makeText(getContext(),"ServiceConnected",Toast.LENGTH_SHORT).show();
            startRecordBtn.setText(recordService.isRecording()?"Stop Record":"Start Record");

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            recordService.stopRecord();
            recordService = null;
            Toast.makeText(getContext(),"ServiceDisconnected",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().bindService(
                new Intent(getContext(), RecordService.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE);
        startServiceBtn.updateStatus(recordService != null);
        textView.updateStatus(recordService != null);


    }
    View.OnClickListener btn_record_ctrl_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (recordService == null) return;
            if (!recordService.isRecording()){
                recordService.startRecord();
            }else {
                recordService.stopRecord();
            }
        }
    };
    View.OnClickListener btn_record_service_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ServiceButton btn = (ServiceButton) v;
            boolean status = recordService!=null;
            if (!status) {
                getActivity().bindService(
                        new Intent(getContext(), RecordService.class),
                        serviceConnection,
                        Context.BIND_AUTO_CREATE);
            } else {

                getContext().unbindService(serviceConnection);
                recordService = null;

            }
            btn.updateStatus(!status);
            textView.updateStatus(!status);

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        startServiceBtn = view.findViewById(R.id.frag_home_btn_record_service);
        textView = view.findViewById(R.id.frag_home_tv_record_status);
        startRecordBtn = view.findViewById(R.id.frag_home_btn_record_ctrl);
        startRecordBtn.setOnClickListener(btn_record_ctrl_listener);
        startServiceBtn.setOnClickListener(btn_record_service_listener);



        startServiceBtn.updateStatus(recordService != null);
        textView.updateStatus(recordService != null);

        return view;

    }

    @Override
    public void onStop() {
        super.onStop();
        if (recordService!=null) getContext().unbindService(serviceConnection);

    }
}

