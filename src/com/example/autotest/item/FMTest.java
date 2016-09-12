package com.example.autotest.item;

import java.util.Timer;
import java.util.TimerTask;

import com.android.fmradio.FmNative;
import com.example.autotest.R;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioSystem;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FMTest extends Fragment {

    public static String TAG = "hgg";
    private static final float POWER_UP_START_FREQUENCY = 87.5f;
    private static final int RADIO_AUDIO_DEVICE_WIRED_HEADSET = 0;
    Context mContext;
    Handler mHandler;
    private static final int MSG_TEST_FM_SEARCH = 0x3006;
    private static final int MSG_POWER_UP = 0;   
	  
    private int isFmPro = 0; 
    private Timer mTimer;
    private TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            mHandler.sendEmptyMessage(MSG_TEST_FM_SEARCH);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
            }
        };
        // manager = new FmManagerSelect(mContext);
        // manager.openDev();
        // manager.setMute(false);
        // manager.powerUp(mFreq);
        // mHandler.sendEmptyMessage(MSG_TEST_HEADSET_CONNECT);
        // mTimer = new Timer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm, null);
        return view;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        startPowerUpFM();
    }

    public void startPowerUpFM() {
        new StartPowerUpThread().start();
    }

    class StartPowerUpThread extends Thread {
        public void run() {
            powerOnFM();
        };
    };

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if(isFmPro !=0){
            stopRender();
            powerOffFM();
        }
    }

    public void powerOnFM() {
        Log.d(TAG, "startPowerUp"); 
        boolean value = false;
        System.loadLibrary("fmjni");
        FmNative.setMute(true);// 
        value = FmNative.openDev();
        if (!value) {
            Log.d(TAG, "powerUp fail");
            isFmPro = 0;
            return;
        }
        value = FmNative.powerUp(POWER_UP_START_FREQUENCY);
        if (!value) {
            Log.e(TAG, "powerUp fail ");
            isFmPro = 0;
            return;
        }
        isFmPro = 2;
        Log.d(TAG, "sendMessage MSG_POWER_UP");
        mHandler.sendMessage(mHandler.obtainMessage(MSG_POWER_UP));
    }
    
    private synchronized void startRender() {
        Log.d(TAG, "startRender ");
        AudioSystem.setDeviceConnectionState(
                AudioManager.DEVICE_OUT_FM_HEADSET,
                AudioSystem.DEVICE_STATE_AVAILABLE, "", "");
    }

    private synchronized void stopRender() { 
        Log.d(TAG, "stopRender===========");
        AudioSystem.setDeviceConnectionState(
                AudioManager.DEVICE_OUT_FM_HEADSET,
                AudioSystem.DEVICE_STATE_UNAVAILABLE, "", "");
        AudioSystem.setForceUse(AudioSystem.FOR_FM, AudioSystem.FORCE_NONE);
    }
    
    public void setFMPlayerRoute(int headsetOrSpeaker) {
        Log.d(TAG,"set fm route====");
        if (isFmPro==0) {
            isFmPro =1;
            startPowerUpFM();
            return;
        }
        setFMAudioPath(headsetOrSpeaker);
        FmNative.setMute(true);
        FmNative.setRds(false);
        FmNative.tune((float)106.8);
        FmNative.setRds(true);
        FmNative.setMute(false);
    }

    public void setFMAudioPath(int headsetOrSpeaker) {
        Log.d(TAG,"set fm audio path: " + headsetOrSpeaker);
        if (headsetOrSpeaker == RADIO_AUDIO_DEVICE_WIRED_HEADSET) {
            AudioSystem.setForceUse(AudioSystem.FOR_FM, AudioSystem.FORCE_NONE);//0
        } else {
            AudioSystem.setForceUse(AudioSystem.FOR_FM, AudioSystem.FORCE_SPEAKER);//1
        }
    }
    public void powerOffFM() {
        Log.d(TAG, "power off fm");
        try {
            while (isFmPro == 1) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
                Log.d(TAG, "" + e);
        }

        FmNative.setMute(true);
        FmNative.setRds(false);
        FmNative.powerDown(0);
        FmNative.closeDev();
        isFmPro = 0;
    }
}
