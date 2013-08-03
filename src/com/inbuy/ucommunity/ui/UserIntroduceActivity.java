
package com.inbuy.ucommunity.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.inbuy.ucommunity.R;
import com.inbuy.ucommunity.data.User;
import com.inbuy.ucommunity.engine.DataModel;
import com.inbuy.ucommunity.engine.DataUpdateListener;
import com.inbuy.ucommunity.engine.DataUpdater;
import com.inbuy.ucommunity.util.Const;
import com.inbuy.ucommunity.util.NetUtil;
import com.inbuy.ucommunity.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class UserIntroduceActivity extends Activity implements DataUpdateListener {
    private static final String TAG = "UserIntroduceActivity";
    private static final int MSG_UPDATE_LIST = 0;
    private static final int MSG_ACTION = 1;
    private static final int MSG_DATA_UPDATE = 2;

    private LinearLayout mLayoutGroup;

    private LinearLayout mAddressLayout;
    private LinearLayout mPhoneLayout;
    private LinearLayout mInfoLayout;
    private LinearLayout mCardMoreLayout;

    private TextView mTitleUserNameView;
    private TextView mTitleUserDesView;
    private TextView mTitleUserTagView;
    private ImageView mTitleUserPhotoView;
    private ImageView mTitleUserStarView;

    private TextView mUserAddrView;
    private TextView mUserPhoneView;
    private TextView mCardPhoneView;

    private TextView mUserNameView;
    private TextView mUserInfoView;

    private TextView mUserNameView2;
    private TextView mUserCardMoreView;

    private ProgressBar mLoadingBar;

    private String mUserId;

    private User mUser;

    private String mLng;
    private String mLat;

    DisplayImageOptions mOptions;

    private ImageLoadingListener mAnimateFirstListener = new AnimateFirstDisplayListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_user_introduce);

        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_USER, this);
        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_USER_PHOTO, this);

        setupActionbar();

        initViewsRes();

        mUserId = this.getIntent().getStringExtra(Const.EXTRA_USER_ID);

        DataUpdater.requestDataUpdate(DataUpdater.DATA_UPDATE_TYPE_USER, mUserId);

        mOptions = new DisplayImageOptions.Builder().showStubImage(R.drawable.user_default)
                .showImageForEmptyUri(R.drawable.user_default)
                .showImageOnFail(R.drawable.user_default).cacheInMemory(true).cacheOnDisc(true)
                .displayer(new RoundedBitmapDisplayer(10)).build();
    }

    @SuppressLint("NewApi")
    private void setupActionbar() {
        ActionBar actionbar = this.getActionBar();

        actionbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));

        actionbar.setHomeButtonEnabled(true);
        actionbar.setIcon(R.drawable.ic_actionbar_back_normal);

        int flag = actionbar.getDisplayOptions() ^ ActionBar.DISPLAY_SHOW_TITLE;
        actionbar.setDisplayOptions(flag);

        actionbar.setDisplayShowCustomEnabled(true);

        View customView = this.getLayoutInflater().inflate(R.layout.actionbar_title_view, null);
        TextView titleView = (TextView) customView.findViewById(R.id.txt_actionbar_title);
        titleView.setText(getResources().getString(R.string.title_user_info));
        actionbar.setCustomView(customView, new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        ActionBar.LayoutParams lp = (ActionBar.LayoutParams) customView.getLayoutParams();
        lp.gravity &= Gravity.CENTER;
        actionbar.setCustomView(customView, lp);
    }

    private void initViewsRes() {
        mLayoutGroup = (LinearLayout) this.findViewById(R.id.layout_group);
        mTitleUserNameView = (TextView) this.findViewById(R.id.txt_name);
        mTitleUserDesView = (TextView) this.findViewById(R.id.txt_price);
        mTitleUserTagView = (TextView) this.findViewById(R.id.txt_tag);
        mTitleUserPhotoView = (ImageView) this.findViewById(R.id.img_photo);
        mTitleUserStarView = (ImageView) this.findViewById(R.id.img_star);

        mUserAddrView = (TextView) this.findViewById(R.id.txt_address);

        mUserPhoneView = (TextView) this.findViewById(R.id.txt_user_phone);
        mCardPhoneView = (TextView) this.findViewById(R.id.txt_credit_hot_line);

        mUserNameView = (TextView) this.findViewById(R.id.txt_user_name);
        mUserInfoView = (TextView) this.findViewById(R.id.txt_user_info);

        mUserNameView2 = (TextView) this.findViewById(R.id.txt_user_name2);
        mUserCardMoreView = (TextView) this.findViewById(R.id.txt_user_card_more);

        mLoadingBar = (ProgressBar) this.findViewById(R.id.loading);

        mAddressLayout = (LinearLayout) this.findViewById(R.id.layout_address);
        mPhoneLayout = (LinearLayout) this.findViewById(R.id.layout_user_phone);
        LinearLayout cardLayout = (LinearLayout) this.findViewById(R.id.layout_credit_hot_line);
        mInfoLayout = (LinearLayout) this.findViewById(R.id.layout_user_info);
        mCardMoreLayout = (LinearLayout) this.findViewById(R.id.layout_user_price);

        mAddressLayout.setOnClickListener(mClickListener);
        mPhoneLayout.setOnClickListener(mClickListener);
        cardLayout.setOnClickListener(mClickListener);
        mInfoLayout.setOnClickListener(mClickListener);
        mCardMoreLayout.setOnClickListener(mClickListener);
    }

    private OnClickListener mClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.layout_address:
                    launchMap();
                    break;
                case R.id.layout_user_phone:
                    makeCall(mUserPhoneView.getText().toString());
                    break;
                case R.id.layout_credit_hot_line:
                    makeCall(mCardPhoneView.getText().toString());
                    break;
                case R.id.layout_user_info:
                    gotoUserDetailScreen(UserDetailActivity.TYPE_USER_INFO);
                    break;
                case R.id.layout_user_price:
                    gotoUserDetailScreen(UserDetailActivity.TYPE_USER_PRICE);
                    break;
            }
        }
    };

    private void makeCall(String number) {
        if (number == null) {
            return;
        }
        Intent phone = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        startActivity(phone);
    }

    private void refresh() {
        mUser = DataModel.getUserItem();
        if (mUser != null) {
            mLng = mUser.mLng;
            mLat = mUser.mLat;

            mTitleUserNameView.setText(mUser.mName);
            mTitleUserDesView.setText(mUser.mDes);
            mTitleUserTagView.setText(mUser.mTag);
            setPhotoView();
            setStarView();

            String str = Util.clearStrings(mUser.mAddress);
            String format = getResources().getString(R.string.user_address);
            str = String.format(format, str);
            mUserAddrView.setText(str);
            str = Util.clearStrings(mUser.mPhone);
            format = getResources().getString(R.string.user_phone);
            str = String.format(format, str);
            mUserPhoneView.setText(str);
            str = Util.clearStrings(mUser.mCardPhone);
            format = getResources().getString(R.string.credit_hot_line);
            str = String.format(format, str);
            mCardPhoneView.setText(str);

            mUserNameView.setText(Util.clearStrings(mUser.mName));
            mUserInfoView.setText(Util.clearStrings(mUser.mInfo));

            mUserNameView2.setText(Util.clearStrings(mUser.mName));
            mUserCardMoreView.setText(Util.clearStrings(mUser.mFckCardMore));
        }
    }

    // private void setPhotoView() {
    // Drawable drawable = null;
    //
    // if (mTitleUserPhotoView != null && mUser != null) {
    // drawable = Util.getUserPhotoDrawable(this, mUser.mId,
    // R.drawable.user_default);
    //
    // if (drawable != null) {
    // mTitleUserPhotoView.setImageDrawable(drawable);
    // } else {
    // mTitleUserPhotoView.setImageResource(R.drawable.user_default);
    //
    // String photoUrl = mUser.mImageUrl;
    // int index = photoUrl.indexOf(";");
    // String imgUrl = photoUrl.substring(0, index);
    // Object[] args = {
    // mUser.mId, imgUrl
    // };
    // DataUpdater.requestDataUpdate(DataUpdater.DATA_UPDATE_TYPE_USER_PHOTO,
    // args);
    // }
    // }
    //
    // }

    private void setPhotoView() {

        if (mTitleUserPhotoView != null && mUser != null) {
            String photoUrl = mUser.mImageUrl;
            int index = photoUrl.indexOf(";");
            String imgUrl = photoUrl.substring(0, index);

            ImageLoader.getInstance().displayImage(NetUtil.getPhotoUrl(imgUrl),
                    mTitleUserPhotoView, mOptions, mAnimateFirstListener);
        }

    }

    private void setStarView() {
        if (mUser == null || mTitleUserStarView == null) {
            return;
        }
        int starCount = Integer.valueOf(mUser.mStar);

        // Drawable drawable =
        // getResources().getDrawable(R.drawable.ic_star_light);
        // Bitmap bitmap = Util.drawableToBitmap(drawable);

        // Bitmap starBitmap = Util.createStarsImageBitmap(bitmap, starCount);
        // mTitleUserStarView.setImageBitmap(starBitmap);
        int resId = Util.getStarsResourceId(starCount);
        mTitleUserStarView.setImageResource(resId);

    }

    private void gotoUserDetailScreen(int type) {
        Intent intent = new Intent();
        intent.setClass(this, UserDetailActivity.class);
        intent.putExtra(Const.EXTRA_ACTION_VIEW_TYPE, type);
        intent.putExtra(Const.EXTRA_USER_ID, mUser.mId);
        this.startActivity(intent);
    }

    private void launchMap() {
        StringBuffer sb = new StringBuffer();
        sb.append("geo:").append(0).append(",").append(0);

        String query = mUser.mName;
        int index = query.indexOf(" ");

        if (index != -1) {
            query = mUser.mName.substring(0, index);
        }

        sb.append("?q=").append("(").append(mLat).append(",").append(mLng).append(")")
                .append(query);
        Uri uri = Uri.parse(sb.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        this.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        DataUpdater.unregisterDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_USER, this);
        DataUpdater.unregisterDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_USER_PHOTO, this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "onOptionsItemSelected: id = " + id);
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    private void updateUserInfo(int status) {
        Log.d(TAG, "updateUserInfo: status = " + status);
        switch (status) {
            case DataUpdater.DATA_UPDATE_STATUS_LOADING:
                mLoadingBar.setVisibility(View.VISIBLE);
                mLayoutGroup.setVisibility(View.INVISIBLE);
                break;
            case DataUpdater.DATA_UPDATE_STATUS_READY:
                mLoadingBar.setVisibility(View.GONE);
                mLayoutGroup.setVisibility(View.VISIBLE);
                refresh();
                break;
            case DataUpdater.DATA_UPDATE_STATUS_ERROR:
                mLoadingBar.setVisibility(View.GONE);
                mLayoutGroup.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private Handler mHanlder = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            int what = msg.what;
            switch (what) {
                case MSG_UPDATE_LIST:
                    break;
                case MSG_ACTION:
                    break;
                case MSG_DATA_UPDATE:
                    int dataType = msg.arg2;
                    int status = msg.arg1;
                    switch (dataType) {
                        case DataUpdater.DATA_UPDATE_TYPE_USER:
                            updateUserInfo(status);
                            break;
                        default:
                            break;
                    }

                    break;
                default:
                    break;
            }

            super.handleMessage(msg);
        }

    };

    @Override
    public void onUpdate(int status, int dataType, int id, Object arg) {
        Log.d(TAG, "onUpdate: status = " + status + " dataType = " + dataType);
        Message msg = mHanlder.obtainMessage(MSG_DATA_UPDATE);
        msg.arg1 = status;
        msg.arg2 = dataType;
        msg.sendToTarget();

    }

}
