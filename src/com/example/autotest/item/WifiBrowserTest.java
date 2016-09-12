package com.example.autotest.item;

import java.util.Timer;
import java.util.TimerTask;

import com.example.autotest.R;
import com.example.autotest.TestActivity;
import com.example.autotest.Util.TestCode;
import com.example.autotest.tool.FunctionControl;
import com.example.autotest.tool.WifiAutoConnectManager;
import com.example.autotest.tool.WifiAutoConnectManager.WifiCipherType;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;
import static android.net.ConnectivityManager.TYPE_MOBILE;
public class WifiBrowserTest extends Fragment {
	public static int netType = 1;
	private final int status_open=1;
	private final int status_close=0;
	private final int pass_next=111;
    WebView wv_page;
    TextView tv_head;
	Context mContext;
	WifiManager wifiManager;
	public MyHandler mHandler;
	boolean isConn =false;
	public TestActivity activity;
	FunctionControl control;
	int time = 0;
	boolean isLoading = false;
	WifiAutoConnectManager manager;
	
	
    @SuppressLint("HandlerLeak")
    class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case pass_next:
                wv_page.stopLoading();
                if (activity != null) {
                     activity.next(TestCode.Browser_wifi_video);
                }
                break;
            case 5:
                if(msg.arg1 == 1){
                    Toast.makeText(mContext, "WIFI 连接成功 ",Toast.LENGTH_SHORT).show();
                    setPageSettings();
                }else{
                    Toast.makeText(mContext, "WIFI 连接失败",Toast.LENGTH_SHORT).show();
                    activity.next(TestCode.Browser_wifi_video);
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
		mContext=this.getActivity();
		control = new FunctionControl(mContext);
		wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		activity=(TestActivity)getActivity();
		mHandler = new MyHandler();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view =inflater.inflate(R.layout.wifi_page, null);
		tv_head = (TextView) view.findViewById(R.id.textView1);
		tv_head.setText("WIFI Browsing Test");
		wv_page = (WebView) view.findViewById(R.id.webview);
		return view;
	}
	
	@Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        
        
        timer = new Timer();
        
        if (netType == 3) {
            manager = new WifiAutoConnectManager( wifiManager, mContext){
                @Override
                public void setIsConn(boolean conn) {
                        Message msg = new Message();
                        msg.arg1 = conn?1:0;
                        msg.what = 5;
                        SystemClock.sleep(2000);
                        mHandler.sendMessage(msg);
                }
            };
            mContext.registerReceiver(manager.wifiReceiver, filter);
            manager.connect("WiFi-59", "hgg12345", WifiCipherType.WIFICIPHER_WPA);
            
        }
        if (netType == 1 || netType == 2) {
            final ConnectivityManager mConnectivityManager;
            mConnectivityManager = ConnectivityManager.from(mContext);
            Log.d("hgg","isNetworkSupported======"+ mConnectivityManager.isNetworkSupported(TYPE_MOBILE));

            
            control.openGprs(true);
            final TelephonyManager mTelephonyManager;
            mTelephonyManager = TelephonyManager.from(mContext);
            int simStatus =mTelephonyManager.getSimState();
            Log.d("hgg","getSimState======"+ simStatus);
            if(simStatus == 1){
                Toast.makeText(mContext, "没插卡，移动网络不可用", Toast.LENGTH_SHORT).show();
            }
        }
    }
	
    public void setPageSettings() {
        
        WebSettings webSettings = wv_page.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBlockNetworkImage(true);
        webSettings.setRenderPriority(RenderPriority.HIGH);
        wv_page.setScrollBarStyle(0);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);
        wv_page.loadUrl("http://weibo.cn/tcljituan");
        isLoading = true;
        wv_page.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Log.d("hgg", "newProgress=========" + newProgress + "    isConn===" + isConn);
                if (newProgress == 100) {
                    SystemClock.sleep(2000);
//                   mHandler.sendEmptyMessage(3);
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
                try {
                    isLoading = true;
                    Log.d("hgg", "onLoadResource=========url===");
                    if(task !=null){
                        task.cancel();
                    }
                    if(timer!=null){
                    task = new MyTimerTask();
                    timer.schedule(task, 10000L);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }


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
            timer.cancel();
            timer=null;
            mHandler.sendEmptyMessage(pass_next);
        }
    };
    
	public void setPage(){
		wv_page.loadUrl("http://weibo.cn/tcljituan");
	}
	
	
	public boolean isIsConn() {
		return isConn;
	}

	public void setIsConn(boolean isConn) {
		this.isConn = isConn;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(manager!=null)
		    mContext.unregisterReceiver(manager.wifiReceiver);
		wv_page.onPause();
	}
	@Override
	public void onDestroy() {
		wv_page.destroy();
		super.onDestroy();
	}
	
}
