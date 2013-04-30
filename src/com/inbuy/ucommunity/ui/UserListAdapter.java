
package com.inbuy.ucommunity.ui;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inbuy.ucommunity.R;
import com.inbuy.ucommunity.data.User;

import java.util.ArrayList;

public class UserListAdapter extends ArrayAdapter<User> {
    private static final int LAYOUT_ID = R.layout.user_list_item;
    private ArrayList<User> mUsers;

    private Handler mHandler;

    public UserListAdapter(Context context, ArrayList<User> users, Handler handler) {
        super(context, LAYOUT_ID);
        mUsers = users;
        mHandler = handler;
    }

    @Override
    public int getCount() {
        if (mUsers == null) {
            return 0;
        }
        return mUsers.size();
    }

    public void setUserList(ArrayList<User> users) {
        this.mUsers = users;
    }

    @Override
    public User getItem(int position) {
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
            holder.mPhoto = (ImageView) convertView.findViewById(R.id.img_photo);
            holder.mStarIcon = (ImageView) convertView.findViewById(R.id.img_star);
            holder.mNameView = (TextView) convertView.findViewById(R.id.txt_name);
            holder.mPriceView = (TextView) convertView.findViewById(R.id.txt_price);
            holder.mTagView = (TextView) convertView.findViewById(R.id.txt_note);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (holder != null) {
            User user = mUsers.get(position);
            holder.mNameView.setText(user.mName);
            holder.mTagView.setText(user.mTag);
            holder.mPriceView.setText(user.mDes);

            setUserPhotoView();
            setUserStarView();
        }

        return convertView;
    }

    private void setUserPhotoView() {
        // TODO
    }

    private void setUserStarView() {
        // TODO
    }

    @Override
    public void notifyDataSetChanged() {
        // TODO Auto-generated method stub
        super.notifyDataSetChanged();
    }

    private class ViewHolder {
        ImageView mPhoto;
        ImageView mStarIcon;
        TextView mNameView;
        TextView mPriceView;
        TextView mTagView;

    }

}
