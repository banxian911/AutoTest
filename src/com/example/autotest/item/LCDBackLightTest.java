package com.example.autotest.item;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.example.autotest.AutoTestApp;
import com.example.autotest.TestActivity;
import com.example.autotest.R;
import com.example.autotest.Util;
import com.example.autotest.Util.TestCode;

public class LCDBackLightTest extends Fragment {

    private static final String TAG = "LCDBackLight Test";
    private TextView mTitleView;
    private int storedScreenBrightness;

    private int getScreenBrightness(Activity activity) {
        Window localWindow = activity.getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        return (int) localLayoutParams.screenBrightness * 255;
    }

    private void setScreenBrightness(Activity activity, int brightness) {
        Window localWindow = activity.getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        localLayoutParams.screenBrightness = brightness / 255.0f;
        localWindow.setAttributes(localLayoutParams);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        storedScreenBrightness = getScreenBrightness(getActivity());
        setScreenBrightness(getActivity(), 255);
        if(task!=null){
            task.cancel();
        }
        task = new Task();
        timer.schedule(task, AutoTestApp.LCDBackLight_time * 1000);
    }

    Timer timer = new Timer();
    Task task;
    class Task extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            SystemClock.sleep(2000);
            ((TestActivity) getActivity()).next(TestCode.FlightMode);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if(task!=null){
            task.cancel();
        }
        setScreenBrightness(getActivity(), storedScreenBrightness);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lcd, null);
        mTitleView = (TextView) view.findViewById(android.R.id.title);
        mTitleView.setText(Util.TestCode.LCDBackLight.toString());
        return view;
    }
}
