package com.example.autotest.item;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.example.autotest.TestActivity;
import com.example.autotest.Util.TestCode;

import android.app.Fragment;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import com.example.autotest.R;

public class CameraRecorderTest extends Fragment implements OnErrorListener, OnInfoListener {

    public int duration = 5000;
    MediaRecorder mMediaRecorder;
    SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    File video_file;
    Camera camera;
    protected boolean isPreview;
    /**
     * 0.back 1.front
     */
    public static int CAMERAID = 0;

    Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:
                        setMediaRecorder();
                        // startRecord();
                        break;
                    case 2:
                        if (CAMERAID == 0) {
                            ((TestActivity) getActivity()).next(TestCode.FrontCamer);
                        } else if (CAMERAID == 1) {
                            // ((TestActivity)getActivity()).next(TestCode.FMPlay);
                            ((TestActivity) getActivity()).next(TestCode.Recoder);
                            CAMERAID = 0;
                        }
                        break;
                    default:
                        break;
                }
            };
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_record, null);
        mSurfaceView = (SurfaceView) view.findViewById(R.id.sf_video_view);

        return view;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(new Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder arg0) {
                Log.d("hgg", "surfaceDestroyed=====" + (arg0 == null));

                if (camera != null) {
                    if (isPreview) {
                        camera.stopPreview();
                        isPreview = false;
                    }
                    camera.release();
                    camera = null;
                }
                mSurfaceView = null;
                mSurfaceHolder = null;
                mMediaRecorder = null;
            }

            @Override
            public void surfaceCreated(SurfaceHolder arg0) {
                // TODO Auto-generated method stub
                try {
                    Log.d("hgg", "surfaceCreated=====" + (arg0 == null));

                    mSurfaceHolder = arg0;
                    camera = Camera.open(CAMERAID);
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setPreviewFrameRate(5); // 每秒5帧
                    parameters.setPictureFormat(PixelFormat.JPEG);// 设置照片的输出格式
                    parameters.set("jpeg-quality", 85);// 照片质量
                    camera.setParameters(parameters);
                    camera.setPreviewDisplay(arg0);
                    camera.startPreview();
                    isPreview = true;
                } catch (Exception e) {

                    Log.d("hgg", e.getMessage());
                    e.printStackTrace();
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
                Log.d("hgg", "surfaceChanged=====" + (arg0 == null));
                mSurfaceHolder = arg0;
            }
        });
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHandler.sendEmptyMessage(1);
    }


    public void setMediaRecorder() {
        try {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.reset();
            mMediaRecorder.setOnInfoListener(this);
            mMediaRecorder.setOnErrorListener(this);
            camera.unlock();
            mMediaRecorder.setCamera(camera);
            Log.d("hgg", "setMediaRecorder=====" + (mSurfaceHolder == null));

            mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            // mMediaRecorder.setVideoSize(320, 240);
            mMediaRecorder.setVideoFrameRate(15);

            // video_file = File.createTempFile("Video", ".mp4", new File(
            // Environment.getExternalStorageDirectory() + "/DCIM"));
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String dateString = formatter.format(currentTime);
            Log.d("hgg", "dateString===" + dateString);
            // mMediaRecorder.setOutputFile(video_file.getAbsolutePath());
            // getInternalStoragePath()
            mMediaRecorder.setOutputFile(Environment.getExternalStorageDirectory() + "/DCIM/test_"
                    + dateString + ".mp4");
            mMediaRecorder.setMaxDuration(duration);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            Timer timer = new Timer();
            timer.schedule(task, duration + 2000);
        } catch (Exception e) {
            Log.d("hgg", e.getMessage());
            e.printStackTrace();

        }
    }

    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            stopRecord();
            mHandler.sendEmptyMessage(2);
        }
    };

    public void stopRecord() {
        try {
            if (mMediaRecorder != null) {
                mMediaRecorder.setOnErrorListener(null);
                mMediaRecorder.setPreviewDisplay(null);
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
            if (camera != null) {
                camera.release();
                camera = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        stopRecord();
    }

    @Override
    public void onError(MediaRecorder arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        Log.d("hgg", "onError===========");
    }

    @Override
    public void onInfo(MediaRecorder arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }
}
