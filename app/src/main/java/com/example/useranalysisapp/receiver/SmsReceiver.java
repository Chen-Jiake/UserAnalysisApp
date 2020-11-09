package com.example.useranalysisapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        StringBuilder content = new StringBuilder();//用于存储短信内容
        Bundle bundle = intent.getExtras();//通过getExtras()方法获取短信内容
        String format = intent.getStringExtra("format");
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");//根据pdus关键字获取短信字节数组，数组内的每个元素都是一条短信
            for (Object object : pdus) {
                SmsMessage message = null;
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    message = SmsMessage.createFromPdu((byte[]) object, format);//将字节数组转化为Message对象
                } else {
                    message = SmsMessage.createFromPdu((byte[]) object);
                }
                content.append(message.getOriginatingAddress());//获取短信手机号
                content.append(message.getMessageBody());//获取短信内容
            }
        }
        Toast.makeText(context, content.toString(), Toast.LENGTH_SHORT).show();
    }
}
