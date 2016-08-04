package com.jiaji.cookbook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jiaji.cookbook.adapter.MainMenuAdapter;
import com.jiaji.cookbook.adapter.MyViewPagerAdapter;
import com.jiaji.cookbook.cookerys.ShowCookeryActivity;
import com.jiaji.cookbook.index.IndexActivity;
import com.jiaji.cookbook.info.UpdateEntity;
import com.jiaji.cookbook.service.UpdateService;
import com.jiaji.cookbook.util.Helper;
import com.jiaji.cookbook.util.ProgressDialogUtil;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnTouch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {
    private Handler mHandler;
    //图片
    private ImageView[] imageViews;
    //图片id
    private int[] imgids;
    //小点
    private ImageView[] tips;
    //搁置小点的容器
    @ViewInject(R.id.im)
    private LinearLayout im;
    @ViewInject(R.id.main_viewpager)
    private ViewPager main_viewpager;
    @ViewInject(R.id.main_mygridview)
    private GridView main_mygridview;
    @ViewInject(R.id.edit)
    private EditText edit;
    private static boolean viewPageRun = true;
    private BroadcastReceiver receiver;

    @OnClick(R.id.my_collect)
    public void collectClick(View v) {
        Intent intent = new Intent(MainActivity.this, ShowCookeryActivity.class);
        intent.putExtra("title", "我的收藏");
        intent.putExtra("tab_cursor", 2);
        startActivity(intent);
        overridePendingTransition(R.anim.new_to_left, R.anim.old_to_left);
    }

    @OnClick(R.id.recent_scan)
    public void recentClick(View v) {
        Intent intent = new Intent(MainActivity.this, ShowCookeryActivity.class);
        intent.putExtra("title", "最近浏览");
        intent.putExtra("tab_cursor", 1);
        startActivity(intent);
        overridePendingTransition(R.anim.new_to_left, R.anim.old_to_left);
    }

    private MyViewPagerAdapter adapter;
    private MainMenuAdapter menuadapter;
    private List<String> list;
    public static final int AUTO = 198;

    private Handler.Callback callback;

    @OnClick(R.id.all_menu)
    public void allMenuClick(View view) {
        Intent intent = new Intent(MainActivity.this, IndexActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.new_to_top, R.anim.old_to_top);
    }

    @ViewInject(R.id.img_search)
    private ImageView img_search;

    @OnClick(R.id.img_search)
    public void imgSearchCLick(View view) {
        String content = edit.getText().toString();
        edit.setText("");
        content = content.replace(" ", "");
        if (content.length() != 0) {
            Intent intent = new Intent(MainActivity.this, ShowCookeryActivity.class);
            intent.putExtra("title", "菜谱搜索");
            intent.putExtra("search_key", content);
            startActivity(intent);
            overridePendingTransition(R.anim.new_to_left, R.anim.old_to_left);
        }
    }

    @OnTouch(R.id.main_viewpager)
    public boolean onTouch(View v, MotionEvent event) {
        if (v == main_viewpager) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    viewPageRun = false;
                    return true;
                case MotionEvent.ACTION_UP:
                    viewPageRun = true;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    viewPageRun = true;
                    break;
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewPageRun = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        viewPageRun = true;
    }

    @Override
    public void initBefore() {
        //当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setStatusHeight();
        }
    }


    @Override
    public void init() {
        imgids = new int[]{R.mipmap.p25, R.mipmap.p31, R.mipmap.p29};
//        imageViews = new ImageView[imgids.length];
        imageViews = imgids.length <= 3 ? new ImageView[imgids.length * 4] : new ImageView[imgids.length];
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i] = new ImageView(this);
            //图片填充容器多余的空白
            imageViews[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageViews[i].setImageResource(imgids[i % imgids.length]);
        }
        setPoint();
        adapter = new MyViewPagerAdapter(imageViews);
        main_viewpager.setAdapter(adapter);
        main_viewpager.setCurrentItem(imageViews.length * 100);
        main_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setImageBackgrpund(position % imgids.length);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        autoSlidePager();
        addBroadcast();
        Helper.checkUpdate();
    }

    private void setImageBackgrpund(int selecItems) {
        for (int i = 0; i < tips.length; i++) {
            if (i == selecItems) {
                tips[i].setImageResource(R.mipmap.yuanquan_up2);
            } else {
                tips[i].setImageResource(R.mipmap.yuanquan_down2);
            }
        }

    }

    //把点点加在图片上
    public void setPoint() {
        tips = new ImageView[imgids.length];
        for (int i = 0; i < tips.length; i++) {
            tips[i] = new ImageView(this);
            if (i == 0) {
                tips[i].setImageResource(R.mipmap.yuanquan_up2);
            } else {
                tips[i].setImageResource(R.mipmap.yuanquan_down2);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                    (new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            layoutParams.width = 20;
            layoutParams.height = 20;
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            im.addView(tips[i], layoutParams);

        }
    }

    @Override
    public void initAfter() {
        list = new ArrayList<>();
        list.add("家常菜");
        list.add("快手菜");
        list.add("创意菜");
        list.add("素菜");
        list.add("凉菜");
        list.add("烘焙");
        list.add("面食");
        list.add("汤");
        list.add("自制调味料");
        menuadapter = new MainMenuAdapter(list);
        main_mygridview.setAdapter(menuadapter);
        main_mygridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ShowCookeryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", position + 1);
                bundle.putString("title", list.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.new_to_left, R.anim.old_to_left);
            }
        });

        initSearch();
    }

    /**
     * 处理搜索
     */
    private void initSearch() {
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //没有内容时，搜索图表隐藏，内容改变，现实搜索图表
                if (TextUtils.isEmpty(s)) {
                    img_search.setVisibility(View.GONE);
                } else {
                    img_search.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 自动循环ViewPager
     */
    private void autoSlidePager() {
        Handler.Callback callback = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == AUTO) {
                    if (viewPageRun) {
                        main_viewpager.setCurrentItem(main_viewpager.getCurrentItem() + 1);
                    }
                    mHandler.sendEmptyMessageDelayed(AUTO, 3000);
                }
                return false;
            }
        };
        mHandler = new Handler(callback);
        mHandler.sendEmptyMessageDelayed(AUTO, 3000);
    }

    private void addBroadcast() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("com.jiaji.cookbook.update".equals(action)) {
                    UpdateEntity updateEntity = intent.getParcelableExtra("update_entity");
                    showUpdateDialog("更新内容：" + updateEntity.getChangelog()
                            + "\n版　　本：" + updateEntity.getVersionShort()
                            + "\n大　　小：" + String.format("%.2f M", (updateEntity.getBinary().getFsize()) / (1000.0 * 1000.0)), updateEntity.getInstallUrl());
                } else if ("com.jiaji.cookbook.update_progress".equals(action)) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setDataAndType(Uri.fromFile(new File(intent.getStringExtra(UpdateService.APK_LOCAL))), "application/vnd.android.package-archive");
                    startActivity(i);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.jiaji.cookbook.update");
        filter.addAction("com.jiaji.cookbook.update_progress");
        registerReceiver(receiver, filter);
    }

    private void showUpdateDialog(String msg, final String url) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("检查到更新")
                .setIcon(R.drawable.ic_update)
                .setMessage(msg)
                .setNegativeButton("一会儿吧", null)
                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateService.start(url);
                        ProgressDialogUtil.show(MainActivity.this);
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        ProgressDialogUtil.destroy();
        super.onDestroy();
    }
}
