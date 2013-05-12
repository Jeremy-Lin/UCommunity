
package com.inbuy.ucommunity.ui;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inbuy.ucommunity.R;
import com.inbuy.ucommunity.data.PanelListItem;

import java.util.ArrayList;

public class PanelListAdapter extends ArrayAdapter<PanelListItem> {
    private static final int LAYOUT_ID = R.layout.panel_list_item;
    private ArrayList<PanelListItem> mItems;

    private Handler mHandler;

    public PanelListAdapter(Context context, ArrayList<PanelListItem> items, Handler handler) {
        super(context, LAYOUT_ID);
        mItems = items;
        mHandler = handler;
    }

    @Override
    public int getCount() {
        if (mItems == null) {
            return 0;
        }
        return mItems.size();
    }

    public void setListItems(ArrayList<PanelListItem> items) {
        this.mItems = items;
    }

    @Override
    public PanelListItem getItem(int position) {
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
            holder.mNameView = (TextView) convertView.findViewById(R.id.txt_name);
            holder.mRoot = (LinearLayout) convertView.findViewById(R.id.panel_list_item_root);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PanelListItem item = mItems.get(position);

        if (holder != null) {
            holder.mNameView.setText(item.mName);
        }

        if (item.mType == PanelListItem.TYPE_LEFT) {
            holder.mRoot.setBackgroundColor(item.mSelected ? 0xffe4e3de : 0x00ffffff);
        } else {
            holder.mNameView.setTextColor(item.mSelected ? 0xffff2345 : 0xff000000);
        }

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        // TODO Auto-generated method stub
        super.notifyDataSetChanged();
    }

    private class ViewHolder {
        LinearLayout mRoot;
        TextView mNameView;

    }
}
