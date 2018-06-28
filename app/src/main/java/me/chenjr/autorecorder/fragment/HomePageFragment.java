package me.chenjr.autorecorder.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.chenjr.autorecorder.NotificationService;

import me.chenjr.autorecorder.R;
import me.chenjr.autorecorder.button.ServiceButton;
import me.chenjr.autorecorder.textview.StatusTextView;

public class HomePageFragment extends Fragment {
    ServiceButton startServiceBtn;
    StatusTextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        startServiceBtn = view.findViewById(R.id.frag_home_btn_record_service);
        textView = view.findViewById(R.id.frag_home_tv_record_status);
        startServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceButton btn = (ServiceButton) v;
                boolean status = btn.getServicesStatus();
                if (!status) {
                    getContext().startService(new Intent(getContext(), NotificationService.class));

                } else {
                    getContext().stopService(new Intent(getContext(), NotificationService.class));
                }
                btn.updateStatus(!status);
                textView.updateStatus(!status);

            }
        });

        return view;

    }
}

