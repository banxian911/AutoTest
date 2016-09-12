package com.example.autotest.item;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.example.autotest.AutoTestApp;
import com.example.autotest.R;
import com.example.autotest.TestActivity;
import com.example.autotest.Util.TestCode;
import com.example.autotest.tool.FunctionControl;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;

@SuppressLint("SdCardPath")
public class LocalVideo extends Fragment {

    ProgressDialog dialog;
    TestActivity activity;
    MyVideoView vv;
    Timer timer;
    String localVideoPath1 = "/storage/emulated/0/DCIM/test_720p.mp4";
    String localVideoPath2 = "/storage/emulated/0/DCIM/test_1020p.mp4";
    String localVideoPath3 = "/storage/emulated/0/DCIM/test_4k*2k.mp4";

    Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        activity = (TestActivity) getActivity();
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        dialog = ProgressDialog.show(getActivity(), "1", "1");
                        playVideo(localVideoPath1);
                        break;
                    case 2:
                        dialog = ProgressDialog.show(getActivity(), "2", "2");
                        playVideo(localVideoPath2);
                        break;
                    case 3:
                        dialog = ProgressDialog.show(getActivity(), "3", "3");
                        playVideo(localVideoPath3);
                        break;
                    case 4:
                        Toast toast = Toast.makeText(getActivity(), "视频文件不存在，测试下一项。",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        SystemClock.sleep(1000);
                    case 5:
                        if (FunctionControl.typeVideo == 1) {
                            activity.next(TestCode.LocalVideo2);
                        } else if (FunctionControl.typeVideo == 2) {
                            activity.next(TestCode.LocalVideo3);
                        } else if (FunctionControl.typeVideo == 3) {
                            activity.next(TestCode.NormolMP3);
                            FunctionControl.typeVideo = 0;
                        } else {
                            activity.next(TestCode.NormolMP3);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.localvideo, null);
        vv = (MyVideoView) view.findViewById(R.id.vv_localvideo);
        vv.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer arg0) {
                // TODO Auto-generated method stub
                if (dialog != null && dialog.isShowing()) {
                    dialog.hide();
                }
                mHandler.post(run);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Timer();
        timer.schedule(task, 1000);
    }

    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            play();
        }
    };

    public void play() {
        Log.d("hgg", "FunctionControl.typeVideo------" + FunctionControl.typeVideo);
        if (FunctionControl.typeVideo == 1) {
            Log.d("hgg", "isExitFile(localVideoPath1)=====" + isExitFile(localVideoPath1));
            Log.d("hgg", "isExitFile(localVideoPath1)====="
                    + Environment.getExternalStorageDirectory().getPath());
            if (isExitFile(localVideoPath1)) {
                mHandler.sendEmptyMessage(1);
                // activity.next(TestCode.LocalVideo2);
            } else {
                mHandler.sendEmptyMessage(4);
            }
        } else if (FunctionControl.typeVideo == 2) {
            if (isExitFile(localVideoPath2)) {
                mHandler.sendEmptyMessage(2);
                activity.next(TestCode.LocalVideo3);
            } else {
                mHandler.sendEmptyMessage(4);
            }
        } else if (FunctionControl.typeVideo == 3) {
            if (isExitFile(localVideoPath3)) {
                mHandler.sendEmptyMessage(3);
                activity.next(TestCode.NormolMP3);
            } else {
                mHandler.sendEmptyMessage(4);
            }
        } else {
            activity.next(TestCode.NormolMP3);
        }
    }

    private void playVideo(String strPath) {
        setFullScreen();
        if (strPath != "") {
            vv.setVideoURI(Uri.parse(strPath));
            vv.setMediaController(new MediaController(this.getActivity()));
            vv.requestFocus();
            vv.start();
        }
    }

    public void setFullScreen() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        vv.setLayoutParams(layoutParams);

    }

    private boolean isExitFile(String strPath) {
        File file = new File(strPath);
        return file.exists();
    }

    private Runnable run = new Runnable() {
        int currentPosition, duration;

        public void run() {
            // 获得当前播放时间和当前视频的长度
            currentPosition = vv.getCurrentPosition();
            duration = vv.getDuration();
            int time = ((currentPosition * 100) / duration);
            Log.d("hgg", "play video time==" + time);
            if (time == AutoTestApp.video_time) {
                vv.pause();
                vv.stopPlayback();
                mHandler.sendEmptyMessage(5);
            } else {
                mHandler.postDelayed(run, 1000);
            }
        }
    };

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (dialog != null && dialog.isShowing()) {
            dialog.hide();
        }
        if(task!=null) {
            task.cancel();
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d("hgg", "localvideo  ondestroy");
        mHandler.removeCallbacks(run);
    }
}
