package com.example.autotest.tool;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;

public class NetworkManage {
	private Context context;
    private ConnectivityManager connManager;
 
    public NetworkManage(Context context) {
        this.context = context;
        connManager = (ConnectivityManager) this.context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isNetworkConnected() {
        NetworkInfo networkinfo = connManager.getActiveNetworkInfo();
        if (networkinfo != null) {
            return networkinfo.isConnected();
        }
        return false;
    }

    public boolean isWifiConnected() {
        NetworkInfo mWifi = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi != null) {
            return mWifi.isConnected();
        }
        return false;
    }

    public boolean isMobileConnected() {
        NetworkInfo mMobile = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mMobile != null) {
            return mMobile.isConnected();
        }
        return false;
    }

    public void toggleGprs(boolean isEnable) throws Exception {
        Class<?> cmClass = connManager.getClass();
        Class<?>[] argClasses = new Class[1];
        argClasses[0] = boolean.class;
 
        Method method = cmClass.getMethod("setMobileDataEnabled", argClasses);
        method.invoke(connManager, isEnable);
    }

    public boolean toggleWiFi(boolean enabled) {
        WifiManager wm = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        return wm.setWifiEnabled(enabled);
    }
    
    public boolean isAirplaneModeOn() { 
        int modeIdx = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0); 
        boolean isEnabled = (modeIdx == 1);
        return isEnabled;
    } 
    public void toggleAirplaneMode(boolean setAirPlane) { 
    	final ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		mgr.setAirplaneMode(setAirPlane);
    }
}
