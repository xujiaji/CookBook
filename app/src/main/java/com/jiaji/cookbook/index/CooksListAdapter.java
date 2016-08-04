package com.jiaji.cookbook.index;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.jiaji.cookbook.R;
import com.jiaji.cookbook.cookerys.ShowCookeryActivity;
import com.jiaji.cookbook.info.SortTagInfo;

/**
 * Created by JiaJi on 2015/12/12.
 */
public class CooksListAdapter extends BaseAdapter{
    private SortTagInfo sortTagInfo;
    private Context context;
    public CooksListAdapter(Context context, SortTagInfo sortTagInfo) {
        this.context = context;
        this.sortTagInfo = sortTagInfo;
    }

    @Override
    public int getCount() {
        return sortTagInfo.getResult().size();
    }

    @Override
    public Object getItem(int position) {
        return sortTagInfo.getResult().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ListHolder listHolder;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_books, parent, false);
            listHolder = new ListHolder();
            listHolder.textView = (TextView) convertView.findViewById(R.id.tvLetter_item_books);
            listHolder.gridView = (GridView) convertView.findViewById(R.id.show_books_grid_view);
            Typeface face = Typeface.createFromAsset(context.getAssets(), "jianxiyuan.ttf");
            listHolder.textView.setTypeface(face);
            convertView.setTag(listHolder);
            listHolder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int parentPosition = (int) listHolder.gridView.getTag();
                    Intent intent = new Intent(context, ShowCookeryActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", Integer.parseInt(sortTagInfo.getResult().get(parentPosition).getList().get(position).getId()));
                    bundle.putString("title", sortTagInfo.getResult().get(parentPosition).getList().get(position).getName());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }
        else
        {
            listHolder = (ListHolder) convertView.getTag();
        }
        listHolder.textView.setText(sortTagInfo.getResult().get(position).getName());
        listHolder.gridView.setAdapter(new CooksGridAdapter(sortTagInfo.getResult().get(position)));
        listHolder.gridView.setTag(position);

        return convertView;
    }
    private class ListHolder
    {
        public TextView textView;
        public GridView gridView;
    }
    private class CooksGridAdapter extends BaseAdapter
    {
        private SortTagInfo.Result result;

        public CooksGridAdapter(SortTagInfo.Result result) {
            this.result = result;
        }

        @Override
        public int getCount() {
            return result.getList().size();
        }

        @Override
        public Object getItem(int position) {
            return result.getList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GridHolder gridHolder;
            if (convertView == null)
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_gridview, parent, false);
                gridHolder = new GridHolder();
                gridHolder.textView = (TextView) convertView.findViewById(R.id.item_gride_text);
                convertView.setTag(gridHolder);
            }
            else
            {
                gridHolder = (GridHolder) convertView.getTag();
            }
            gridHolder.textView.setText(result.getList().get(position).getName());
            return convertView;
        }
        private class GridHolder
        {
            public TextView textView;
        }
    }
}
