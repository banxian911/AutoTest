package com.example.autotest.tool;

import java.util.List;

import com.example.autotest.item.WifiBrowserTest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class WifiAutoConnectManager {

    private static final String TAG = "hgg";

    WifiManager wifiManager;
    Context context;

    // 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    // 构造函数
    public WifiAutoConnectManager(WifiManager wifiManager, Context context) {
        this.wifiManager = wifiManager;
        this.context = context;
    }

    /**
     * 提供一个外部接口，传入要连接的无线网
     * 
     * @param ssid
     * @param password
     * @param type
     */
    public void connect(String ssid, String password, WifiCipherType type) {
        Thread thread = new Thread(new ConnectRunnable(ssid, password, type));
        thread.start();
    }

    /**
     * 查看以前是否也配置过这个网络
     */
    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        Log.d(TAG, "existingConfigs.size ======" + (existingConfigs==null?0:existingConfigs.size()) + "    SSI=" + SSID);
        if(existingConfigs!=null){
        for (WifiConfiguration existingConfig : existingConfigs) {
            Log.d(TAG, "existingConfig.SSID ======" + existingConfig !=null ? existingConfig.SSID :"null");
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        }
        return null;
    }

    private WifiConfiguration createWifiInfo(String SSID, String Password,
            WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        // nopass
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        // wep
        if (Type == WifiCipherType.WIFICIPHER_WEP) {
            if (!TextUtils.isEmpty(Password)) {
                if (isHexWepKey(Password)) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = "\"" + Password + "\"";
                }
            }
            config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        // wpa
        if (Type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            // 此处需要修改否则不能自动重联
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    // 打开wifi功能
    private boolean openWifi() {
        boolean bRet = true;
        if (!wifiManager.isWifiEnabled()) {
            bRet = wifiManager.setWifiEnabled(true);
        }
        return bRet;
    }

    class ConnectRunnable implements Runnable {
        private String ssid;

        private String password;

        private WifiCipherType type;

        public ConnectRunnable(String ssid, String password, WifiCipherType type) {
            this.ssid = ssid;
            this.password = password;
            this.type = type;
        }

        @Override
        public void run() {
            // 打开wifi
            openWifi();
            // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
            // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
            int time = 0;
            while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                try {
                    // 为了避免程序一直while循环，让它睡个500毫秒检测……
                    Thread.sleep(500);
                    time++;
                    Log.d(TAG, "time   ===" + time);
                } catch (InterruptedException ie) {
                }
            }
            
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            Log.d(TAG,"wifiInfo != null======== " + (wifiInfo != null));
            if(wifiInfo != null && wifiInfo.getSSID().equalsIgnoreCase(ssid)){
                Log.d(TAG,"连接到网络 ========" + wifiInfo.getSSID());
                setIsConn(true);
                return;
            }
            
            WifiConfiguration wifiConfig = createWifiInfo(ssid, password, type);
            //
            if (wifiConfig == null) {
                Log.d(TAG, "wifiConfig is null!");
                return;
            }

            WifiConfiguration tempConfig = isExsits(ssid);

            if (tempConfig != null) {
                wifiManager.removeNetwork(tempConfig.networkId);
            }

            int netID = wifiManager.addNetwork(wifiConfig);
            boolean enabled = wifiManager.enableNetwork(netID, true);
            Log.d(TAG, "enableNetwork status enable=" + enabled);
            boolean connected = wifiManager.reconnect();
            Log.d(TAG, "enableNetwork connected=" + connected);
            SystemClock.sleep(3000);
//            setIsConn(connected);
        }
    }

    public void setIsConn(boolean conn){}
    private static boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();

        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }

        return isHex(wepKey);
    }

    private static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f')) {
                return false;
            }
        }

        return true;
    }
    int disConntime = 0;
    public  BroadcastReceiver wifiReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            try {
                String action = arg1.getAction();
                Log.d("hgg", "wifiReceiver  action=== " + action);
                if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                    int status = arg1.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                            0);
                    Log.d("hgg", "wifiReceiver  status=== " + status);

                    switch (status) {
                    case WifiManager.WIFI_STATE_ENABLED:// 3
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:// 1
                    case WifiManager.WIFI_STATE_DISABLING:// 0
                    case WifiManager.WIFI_STATE_UNKNOWN:// 4
                    case WifiManager.WIFI_STATE_ENABLING:// 2

                    default:
                        break;
                    }
                } else if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)){
                    NetworkInfo info = arg1.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if(info.getState().equals(NetworkInfo.State.DISCONNECTED)){  
                        Log.d(TAG, "wifi网络连接断开");
                        disConntime++;
                        if(disConntime == 25)
                            setIsConn(false);
                    }  
                    else if(info.getState().equals(NetworkInfo.State.CONNECTED)){  
                        WifiManager wifiManager = (WifiManager)arg0.getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        //获取当前wifi名称  
                        Log.d(TAG,"连接到网络 " + wifiInfo.getSSID());
                        setIsConn(true);
                    }
                }
                
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    };
}