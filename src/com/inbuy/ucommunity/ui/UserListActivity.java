
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
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.inbuy.ucommunity.R;
import com.inbuy.ucommunity.data.Area;
import com.inbuy.ucommunity.data.BigCategory;
import com.inbuy.ucommunity.data.User;
import com.inbuy.ucommunity.engine.DataModel;
import com.inbuy.ucommunity.engine.DataUpdateListener;
import com.inbuy.ucommunity.engine.DataUpdater;
import com.inbuy.ucommunity.util.Const;
import com.inbuy.ucommunity.util.NetUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class UserListActivity extends Activity implements DataUpdateListener {
    private static final String TAG = "UserListActivity";
    private static final int MSG_UPDATE_LIST = 0;
    private static final int MSG_ACTION = 1;
    private static final int MSG_DATA_UPDATE = 2;

    private LinearLayout mSpinnerGroup;
    private Spinner mSpinnerLeft;
    private Spinner mSpinnerMiddle;
    private Spinner mSpinnerRight;

    private ListView mUserListView;

    private ProgressBar mLoadingBar;

    ArrayAdapter<String> mAreaAdapter;
    ArrayList<String> mAreaNameList = new ArrayList<String>();
    ArrayList<Area> mAreaList;

    ArrayAdapter<String> mBigCateAdapter;
    ArrayList<String> mBigCateNameList = new ArrayList<String>();
    ArrayList<BigCategory> mBigCateList;

    ArrayList<User> mUserList;
    UserListAdapter mUserListAdapter;

    private String mCurrentCityId;
    private int mCurAreaPos;
    private int mCurBigCatePos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_user_list);
        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_AREAES, this);
        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_BIGCATES, this);
        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_USERS, this);
        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_USER_PHOTO, this);
        Intent intent = this.getIntent();
        mCurrentCityId = intent.getStringExtra(Const.EXTRA_CITY_ID);
        mCurAreaPos = intent.getIntExtra(Const.EXTRA_AREA_POSITION, 0);
        mCurBigCatePos = intent.getIntExtra(Const.EXTRA_BIGCATE_POSITION, 0);

        initViewsRes();
        setupActionBar();
        setupSpinnerGroup();
        setupListView();
    }

    private void initViewsRes() {
        mSpinnerLeft = (Spinner) findViewById(R.id.spinner_left);
        mSpinnerRight = (Spinner) findViewById(R.id.spinner_right);
        mSpinnerMiddle = (Spinner) findViewById(R.id.spinner_middle);
        mSpinnerGroup = (LinearLayout) findViewById(R.id.group_spinners);
        mUserListView = (ListView) findViewById(R.id.list_users);

        mLoadingBar = (ProgressBar) this.findViewById(R.id.loading);
    }

    private void setupActionBar() {
        ActionBar actionbar = this.getActionBar();
        actionbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        actionbar.setDisplayHomeAsUpEnabled(true);
        int flag = actionbar.getDisplayOptions() ^ ActionBar.DISPLAY_SHOW_TITLE;
        actionbar.setDisplayOptions(flag);
        actionbar.setDisplayShowCustomEnabled(true);

        View customView = this.getLayoutInflater().inflate(R.layout.actionbar_title_view, null);
        TextView titleView = (TextView) customView.findViewById(R.id.txt_actionbar_title);
        titleView.setText("商户列表");
        actionbar.setCustomView(customView, new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        ActionBar.LayoutParams lp = (ActionBar.LayoutParams) customView.getLayoutParams();
        lp.gravity &= Gravity.CENTER;
        actionbar.setCustomView(customView, lp);
    }

    private void setupSpinnerGroup() {
        setupSpinnerLeft();
        setupSpinnerMiddle();
        setupSpinnerRight();
    }

    private void setupSpinnerLeft() {
        setAreaNameList();
        mAreaAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                mAreaNameList);

        mAreaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerLeft.setAdapter(mAreaAdapter);

        mSpinnerLeft.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurAreaPos = position;
                requestUserList();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpinnerLeft.setSelection(mCurAreaPos);
    }

    private void setupSpinnerMiddle() {
        setBigCateNameList();
        mBigCateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                mBigCateNameList);

        mBigCateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMiddle.setAdapter(mBigCateAdapter);

        mSpinnerMiddle.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO
                mCurBigCatePos = position;
                requestUserList();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpinnerMiddle.setSelection(mCurBigCatePos + 1);
    }

    private void setupSpinnerRight() {
        mSpinnerRight.setVisibility(View.INVISIBLE);

    }

    private void requestUserList() {
        HashMap<String, String> arg = new HashMap<String, String>();

        if (mCurBigCatePos == 0 && mCurAreaPos == 0) {
            arg.put(NetUtil.PARAM_NAME_CITY, mCurrentCityId);
        }

        if (mCurAreaPos != 0) {
            arg.put(NetUtil.PARAM_NAME_XZ, mAreaList.get(mCurAreaPos - 1).mId);
        }

        if (mCurBigCatePos != 0) {
            arg.put(NetUtil.PARAM_NAME_BCATE, mBigCateList.get(mCurBigCatePos - 1).mId);
        }
        DataUpdater.requestDataUpdate(DataUpdater.DATA_UPDATE_TYPE_USERS, arg);
    }

    private void setupListView() {
        mUserList = DataModel.getUserListItems();
        HashMap<String, String> arg = new HashMap<String, String>();

        requestUserList();

        mUserListAdapter = new UserListAdapter(this, mUserList, null);

        mUserListView.setAdapter(mUserListAdapter);

        mUserListView.setOnItemClickListener(mUserItemClickListener);

    }

    private OnItemClickListener mUserItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
            gotoUserIntroduceScreen(position);
        }

    };

    private void gotoUserIntroduceScreen(int position) {
        Intent intent = new Intent();
        intent.setClass(this, UserIntroduceActivity.class);
        intent.putExtra(Const.EXTRA_USER_ID, mUserList.get(position).mId);
        this.startActivity(intent);
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
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        DataUpdater.unregisterDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_AREAES, this);
        DataUpdater.unregisterDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_BIGCATES, this);
        DataUpdater.unregisterDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_USERS, this);
        DataUpdater.unregisterDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_USER_PHOTO, this);

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    private void setAreaNameList() {
        mAreaList = null;
        mAreaList = DataModel.getAreaListItems(Integer.valueOf(mCurrentCityId));
        if (mAreaList != null) {
            if (mAreaNameList == null) {
                mAreaNameList = new ArrayList<String>();
            } else {
                mAreaNameList.clear();
            }
            mAreaNameList.add("全部商户");
            for (Area area : mAreaList) {
                mAreaNameList.add(area.mName);
            }
        }
    }

    private void setBigCateNameList() {
        mBigCateList = null;
        mBigCateList = DataModel.getBigCatesListItems();
        if (mBigCateList != null) {
            if (mBigCateNameList == null) {
                mBigCateNameList = new ArrayList<String>();
            } else {
                mBigCateNameList.clear();
            }
            mBigCateNameList.add("全部分类");
            for (BigCategory bigCate : mBigCateList) {
                mBigCateNameList.add(bigCate.mName);
            }
        }
    }

    private void setUserList() {
        mUserList = null;
        mUserList = DataModel.getUserListItems();
        mUserListAdapter.setUserList(mUserList);
    }

    private void updateSpinnerLeft(int status) {
        Log.d(TAG, "updateSpinner: status = " + status);
        switch (status) {
            case DataUpdater.DATA_UPDATE_STATUS_LOADING:
                mLoadingBar.setVisibility(View.VISIBLE);
                break;
            case DataUpdater.DATA_UPDATE_STATUS_READY:
                mLoadingBar.setVisibility(View.GONE);
                setAreaNameList();
                mAreaAdapter.notifyDataSetChanged();
                break;
            case DataUpdater.DATA_UPDATE_STATUS_ERROR:
                mLoadingBar.setVisibility(View.GONE);
                break;
        }
    }

    private void updateSpinnerMiddle(int status) {
        Log.d(TAG, "updateSpinner: status = " + status);
        switch (status) {
            case DataUpdater.DATA_UPDATE_STATUS_LOADING:
                mLoadingBar.setVisibility(View.VISIBLE);
                break;
            case DataUpdater.DATA_UPDATE_STATUS_READY:
                mLoadingBar.setVisibility(View.GONE);
                setBigCateNameList();
                mBigCateAdapter.notifyDataSetChanged();
                break;
            case DataUpdater.DATA_UPDATE_STATUS_ERROR:
                mLoadingBar.setVisibility(View.GONE);
                break;
        }
    }

    private void updateUserListView(int status) {
        Log.d(TAG, "updateUserListView: status = " + status);
        switch (status) {
            case DataUpdater.DATA_UPDATE_STATUS_LOADING:
                mLoadingBar.setVisibility(View.VISIBLE);
                break;
            case DataUpdater.DATA_UPDATE_STATUS_READY:
                mLoadingBar.setVisibility(View.GONE);
                setUserList();
                mUserListAdapter.notifyDataSetChanged();
                break;
            case DataUpdater.DATA_UPDATE_STATUS_ERROR:
                mLoadingBar.setVisibility(View.GONE);
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
                        case DataUpdater.DATA_UPDATE_TYPE_AREAES:
                            updateSpinnerLeft(status);
                            break;
                        case DataUpdater.DATA_UPDATE_TYPE_BIGCATES:
                            updateSpinnerMiddle(status);
                            break;
                        case DataUpdater.DATA_UPDATE_TYPE_USERS:
                            updateUserListView(status);
                            break;
                        case DataUpdater.DATA_UPDATE_TYPE_USER_PHOTO:
                            mUserListAdapter.notifyDataSetChanged();
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
