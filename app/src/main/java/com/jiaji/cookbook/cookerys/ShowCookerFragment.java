package com.jiaji.cookbook.cookerys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jiaji.cookbook.R;
import com.jiaji.cookbook.db.CooksDBManager;

/**
 * Created by JiaJi on 2015/12/13.
 */
public class ShowCookerFragment extends Fragment {
    PullToRefreshListView cooks_lv;
    ShowCookeryActivity showCookeryActivity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_viewpager_list, null);
        showCookeryActivity = (ShowCookeryActivity) getActivity();
        cooks_lv = (PullToRefreshListView) view.findViewById(R.id.cooks_lv);
        setFrag1();
        return view;
    }
    public void setFrag3()
    {
        cooks_lv.setMode(PullToRefreshBase.Mode.DISABLED);
    }
    public void setFrag2()
    {
        cooks_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        ILoadingLayout startLabels = cooks_lv.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉清除最近浏览");
        startLabels.setRefreshingLabel("正在清除");
        startLabels.setReleaseLabel("放开清除最近浏览");
        cooks_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (refreshView.isHeaderShown()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            CooksDBManager.getCooksDBManager(getActivity()).delData(null);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showCookeryActivity.updatData();
                                    cooks_lv.onRefreshComplete();
                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }
    public void setFrag1()
    {
        cooks_lv.setMode(PullToRefreshBase.Mode.BOTH);
        ILoadingLayout startLabels = cooks_lv.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");
        startLabels.setRefreshingLabel("正在刷新");
        startLabels.setReleaseLabel("放开刷新");
        ILoadingLayout endLabels = cooks_lv.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载更多");
        endLabels.setRefreshingLabel("正在载入");
        endLabels.setReleaseLabel("放开加载");
        cooks_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (refreshView.isHeaderShown()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            showCookeryActivity.getDataFromNetWork(false);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cooks_lv.onRefreshComplete();
                                }
                            });
                        }
                    }).start();
                } else if (cooks_lv.isFooterShown()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            showCookeryActivity.getDataFromNetWork(true);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cooks_lv.onRefreshComplete();
                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }
    public PullToRefreshListView getCooks_lv() {
        return cooks_lv;
    }
}
