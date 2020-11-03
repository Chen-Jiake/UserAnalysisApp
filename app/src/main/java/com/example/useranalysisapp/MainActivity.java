package com.example.useranalysisapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.os.Bundle;

import com.example.useranalysisapp.receiver.SmsReceiver;

public class MainActivity extends AppCompatActivity {

    IntentFilter filter;
    SmsReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filter=new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED" );
        receiver=new SmsReceiver();
        registerReceiver(receiver,filter);//注册广播接收器

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);//解绑广播接收器
    }

}
