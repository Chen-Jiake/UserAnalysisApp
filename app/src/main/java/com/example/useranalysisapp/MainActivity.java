package com.example.useranalysisapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.useranalysisapp.activity.BindingActivity;
import com.example.useranalysisapp.receiver.SmsReceiver;
import com.example.useranalysisapp.service.ScreenshotService;
import com.example.useranalysisapp.utils.SendUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    SmsReceiver receiver;

    private static final int REQUEST_MEDIA_PROJECTION = 48;
    MediaProjectionManager mediaProjectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter=new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED" );
        receiver=new SmsReceiver();
        registerReceiver(receiver,filter);//注册广播接收器

        openAccessibilityService();

        Button startbtn = findViewById(R.id.start_service);
        Button stopbtn = findViewById(R.id.stop_service);
        stopbtn.setOnClickListener(this);
        startbtn.setOnClickListener(this);
        findViewById(R.id.binding).setOnClickListener(this);
        //设置发送图片前,图片在手机上的暂存路径
        String path = getExternalFilesDir(null).getAbsolutePath();
        SendUtils.setPath(path);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);//解绑广播接收器
    }

    // 开启无障碍服务
    private void openAccessibilityService() {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.i("accessibility", accessibilityEnabled + "");
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if (accessibilityEnabled == 0) { //无障碍服务未开启
            Intent startListenIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(startListenIntent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_service:{
                requestCapturePermission();
                break;
            } case R.id.stop_service:{
                Intent intent = new Intent(this, ScreenshotService.class);
                stopService(intent);
                break;
            } case R.id.binding:{
                Intent intent = new Intent(this, BindingActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    //初始化MediaProjectionManager，请求权限
    public void requestCapturePermission() {
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
    }

    //申请权限的activiry的返回信息处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION:
                if (resultCode == RESULT_OK && data != null) {
                    ScreenshotService.setResultData(data);
                    startService(new Intent(this, ScreenshotService.class));
                }
                break;
        }
    }

}
