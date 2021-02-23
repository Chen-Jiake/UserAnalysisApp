package com.example.useranalysisapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.useranalysisapp.activity.BindingActivity;
import com.example.useranalysisapp.fragment.FragmentHome;
import com.example.useranalysisapp.fragment.FragmentMine;
import com.example.useranalysisapp.receiver.SmsReceiver;
import com.example.useranalysisapp.service.ScreenshotService;
import com.example.useranalysisapp.utils.SendUtils;

import java.util.ArrayList;
import java.util.List;

import Adapter.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BottomNavigationBar bottomNavigationBar;
    private ViewPager2 viewPager;

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

        //findViewById(R.id.start_service).setOnClickListener(this);
        //findViewById(R.id.stop_service).setOnClickListener(this);
        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar_container);
        viewPager = findViewById(R.id.viewpager);
        //设置发送图片前,图片在手机上的暂存路径
        String path = getExternalFilesDir(null).getAbsolutePath();
        SendUtils.setPath(path);

        initBottomNavigationBar();
        initViewPager();
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

    private void initBottomNavigationBar() {
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
        bottomNavigationBar.clearAll();
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED); //自适应大小
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_DEFAULT);
        bottomNavigationBar.setBarBackgroundColor(R.color.bg_gray)
                .setActiveColor(R.color.theme)
                .setInActiveColor(R.color.black);
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.homepage_fill,"首页").setInactiveIconResource(R.drawable.homepage))
                .addItem(new BottomNavigationItem(R.drawable.mine_fill,"我的").setInactiveIconResource(R.drawable.mine))
                .setFirstSelectedPosition(0) //第一个选中的位置
                .initialise();
    }

    private void initViewPager() {
        viewPager.setOffscreenPageLimit(1);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new FragmentHome());
        fragments.add(new FragmentMine());
        viewPager.setAdapter(new ViewPagerAdapter(this, fragments));
        viewPager.setCurrentItem(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigationBar.selectTab(position);
            }
        });
    }

}
