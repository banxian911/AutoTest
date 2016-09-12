package com.example.autotest.item;

import java.util.Timer;
import java.util.TimerTask;

import com.example.autotest.R;
import com.example.autotest.TestActivity;
import com.example.autotest.tool.WifiAutoConnectManager;
import com.example.autotest.tool.WifiAutoConnectManager.WifiCipherType;

import android.app.Fragment;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class WifiPlayvideoTest extends Fragment {
    public static int netType = 3;
    private final int status_open = 1;
    private final int status_close = 0;
    private final int pass_next = 111;
    WebView wv_page;
    TextView tv_head;
    Context mContext;
    WifiManager wifiManager;
    private WifiInfo currentWifiInfo;
    public MyHandler mHandler;
    public TestActivity activity;
    boolean isConn = false;
    boolean isLoading = false;
    WifiAutoConnectManager manager;
    
    class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case pass_next:
                wv_page.stopLoading();
                if (activity != null) {
                }
                break;
            case 5:
                if(msg.arg1 == 1){
                    Toast.makeText(mContext, "WIFI 连接成功 ",Toast.LENGTH_SHORT).show();
                    setPageSettings();
                }else{
                    Toast.makeText(mContext, "WIFI 连接失败",Toast.LENGTH_SHORT).show();
                }
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
        activity = (TestActivity) getActivity();
        wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mHandler = new MyHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wifi_page, null);
        tv_head = (TextView) view.findViewById(R.id.textView1);
        tv_head.setText("WIFI Video Test");
        wv_page = (WebView) view.findViewById(R.id.webview);
        wv_page.loadUrl("http://v.youku.com/v_show/id_XMTU5MTQ0MTM5Mg==.html?x");
        // http://v.youku.com/v_show/id_XMTU5MTQ0MTM5Mg==.html?x

        return view;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        
        timer = new Timer();
        
        if (netType == 3) {
            manager = new WifiAutoConnectManager(
                    wifiManager, mContext) {
                @Override
                public void setIsConn(boolean conn) {
                    Message msg = new Message();
                    msg.arg1 = conn ? 1 : 0;
                    msg.what = 5;
                    mHandler.sendMessage(msg);
                }
            };
            mContext.registerReceiver(manager.wifiReceiver, filter);
            manager.connect("WiFi-59", "hgg12345",
                    WifiCipherType.WIFICIPHER_WPA);
        } else {

        }
    }

    public void setPageSettings() {
        WebSettings webSettings = wv_page.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv_page.setScrollBarStyle(0);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);
        wv_page.loadUrl("http://imps.tcl-ta.com/cailiang/media/lq/3gp/h264_aac_320_240_v50_10_a16_22_m.mp4");
        wv_page.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    SystemClock.sleep(1000);
                    mHandler.sendEmptyMessage(3);
                } else {
                }
            }
        });
        wv_page.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(final WebView view,
                    final String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.getSettings().setBlockNetworkImage(false);
            }
            
            @Override
            public void onFormResubmission(WebView view, Message dontResend,
                    Message resend) {
                super.onFormResubmission(view, dontResend, resend);
            }
            
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                Log.d("hgg", "onLoadResource=========url===");
                isLoading = true;
                if(task !=null){
                    task.cancel();
                }
                task = new MyTimerTask();
                timer.schedule(task, 10000L);

            }
        });
     }
     Timer timer;
     MyTimerTask task;
     class MyTimerTask extends TimerTask{
         
         @Override
         public void run() {
             Log.d("hgg", "MyTimerTask=========url===");
             isLoading = false;
         }
     };
    public void setPage() {
        wv_page.loadUrl("http://imps.tcl-ta.com/cailiang/media/lq/3gp/h264_aac_320_240_v50_10_a16_22_m.mp4");
    }

    public boolean isConn() {
        return isConn;
    }

    public void setConn(boolean isConn) {
        this.isConn = isConn;
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if(manager!=null)
            mContext.unregisterReceiver(manager.wifiReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
