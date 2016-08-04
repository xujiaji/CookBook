package com.jiaji.cookbook.index;

import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jiaji.cookbook.BaseActivity;
import com.jiaji.cookbook.R;
import com.jiaji.cookbook.info.SortTagInfo;
import com.jiaji.cookbook.view.IndexView;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by JiaJi on 2015/12/12.
 */
@ContentView(R.layout.index_layout)
public class IndexActivity extends BaseActivity {

    private SortTagInfo sortTagInfo;
    @ViewInject(R.id.show_now_index)
    private TextView tvToast;
    @ViewInject(R.id.index_view)
    private IndexView indexView;
    @ViewInject(R.id.listView_cooks)
    private ListView lv;
    /*顶部的view*/
    @ViewInject(R.id.viewOverlay_books)
    private View viewTop;
    @ViewInject(R.id.tvOverlay_books)
    private TextView tvTop;
    private RelativeLayout.LayoutParams params;
    @Override
    public void initBefore() {
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        Typeface face = Typeface.createFromAsset(getAssets(), "jianxiyuan.ttf");
        tvTop.setTypeface(face);
        tvToast.setTypeface(face);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

    }
    @OnClick(R.id.img_back)
    public void backClick(View view){
        finish();
        overridePendingTransition(R.anim.new_to_right, R.anim.old_to_right);
    }
    @Override
    public void init() {
        Gson gson = new Gson();
        try {
            InputStream in = getAssets().open("cooks_classify");
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String len;
            while ((len = reader.readLine()) != null) {
                builder.append(len);
            }
            reader.close();
            sortTagInfo = gson.fromJson(builder.toString(), SortTagInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initAfter() {

        lv.setAdapter(new CooksListAdapter(this, sortTagInfo));
        indexView.setOnLetterChangeListener(new IndexView.OnLetterChangeListener() {
            @Override
            public void onLetterChange(int selectedIndex) {
                //
                lv.setSelection(selectedIndex - 1);
                tvToast.setText(sortTagInfo.getResult().get(selectedIndex - 1).getName());//设置中间显示的字
                tvToast.setVisibility(View.VISIBLE);//设置为可见
            }

            @Override
            public void onClickUp() {
                tvToast.setVisibility(View.GONE);//当放开时，设置为不可见
            }
        });
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                View v = view.getChildAt(0);
                if (v == null)
                {
                    return;
                }
                int dex = v.getBottom() - tvTop.getHeight();
                if (dex <= 0)
                {
                    params.topMargin = dex;
                }
                else
                {
                    params.topMargin = 0;
                }
                Log.i("myout","firstVisibleItem = " + firstVisibleItem);
                tvTop.setText(sortTagInfo.getResult().get(firstVisibleItem).getName());
                viewTop.setLayoutParams(params);
                indexView.setSelected(firstVisibleItem + 1);
            }
        });
    }
}
