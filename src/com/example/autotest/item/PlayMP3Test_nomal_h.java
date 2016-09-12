package com.example.autotest.item;

import java.util.Timer;
import java.util.TimerTask;

import com.example.autotest.AutoTestApp;
import com.example.autotest.R;
import com.example.autotest.TestActivity;
import com.example.autotest.Util.TestCode;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class PlayMP3Test_nomal_h extends Fragment {
    MediaPlayer player;
    AudioManager mAudioManager;
    private Context mContext;
    private TextView tv_general;
    private TextView tv_general_status;

    int oldMode;
    int oldStreamMusicVolume;

    public final int status_play = 0;
    public final int status_stop = 1;
    public final int status_no_head = 2;
    final int pass_next = 111;
    AlertDialog dialog;
    TestActivity activity;
    MyHandler mHandler;
    private Timer mTimer;
    private TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                mHandler.sendEmptyMessage(pass_next);
            }
        }
    };

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case status_play:
                    tv_general_status.setText("play...");
                    break;
                case status_stop:
                    tv_general_status.setText("stop");
                    break;
                case status_no_head:
                    dialog = new AlertDialog.Builder(getActivity()).setTitle("系统提示")
                            .setMessage("请插入耳机").create();
                    dialog.show();
                    mTimer = new Timer();
                    mTimer.schedule(timerTask, 5000);
                    break;
                case pass_next:
                    activity.next(TestCode.AirPlaneMP3);
                default:
                    break;
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        activity = (TestActivity) getActivity();
        mHandler = new MyHandler();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playmp3, null);
        tv_general = (TextView) view.findViewById(R.id.playmp3_normel_model);
        tv_general_status = (TextView) view.findViewById(R.id.playmp3_normel_model_status);
        tv_general.setText(tv_general.getText() + "  headset");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.HEADSET_PLUG");
        mContext.registerReceiver(HeadsetPlugReceiver, filter);
        oldMode = mAudioManager.getMode();
        oldStreamMusicVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
        // mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);
        Timer t = new Timer();
        t.schedule(task, 500);
    }

    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            if (!mAudioManager.isWiredHeadsetOn()) {
                mHandler.sendEmptyMessage(status_no_head);
            } else {
                startPlay();
            }
        }
    };

    public void startPlay() {
        String uri = "android.resource://" + mContext.getPackageName() + "/" + R.raw.test_music;
        Log.d("hgg", "uri==" + uri);
        player = MediaPlayer.create(mContext, Uri.parse(uri));
        if (player != null && !player.isPlaying()) {
            player.start();
            mHandler.sendEmptyMessage(status_play);
            mHandler.post(run);
        }
    }

    public void stopPlay() {
        if (player != null && player.isPlaying()) {
            player.stop();
        }
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private Runnable run = new Runnable() {
        int buffer, currentPosition, duration;

        public void run() {
            if (player != null) {
                // 获得当前播放时间和当前视频的长度
                currentPosition = player.getCurrentPosition();
                duration = player.getDuration();
                int time = ((currentPosition * 100) / duration);
                Log.d("hgg", "play music time==" + time);
                if (time == AutoTestApp.music_time) {
                    stopPlay();
                    mHandler.sendEmptyMessage(status_stop);
                    SystemClock.sleep(1000);
                    mHandler.sendEmptyMessage(pass_next);
                } else {
                    mHandler.postDelayed(run, 1000);
                }
            }
        }
    };

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mContext.unregisterReceiver(HeadsetPlugReceiver);
        stopPlay();
        mAudioManager.setMode(oldMode);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, oldStreamMusicVolume, 0);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    BroadcastReceiver HeadsetPlugReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Log.d("hgg", "HeadsetPlugReceiver=========");
            if (intent.hasExtra("state")) {
                if (intent.getIntExtra("state", 0) == 0) {
                    Toast.makeText(context, "headset not connected", Toast.LENGTH_LONG).show();
                } else if (intent.getIntExtra("state", 0) == 1) {
                    Toast.makeText(context, "headset  connected", Toast.LENGTH_LONG).show();
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    startPlay();
                }
            }
        }
    };
}
