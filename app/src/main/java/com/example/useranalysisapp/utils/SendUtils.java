package com.example.useranalysisapp.utils;

import android.graphics.Bitmap;
import android.util.Log;
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
import okhttp3.ResponseBody;

public class SendUtils {

    private static String Url="http://10.0.2.2:8080/upload/uploadFile";//上传文件操作的服务器地址
    private static String path;
    //bitmap转为file并发送，文件名为id
    public static void send(Bitmap bitmap,int id){
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
                        new Request.Builder().post(body).url(Url).build()
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
}
