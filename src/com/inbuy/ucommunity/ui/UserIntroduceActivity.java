
package com.inbuy.ucommunity.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_user_introduce);

        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_USER, this);

        setupActionbar();

        initViewsRes();

        mUserId = this.getIntent().getStringExtra(Const.EXTRA_USER_ID);

        DataUpdater.requestDataUpdate(DataUpdater.DATA_UPDATE_TYPE_USER, mUserId);
    }

    private void setupActionbar() {
        ActionBar actionbar = this.getActionBar();

        actionbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));

        int flag = actionbar.getDisplayOptions() ^ ActionBar.DISPLAY_SHOW_TITLE;
        actionbar.setDisplayOptions(flag);

        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);

        View customView = this.getLayoutInflater().inflate(R.layout.actionbar_title_view, null);
        TextView titleView = (TextView) customView.findViewById(R.id.txt_actionbar_title);
        titleView.setText("商户信息");
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
        mCardPhoneView = (TextView) this.findViewById(R.id.txt_card_phone);

        mUserNameView = (TextView) this.findViewById(R.id.txt_user_name);
        mUserInfoView = (TextView) this.findViewById(R.id.txt_user_info);

        mUserNameView2 = (TextView) this.findViewById(R.id.txt_user_name2);
        mUserCardMoreView = (TextView) this.findViewById(R.id.txt_user_card_more);

        mLoadingBar = (ProgressBar) this.findViewById(R.id.loading);

        mAddressLayout = (LinearLayout) this.findViewById(R.id.layout_address);
        mPhoneLayout = (LinearLayout) this.findViewById(R.id.layout_phone);
        mInfoLayout = (LinearLayout) this.findViewById(R.id.layout_user_info);
        mCardMoreLayout = (LinearLayout) this.findViewById(R.id.layout_user_price);

        mAddressLayout.setOnClickListener(mClickListener);
        mPhoneLayout.setOnClickListener(mClickListener);
        mInfoLayout.setOnClickListener(mClickListener);
        mCardMoreLayout.setOnClickListener(mClickListener);
    }

    private OnClickListener mClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.layout_address:
                    break;
                case R.id.layout_phone:
                    break;
                case R.id.layout_user_info:
                    gotoUserDetailScreen();
                    break;
                case R.id.layout_user_price:
                    break;
            }
        }
    };

    private void refresh() {
        mUser = DataModel.getUserItem();
        if (mUser != null) {
            mTitleUserNameView.setText(mUser.mName);
            mTitleUserDesView.setText(mUser.mDes);
            mTitleUserTagView.setText(mUser.mTag);
            setPhotoView();
            setStarView();

            mUserAddrView.setText(mUser.mAddress);
            mUserPhoneView.setText(mUser.mPhone);
            mCardPhoneView.setText(mUser.mCardPhone);

            mUserNameView.setText(mUser.mName);
            mUserInfoView.setText(mUser.mInfo);

            mUserNameView2.setText(mUser.mName);
            mUserCardMoreView.setText(mUser.mFckCardMore);
        }
    }

    private void setPhotoView() {
        // TODO
    }

    private void setStarView() {
        // TODO
    }

    private void gotoUserDetailScreen() {
        Intent intent = new Intent();
        intent.setClass(this, UserDetailActivity.class);
        intent.putExtra(Const.EXTRA_USER_ID, mUser.mId);
        this.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        DataUpdater.unregisterDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_USER, this);

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
