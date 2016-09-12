package com.example.autotest;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends Activity implements OnClickListener {
	private EditText etScreenOnTime;
	private EditText etScreenOffTime;
    private Button btn_test;
	Timer timer = new Timer();
	TimerTask task;
	PowerManager pm;
	PowerManager.WakeLock wl;
	long ontime;
	long offtime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
		
//		etScreenOffTime = (EditText) findViewById(R.id.et_screenoff_time);
//		etScreenOnTime = (EditText) findViewById(R.id.et_screenon_time);
//		btn_test=(Button) findViewById(R.id.btn_test);
		btn_test.setOnClickListener(this);
        task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				pm.goToSleep(SystemClock.uptimeMillis());
				wl.release();
				Log.d("hgg", "=========");
				timer.cancel();
			}
		};
	}
	
	private void saveSettings(){
		offtime = Long.parseLong(etScreenOffTime.getText().toString())*1000;
		ontime = Long.parseLong(etScreenOnTime.getText().toString())*1000;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {

		default:
			break;
		}
	}

	private void setScreenBrightnessMax() {
        wl.acquire();
		
		Window localWindow = getWindow();
		WindowManager.LayoutParams localLayoutParams = localWindow
				.getAttributes();
		localLayoutParams.screenBrightness = 1;
		localWindow.setAttributes(localLayoutParams);
	}
	
}
