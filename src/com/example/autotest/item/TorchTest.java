package com.example.autotest.item;

import java.util.List;

import com.example.autotest.AutoTestApp;
import com.example.autotest.R;
import com.example.autotest.TestActivity;
import com.example.autotest.Util.TestCode;
import com.example.autotest.R.id;
import com.example.autotest.R.layout;
import com.example.autotest.R.string;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraAccessException;

public class TorchTest extends Fragment{
	CameraManager mCameraManager;
	private TextView tv_status;
	final int status_close=0;
	final int status_open=1;
	final int pass_next=111;
	private String cameraId;
	private boolean torchOpen = false; 
	Context mContext;
	TestActivity activity;
	Handler mHandler =new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case status_close:
				tv_status.setText(R.string.flashlight_status_close);
				break;
			case status_open:
				tv_status.setText(R.string.flashlight_status_open);
				break;
			case pass_next:
				activity.next(TestCode.Vibrate);
			default:
				break;
			}
		};
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext=this.getActivity();
		activity =(TestActivity) getActivity();
		mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
		Contrl contrl= new Contrl();
		contrl.start();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.flashlight, null);
		tv_status = (TextView) view.findViewById(R.id.flashlight_status);
		return view;
	}
	
	class Contrl extends Thread{
		Camera camera;
		
		Parameters parameters;

		Contrl() {
			//camera = Camera.open();
		}
		@Override
		public void run() {
			super.run();
			try {
				turnOn();
				Thread.sleep(AutoTestApp.flight_time*1000);
				turnOff();
				Thread.sleep(1000L);
            	mHandler.sendEmptyMessage(pass_next);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private void turnOn(){
			try {
				if(mCameraManager == null)
					return;
				String[] ids = mCameraManager.getCameraIdList();
		        for (String id : ids) {
		            CameraCharacteristics c = mCameraManager.getCameraCharacteristics(id);
		            Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
		            Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
		            if (flashAvailable != null && flashAvailable
		                    && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
		            	mCameraManager.setTorchMode(id, true);
		            	cameraId=id;
		            	torchOpen = true;
		            	mHandler.sendEmptyMessage(status_open);
		            	break;
		            }
		        }
	            } catch (CameraAccessException e) {
	                Log.e("hgg", "Couldn't set torch mode", e);
	            }
//			parameters = camera.getParameters();
//			List<String> flashModes = parameters.getSupportedFlashModes();
//			Log.d("hgg", "turnOn====flashModeqqqqq="+flashModes.toString());
//			if (flashModes == null) {
//				return;
//			}
//			String flashMode = parameters.getFlashMode();
//			if (!Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
//				// Turn on the flash
//				if (flashModes.contains(Parameters.FLASH_MODE_TORCH)) {
//					parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
//					camera.setParameters(parameters);
//					Log.d("hgg", "status_open=====");
//					mHandler.sendEmptyMessage(status_open);
//				} else {
//				}
//			}
		}

		private void turnOff() {
			try {
			if(torchOpen){
				mCameraManager.setTorchMode(cameraId, false);
				torchOpen=false;
				mHandler.sendEmptyMessage(status_close);
			}
			} catch (CameraAccessException e) {
                Log.e("hgg", "Couldn't set torch mode", e);
            }
		}
	}
}
