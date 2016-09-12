package com.example.autotest.tool;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.PowerManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;

import com.android.internal.view.RotationPolicy;
import com.android.settingslib.bluetooth.LocalBluetoothAdapter;
import com.android.settingslib.bluetooth.LocalBluetoothManager;

/**
 * 
 * @author huanggaige,2016.06.07
 *         andorid 6.0
 */
public class FunctionControl { 
    Context mContext;
    ContentResolver mContentResolver;
    String state;
    private NfcAdapter mNfcAdapter;
    LocalBluetoothManager manager;
    private LocalBluetoothAdapter mLocalAdapter;
    long currentTimeout;
    int currentAfterTimeout;

    /**
     * 1.720p 2.1080p 3.4k*2k
     */
    public static int typeVideo = 0;

    public FunctionControl(Context context) {
        this.mContext = context;
        mContentResolver = mContext.getContentResolver();
        state = Settings.System.getStringForUser(mContentResolver,
                "gesture_enable", UserHandle.USER_OWNER);
        manager = LocalBluetoothManager.getInstance(mContext, null);
    }

    public void setAppContext(Context context) {
        mContext = context;
    }

    public void closeFlightMode() {
        // android 6.0
        ConnectivityManager conMgr = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        conMgr.setAirplaneMode(false);
        // Settings.Global.putInt(mContext.getContentResolver(),
        // Settings.Global.AIRPLANE_MODE_ON,
        // false ? 1 : 0);
        // // Post the intent
        // Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        // intent.putExtra("state", false);
        // mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    public void openFlightMode() {
        // android 6.0
        ConnectivityManager conMgr = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        conMgr.setAirplaneMode(true);
        
//        Settings.Global.putInt(mContext.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 
//                true ? 1 : 0);
//        // // Post the intent
//        Intent intent1 = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
//        intent1.putExtra("state", true);
//        mContext.sendBroadcastAsUser(intent1, UserHandle.ALL);
    }

    public void closeLCD() {
        currentTimeout = Settings.Secure.getLong(mContext.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, 30000);
        currentAfterTimeout = Settings.Secure.getInt(
                mContext.getContentResolver(),
                Settings.Secure.LOCK_SCREEN_LOCK_AFTER_TIMEOUT, 3000);
        Settings.System.putInt(mContext.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, 1000);
        Settings.Secure.putInt(mContext.getContentResolver(),
                Settings.Secure.LOCK_SCREEN_LOCK_AFTER_TIMEOUT, 10000);
    }

    public void openLCD() {
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);

