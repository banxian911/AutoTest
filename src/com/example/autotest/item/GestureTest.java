package com.example.autotest.item;

import java.util.Timer;
import java.util.TimerTask;

import com.example.autotest.AutoTestApp;
import com.example.autotest.R;
import com.example.autotest.TestActivity;
import com.example.autotest.Util.TestCode;
import com.example.autotest.tool.FunctionControl;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GestureTest extends Fragment {
    FunctionControl control;
    TextView tv_status;
    Timer timer;
    TestActivity activity;
    Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:
                        tv_status.append("打开\n");
                        break;
                    case 2:
                        tv_status.append("关闭");
                        break;
                    case 3:
                        activity.next(TestCode.LocalVideo1);
                        break;
                    default:
                        break;
                }
            };
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gesture, null);
        tv_status = (TextView) view.findViewById(R.id.gesture_status);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity = (TestActivity) getActivity();
        timer = new Timer();
        control = new FunctionControl(getActivity());
        if (control.isOpenGesture()) {
            tv_status.setText(tv_status.getText() + "已打开");
        }
        timer.schedule(task, 2000);
    }

    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            control.openGesture();
            handler.sendEmptyMessage(1);
            SystemClock.sleep(AutoTestApp.guester_time * 1000);
            control.closeGesture();
            handler.sendEmptyMessage(2);
            SystemClock.sleep(2000);
            handler.sendEmptyMessage(3);
        }
    };

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
    }
}
