package com.jiaji.cookbook.details;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jiaji.cookbook.BaseActivity;
import com.jiaji.cookbook.R;
import com.jiaji.cookbook.db.CooksDBManager;
import com.jiaji.cookbook.info.ShowCookersInfo;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.File;

/**
 * Created by JiaJi on 2015/12/15.
 */
@ContentView(R.layout.activity_cook_details)
public class CookDetailsActivity extends BaseActivity {
    @ViewInject(R.id.header_img)
    private ImageView header_img;
    @ViewInject(R.id.name)
    private TextView name;
    @ViewInject(R.id.text_intro)
    private TextView text_intro;
    @ViewInject(R.id.materials_layout)
    private LinearLayout materials_layout;
    @ViewInject(R.id.listView)
    private ListView listView;
    @ViewInject(R.id.details_title)
    private Toolbar details_title;
    private CookStepListAdapter adapter;
    private BitmapUtils utils;
    private ShowCookersInfo.Result.Data data;
    @ViewInject(R.id.collect_tv_1)
    private CheckedTextView collect;

    @OnClick(R.id.collect_tv_1)
    public void collectClick(View view) {
        collect.toggle();
        CooksDBManager.getCooksDBManager(this).updateData(data, collect.isChecked());
    }

    private View view;
    private TextView dialog_number;
    private ImageView dialog_img;
    private TextView dialog_text_info;

    @OnClick(R.id.share_tv)
    public void shareClick(View v) {
//        File file = utils.getBitmapFileFromDiskCache(data.getAlbums().get(0));
//        Log.i("myout", "imgFilePath" + file.toString());
        StringBuilder builder = new StringBuilder();
        builder.append("美　　食：" + data.getTitle() + "\n");
        builder.append("美食定位：\n　　" + data.getTags() + "\n");
        builder.append("介　　绍：\n　　" + data.getImtro() + "\n");
        builder.append("食　　材：\n　　" + data.getIngredients() + ";" + data.getBurden() + "\n");
        builder.append("制作步骤：" + "\n");
        for (int i = 0; i < data.getSteps().size(); i++) {
            builder.append("第" + (i + 1) + "步：\n　　" + data.getSteps().get(i).getStep() + "\n");
        }

        shareMsg("选择分享", "分享", builder.toString(), null);
    }

    @Override
    public void initBefore() {
        data = CooksDBManager.getCooksDBManager(this).getData();
        details_title.setNavigationIcon(R.drawable.ic_chevron_left_24dp1);
        details_title.setTitle(data.getTitle());
        setSupportActionBar(details_title);
        details_title.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CookDetailsActivity.this.finish();
                overridePendingTransition(R.anim.new_to_right, R.anim.old_to_right);
            }
        });
        utils = new BitmapUtils(CookDetailsActivity.this, Environment.getExternalStorageDirectory().getPath() + "/cooks/img");

    }

    @Override
    public void init() {
        view = LayoutInflater.from(this).inflate(R.layout.details_dialog_layout, null);
        utils.display(header_img, data.getAlbums().get(0));
        name.setText(data.getTitle());
        text_intro.setText(data.getImtro());
        collect.setChecked(CooksDBManager.getCooksDBManager(this).isLikeNowCook(data.getId()));
        addfood();
        adapter = new CookStepListAdapter(data.getSteps(), this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShowCookersInfo.Result.Data.Steps step = adapter.getItem(position);
                getDialog("第" + (position + 1) + "步", step.getImg(), step.getStep());
            }
        });
    }

    @Override
    public void initAfter() {

    }

    //添加食材
    private void addfood() {
        //食材明细
        String ingredients = data.getIngredients();//主食材
        String burden = data.getBurden();//辅助食材
        String materialsStr = ingredients + ";" + burden;
        String[] split = materialsStr.split(";");
        //每行放两项  算用多少行
        int lines = (split.length % 2) == 0 ? split.length / 2 : split.length / 2 + 1;
        for (int i = 0; i < lines; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_material, null);
            materials_layout.addView(view);
            String[] texts = split[i * 2].split(",");
            TextView tv1 = (TextView) view.findViewById(R.id.text1);
            tv1.setText(texts[0]);
            TextView tv2 = (TextView) view.findViewById(R.id.text2);
            tv2.setText(texts[1]);

            if (i == lines - 1 && split.length % 2 != 0) {

                continue;
            }
            texts = split[i * 2 + 1].split(",");
            TextView tv3 = (TextView) view.findViewById(R.id.text3);
            tv3.setText(texts[0]);
            TextView tv4 = (TextView) view.findViewById(R.id.text4);
            tv4.setText(texts[1]);
            texts = null;
        }
    }

    private AlertDialog dialog;

    private void getDialog(String itemId, String img, String infoText) {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(this).create();
            dialog.show();
            dialog.setContentView(view);
            dialog_number = (TextView) view.findViewById(R.id.dialog_number);
            dialog_img = (ImageView) view.findViewById(R.id.dialog_img);
            dialog_text_info = (TextView) view.findViewById(R.id.dialog_text_info);
        }

        if (dialog != null) {
            dialog_number.setText(itemId);
            utils.display(dialog_img, img);
            dialog_text_info.setText(infoText);
            dialog.show();
        }
    }

    /**
     * @param activityTitle 分享列表标题
     * @param msgTitle      消息标题
     * @param msgText       内容
     * @param imgPath       图片路径
     */
    public void shareMsg(String activityTitle, String msgTitle, String msgText, String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);// 系统分享功能
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本,// 分享发送的数据类型
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/jpg");// 分享发送的数据类型
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);// 分享的内容
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, activityTitle));// 目标应用选择对话框的标题
    }
}
