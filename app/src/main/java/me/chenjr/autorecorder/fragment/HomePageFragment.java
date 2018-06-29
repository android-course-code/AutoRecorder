package me.chenjr.autorecorder.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import me.chenjr.autorecorder.R;
import me.chenjr.autorecorder.button.ServiceButton;
import me.chenjr.autorecorder.service.RecordService;
import me.chenjr.autorecorder.textview.StatusTextView;

public class HomePageFragment extends Fragment {
    ServiceButton startRecordServiceBtn;
    ServiceButton startRecordBtn;
    StatusTextView recordServiceTv;
    boolean isRecording = false;
    boolean recordServiceRunning = false;
    private ServiceStatusReceiver receiver ;



    @Override
    public void onResume() {
        super.onResume();
        /* 注册状态广播接收器 */
        receiver=new ServiceStatusReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RecordService.BROADCAST_STATUS_ACTION);
        getActivity().registerReceiver(receiver,intentFilter);

        /* 回到/启动 fragment的时候启动请求一次状态并启动服务 */
        getActivity().startService(createNewActionIntent(RecordService.BROADCAST_STATUS));


    }
    View.OnClickListener btn_record_ctrl_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!isRecording){
                getActivity().startService(createNewActionIntent(RecordService.START_RECORD));

            }else {
                getActivity().startService(createNewActionIntent(RecordService.STOP_RECORD));
            }
            isRecording =!isRecording;
        }
    };
    View.OnClickListener btn_record_service_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ServiceButton btn = (ServiceButton) v;

            if (!recordServiceRunning) {

                getActivity().startService(createNewActionIntent(RecordService.NO_ACTION));

            } else {


                getActivity().startService(createNewActionIntent(RecordService.STOP_SERVICE));
            }
            recordServiceRunning = !recordServiceRunning ;


        }
    };
    public Intent createNewActionIntent(int action){
        Intent newIntent  = new Intent(getContext(), RecordService.class);
        newIntent.putExtra(RecordService.ACTION_INTENT_KEY,action);
        return newIntent;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        startRecordServiceBtn = view.findViewById(R.id.frag_home_btn_record_service);
        recordServiceTv = view.findViewById(R.id.frag_home_tv_record_service_status);
        startRecordBtn = view.findViewById(R.id.frag_home_btn_record_ctrl);
        startRecordBtn.setOnClickListener(btn_record_ctrl_listener);
        startRecordServiceBtn.setOnClickListener(btn_record_service_listener);

        return view;

    }

    public void refreshViews(int status){
        isRecording = (status & RecordService.STATUS_RECORDING) !=0;
        recordServiceRunning = (status & RecordService.STATUS_SERVICE_STARTED) !=0;
        recordServiceTv.updateStatus(recordServiceRunning);
        startRecordServiceBtn.updateStatus(recordServiceRunning);
        startRecordBtn.updateStatus(isRecording);

    }
    @Override
    public void onStop() {
        super.onStop();
//        if (recordService!=null) getContext().unbindService(serviceConnection);

        Log.d("@HomePageFragment", "onStop: ");
        getActivity().unregisterReceiver(receiver);
    }


    class ServiceStatusReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(RecordService.STATUS_INTENT_KEY,0);
            Log.d("@ServiceStatusReceiver", "onReceive: "+status);
            refreshViews(status);
        }
    }
}

