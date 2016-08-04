package com.jiaji.cookbook.cookerys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jiaji.cookbook.BaseActivity;
import com.jiaji.cookbook.R;
import com.jiaji.cookbook.db.CooksDBManager;
import com.jiaji.cookbook.details.CookDetailsActivity;
import com.jiaji.cookbook.info.ShowCookersInfo;
import com.jiaji.cookbook.util.NetworkUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_tabs_show)
public class ShowCookeryActivity extends BaseActivity {
    private static ShowCookersInfo info;
    private ShowCookersInfo tab2Info;
    private ShowCookersInfo tab3Info;
    @ViewInject(R.id.edit_note_bar)
    private Toolbar edit_note_bar;
    @ViewInject(R.id.tablayout)
    private TabLayout tablayout;
    @ViewInject(R.id.show_detail_viewpager)
    private ViewPager show_detail_viewpager;
    private List<String> titles;
    private List<ShowCookerFragment> frags;
    private ShowCookerAdapter adapter;
    private int cookId;
    private int pn = 0;
    private String title;
    private String search_key;
    private int tab_cursor;
    private CookItemAdapter cookItemAdapter;
    @Override
    public void initBefore() {
        frags = new ArrayList<>();
        titles = new ArrayList<>();
        titles.add("全部菜谱");
        titles.add("最近浏览");
        titles.add("我的收藏");
        frags.add(new ShowCookerFragment());
        frags.add(new ShowCookerFragment());
        frags.add(new ShowCookerFragment());
        getBeforeBundle();
        edit_note_bar.setNavigationIcon(R.drawable.ic_chevron_left_24dp1);
        edit_note_bar.setTitle(title);
        setSupportActionBar(edit_note_bar);
        edit_note_bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowCookeryActivity.this.finish();
                overridePendingTransition(R.anim.new_to_right, R.anim.old_to_right);
            }
        });
    }

    private void getBeforeBundle() {
        Bundle bundle = getIntent().getExtras();
        tab_cursor = bundle.getInt("tab_cursor", 0);
        search_key = bundle.getString("search_key");
        cookId = bundle.getInt("id", 0);
        title = bundle.getString("title");
        if (tab_cursor >= 1)
        {
            cookId = 1;
        }
    }

    @Override
    public void init() {
        adapter = new ShowCookerAdapter(getSupportFragmentManager(), frags, titles);
        show_detail_viewpager.setAdapter(adapter);
        tablayout.setTabsFromPagerAdapter(adapter);
        tablayout.setupWithViewPager(show_detail_viewpager);
    }

    /**
     * @param isLoad 是否是加载
     */
    public void getDataFromNetWork(final boolean isLoad) {
        if (isLoad) {
            pn += 10;
        } else {
            pn = 0;
        }
        HttpUtils utils = new HttpUtils();
        utils.configCurrentHttpCacheExpiry(5000);
        utils.send(HttpRequest.HttpMethod.GET, NetworkUtil.getURL(cookId, search_key, pn, 10), new RequestCallBack<Object>() {
            @Override
            public void onSuccess(ResponseInfo<Object> responseInfo) {
                Gson gson = new Gson();
                if (isLoad) {
                    ShowCookersInfo loadInfo = gson.fromJson(responseInfo.result.toString(), ShowCookersInfo.class);
                    if (NetworkUtil.isWrong(getApplicationContext(), loadInfo.getError_code(), Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT))) {
                        for (int i = 0, lengh = loadInfo.getResult().getData().size(); i < lengh; i++) {
                            info.getResult().getData().add(loadInfo.getResult().getData().get(i));
                            cookItemAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    info = gson.fromJson(responseInfo.result.toString(), ShowCookersInfo.class);
                    if (NetworkUtil.isWrong(getApplicationContext(), info.getError_code(), Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT))) {
                        fillingFragList();
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i("myout", s);
            }
        });
    }

    @Override
    public void initAfter() {
        getDataFromNetWork(false);
        show_detail_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        fillingFragList();
                        break;
                    case 1:
                        ShowCookerFragment frag = frags.get(1);
                        frag.setFrag2();
                        tab2Info = CooksDBManager.getCooksDBManager(ShowCookeryActivity.this).getData(true, false);
                        frag.getCooks_lv().setAdapter(new CookItemAdapter(tab2Info));
                        frag.getCooks_lv().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(ShowCookeryActivity.this,"position = " + id+"---id = " + info.getResult().getData().get((int) id).getId()+"---name = " + info.getResult().getData().get((int) id).getTitle(),Toast.LENGTH_SHORT).show();
                                CooksDBManager.getCooksDBManager(ShowCookeryActivity.this).setData(tab2Info.getResult().getData().get((int) id));
                                CooksDBManager.getCooksDBManager(ShowCookeryActivity.this).insertData(tab2Info.getResult().getData().get((int) id));
                                Intent intent = new Intent(ShowCookeryActivity.this, CookDetailsActivity.class);
                                startActivity(intent);
                            }
                        });

                        break;
                    case 2:
                        ShowCookerFragment frag2 = frags.get(2);
                        frag2.setFrag3();
                        tab3Info = CooksDBManager.getCooksDBManager(ShowCookeryActivity.this).getData(false, true);
                        frag2.getCooks_lv().setAdapter(new CookItemAdapter(tab3Info));
                        frag2.getCooks_lv().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(ShowCookeryActivity.this,"position = " + id+"---id = " + info.getResult().getData().get((int) id).getId()+"---name = " + info.getResult().getData().get((int) id).getTitle(),Toast.LENGTH_SHORT).show();
                                CooksDBManager.getCooksDBManager(ShowCookeryActivity.this).setData(tab3Info.getResult().getData().get((int) id));
                                CooksDBManager.getCooksDBManager(ShowCookeryActivity.this).insertData(tab3Info.getResult().getData().get((int) id));
                                Intent intent = new Intent(ShowCookeryActivity.this, CookDetailsActivity.class);
                                startActivity(intent);
                            }
                        });

                        break;


                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (tab_cursor >= 1)
        {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    show_detail_viewpager.setCurrentItem(tab_cursor, true);
                }
            },500);
        }
    }

    private void fillingFragList() {
        if (info == null) {
            return;
        }
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (frags.get(0).getCooks_lv() == null) return;
                cookItemAdapter = new CookItemAdapter(info);
                frags.get(0).setFrag1();
                frags.get(0).getCooks_lv().setAdapter(cookItemAdapter);
                frags.get(0).getCooks_lv().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(ShowCookeryActivity.this,"position = " + id+"---id = " + info.getResult().getData().get((int) id).getId()+"---name = " + info.getResult().getData().get((int) id).getTitle(),Toast.LENGTH_SHORT).show();
                        CooksDBManager.getCooksDBManager(ShowCookeryActivity.this).setData(info.getResult().getData().get((int) id));
                        CooksDBManager.getCooksDBManager(ShowCookeryActivity.this).insertData(info.getResult().getData().get((int) id));
                        Intent intent = new Intent(ShowCookeryActivity.this, CookDetailsActivity.class);
                        startActivity(intent);
                    }
                });
            }
        };
        if (frags.get(0).getCooks_lv() == null) {
            handler.postDelayed(runnable, 300);
        } else {
            handler.post(runnable);
        }

    }

    public class CookItemAdapter extends BaseAdapter {
        private ShowCookersInfo adapterInfo;
        private BitmapUtils utils = new BitmapUtils(ShowCookeryActivity.this, Environment.getExternalStorageDirectory().getPath() + "//img");

        public CookItemAdapter(ShowCookersInfo adapterInfo) {
            this.adapterInfo = adapterInfo;
        }

        @Override
        public int getCount() {
            if (adapterInfo.getResult() == null) {
                return 0;
            }
            return adapterInfo.getResult().getData().size();
        }

        @Override
        public Object getItem(int position) {
            return adapterInfo.getResult().getData().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(ShowCookeryActivity.this).inflate(R.layout.item_cooks_list, null);
                holder = new ViewHolder();
                holder.cook_name = (TextView) convertView.findViewById(R.id.cook_name);
                holder.cook_tags = (TextView) convertView.findViewById(R.id.cook_tags);
                holder.cook_ingredients = (TextView) convertView.findViewById(R.id.cook_ingredients);
                holder.cook_burden = (TextView) convertView.findViewById(R.id.cook_burden);
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.cook_name.setText(adapterInfo.getResult().getData().get(position).getTitle());
            holder.cook_tags.setText(adapterInfo.getResult().getData().get(position).getTags());
            holder.cook_ingredients.setText(adapterInfo.getResult().getData().get(position).getIngredients());
            holder.cook_burden.setText(adapterInfo.getResult().getData().get(position).getBurden());
            utils.display(holder.img, adapterInfo.getResult().getData().get(position).getAlbums().get(0));

            return convertView;
        }

        class ViewHolder {
            TextView cook_name;
            TextView cook_tags;
            TextView cook_ingredients;
            TextView cook_burden;
            ImageView img;
        }

    }

    /**
     * 当浏览数据清除时调用，更新listview
     */
    public void updatData()
    {
        ShowCookerFragment frag = frags.get(1);
        ShowCookersInfo sfasf = CooksDBManager.getCooksDBManager(ShowCookeryActivity.this).getData(true, false);
        frag.getCooks_lv().setAdapter(new CookItemAdapter(sfasf));
    }

}
