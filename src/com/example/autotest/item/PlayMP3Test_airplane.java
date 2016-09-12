package com.example.autotest.item;

import java.util.Timer;
import java.util.TimerTask;

import com.example.autotest.AutoTestApp;
import com.example.autotest.R;
import com.example.autotest.TestActivity;
import com.example.autotest.Util.TestCode;
import com.example.autotest.tool.FunctionControl;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class PlayMP3Test_airplane extends Fragment {
    /**
     * 0:no headset 1:WiredHeadset 2:bt
     */
    public static int headsetType = 0;
    MediaPlayer player;
    AudioManager mAudioManager;
    private Context mContext;
    private TextView tv_general;
    private TextView tv_general_status;
    public final int status_play=0;
    public final int status_stop=1;
    public final int status_no_head=2;
    public final int error=10;
    final int pass_next=111;

    int oldMode;
    int oldStreamMusicVolume;
    MyHandler mHandler;
    FunctionControl control;
    TestActivity activity;
    AlertDialog dialog;
    private Timer mTimer;
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if(dialog !=null && dialog.isShowing()){
                dialog.dismiss();
                mHandler.sendEmptyMessage(pass_next);
            }
        }
    };
    
    class MyHandler extends Handler {
        @Override  
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case status_play:
                tv_general_status.append("play...\n");
                 break;
            case status_stop:
                tv_general_status.append("stop");
                break;
            case status_no_head:
                dialog = new AlertDialog.Builder(mContext).setTitle("系统提示").setMessage("请插入耳机").create();
                dialog.show();
                mTimer = new Timer();
                mTimer.schedule(timerTask, 5000);
                break;
            case pass_next:
                if (headsetType == 0) {
                    activity.next(TestCode.AirPlaneMP3_h);
                }else if (headsetType == 1) {
                    activity.next(TestCode.RearCamera);
                }
                break;
            case error:
                Toast.makeText(mContext, "由于权限问题，此项暂不可用，请手动测试。",Toast.LENGTH_SHORT).show();
                SystemClock.sleep(2000);
                if (headsetType == 0) {
                    activity.next(TestCode.AirPlaneMP3_h);
                }else if (headsetType == 1) {
                    activity.next(TestCode.RearCamera);
                }
                break;
            default:
                break;
            }
        }
  
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext =this.getActivity();
        activity = (TestActivity) getActivity();
        mHandler = new MyHandler();
        control = new FunctionControl(mContext);
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playmp3, null);
        tv_general=(TextView)view.findViewById(R.id.playmp3_normel_model);
        tv_general_status=(TextView)view.findViewById(R.id.playmp3_normel_model_status);
        if(headsetType == 0){
            tv_general.setText("Airplane mode speak out");
        }else if(headsetType == 1){
            tv_general.setText("Airplane mode headset on");
        }
        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume(); 
        if(headsetType == 1){
            IntentFilter  filter = new IntentFilter();  
            filter.addAction("android.intent.action.HEADSET_PLUG");
            mContext.registerReceiver(HeadsetPlugReceiver, filter);
        }
        oldMode = mAudioManager.getMode();
        oldStreamMusicVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//      mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        try {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);
            control.openFlightMode();
            Timer timer = new Timer();
            timer.schedule(task, 1000);
        } catch (Exception e) {
            mHandler.sendEmptyMessage(error);
            return;
        }
        
    }
    
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if(headsetType == 0){
                mAudioManager.setSpeakerphoneOn(true);
                startPlay();
            } else if(headsetType == 1){
                if(!mAudioManager.isWiredHeadsetOn()){
                    mHandler.sendEmptyMessage(status_no_head);
                } else {
                    startPlay();
                }
            }else if(headsetType == 2){
//                if(!mAudioManager.()){
//                    mHandler.sendEmptyMessage(status_no_head);
//                } else {
//                    startPlay();
//                }
            }
            
        }
    };
    
    public void setAudioGenaralMode(){
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
    }
    
    public void startPlay(){
        setAudioGenaralMode();
        String uri = "android.resource://" + mContext.getPackageName() + "/" + R.raw.test_music;
        Log.d("hgg", "uri=="+uri);
        if(player == null)
            player = MediaPlayer.create(mContext, Uri.parse(uri));
        if(player!=null && !player.isPlaying()){
            player.start();
            mHandler.sendEmptyMessage(status_play);
            mHandler.post(run);
        }
        
    }
    
    private Runnable run = new Runnable() {
        int  currentPosition, duration;
        public void run() {
            if(player!=null){
                // 获得当前播放时间和当前视频的长度
                currentPosition = player.getCurrentPosition();
                duration = player.getDuration();
                int time = ((currentPosition * 100) / duration);
                Log.d("hgg", "play music time=="+time);
                if(time == AutoTestApp.music_time){
                    stopPlay();
                    mHandler.sendEmptyMessage(status_stop);
                    SystemClock.sleep(1000);
                    mHandler.sendEmptyMessage(pass_next);
                }else{
                mHandler.postDelayed(run, 1000);
                }
            }
        }
    };
    
    public void stopPlay(){
        if(player!=null && player.isPlaying()){
            player.stop();
        }
        if (player != null) {
            player.release();
            player=null;
        }
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if(headsetType == 1){
            try{
            mContext.unregisterReceiver(HeadsetPlugReceiver);
            }catch(Exception e){
                
            }
        }
        stopPlay();
        try {
            control.closeFlightMode();
        } catch (Exception e) {
            // TODO: handle exception
        }
        mAudioManager.setMode(oldMode);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, oldStreamMusicVolume, 0);
        mHandler.removeCallbacks(run);
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d("hgg", "PlayMP3Test_airplane   onDestroy ");
    }
    
    BroadcastReceiver HeadsetPlugReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Log.d("hgg", "HeadsetPlugReceiver=========");
            if (intent.hasExtra("state")) {
                if (intent.getIntExtra("state", 0) == 0) {
                    Toast.makeText(context, "headset not connected",
                            Toast.LENGTH_LONG).show();
                } else if (intent.getIntExtra("state", 0) == 1) {
                    Toast.makeText(context, "headset  connected",
                            Toast.LENGTH_LONG).show();
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    startPlay();
                }
            }
        }
    };
}
