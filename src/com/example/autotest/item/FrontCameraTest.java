package com.example.autotest.item;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.autotest.TestActivity;
import com.example.autotest.Util;
import com.example.autotest.Util.TestCode;
import com.example.autotest.tool.CameraUtil;
import com.example.autotest.tool.ComboPreferences;
import com.example.autotest.tool.Tuple;
import com.example.autotest.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;


/**
 * Created by Junhong
 * on 2016/3/21.
 */
public class FrontCameraTest extends Fragment implements TextureView.SurfaceTextureListener {

    private static final boolean DEBUG = true;
    private static final String TAG = "hgg";
    private static final String mTitle = "Front Camera Test";
    private static final int MSG_UPDATE_MSG = 0x3001;
    private static final int MSG_START_CAMERA = 0x3003;
    private static final int MSG_STOP_CAMERA = 0x3004;
    private static final int MSG_TEST_CAN_PASS = 0x3002;
    private static final int MSG_TEST_SHOW_CAPTURE = 0x3005;
    private static final int MSG_TEST_START_PREVIEW = 0x3006;
    private static final int MSG_TEST_SET_PICTURE_ECHO = 0x3007;
    private static final int TESTING_CAMERA_ID = 1;
    private TextView mMsgView;
    private ImageView mImageView;
    private Handler mHandler;
    private Context mContext;
    private Camera mCamera = null;
    private CameraScreenNailProxy mCameraScreenNailProxy;
    private TextureView mTextureView;
    private SurfaceTexture mSurfaceTexture;
    private CameraPreviewFrameLayout mPreviewFrameLayout;
    private ComboPreferences mPreferences;
    private boolean mIsCameraUsing = false;
    private int mPreviewWidth;
    private int mPreviewHeight;
    private boolean isTakingPicture = false;
    private boolean mTestCanPass = false;
    private final boolean SAVE_PICTURE = true;

    public static int getDisplayRotation() {
        return 0;
    }

    public static void setCameraDisplayOrientation(
            int cameraId, Camera camera) {
        int result = getCameraDisplayOrientation(cameraId, camera);
        camera.setDisplayOrientation(result);
    }

    public static int getCameraDisplayOrientation(
            int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int degrees = getDisplayRotation();
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }
    
