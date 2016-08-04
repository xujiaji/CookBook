package com.jiaji.cookbook.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.jiaji.cookbook.app.App;
import com.jiaji.cookbook.util.FileUtils;
import com.jiaji.cookbook.util.ProgressDialogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 更新服务
 */
public class UpdateService extends IntentService {
    public static final String UPDATE_URL = "update_url";
    public static final String APK_LOCAL = "apk_local";
    private String appName = "cookbook.apk";

    public UpdateService() {
        super(UpdateService.class.getSimpleName());
    }

    public static void start(String url) {
        Intent intent = new Intent(App.getContext(), UpdateService.class);
        intent.putExtra(UPDATE_URL, url);
        App.getContext().startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            URL url = new URL(intent.getStringExtra(UPDATE_URL));
            // 创建连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            // 获取文件大小
            int length = conn.getContentLength();
            // 创建输入流
            InputStream is = conn.getInputStream();
            File file = new File(FileUtils.getAppCacheDir(App.getContext()));
            File apkFile = new File(file, appName);
            FileOutputStream fos = new FileOutputStream(apkFile);
            int count = 0;
            int progress;
            // 缓存
            byte buf[] = new byte[1024];
            while (true) {
                int numread = is.read(buf);
                count += numread;
                // 计算进度条位置
                progress = (int) (((float) count / length) * 100);
                Intent intent1 = new Intent("com.jiaji.cookbook.update_progress");
                if (numread <= 0) {
                    // 下载完成
                    ProgressDialogUtil.dismiss();
                    Log.e("UpdateService", "update apk = " + apkFile.getAbsolutePath());
                    intent1.putExtra(APK_LOCAL, apkFile.getAbsolutePath());
                    sendBroadcast(intent1);
                    break;
                }

                // 写入文件
                fos.write(buf, 0, numread);
                // 更新进度
                ProgressDialogUtil.setProgress(progress);
            }
            fos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
