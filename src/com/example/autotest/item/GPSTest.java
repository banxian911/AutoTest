package com.example.autotest.item;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import com.example.autotest.R;
import com.example.autotest.TestActivity;
import com.example.autotest.Util.TestCode;

import android.app.Fragment;
import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class GPSTest extends Fragment {
    private LocationManager manager;
    private GpsStatus.Listener gpsStatusListener;
    private LocationListener locationListener;
    private Context mContext;
    TextView tv_gpsstatus;
    TextView tv_gpsinfo;
    TextView tv_count;
    TextView tv_time;
    boolean isOpengps = false;
    private static final int SATELLITE_COUNT_MIN = 4;
    private static final int MSG_UPDATE_TIME = 111;
    private int mSatelliteCount;
    private int timeCount = 0;
    public Handler mHandler;
    
    private Runnable mR = new Runnable() {
        public void run() {
            if (mSatelliteCount > 0) {
                Toast.makeText(getActivity(), "pass",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "fail",
                        Toast.LENGTH_SHORT).show();
            }
            ((TestActivity)getActivity()).next(TestCode.Torch);
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        tv_time.setText(timeCount + " ");
                        break;
                    default:
                        break;
                }
            }
        };
        manager = (LocationManager) mContext
                .getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gps, null);
        tv_gpsstatus = (TextView) view.findViewById(R.id.gps_status);
        tv_gpsinfo = (TextView) view.findViewById(R.id.gps_info);
        tv_count = (TextView) view.findViewById(R.id.gps_count);
        tv_time = (TextView) view.findViewById(R.id.txt_gps_time_count);
        return view;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        startTimer();
        mHandler.postDelayed(mR, 60000);
        if (isGpsEnabled()) {
            tv_gpsstatus.setText("GPS 已打开\n");
        }
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        gpsStatusListener = new Listener() {

            @Override
            public void onGpsStatusChanged(int arg0) {
                // TODO Auto-generated method stub
                if (arg0 == GpsStatus.GPS_EVENT_FIRST_FIX) {
                    tv_gpsstatus.append("第一次定位\n");
                    Log.d("hgg", "第一次定位");
                } else if (arg0 == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
                   // tv_gpsstatus.setText("卫星状态改变\n");
                    showGps();
                } else if (arg0 == GpsStatus.GPS_EVENT_STARTED) {
                    tv_gpsstatus.append("定位启动\n");
                    Log.d("hgg", "定位启动");
                } else if (arg0 == GpsStatus.GPS_EVENT_STOPPED) {
                    tv_gpsstatus.append("定位结束\n");
                    Log.d("hgg", "定位结束");
                }
            }
        };
        locationListener = new LocationListener() {// 位置监听
            @Override
            public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
                // TODO Auto-generated method stub
                if (arg1 == LocationProvider.AVAILABLE) {
                    System.out.println("当前GPS状态：可见的\n");
                } else if (arg1 == LocationProvider.OUT_OF_SERVICE) {
                    System.out.println("当前GPS状态：服务区外\n");
                } else if (arg1 == LocationProvider.TEMPORARILY_UNAVAILABLE) {
                    System.out.println("当前GPS状态：暂停服务\n");
                }
            }

            @Override
            public void onProviderEnabled(String arg0) {
                // TODO Auto-generated method stub
                tv_gpsstatus.append("当前GPS状态：开启\n");
                System.out.println("当前GPS状态：开启\n");
            }

            @Override
            public void onProviderDisabled(String arg0) {
                // TODO Auto-generated method stub
                tv_gpsstatus.append("当前GPS状态：禁用\n");
                System.out.println("当前GPS状态：禁用\n");
                boolean gps = manager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);
                Log.d("hgg", "gps=========" + gps);
            }

            @Override
            public void onLocationChanged(Location arg0) {
                // TODO Auto-generated method stub
                System.out.println("时间：" + arg0.getTime());
                System.out.println("经度：" + arg0.getLongitude());
                System.out.println("纬度：" + arg0.getLatitude());
                System.out.println("海拔：" + arg0.getAltitude());
            }
        };
        openOrCloseGPS(getActivity(), true);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0,
                locationListener);
        manager.addGpsStatusListener(gpsStatusListener);
        
        // manager.setTestProviderEnabled(provider, enabled)
    }

    private Timer mTimer;
    private TimerCountTask task;
    private class TimerCountTask extends TimerTask {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            timeCount++;
        }
    }

    private void startTimer() {
        mTimer = new Timer();
        if(task!=null){
            task.cancel();
        }
        task = new TimerCountTask();
        mTimer.scheduleAtFixedRate(task, 0, 1000);
    }

    @Override
    public void onStop() {
        openOrCloseGPS(mContext, false);
        if (gpsStatusListener != null) {
            manager.removeGpsStatusListener(gpsStatusListener);
        }
        if (locationListener != null) {
            manager.removeUpdates(locationListener);
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mR);
        if(task!=null){
            task.cancel();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    private boolean isGpsEnabled() {
        if (manager == null) {
            return false;
        }
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void showGps() {
        boolean flag = false;
        if (manager != null) {
            GpsStatus gpsStatus = manager.getGpsStatus(null);
            //int maxSatellites = gpsStatus.getMaxSatellites();
            Iterator<GpsSatellite> iterator = gpsStatus.getSatellites().iterator();// gps卫星
            int count = 0;
            tv_gpsinfo.setText("gps info :\n");
            while (iterator.hasNext()) {
                System.out.println("has next");
                count++;
                GpsSatellite gpsSatellite = iterator.next();
                float SNR = gpsSatellite.getSnr();
                if(SNR>35.0)
                    flag = true;
                tv_gpsinfo.append("id " + count + "\n");
                tv_gpsinfo.append("Azimuth " + gpsSatellite.getAzimuth() + "\n");
                tv_gpsinfo.append("Prn " + gpsSatellite.getPrn() + "\n");
                tv_gpsinfo.append("Snr " + gpsSatellite.getSnr() + "\n");
                System.out.println(tv_gpsinfo.getText().toString());
            }
            
            if (count >= SATELLITE_COUNT_MIN /*&& flag*/) {
                Toast.makeText(getActivity(), "PASS",
                        Toast.LENGTH_SHORT).show();
                ((TestActivity)getActivity()).next(TestCode.Torch);
            }
            if (count > mSatelliteCount) {
                mSatelliteCount = count;
            }
        }
        tv_count.setText(" " + mSatelliteCount);
    }

    @SuppressWarnings("deprecation")
    public final void openOrCloseGPS(Context context,boolean isopen) {
        tv_gpsstatus.append(isopen ?"当前GPS状态：正在开启...\n":"当前GPS状态：正在关闭...\n");
        Settings.Secure.setLocationProviderEnabled(context.getContentResolver(), LocationManager.GPS_PROVIDER, isopen);

//        Intent GPSIntent = new Intent();
//        GPSIntent.setAction("android.location.MODE_CHANGED");
//        GPSIntent.setClassName("com.android.settings",
//                "com.android.settings.widget.SettingsAppWidgetProvider");
//        GPSIntent.addCategory(Intent.CATEGORY_ALTERNATIVE);
//        GPSIntent.setData(Uri.parse("custom:3"));
//        try {
//            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
//        } catch (CanceledException e) {
//            e.printStackTrace();
//        }
    }
}
