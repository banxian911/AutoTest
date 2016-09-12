package com.example.autotest.item;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.example.autotest.R;
import com.example.autotest.TestActivity;
import com.example.autotest.Util.TestCode;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RecordTest extends Fragment {
    private TextView tv_status;
    final int status_recoding = 0;
    final int status_stop = 1;
    final int pass_next = 111;

    private Context mContext;
    private AudioManager mAudioManager;
    MediaRecorder mediaRecorder;
    private String fileDir;
    private String fileName = "recor_";
    MyHandler mHandler;
    TestActivity activity;

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case status_recoding:
                    tv_status.setText(R.string.record_status_recording);
                    break;
                case status_stop:
                    tv_status.setText(R.string.record_status_stop);
                    break;
                case pass_next:
                    activity.next(TestCode.SD_W_R);

                default:
                    break;
            }
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.record, null);
        tv_status = (TextView) view.findViewById(R.id.record_status);
        return view;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        activity = (TestActivity) getActivity();
        mHandler = new MyHandler();
        fileDir = Environment.getExternalStorageDirectory() + "/Recordings";
        mAudioManager = ((AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE));
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        RecordThread thread = new RecordThread();
        thread.start();
    }

    class RecordThread extends Thread {
        @Override
        public void run() {
            test();
        }

        private void test() {
            try {
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                File dir = new File(fileDir);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                Date currentTime = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                String dateString = formatter.format(currentTime);
                // File file = File.createTempFile(fileName, ".mp3", dir);
                // Runtime localRuntime = Runtime.getRuntime();
                // localRuntime.exec("chmod 777 " + file).waitFor();
                mediaRecorder.setOutputFile(dir.getAbsolutePath() + "/" + dateString + ".mp3");
                mediaRecorder.setMaxDuration(5000);
                mediaRecorder.prepare();
                mHandler.sendEmptyMessage(status_recoding);
                mediaRecorder.start();
                Timer timer = new Timer();
                timer.schedule(task, 5000 + 2000);
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            mHandler.sendEmptyMessage(status_stop);
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            mHandler.sendEmptyMessage(pass_next);
        }
    };
}
