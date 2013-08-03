
package com.inbuy.ucommunity.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inbuy.ucommunity.R;
import com.inbuy.ucommunity.data.User;
import com.inbuy.ucommunity.util.NetUtil;
import com.inbuy.ucommunity.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;

public class UserListAdapter extends ArrayAdapter<User> {
    private static final int LAYOUT_ID = R.layout.user_list_item;
    private ArrayList<User> mUsers;

    private Handler mHandler;

    DisplayImageOptions mOptions;

    private ImageLoadingListener mAnimateFirstListener = new AnimateFirstDisplayListener();

    public UserListAdapter(Context context, ArrayList<User> users, Handler handler) {
        super(context, LAYOUT_ID);
        mUsers = users;
        mHandler = handler;

        mOptions = new DisplayImageOptions.Builder().showStubImage(R.drawable.user_default)
                .showImageForEmptyUri(R.drawable.user_default)
                .showImageOnFail(R.drawable.user_default).cacheInMemory(true).cacheOnDisc(true)
                .displayer(new RoundedBitmapDisplayer(10)).build();
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
            holder.mTagView = (TextView) convertView.findViewById(R.id.txt_tag);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (holder != null) {
            User user = mUsers.get(position);
            holder.mNameView.setText(user.mName);
            holder.mTagView.setText(user.mTag);
            holder.mPriceView.setText(user.mDes);

            setUserPhotoView(user, holder.mPhoto);
            setUserStarView(user, holder.mStarIcon);
        }

        return convertView;
    }

    private void setUserPhotoView(User user, ImageView photo) {
        Drawable drawable = null;

        if (user != null) {
            String photoUrl = user.mImageUrl;
            int index = photoUrl.indexOf(";");
            String imgUrl = photoUrl.substring(0, index);
            Object[] args = {
                    user.mId, imgUrl
            };

            ImageLoader.getInstance().displayImage(NetUtil.getPhotoUrl(imgUrl), photo, mOptions,
                    mAnimateFirstListener);
        }

        // if (photo != null) {
        // drawable = Util.getUserPhotoDrawable(getContext(), user.mId,
        // R.drawable.user_default);
        //
        // if (drawable != null) {
        // photo.setImageDrawable(drawable);
        // } else {
        // photo.setImageResource(R.drawable.user_default);
        //
        // if (isScroll) {
        // return;
        // }
        //
        // if (user != null) {
        // String photoUrl = user.mImageUrl;
        // int index = photoUrl.indexOf(";");
        // String imgUrl = photoUrl.substring(0, index);
        // Object[] args = {
        // user.mId, imgUrl
        // };
        // DataUpdater.requestDataUpdate(DataUpdater.DATA_UPDATE_TYPE_USER_PHOTO,
        // args);
        // }
        // }
        // }
    }

    private void setUserStarView(User user, ImageView starView) {
        if (user == null || starView == null) {
            return;
        }
        int starCount = Integer.valueOf(user.mStar);

        // Drawable drawable =
        // this.getContext().getResources().getDrawable(R.drawable.ic_star_light);
        // Bitmap bitmap = Util.drawableToBitmap(drawable);

        // Bitmap starBitmap = Util.createStarsImageBitmap(bitmap, starCount);
        // starView.setImageBitmap(starBitmap);

        int resId = Util.getStarsResourceId(starCount);
        starView.setImageResource(resId);
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
