package com.jiaji.cookbook.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by jiana on 16-8-3.
 */
public class ProgressDialogUtil {
    private static ProgressDialog pd;

    public static void show(Context context) {
        // 创建进度对话框
        pd = new ProgressDialog(context);
        pd.setMax(100);
        // 设置对话框标题
        pd.setTitle("下载更新");
        // 设置对话框显示的内容
        pd.setMessage("进度...");
        // 设置对话框不能用取"消按"钮关闭
        pd.setCancelable(false);
        // 设置对话框的进度条风格
        // pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 设置对话框的进度条是否显示进度
        pd.setIndeterminate(false);
        pd.show();
    }

    public static void setProgress(int progress) {
        if (pd == null) {
            return;
        }
        pd.setProgress(progress);
    }

    public static void dismiss() {
        pd.dismiss();
        pd =null;
    }

    public static void destroy() {
        pd = null;
    }
}
