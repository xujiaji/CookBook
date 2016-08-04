package com.jiaji.cookbook.cookerys;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by JiaJi on 2015/12/13.
 */
public class ShowCookerAdapter extends FragmentPagerAdapter {
    List<ShowCookerFragment> list;
    List<String> title;


    public ShowCookerAdapter(FragmentManager fm, List<ShowCookerFragment> list, List<String> title) {
        super(fm);
        this.list = list;
        this.title = title;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title.get(position);
    }
}
