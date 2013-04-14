
package com.inbuy.ucommunity.ui;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.inbuy.ucommunity.R;
import com.inbuy.ucommunity.data.BigCategory;

import java.util.ArrayList;

public class BigCategoryListAdapter extends ArrayAdapter<BigCategory> {
    private static final int LAYOUT_ID = R.layout.big_category_list_item;
    private ArrayList<BigCategory> mCategories;

    private Handler mHandler;

    public BigCategoryListAdapter(Context context, ArrayList<BigCategory> categories,
            Handler handler) {
        super(context, LAYOUT_ID);
        mCategories = categories;
        mHandler = handler;
    }

    @Override
    public int getCount() {
        if (mCategories == null) {
            return 0;
        }
        return mCategories.size();
    }

    public void setBigCategoryList(ArrayList<BigCategory> categories) {
        this.mCategories = categories;
    }

    @Override
    public BigCategory getItem(int position) {
        // TODO Auto-generated method stub
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(LAYOUT_ID, parent, false);

            holder = new ViewHolder();
            holder.mCheckbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            holder.mNameView = (TextView) convertView.findViewById(R.id.txt_title);

            holder.mCheckbox.setOnCheckedChangeListener(mCheckedChangedListener);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (holder != null) {
            BigCategory category = mCategories.get(position);
            Log.d("", "category = " + category);
            holder.mNameView.setText(category.mName);
        }

        return convertView;
    }

    private OnCheckedChangeListener mCheckedChangedListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // TODO Auto-generated method stub

        }

    };

    @Override
    public void notifyDataSetChanged() {
        // TODO Auto-generated method stub
        super.notifyDataSetChanged();
    }

    private class ViewHolder {
        CheckBox mCheckbox;
        TextView mNameView;
    }

}
