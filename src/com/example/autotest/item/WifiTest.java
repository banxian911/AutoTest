package com.example.autotest.item;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.example.autotest.R;
import com.example.autotest.TestActivity;
import com.example.autotest.Util.TestCode;
import com.example.autotest.tool.WifiTestUtil;

import android.app.Fragment;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class WifiTest extends Fragment {
    private final int status_open = 1;
    private final int status_close = 0;
    private final int pass_next = 111;
    private TextView tv_status;
    private TextView tv_list;
    Context mContext;
    Timer timer;
    TestActivity activity;
    WifiManager wifiManager;
    WifiTestUtil testUtil;
    private boolean mFindFlag = false;
    public Handler mHandler;

    private Runnable mR = new Runnable() {
        public void run() {
            if (mFindFlag) {
                Toast.makeText(mContext, "pass", Toast.LENGTH_SHORT).show();
                activity.next(TestCode.GPS);
            } else {
                Toast.makeText(mContext, "fail", Toast.LENGTH_SHORT).show();
                activity.next(TestCode.GPS);
            }
            mHandler.removeCallbacks(mR);
            testUtil.stopTest();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        activity = (TestActivity) getActivity();
        mHandler = new Handler();
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        testUtil = new WifiTestUtil(wifiManager) {

            @Override
            public void wifiStateChange(int newState) {
                switch (newState) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        tv_status.setText("Wifi ON,Discovering...");
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        tv_status.setText("Wifi OFF");
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        tv_status.setText("Wifi Closing");
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        tv_status.setText("Wifi Opening");
                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN:
                    default:
                        tv_status.setText("Wifi state Unknown");
                        // do nothing
                        break;
                }
            }

            @Override
            public void wifiDeviceListChange(List<ScanResult> wifiDeviceList) {
                if (wifiDeviceList == null) {
                    return;
                }

                tv_list.setText("");
                for (ScanResult result : wifiDeviceList) {
                    tv_list.append("device name: ");
                    tv_list.append(result.SSID);
                    tv_list.append("\nsignal level: ");
                    tv_list.append(String.valueOf(result.level));
                    tv_list.append("\n\n");
                    if (result.SSID != null && result.SSID.matches("WiFi-[0-9]{1,2}")) {
                        mFindFlag = true;
                    }
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wifi, null);
        tv_status = (TextView) view.findViewById(R.id.wifi_status);
        tv_list = (TextView) view.findViewById(R.id.wifi_list);
        tv_status.setText("wifi 状态：\n");
        return view;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mHandler.postDelayed(mR, 10000);
        testUtil.startTest(mContext);
    }

    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (wifiManager.isWifiEnabled()) {
                mHandler.sendEmptyMessage(WifiManager.WIFI_STATE_ENABLED);
                wifiManager.setWifiEnabled(false);
                SystemClock.sleep(2000);
                if (!wifiManager.isWifiEnabled()) {
                    mHandler.sendEmptyMessage(WifiManager.WIFI_STATE_DISABLED);
                    SystemClock.sleep(2000);
                }
            } else {
                mHandler.sendEmptyMessage(WifiManager.WIFI_STATE_DISABLED);
                wifiManager.setWifiEnabled(true);
                SystemClock.sleep(2000);
                if (wifiManager.isWifiEnabled()) {
                    mHandler.sendEmptyMessage(WifiManager.WIFI_STATE_ENABLED);
                    SystemClock.sleep(2000);
                }
            }
            mHandler.sendEmptyMessage(pass_next);
            // testUtil.startTest(mContext);
        }
    };

    public void onPause() {
        mHandler.removeCallbacks(mR);
        testUtil.stopTest();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
