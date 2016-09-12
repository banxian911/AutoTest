package com.example.autotest.item;

import com.example.autotest.R;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BtTest extends Fragment {
	private final int status_open = 1;
	private final int status_searching=2;
	private final int status_finish=3;
	Context mContext;
	BluetoothAdapter adapter;
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case status_open:
				
				break;
			case status_searching:
				break;
			case status_finish:
				break;
			default:
				break;
			}
		};
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this.getActivity();
		openBt();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		mContext.registerReceiver(receiverSerch, filter);
		if(!T.isAlive())
			T.start();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view =inflater.inflate(R.layout.bluetooth, null);
		return view;
	}
	
	
	private void openBt(){
		adapter=BluetoothAdapter.getDefaultAdapter();
		if(adapter == null)
			return ;
		if(!adapter.isEnabled()){
			adapter.enable();
//			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//			intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//			mContext.startActivity(intent);
		} 
	}
	
	Thread T = new Thread(new Runnable() {
		
		@Override
		public void run() {
			if(adapter !=null){
				adapter.startDiscovery();
			}
		}
	});
	
	private BroadcastReceiver receiverSerch = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String action = arg1.getAction();
			Log.d("hgg", "action======="+action);
			if(BluetoothDevice.ACTION_FOUND.equals(action)){
				BluetoothDevice device =arg1.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Log.d("hgg", "device.getName()=="+device.getName());
			}
		}
		
	};
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mContext.unregisterReceiver(receiverSerch);
	}
}
