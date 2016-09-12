package com.example.autotest.item;

import java.util.Timer;
import java.util.TimerTask;

import com.example.autotest.AutoTestApp;
import com.example.autotest.R;
import com.example.autotest.TestActivity;
import com.example.autotest.Util.TestCode;
import com.example.autotest.tool.FunctionControl;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class FlightModeTest extends Fragment {
    FunctionControl control;
    TextView tv_status;
    Timer timer;
    TestActivity activity;

    Handler handler;
    Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:
                        tv_status.append("正在打开\n");
                        break;
                    case 2:
                        tv_status.append("打开\n");
                        break;
                    case 3:
                        tv_status.append("关闭");
                        break;
                    case 4:
                        Toast.makeText(mContext, "由于权限问题，此项暂不可用，请手动测试。", Toast.LENGTH_SHORT).show();
                        SystemClock.sleep(2000);
                        activity.next(TestCode.WIFI);
                        break;
                    default:
                        break;
                }
            };
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flightmode, null);
        tv_status = (TextView) view.findViewById(R.id.flight_status);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity = (TestActivity) getActivity();
        timer = new Timer();
        control = new FunctionControl(getActivity());
        if(task!=null) {
            timer.cancel();
        }
        task = new Task();
        timer.schedule(task, 2000);
    }

    Task task;
    class Task extends TimerTask {

        @Override
        public void run() {
            handler.sendEmptyMessage(1);
            try {
                control.openFlightMode();
                handler.sendEmptyMessage(2);
                SystemClock.sleep(AutoTestApp.FlightMode_time * 1000);
                control.closeFlightMode();
                handler.sendEmptyMessage(3);
                SystemClock.sleep(2000);
                activity.next(TestCode.WIFI);
            } catch (Exception e) {
                Log.d("hgg", "e===" + e.getMessage());
                handler.sendEmptyMessage(4);
            }

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
