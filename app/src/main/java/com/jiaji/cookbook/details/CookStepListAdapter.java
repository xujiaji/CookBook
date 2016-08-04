package com.jiaji.cookbook.details;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiaji.cookbook.R;
import com.jiaji.cookbook.info.ShowCookersInfo;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;


public class CookStepListAdapter extends BaseAdapter{
	private BitmapUtils utils;
	private List<ShowCookersInfo.Result.Data.Steps> cookSteps;

	public CookStepListAdapter(List<ShowCookersInfo.Result.Data.Steps> cookSteps,Context context) {
		this.cookSteps = cookSteps;
		utils = new BitmapUtils(context,Environment.getExternalStorageDirectory().getPath() + "/cooks/img");
	}

	@Override
	public int getCount() {
		return cookSteps.size();
	}

	@Override
	public ShowCookersInfo.Result.Data.Steps getItem(int position) {
		return cookSteps.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cook_step,null);
			holder = new ViewHolder();
			holder.stepNum = (TextView) convertView.findViewById(R.id.step_num);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.text = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		ShowCookersInfo.Result.Data.Steps cookStep = getItem(position);
		holder.stepNum.setText(position+1+"");
		holder.text.setText(cookStep.getStep().substring(2));
		utils.display(holder.img, cookStep.getImg());
		return convertView;
	}

	private class ViewHolder{
		TextView stepNum;
		ImageView img;
		TextView text;
	}
}