    public static int getFrontCameraId(){
    	for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
    		Camera.CameraInfo info = new Camera.CameraInfo();
    	    Camera.getCameraInfo(i, info);
    	    Log.d(TAG, "info.facing========"+info.facing);
    	    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//这就是前置摄像头。
    	    	Log.d(TAG, "i========"+i);
    	     return i;
    	    }
    	}
		return TESTING_CAMERA_ID;
    }

    public static Camera.Size getOptimalPreviewSize(Activity currentActivity,
                                                    List<Camera.Size> sizes, double targetRatio) {
        final double ASPECT_TOLERANCE = 0.001;
        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        Display display = currentActivity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int targetHeight = Math.min(point.x, point.y);

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
                break;
            }
        }

        if (optimalSize == null) {
            Log.w(TAG, "No preview size match the aspect ratio");
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    void Log(String tag, String message) {
        Log.e(tag, message);
        if (DEBUG) {
            if (mHandler != null) {
                Message msg = mHandler.obtainMessage(MSG_UPDATE_MSG);
                msg.obj = message;
                mHandler.sendMessage(msg);
            } else {
                Log.e(tag, "mHandler == null");
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity().getApplicationContext();

        mCameraScreenNailProxy = new CameraScreenNailProxy();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_UPDATE_MSG:
                        if (mMsgView != null) {
                            mMsgView.append(msg.obj.toString() + "\n");
                        }
                        break;
                    case MSG_TEST_CAN_PASS:
                        if (msg.arg1 == 1) {
							mTestCanPass = true;
//                            ((BaseActivity) getActivity()).showButtons(BaseActivity.FLAG_SHOW_PASS_BTN | BaseActivity.FLAG_SHOW_FAIL_BTN);
                        } else {
							mTestCanPass = false;
//                            ((BaseActivity) getActivity()).showButtons(BaseActivity.FLAG_SHOW_FAIL_BTN);
                        }
                        break;
                    case MSG_START_CAMERA:
                        startCamera();
                        break;
                    case MSG_STOP_CAMERA:
                        stopCamera();
                        break;
                    case MSG_TEST_SHOW_CAPTURE:
//                        ((BaseActivity) getActivity()).showButtons(BaseActivity.FLAG_SHOW_CAPTURE_BTN | BaseActivity.FLAG_SHOW_FAIL_BTN);
                    	if(mCamera!=null){
                    		capture();
                    	}
						break;
					case MSG_TEST_START_PREVIEW:
						if(mCamera != null) {
							mPreviewFrameLayout.setVisibility(View.VISIBLE);
							mImageView.setVisibility(View.GONE);
							mCamera.startPreview();
						}
						break;
					case MSG_TEST_SET_PICTURE_ECHO:
						String path = (String) msg.obj;
						setImageEcho(path, true);
						stopCamera();
						((TestActivity)getActivity()).next(TestCode.FrontCamerRecord);
                    default:
                        break;
                }
            }
        };
        Message msg = mHandler.obtainMessage(MSG_TEST_CAN_PASS);
        msg.arg1 = 0;
        mHandler.sendMessage(msg);
//        ((BaseActivity) getActivity()).setCaptureCallback(this);
    }

    @Override
    public void onResume() {
        super.onResume();
		if(! mTestCanPass) {
			mHandler.sendEmptyMessage(MSG_START_CAMERA);
		}
    }

    @Override
    public void onPause() {
        super.onPause();
        stopCamera();
    }
	@Override
    public void onDestroy() {
        super.onDestroy();
		mHandler.removeMessages(MSG_START_CAMERA);
		mHandler.removeMessages(MSG_STOP_CAMERA);
		mHandler.removeMessages(MSG_TEST_CAN_PASS);
		mHandler.removeMessages(MSG_TEST_SHOW_CAPTURE);
		mHandler.removeMessages(MSG_TEST_START_PREVIEW);
		mHandler.removeMessages(MSG_UPDATE_MSG);
		mHandler.removeMessages(MSG_TEST_SET_PICTURE_ECHO);
		mHandler = null;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, null);
        mMsgView = (TextView) view.findViewById(R.id.log_msg);
        mImageView = (ImageView) view.findViewById(R.id.img_echo_view);
		mImageView.setVisibility(View.GONE);
        TextView title = (TextView) view.findViewById(R.id.tv_camera_HEAD);
        mPreviewFrameLayout = (CameraPreviewFrameLayout) view.findViewById(R.id.camera_view);
        mTextureView = (TextureView) view.findViewById(R.id.surfaceView);
        mTextureView.setSurfaceTextureListener(this);
        title.setText(mTitle);
        return view;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureAvailable");
        mSurfaceTexture = surface;
        mHandler.sendEmptyMessage(MSG_START_CAMERA);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    protected boolean getScreenState(ComboPreferences pref) {
        boolean result = false;
        if (pref != null) {
            String str_val = pref.getString("pref_camera_video_full_screen_key", null);
            result = (str_val != null && str_val.equals("On"));
        }
        return result;
    }

    private void initializeCameraOpenAfter() {
        Tuple<Integer, Integer> size =
                mCameraScreenNailProxy.getOptimalSize(
                        CameraScreenNailProxy.KEY_SIZE_PICTURE, mPreferences);
        if (mPreviewFrameLayout != null) {
            mPreviewFrameLayout.setAspectRatio((double) size.first / (double) size.second, true);
        }
    }

    private void stopCamera() {
        Log.e(TAG, "stopCamera       "+(mCamera != null));
        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
                mCamera.stopPreview(); 
                mCamera.release();
                mIsCameraUsing = false;
                mCamera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startCamera() {
        if (mSurfaceTexture == null)
            return;
        if (mIsCameraUsing) {
            stopCamera();
        }
        try {
            Log.e(TAG, "open");
            mCamera = Camera.open(TESTING_CAMERA_ID);//TESTING_CAMERA_ID
        } catch (RuntimeException e) {
            Log.e(TAG, "fail to open camera");
            e.printStackTrace();
            mCamera = null;
        }
//        Log.d(TAG, "open getFrontCameraId()==="+getFrontCameraId());
        if (mCamera != null) {
            setCameraDisplayOrientation(TESTING_CAMERA_ID, mCamera);
            Camera.Parameters parameters = null;
            parameters = mCamera.getParameters();
            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.set("orientation", "portrait");
            Size size = parameters.getPictureSize();
            List<Size> sizes = parameters.getSupportedPreviewSizes();
            Size optimalSize = getOptimalPreviewSize(getActivity(), sizes, (double) size.width / size.height);
            Size original = parameters.getPreviewSize();
            if (!original.equals(optimalSize)) {
                parameters.setPreviewSize(optimalSize.width, optimalSize.height);
                parameters = mCamera.getParameters();
            }
            Log.v(TAG, "Preview size is " + optimalSize.width + "x" + optimalSize.height);
            mPreviewWidth = optimalSize.width;
            mPreviewHeight = optimalSize.height;

            //if (true || android.plugin.Features.JAVA_VALUE_MMI_FRONT_FLASH) {
            //    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            //}
            mCamera.setParameters(parameters);
            try {
                mCamera.setPreviewTexture(mSurfaceTexture);
                Log.e(TAG, "start preview");
                mCamera.startPreview();
                mIsCameraUsing = true;
                initializeCameraOpenAfter();
                SystemClock.sleep(4000);
            } catch (Exception e) {
                mCamera.release();
				mCamera = null;
            }
        }
        mHandler.sendEmptyMessage(MSG_TEST_SHOW_CAPTURE);
    }

    public boolean capture() {
		Log.e(TAG, "start capture");
        if(mCamera != null && !isTakingPicture) {
            isTakingPicture = true;
            mCamera.takePicture(null, null,
                    new PhotoHandler(((TestActivity) getActivity()).getApplicationContext()));
        }
		Message msg = mHandler.obtainMessage(MSG_TEST_CAN_PASS);
		msg.arg1 = 1;
		mHandler.sendMessage(msg);
		return true;
    }

    protected class CameraScreenNailProxy {
        public static final int KEY_SIZE_PICTURE = 0;
        public static final int KEY_SIZE_PREVIEW = 1;
        private static final String TAG = "CameraScreenNailProxy";
        private Tuple<Integer, Integer> mScreenSize;

        protected CameraScreenNailProxy() {
            initializeScreenSize();
        }

        private void initializeScreenSize() {
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            mScreenSize = new Tuple<Integer, Integer>(
                    metrics.widthPixels, metrics.heightPixels);
            Log.d(TAG,
                    String.format("screen size = { %dx%d }",
                            new Object[]{
                                    mScreenSize.first, mScreenSize.second
                            }));
        }

        protected Tuple<Integer, Integer>
        getOptimalSize(int key, ComboPreferences pref) {

            Tuple<Integer, Integer> result = null;
            Camera.Size size = null;
            boolean b_full_screen = getScreenState(pref);
            int orientation = getOrientation();
            int width = mScreenSize.first, height = mScreenSize.second;
            Camera.Parameters mParameters = null;
            mParameters = mCamera.getParameters();
            if (KEY_SIZE_PICTURE == key) {
                size = mParameters.getPictureSize();
                width = size.width;
                height = size.height;
                result = CameraUtil.getOptimalSize(
                        mScreenSize.first, mScreenSize.second, width, height, b_full_screen);
                width = result.first;
                height = result.second;
                if (orientation % 180 == 0) {
                    int tmp = width;
                    width = height;
                    height = tmp;
                }
            }

            if (KEY_SIZE_PREVIEW == key) {
                size = mParameters.getPreviewSize();
                width = size.width;
                height = size.height;
                result = CameraUtil.getOptimalSize(
                        mScreenSize.first, mScreenSize.second, width, height, b_full_screen);
                width = result.first;
                height = result.second;
                if (orientation % 180 == 0) {
                    int tmp = width;
                    width = height;
                    height = tmp;
                }
            }

            result = new Tuple<Integer, Integer>(width, height);
            Log.d(TAG,
                    String.format(
                            "get optimal size: key = %d, is_full_screen = %b, size = { %dx%d }",
                            new Object[]{
                                    key, b_full_screen, result.first, result.second
                            }));
            return result;
        }

        private int getOrientation() {
            return getCameraDisplayOrientation(TESTING_CAMERA_ID, mCamera);
        }
    }
	private class ImageSize {
        int width;
        int height;
    }
	private int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int w = options.outWidth;
        int h = options.outHeight;
        int inSampleSize = 1;
        if (w > reqWidth || h > reqHeight) {
            int wRadio = Math.round(w * 1.0f / reqWidth);
            int hRadio = Math.round(w * 1.0f / reqHeight);
            inSampleSize = Math.max(wRadio, hRadio);
        }
        return inSampleSize;
    }
	private ImageSize getImageViewSize(ImageView view) {
        DisplayMetrics metrics = view.getContext().getResources().getDisplayMetrics();
        ImageSize size = new ImageSize();
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        int width = view.getWidth();
        if (width <= 0) {
            width = lp.width;
        }
        if (width <= 0) {
            width = view.getMaxWidth();
        }
        if (width <= 0) {
            width = metrics.widthPixels;
        }

        int height = view.getHeight();
        if (height <= 0) {
            height = lp.height;
        }
        if (height <= 0) {
            height = view.getMaxHeight();
        }
        if (height <= 0) {
            height = metrics.heightPixels;
        }
        size.width = width;
        size.height = height;
        return size;
    }
	Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = caculateInSampleSize(options, width, height);

        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }
	private void setImageEcho(String path, boolean visible) {
		if(visible) {
			ImageSize size = getImageViewSize(mImageView);
			Bitmap bitmap = decodeSampledBitmapFromPath(path, size.width, size.height);
			Matrix matrix = new Matrix();
			matrix.postScale(1, -1);
			matrix.postRotate((getCameraDisplayOrientation(TESTING_CAMERA_ID, mCamera) + 180) % 360);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			mImageView.setImageBitmap(bitmap);
			mImageView.setVisibility(View.VISIBLE);
			mPreviewFrameLayout.setVisibility(View.GONE);
			bitmap = null;
		}else {
			if(mImageView.getVisibility() == View.VISIBLE) {
				mImageView.setVisibility(View.GONE);
				mPreviewFrameLayout.setVisibility(View.VISIBLE);
			}
		}
	}
	private String getImageDirPath() {
		return ((TestActivity) getActivity()).getFilesDir().getPath();
	}
    public class PhotoHandler implements Camera.PictureCallback {

        private final Context context;

        public PhotoHandler(Context context) {
            this.context = context;
        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
			if(!SAVE_PICTURE) {
				isTakingPicture = false;
				return;
			}			
            File pictureFileDir = new File(Environment.getExternalStorageDirectory() + "/Pictures");

            if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
                Log.d(TAG, "Can't create directory to save image.");
                return;
            }
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String dateString = formatter.format(currentTime);
			String photoFile = "front_test_"+dateString+".jpg";
			String filename = pictureFileDir.getPath() + File.separator + photoFile;

			FileOutputStream outputStream = null;
            FileChannel fileChannel = null;
            ByteBuffer buffer = null;
            try {
                outputStream = new FileOutputStream(filename);
                fileChannel = outputStream.getChannel();
                buffer = ByteBuffer.allocate(data.length);
                buffer.put(data);
                buffer.flip();
                fileChannel.write(buffer);
				Log.d(TAG, "New Image saved:" + filename);
            } catch (IOException e) {
                Log.e(TAG, "File" + filename + "not saved: "
                        + e.getMessage());
            } finally {
                try {
                    if (outputStream != null)
                        outputStream.close();
                    if (fileChannel != null)
                        fileChannel.close();
                    if (buffer != null) {
                        buffer.clear();
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            isTakingPicture = false;
			
			Message msg = mHandler.obtainMessage(MSG_TEST_SET_PICTURE_ECHO);
			msg.obj = filename;
			mHandler.sendMessage(msg);
        }

    }
}
