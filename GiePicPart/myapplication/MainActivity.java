package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_MEDIA_PROJECTION = 48;
    private ImageView imageView;
    private Button startbtn;
    private Button stopbtn;
    MediaProjectionManager mediaProjectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        startbtn = findViewById(R.id.start_service);
        stopbtn = findViewById(R.id.stop_service);
        stopbtn.setOnClickListener(this);
        startbtn.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_service:{
                requestCapturePermission();
                break;
            }
            case R.id.stop_service:{
                Intent intent = new Intent(this,GetPic.class);
                stopService(intent);
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
                    GetPic.setResultData(data);
                    startService(new Intent(this,GetPic.class));
                }
                break;
        }
    }
}