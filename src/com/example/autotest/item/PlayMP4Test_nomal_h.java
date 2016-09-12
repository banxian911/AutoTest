package com.example.autotest.item;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
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
import android.widget.VideoView;

import com.example.autotest.AutoTestApp;
import com.example.autotest.R;
import com.example.autotest.TestActivity;
import com.example.autotest.Util.TestCode;

public class PlayMP4Test_nomal_h extends Fragment{
	
	private VideoView vv_video;
	private TextView tv_head;

	int oldMode;
	int oldStreamMusicVolume;
	AudioManager mAudioManager;
	private Context mContext;
    MyHandler mHandler;
	public final int status_play=0;
	public final int status_stop=1;
	public final int status_no_head=2;
	final int pass_next=111;
	TestActivity activity;
	AlertDialog dialog;
	private Timer mTimer;
	private TimerTask timerTask = new TimerTask() {

		@Override
		public void run() {
			if(dialog !=null){
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
            case 0:
            	playVideo();
                break;
            case status_no_head:
            	dialog = new AlertDialog.Builder(getActivity()).setTitle("系统提示")
            	.setMessage("请插入耳机").create();
            	dialog.show();
            	mTimer = new Timer();
            	mTimer.schedule(timerTask, 5000);
                break;
            case pass_next:
            	activity.next(TestCode.AirPlaneMP3);

            default:
                break;
            }
        }
  
    }
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this.getActivity();
		activity = (TestActivity) getActivity();
		mHandler = new MyHandler();
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.playmp4, null);
		vv_video = (VideoView)view.findViewById(R.id.vv_video);
		tv_head=(TextView)view.findViewById(R.id.tv_head);
		tv_head.setText(tv_head.getText()+" headset");
		return view;
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IntentFilter  filter = new IntentFilter();  
        filter.addAction("android.intent.action.HEADSET_PLUG");
        mContext.registerReceiver(HeadsetPlugReceiver, filter);
		oldMode = mAudioManager.getMode();
		oldStreamMusicVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);
		T t=new T();
		t.start();
	}
	class T extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			if(!mAudioManager.isWiredHeadsetOn()){
				mHandler.sendEmptyMessage(status_no_head);
			} else{
				play();
			}
		}
	}
	public void playVideo(){
		try{
		String uri = "android.resource://" + mContext.getPackageName() + "/" + R.raw.test_video;
		//InputStream is=getResources().getAssets().open("Test_video");
		vv_video.setBackgroundColor(0);
		vv_video.setVideoURI(Uri.parse(uri));
		vv_video.requestFocus();
		vv_video.start();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void play(){
		mHandler.sendEmptyMessage(status_play);
		SystemClock.sleep(AutoTestApp.video_time*1000);
		mHandler.sendEmptyMessage(status_stop);
		SystemClock.sleep(1000);
		mHandler.sendEmptyMessage(pass_next);
	}
	public void stopVideo(){
		try{
			if(vv_video.isPlaying()){
				vv_video.pause();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mContext.unregisterReceiver(HeadsetPlugReceiver);
		mAudioManager.setMode(oldMode);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, oldStreamMusicVolume, 0);
	}
	
	BroadcastReceiver HeadsetPlugReceiver = new BroadcastReceiver() {
	    @Override  
	    public void onReceive(Context context, Intent intent) {
	        // TODO Auto-generated method stub  
	          Log.d("hgg", "HeadsetPlugReceiver=========");
	            if(intent.hasExtra("state")){  
	                if(intent.getIntExtra("state", 0)==0){  
	                    Toast.makeText(context, "headset not connected", Toast.LENGTH_LONG).show();  
	                }  
	                else if(intent.getIntExtra("state", 0)==1){  
	                    Toast.makeText(context, "headset  connected", Toast.LENGTH_LONG).show();
	                    if(dialog!=null &&dialog.isShowing()){
	                    	dialog.dismiss();
	                    }
	                    play();
	                }  
	            }  
	    }  
	};
}
