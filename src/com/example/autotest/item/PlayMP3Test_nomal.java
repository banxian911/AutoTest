
package com.example.autotest.item;

import java.util.Timer;
import java.util.TimerTask;

import com.example.autotest.AutoTestApp;
import com.example.autotest.TestActivity;
import com.example.autotest.Util.TestCode;

import android.app.Fragment;
import android.content.Context;
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
import com.example.autotest.R;

public class PlayMP3Test_nomal extends Fragment {
    MediaPlayer player;
    AudioManager mAudioManager;
    private Context mContext;
    private TextView tv_general;
    private TextView tv_general_status;
    public final int status_play = 0;
    public final int status_stop = 1;
    final int pass_next = 111;

    int oldMode;
    int oldStreamMusicVolume;
    MyHandler mHandler;

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case status_play:
                    tv_general_status.append("play...\n");
                    break;
                case status_stop:
                    tv_general_status.append("stop");
                    break;
                case pass_next:
                    ((TestActivity) getActivity()).next(TestCode.NormolMP3_h);
                default:
                    break;
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        mHandler = new MyHandler();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playmp3, null);
        tv_general = (TextView) view.findViewById(R.id.playmp3_normel_model);
        tv_general_status = (TextView) view.findViewById(R.id.playmp3_normel_model_status);
        tv_general.setText(tv_general.getText() + "  speak out");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        oldMode = mAudioManager.getMode();
        oldStreamMusicVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
        // mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);
        Timer timer = new Timer();
        if (task != null) {
            task.cancel();
        }
        task = new Task();
        timer.schedule(task, 1000);
    }

    TimerTask task;
    class Task extends TimerTask {

        @Override
        public void run() {
            startPlay();
        }
    };

    public void setAudioGenaralMode() {
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
    }

    public void startPlay() {
        setAudioGenaralMode();
        String uri = "android.resource://" + mContext.getPackageName() + "/" + R.raw.test_music;
        Log.d("hgg", "uri==" + uri);
        if(player == null)
            player = MediaPlayer.create(mContext, Uri.parse(uri));
        if (player != null && !player.isPlaying()) {
            player.start();
            mHandler.sendEmptyMessage(status_play);
            mHandler.post(run);
        }

    }

    private Runnable run = new Runnable() {
        int currentPosition, duration;

        public void run() {
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
    };

    public void stopPlay() {
        if (player != null && player.isPlaying()) {
            player.stop();
        }
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onPause() {
        if (task != null) {
            task.cancel();
        }
        stopPlay();
        mAudioManager.setMode(oldMode);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, oldStreamMusicVolume, 0);
        mHandler.removeCallbacks(run);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d("hgg", "PlayMP3Test_nomal   onDestroy ");
    }
}
