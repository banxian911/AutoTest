package com.example.autotest.item;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName; 
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import com.example.autotest.TestActivity;
import com.example.autotest.AutoTestApp;

public class BaseFragment extends Fragment {

    private Timer mTimer;
    private TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            ((TestActivity) getActivity()).next();
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("hgg", "onCreate");
        AutoTestApp app = AutoTestApp.getInstance();

        mTimer = new Timer();
        mTimer.schedule(timerTask, 500, app.timeGap);
    }

    @Override
    public void onDestroy() {
        Log.i("hgg", "onDestroy mTimer.cancel");
        mTimer.cancel();
        super.onDestroy();
    }
}
