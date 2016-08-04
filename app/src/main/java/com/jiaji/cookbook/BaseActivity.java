package com.jiaji.cookbook;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.jiaji.cookbook.util.Helper;
import com.lidroid.xutils.ViewUtils;

import java.lang.reflect.Field;

public abstract class BaseActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        //当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        initBefore();
        init();
        initAfter();
        Helper.addCustomizeValue(getClass().getSimpleName(), "onCreate end");
    }

    /**
     * 设置标题栏高度
     */
    public void setStatusHeight()
    {
        LinearLayout linear_bar=(LinearLayout)findViewById(R.id.linear_bar);
        linear_bar.setVisibility(View.VISIBLE);
        int statusHeight=getStatusBarHeight();
        //获取控件参数
        android.widget.LinearLayout.LayoutParams params=(android.widget.LinearLayout.LayoutParams )linear_bar.getLayoutParams();
        params.height=statusHeight;
        linear_bar.setLayoutParams(params);
    }
    public abstract void initBefore();
    public abstract void init();
    public abstract void initAfter();
    /**
     * 获取状态栏的高度
     * @return
     */
    public int getStatusBarHeight(){
        try
        {
            Class<?> c=Class.forName("com.android.internal.R$dimen");
            Object obj=c.newInstance();
            Field field=c.getField("status_bar_height");
            int x=Integer.parseInt(field.get(obj).toString());
            return  getResources().getDimensionPixelSize(x);
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.new_to_right, R.anim.old_to_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Helper.removeCustomizeValue(getClass().getSimpleName());
    }
}
