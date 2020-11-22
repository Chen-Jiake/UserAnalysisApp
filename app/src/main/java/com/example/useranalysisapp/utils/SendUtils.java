package com.example.useranalysisapp.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.FutureTask;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SendUtils {

    private static String IP="http://10.108.20.192:8001";
    private static String imageUrl = IP + "/data/image";
    private static String messageUrl = IP + "/data/message";
    private static String path;
    //bitmap转为file并发送，文件名为id
    public static void sendImage(Bitmap bitmap, int id){

        Matrix matrix = new Matrix();
        matrix.setScale(0.25f, 0.25f);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        //存文件
        File file=null;
        String filename=id+".png";//图片存为png格式
        try {
            file=saveFile(bitmap,filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!file.exists()) return;
        //发送文件部分
        OkHttpClient okhttp = new OkHttpClient();
        RequestBody body=new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file",filename,RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .addFormDataPart("filename",filename).build();
        FutureTask<Boolean> task =new FutureTask<>(()->{
            try {
                ResponseBody responseBody =okhttp.newCall(
                        new Request.Builder().post(body).url(imageUrl).build()
                ).execute().body();
                if (responseBody!=null) return true;
                return false;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        });
        try {
            new Thread(task).start();
            return ;
        }catch (Exception e){
            e.printStackTrace();
        }
        /**暂时删除失败**/
        file=new File(path+"/"+filename);
        if (file.exists()) file.delete();
    }
    //暂时存文件到手机的部分
    public static File saveFile(Bitmap bm, String fileName) throws IOException {
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            boolean mkdirs = dirFile.mkdirs();
            if (!mkdirs) {
                Log.e("info", "创建：" + mkdirs);
            } else {
                Log.e("info", "创建成功");
            }
        }
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 50, baos);
        baos.flush();
        File myCaptureFile = new File(path +"/"+ fileName);
        try {
            if (myCaptureFile.exists()) myCaptureFile.delete();
            myCaptureFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(myCaptureFile);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            int x = 0;
            byte[] b = new byte[1024 * 100];
            while ((x = is.read(b)) != -1) {
                fos.write(b, 0, x);
            }
            fos.flush();
            fos.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myCaptureFile;
    }

    //在MainActivity中传过来文件存储的地址
    public static void setPath(String p){
        //图片存放的文件夹
        String suffix = "/MyPic";
        path = p + suffix;
    }

    private SendUtils()
    {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void sendMessage(String content, Long time, String app){
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("content",content);
                    obj.put("time",time);
                    obj.put("app", app);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MediaType type = MediaType.parse("application/json;charset=utf-8");
                RequestBody requestBody = RequestBody.create(type, obj.toString());
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            // 指定访问的服务器地址
                            .url(messageUrl).post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
