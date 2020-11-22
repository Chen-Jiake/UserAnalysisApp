package com.example.useranalysisapp.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.useranalysisapp.utils.AsyncTaskCompat;
import com.example.useranalysisapp.utils.ScreenUtils;
import com.example.useranalysisapp.utils.SendUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;

public class ScreenshotService extends Service {
    public ImageReader mImageReader;
    public MediaProjection mMediaProjection;
    public int mScreenWidth;
    public int mScreenHeight;
    public int mScreenDensity;
    public static Intent mResultData;
    public VirtualDisplay mVirtualDisplay;
    private MediaProjectionManager mMediaProjectionManager;
    private int numofShot=0;

    private Timer timer = new Timer();
    final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    startScreenShot();
                    break;
                default:
                    break;
            }
        }
    };

    public ScreenshotService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        initScreenCapture();
        shootByTime();
    }

    //获取屏幕信息，并初始化ImageReader
    @SuppressLint("WrongConstant")
    private void initScreenCapture() {
        mScreenDensity = ScreenUtils.getScreenDensityDpi(this);
        mScreenWidth = ScreenUtils.getScreenWidth(this);
        mScreenHeight = ScreenUtils.getScreenHeight(this);
        mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 2);
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }


    //循环定时截屏
    public void shootByTime() {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
                //暂时截四张图，用于测试，之后可删除
                numofShot+=1;
                if (numofShot>3) timer.cancel();
            }
        }, 3000, 8000);
    }

    //截图的准备和实际过程
    private void startScreenShot() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startVirtual();
            }
        }, 0);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startCapture();
            }
        }, 50);
    }

    //开启MediaProjection
    public void startVirtual() {
        if (mMediaProjection != null) {
            virtualDisplay();
        } else {
            mMediaProjection = mMediaProjectionManager.getMediaProjection(RESULT_OK, mResultData);
            virtualDisplay();
        }
    }

    //开启VisualDisplay
    private void virtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                mScreenWidth, mScreenHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    //截图的实际过程:获取image
    private void startCapture() {
        Image image = null;
        try {
            image = mImageReader.acquireLatestImage();
        } catch (IllegalStateException e) {
            if (null != image) {
                image.close();
                image = null;
                image = mImageReader.acquireLatestImage();
            }
        }
        if (image == null) {
            startScreenShot();
        } else {
            SaveTask mSaveTask = new SaveTask();
            AsyncTaskCompat.executeParallel(mSaveTask, image);//执行SaveTask任务
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopVirtual();
                    stopMediaProjection();
                }
            }, 0);
        }
    }

    //将image转化为bitmap,并后续处理bitmap
    public class SaveTask extends AsyncTask<Image, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Image... params) {
            if (params == null || params.length < 1 || params[0] == null) {
                return null;
            }
            Image image = params[0];
            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            //这就是初始截图
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            image.close();
            return bitmap;
        }

        //后续处理Bitmap
        @Override
        protected void onPostExecute(final Bitmap bitmap) {
            super.onPostExecute(bitmap);
            //saveBitmap(bitmap, numofShot);
            /**发送图片到服务端**/
            SendUtils.sendImage(bitmap,numofShot);
        }
    }

    private void saveBitmap(Bitmap bitmap, int numofShot){
        String path = this.getExternalFilesDir("image").getAbsolutePath();
        File newFile = new File(path + File.separator + "image" + numofShot + ".jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //关闭VisualDisplay
    private void stopVirtual() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
    }
    //关闭MediaProjection
    private void stopMediaProjection() {
        mMediaProjection.stop();
        mMediaProjection = null;
    }
    //从Activity传过ResultData来
    public static void setResultData(Intent data){
        mResultData=data;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("service","*************destory****************");
    }
}