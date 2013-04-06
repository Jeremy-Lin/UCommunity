package com.inbuy.ucommunity.ui;

import java.util.ArrayList;

import com.inbuy.ucommunity.R;
import com.inbuy.ucommunity.data.Category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeGridAdapter extends ArrayAdapter<Category> {
	private static final int LAYOUT_ID = R.layout.home_grid_item;
	private ArrayList<Category> mCategories;

	public HomeGridAdapter(Context context, ArrayList categories) {
		super(context, LAYOUT_ID);
		mCategories = categories;
	}

	@Override
	public int getCount() {
		if(mCategories == null) {
			return 0;
		}
		return mCategories.size();
	}

	@Override
	public Category getItem(int position) {
		// TODO Auto-generated method stub
		return super.getItem(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null) {
			LayoutInflater li = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			convertView = li.inflate(R.layout.home_grid_item, parent, false);
			
			holder = new ViewHolder();
			holder.mLogoView = (ImageView) convertView.findViewById(R.id.img_logo);
			holder.mNameView = (TextView) convertView.findViewById(R.id.txt_name);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(holder != null) {
			Category category = mCategories.get(position);
			holder.mLogoView.setImageDrawable(category.mLogo);
			holder.mNameView.setText(category.mName);
		}
		
		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}

	private class ViewHolder {
		ImageView mLogoView;
		TextView mNameView;
	}
	
}