        Settings.System.putInt(mContext.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT, (int) currentTimeout);
        Settings.Secure.putInt(mContext.getContentResolver(), Settings.Secure.LOCK_SCREEN_LOCK_AFTER_TIMEOUT, currentAfterTimeout);
    }

    public void closeWIFI() {
        WifiManager mWifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    public void openWIFI() {
        WifiManager mWifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
            mWifiManager.startScan();
            List<ScanResult> results = mWifiManager.getScanResults();
        }
    }

    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public void openGprs(boolean enabled) {
        ConnectivityManager conMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        Class<?> conMgrClass = null; // ConnectivityManager类
        Field iConMgrField = null; // ConnectivityManager类中的字段
        Object iConMgr = null; // IConnectivityManager类的引用
        Class<?> iConMgrClass = null; // IConnectivityManager类
        Method setMobileDataEnabledMethod = null; // setMobileDataEnabled方法
        try {
            // 取得ConnectivityManager类
            conMgrClass = Class.forName(conMgr.getClass().getName());
            // 取得ConnectivityManager类中的对象mService
            iConMgrField = conMgrClass.getDeclaredField("mService");
            // 设置mService可访问
            iConMgrField.setAccessible(true);
            // 取得mService的实例化类IConnectivityManager
            iConMgr = iConMgrField.get(conMgr);
            // 取得IConnectivityManager类
            iConMgrClass = Class.forName(iConMgr.getClass().getName());
            // 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
            setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            // 设置setMobileDataEnabled方法可访问
            setMobileDataEnabledMethod.setAccessible(true);
            // 调用setMobileDataEnabled方法
            setMobileDataEnabledMethod.invoke(iConMgr, enabled);
           }catch (ClassNotFoundException e) {
               e.printStackTrace();
           } catch (NoSuchFieldException e) {
            e.printStackTrace();
           } catch (SecurityException e) {
            e.printStackTrace();
           } catch (NoSuchMethodException e) {
            e.printStackTrace();
           } catch (IllegalArgumentException e) {
            e.printStackTrace();
           } catch (IllegalAccessException e) {
            e.printStackTrace();
           } catch (InvocationTargetException e) {
            e.printStackTrace();
           }
    }

    public void closeGprs() {

    }

    public boolean isMobileConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mMobileNetworkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mMobileNetworkInfo != null) {
            return mMobileNetworkInfo.isAvailable();
        }
        return false;
    }
	
    public int getConnectedType() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
            return mNetworkInfo.getType();
        }
        return -1;
    }
	
	@SuppressWarnings("deprecation")
    public  void closeOrOpenGPS(boolean isOpen) {
        Settings.Secure.setLocationProviderEnabled(mContext.getContentResolver(), LocationManager.GPS_PROVIDER, isOpen);

	}

	public  void closeBT() {
		if(manager!=null){
			mLocalAdapter = manager.getBluetoothAdapter();
			if (mLocalAdapter != null) {
	            mLocalAdapter.setBluetoothEnabled(false);
	        }
		} else{
			// Bluetooth is not supported
		}
	}

	public  void openBT() {
		if(manager!=null){
			mLocalAdapter = manager.getBluetoothAdapter();
			if (mLocalAdapter != null) {
	            mLocalAdapter.setBluetoothEnabled(true);
	        }
		} else{
			// Bluetooth is not supported
		}
	}

	public  void closeNFC() {
		mNfcAdapter = NfcAdapter.getDefaultAdapter(mContext);
		if(mNfcAdapter == null){
			//NFC is not supported.
			return;
		}
		mNfcAdapter.disable();
	}

	public  void openNFC() {
		mNfcAdapter = NfcAdapter.getDefaultAdapter(mContext);
		if(mNfcAdapter == null){
			//NFC is not supported.
			return;
		}
		mNfcAdapter.enable();
	}
    public boolean isRotationLocked() {
	        return RotationPolicy.isRotationLocked(mContext);
	    }
	public  void closeAutoRotation() {
		RotationPolicy.setRotationLock(mContext, false);
	}

	public  void openAutoRotation() {
		 RotationPolicy.setRotationLock(mContext, true);
	}

	public  void closeGesture() {
		state = replaceChar(state, 2, "0");
		Settings.System.putStringForUser(mContentResolver, "gesture_enable", state,UserHandle.USER_OWNER);
//		 Settings.System.putString(mContext.getContentResolver(), "gesture_enable", "11011111");
	}

	public  void openGesture() {
		state = replaceChar(state, 2, "1");
		Settings.System.putStringForUser(mContentResolver, "gesture_enable", state,UserHandle.USER_OWNER);
//		Settings.System.putString(mContext.getContentResolver(), "gesture_enable", "11111111");
	}

	public boolean isOpenGesture(){
		if (state == null || "".equals(state)) {
			return false;
		}
		return state.substring(2, 3).endsWith("1") ? true :false;
	}
	private String replaceChar(String str, int at, String c) {
        if(str == null || "".equals(str)){
            return str;
        }
        StringBuilder strb = new StringBuilder(str);
        strb.replace(at, at+1, c);
        return strb.toString();
    }

	public  void closeEIS() {

	}

	public  void openEIS() {

	}

	public  void closeOIS() {

	}

	public  void openOIS() {

	}

	public  void closeFlashLED() {

	}

    public void openFlashLED() {

    }

    public int SIMNum() {
        return 0;
    }

    public void setBacklightBrightness(int brightness) {
        PowerManager pm = (PowerManager) mContext
                .getSystemService(Context.POWER_SERVICE);
        pm.setBacklightBrightness(brightness);
    }
    
    public static BroadcastReceiver wifiReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            try {
            String action = arg1.getAction();
            Log.d("hgg", "wifiReceiver  action=== "+action);
            if(WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)){
                int status = arg1.getIntExtra(WifiManager.EXTRA_WIFI_STATE,0);
                Log.d("hgg", "wifiReceiver  status=== "+status);

                switch (status) {
                    case WifiManager.WIFI_STATE_ENABLED://3
                        break;
                    case WifiManager.WIFI_STATE_DISABLED://1
                    case WifiManager.WIFI_STATE_DISABLING://0
                    case WifiManager.WIFI_STATE_UNKNOWN://4
                    case WifiManager.WIFI_STATE_ENABLING://2
                    
                    default:
                        break;
                }
            }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    };
}
