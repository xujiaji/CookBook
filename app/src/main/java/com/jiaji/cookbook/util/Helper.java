package com.jiaji.cookbook.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.gson.Gson;
import com.jiaji.cookbook.app.App;
import com.jiaji.cookbook.info.UpdateEntity;

import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;

public class Helper {

    private Helper() {
    }

    public static void checkUpdate() {
        FIR.checkForUpdateInFIR("09ec021f5e836152f27b896d88ebded2", new VersionCheckCallback() {
            @Override
            public void onSuccess(String s) {
                UpdateEntity updateEntity = new Gson().fromJson(s, UpdateEntity.class);
                String oldVersion = getVersionName(App.getContext());
                String newVersion = updateEntity.getVersionShort();
                if (!oldVersion.equals(newVersion)) {
                    Intent intent = new Intent("com.jiaji.cookbook.update");
                    intent.putExtra("update_entity", updateEntity);
                    App.getContext().sendBroadcast(intent);
                }
            }

            @Override
            public void onFail(Exception e) {
                Log.i("fir", "获取更新失败" + "\n" + e.getMessage());
            }

            @Override
            public void onStart() {
                Log.i("fir", "正在获取更新");
            }

            @Override
            public void onFinish() {
                Log.i("fir", "成功获取更新");
            }
        });
    }

    public static void addCustomizeValue(String key, String value) {
        FIR.addCustomizeValue(key, value);
    }

    public static void removeCustomizeValue(String key) {
        FIR.removeCustomizeValue(key);
    }

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }
}
