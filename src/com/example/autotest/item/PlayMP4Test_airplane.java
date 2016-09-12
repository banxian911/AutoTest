package com.example.autotest.item;

import java.io.InputStream;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import static android.provider.Settings.Global.AIRPLANE_MODE_ON;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.autotest.AutoTestApp;
import com.example.autotest.R;
import com.example.autotest.TestActivity;
import com.example.autotest.Util.TestCode;

public class PlayMP4Test_airplane extends Fragment{
	
	private VideoView vv_video;
	private TextView tv_head;
	int oldMode;
	int oldStreamMusicVolume;
	AudioManager mAudioManager;
	private Context mContext;
	TestActivity activity;
    MyHandler mHandler;
	public final int status_play=0;
	public final int status_stop=1;
	final int pass_next = 111;
	class MyHandler extends Handler {
        @Override  
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case status_play:
            	playVideo();
                break;
            case status_stop:
            	stopVideo();
                break;
            case pass_next:
            	activity.next(TestCode.Recoder);

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
		tv_head.setText("Airplane : "+tv_head.getText());
		return view;
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		oldMode = mAudioManager.getMode();
		oldStreamMusicVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);
		setAirplaneMode(true);
		T t=new T();
		t.start();
	}
	public void setAirplaneMode(boolean enabled) {
		final ConnectivityManager mgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		mgr.setAirplaneMode(enabled);
//		Settings.System.putInt(mContext.getContentResolver(),
//				AIRPLANE_MODE_ON, enabled ? 1 : 0);
//		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
//		intent.putExtra("state", enabled);
//		mContext.sendBroadcast(intent);
//		SystemClock.sleep(1000);
    }
	
	class T extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			mHandler.sendEmptyMessage(status_play);
			SystemClock.sleep(AutoTestApp.video_time*1000);
			mHandler.sendEmptyMessage(status_stop);
			SystemClock.sleep(1000);
			mHandler.sendEmptyMessage(pass_next);
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
	public void stopVideo(){
		try{
		vv_video.pause();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		setAirplaneMode(false);
		mAudioManager.setMode(oldMode);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, oldStreamMusicVolume, 0);
	}
}
