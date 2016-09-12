package com.example.autotest;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.List;

public class AutoTestApp extends Application {

    private static AutoTestApp instance;
    private List<Util.TestCode> mTestItems = new ArrayList<>();
    public long timeGap = 5 * 1000; //5s
    public static int LCDBackLight_time = 5;
    public static int flight_time = 5;
    public static int FlightMode_time = 5;
    public static int vibrate_time = 5;
    public static int guester_time = 5;
    public static int video_time = 5;
    public static int music_time = 5;
    public static AutoTestApp getInstance() {
        if (instance == null) {
            throw new IllegalStateException("No AutoTest here!");
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        timeGap = PreferenceManager.getDefaultSharedPreferences(this).getInt("time_gap", 5) * 1000;
		android.util.Log.i("hgg", "time_gap = " + timeGap);
		initTime();
        initTestItems();
        
    }

    
    private void initTestItems() {
		mTestItems.add(Util.TestCode.LCDBackLight);
		mTestItems.add(Util.TestCode.FlightMode);
		mTestItems.add(Util.TestCode.WIFI);
		mTestItems.add(Util.TestCode.GPS);
		mTestItems.add(Util.TestCode.Torch);
		mTestItems.add(Util.TestCode.Vibrate);
		mTestItems.add(Util.TestCode.Gesture);
		mTestItems.add(Util.TestCode.LocalVideo1);
		mTestItems.add(Util.TestCode.LocalVideo2);
		mTestItems.add(Util.TestCode.LocalVideo3);
		mTestItems.add(Util.TestCode.NormolMP3);
		mTestItems.add(Util.TestCode.NormolMP3_h);
		mTestItems.add(Util.TestCode.AirPlaneMP3);
		mTestItems.add(Util.TestCode.AirPlaneMP3_h);
		//mTestItems.add(Util.TestCode.AirPlaneMP3_bt);
		mTestItems.add(Util.TestCode.RearCamera);
		mTestItems.add(Util.TestCode.RearCameraRecord);
		mTestItems.add(Util.TestCode.FrontCamer);
		mTestItems.add(Util.TestCode.FrontCamerRecord);
		
//		mTestItems.add(Util.TestCode.FMPlay);
		mTestItems.add(Util.TestCode.Recoder);
		mTestItems.add(Util.TestCode.SD_W_R);
		mTestItems.add(Util.TestCode.Browser_3G);
//		mTestItems.add(Util.TestCode.Browser_3G_video);
//		mTestItems.add(Util.TestCode.Browser_4G_video);
		mTestItems.add(Util.TestCode.Browser_wifi_video);
    }

    public List<Util.TestCode> getTestItems() {
        return mTestItems;
    }
    public  static void initTime(){
        SharedPreferences preferences = instance.getSharedPreferences ("test_option", Context.MODE_PRIVATE);
        LCDBackLight_time = preferences.getInt("LCDBackLight_time",5);
        flight_time = preferences.getInt("LCDBackLight_time",5);
        FlightMode_time = preferences.getInt("FlightMode_time",5);
        vibrate_time = preferences.getInt("vibrate_time",5);
        guester_time = preferences.getInt("guester_time",5);
        video_time = preferences.getInt("video_time",5);
        music_time = preferences.getInt("music_time",5);
    }
}
