package com.example.autotest.item;

import com.example.autotest.AutoTestApp;
import com.example.autotest.R;
import com.example.autotest.TestActivity;
import com.example.autotest.Util.TestCode;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VibratorTest extends Fragment {
    Vibrator vibrator;
    long time = 3000;
    TestActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        vibrator = (Vibrator) this.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vibrator, null);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity = (TestActivity) getActivity();
        T.start();
    }

    Thread T = new Thread() {
        public void run() {
            vibrator.vibrate(AutoTestApp.vibrate_time * 1000);
            for (int i = 0; i < (AutoTestApp.vibrate_time); i++) {
                SystemClock.sleep(1000);
                i++;
            }
            activity.next(TestCode.Gesture);
        };
    };

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (vibrator.hasVibrator())
            vibrator.cancel();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
