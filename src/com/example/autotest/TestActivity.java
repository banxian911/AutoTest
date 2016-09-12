package com.example.autotest;

import android.app.ActivityGroup;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.view.WindowManager;

import com.example.autotest.item.*;
import com.example.autotest.tool.FunctionControl;

import java.util.List;

public class TestActivity extends ActivityGroup implements View.OnClickListener {

    private PowerManager.WakeLock mWakeLock;

    private List<Util.TestCode> mTestItems;

    private int currentTest = 0;

    public FrameLayout container;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_test_activity);

        mTestItems = AutoTestApp.getInstance().getTestItems();
        container = (FrameLayout) findViewById(R.id.content);
        Log.d("hgg", "container==null=====1======" + (container == null));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (mWakeLock == null) {
            mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                    getPackageName());
        }
        mWakeLock.acquire();
        startTest();
    }

    private void startTest() {
        next();
    }

    public void next() {
        Log.i("hgg", "next currentTest = " + currentTest);
        if (currentTest >= mTestItems.size()) {
            Log.i("hgg", "test end");
            finish();
        } else {
            Util.TestCode code = mTestItems.get(currentTest);
            switch (code) {
            case LCDBackLight:
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new LCDBackLightTest()).commit();
                break;
            default:
                throw new IllegalStateException("No " + code.toString()
                        + " test!");
            }
            currentTest += 1;
        }
    }

    public void next(Util.TestCode code) {
        Log.d("hgg", "currentTest===" + currentTest+"      mTestItems.size()=="+mTestItems.size());
        if (currentTest >= mTestItems.size()) {
            Log.i("hgg", "test end");
            finish();
            return;
        }
        Util.TestCode curcode = mTestItems.get(currentTest-1);
        Log.d("hgg", "code===" + code);
        switch (code) {
        case LCDBackLight:
            if (code.isChose()) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new LCDBackLightTest()).commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
        case FlightMode:
            if (code.isChose()) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new FlightModeTest()).commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
        case WIFI:
            if (code.isChose()) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new WifiTest()).commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
        case GPS:
            if (code.isChose()) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new GPSTest()).commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
        case Torch:
            if (code.isChose()) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new TorchTest()).commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
        case Vibrate:
            if (code.isChose()) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new VibratorTest()).commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
        case Gesture:
            if (code.isChose()) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new GestureTest()).commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
        case LocalVideo1:
            if (code.isChose()) {
                FunctionControl.typeVideo = 1;
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new LocalVideo()).commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
        case LocalVideo2:
            if (code.isChose()) {
                FunctionControl.typeVideo = 2;
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new LocalVideo()).commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
        case LocalVideo3:
            if (code.isChose()) {
                FunctionControl.typeVideo = 3;
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new LocalVideo()).commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
        case NormolMP3:
            if (code.isChose()) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new PlayMP3Test_nomal())
                        .commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
        case NormolMP3_h:
            if (code.isChose()) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new PlayMP3Test_nomal_h())
                        .commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
        case AirPlaneMP3:
            if (code.isChose()) {
                PlayMP3Test_airplane.headsetType = 0;
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new PlayMP3Test_airplane())
                        .commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
        case AirPlaneMP3_h:
            if (code.isChose()) {
                PlayMP3Test_airplane.headsetType = 1;
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new PlayMP3Test_airplane())
                        .commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
//        case AirPlaneMP3_bt:
//            if (code.isChose()) {
//                PlayMP3Test_airplane.headsetType = 2;
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.content, new PlayMP3Test_airplane())
//                        .commit();
//                break;
//            } else {
//                currentTest += 1;
//                code = mTestItems.get(currentTest);
//            }
        case RearCamera:// hou
            if (code.isChose()) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new BackCameraTest()).commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
        case RearCameraRecord:// hou
            if (code.isChose()) {
                CameraRecorderTest.CAMERAID = 0;
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new CameraRecorderTest())
                        .commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
        case FrontCamer:// qian
            if (code.isChose()) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new FrontCameraTest()).commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
        case FrontCamerRecord:// qian
            if (code.isChose()) {
                CameraRecorderTest.CAMERAID = 1;
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new CameraRecorderTest())
                        .commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
//        case FMPlay:
//            if (code.isChose()) {
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.content, new FMTest()).commit();
//                break;
//            } else {
//                currentTest += 1;
//                code = mTestItems.get(currentTest);
//            }
        case Recoder:
            if (code.isChose()) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new RecordTest()).commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
        case SD_W_R:
            if (code.isChose()) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new SDReadWriteTest()).commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
        case Browser_3G:
            if (code.isChose()) {
                WifiBrowserTest.netType = 3;
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new WifiBrowserTest()).commit();
                break;
            } else {
                currentTest += 1;
                code = mTestItems.get(currentTest);
            }
//        case Browser_3G_video:
//            if (code.isChose()) {
//                WifiPlayvideoTest.netType = 1;
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.content, new WifiPlayvideoTest())
//                        .commit();
//                break;
//            } else {
//                currentTest += 1;
//                code = mTestItems.get(currentTest);
//            }
//        case Browser_4G_video:
//            if (code.isChose()) {
//                WifiPlayvideoTest.netType = 2;
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.content, new WifiPlayvideoTest())
//                        .commit();
//                break;
//            } else {
//                currentTest += 1;
//                code = mTestItems.get(currentTest);
//            }
        case Browser_wifi_video:
            if (code.isChose()) {
                WifiPlayvideoTest.netType = 3;
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new WifiPlayvideoTest()).commit();
                break;
            } else {
                currentTest += 1;
                Log.i("hgg", "test end   currentTest="+currentTest);
                if (currentTest >= mTestItems.size()) {
                    Log.i("hgg", "test end");
                    finish();
                    return;
                }
            }
        default:
            throw new IllegalStateException("No " + code.toString() + " test!");
        }
        currentTest += 1;
    }

    @Override
    protected void onDestroy() {
        getWindow()
                .clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
    }
}
