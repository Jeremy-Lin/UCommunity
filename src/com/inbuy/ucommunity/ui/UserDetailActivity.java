
package com.inbuy.ucommunity.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.inbuy.ucommunity.R;
import com.inbuy.ucommunity.data.User;
import com.inbuy.ucommunity.engine.DataModel;
import com.inbuy.ucommunity.engine.DataUpdateListener;
import com.inbuy.ucommunity.engine.DataUpdater;
import com.inbuy.ucommunity.util.Const;
import com.inbuy.ucommunity.util.Util;

public class UserDetailActivity extends Activity implements DataUpdateListener {
    private static final String TAG = "UserDetailActivity";
    private static final int MSG_UPDATE_LIST = 0;
    private static final int MSG_ACTION = 1;
    private static final int MSG_DATA_UPDATE = 2;

    private LinearLayout mLayoutGroup;

    private TextView mUserAddrView;
    private TextView mUserPhoneView;

    private TextView mUserNameView;
    private TextView mUserInfoView;

    private ProgressBar mLoadingBar;

    private String mUserId;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_user_detail);

        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_USER, this);

        setupActionbar();

        initViewsRes();

        mUserId = this.getIntent().getStringExtra(Const.EXTRA_USER_ID);

        DataUpdater.requestDataUpdate(DataUpdater.DATA_UPDATE_TYPE_USER, mUserId);
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

        mUserAddrView = (TextView) this.findViewById(R.id.txt_user_address);

        mUserPhoneView = (TextView) this.findViewById(R.id.txt_user_phone);

        mUserNameView = (TextView) this.findViewById(R.id.txt_user_name);
        mUserInfoView = (TextView) this.findViewById(R.id.txt_user_info);

        mLoadingBar = (ProgressBar) this.findViewById(R.id.loading);

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
                    break;
                case R.id.layout_user_price:
                    break;
            }
        }
    };

    private void refresh() {
        mUser = DataModel.getUserItem();
        if (mUser != null) {

            mUserAddrView.setText(Util.clearStrings(mUser.mAddress));
            mUserPhoneView.setText(Util.clearStrings(mUser.mPhone));

            mUserNameView.setText(Util.clearStrings(mUser.mName));
            mUserInfoView.setText(Util.clearStrings(mUser.mInfo));

        }
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
