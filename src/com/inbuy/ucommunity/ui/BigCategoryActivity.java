
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.inbuy.ucommunity.R;
import com.inbuy.ucommunity.data.Area;
import com.inbuy.ucommunity.data.BigCategory;
import com.inbuy.ucommunity.engine.DataModel;
import com.inbuy.ucommunity.engine.DataUpdateListener;
import com.inbuy.ucommunity.engine.DataUpdater;
import com.inbuy.ucommunity.util.Const;

import java.util.ArrayList;

public class BigCategoryActivity extends Activity implements DataUpdateListener {
    private static final String TAG = "BigCategoryActivity";
    private static final int MSG_UPDATE_LIST = 0;
    private static final int MSG_ACTION = 1;
    private static final int MSG_DATA_UPDATE = 2;

    private ListView mBigCategoryList;
    private Spinner mAreaSpinner;
    private ProgressBar mLoadingBar;

    ArrayAdapter<String> mAreaAdapter;

    ArrayList<Area> mAreaList;
    ArrayList<BigCategory> mBigCategories;
    BigCategoryListAdapter mBigCateAdapter;

    ArrayList<String> mAreaNameList = new ArrayList<String>();

    private String mCurrentCityId;

    private int mAreaPosition;
    private int mBigCatePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_big_category);

        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_AREAES, this);
        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_BIGCATES, this);

        setupActionbar();
        mBigCategoryList = (ListView) this.findViewById(R.id.list_users);
        mAreaSpinner = (Spinner) this.findViewById(R.id.spinner);
        mLoadingBar = (ProgressBar) this.findViewById(R.id.loading);

        mCurrentCityId = this.getIntent().getStringExtra(Const.EXTRA_CITY_ID);
        DataUpdater.requestDataUpdate(DataUpdater.DATA_UPDATE_TYPE_AREAES, mCurrentCityId);

        setupSpinner();
        setupListview();

    }

    private void setupActionbar() {
        ActionBar actionbar = this.getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        int flag = actionbar.getDisplayOptions() ^ ActionBar.DISPLAY_SHOW_HOME
                ^ ActionBar.DISPLAY_SHOW_TITLE;
        actionbar.setDisplayOptions(flag);
        actionbar.setDisplayShowCustomEnabled(true);

        View customView = this.getLayoutInflater().inflate(R.layout.actionbar_title_view, null);
        TextView titleView = (TextView) customView.findViewById(R.id.txt_actionbar_title);
        titleView.setText(R.string.title_users_category);
        actionbar.setCustomView(customView, new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        ActionBar.LayoutParams lp = (ActionBar.LayoutParams) customView.getLayoutParams();
        lp.gravity &= Gravity.CENTER_HORIZONTAL;
        actionbar.setCustomView(customView, lp);
    }

    private void setupSpinner() {
        // FIXME to get areas from the server.
        mAreaNameList.add("全部商户");
        mAreaAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                mAreaNameList);

        mAreaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAreaSpinner.setAdapter(mAreaAdapter);

        mAreaSpinner.setOnItemSelectedListener(mAreaItemSelectedListener);

    }

    private OnItemSelectedListener mAreaItemSelectedListener = new OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mAreaPosition = position;
        }

        public void onNothingSelected(AdapterView<?> parent) {
            mAreaPosition = 0;
        }
    };

    private void setupListview() {
        mBigCategories = DataModel.getBigCatesListItems();
        Log.d(TAG, "setupListview: mBigCategories = " + mBigCategories);
        mBigCateAdapter = new BigCategoryListAdapter(this, mBigCategories, mHanlder);
        mBigCategoryList.setAdapter(mBigCateAdapter);
        mBigCategoryList.setOnItemClickListener(mBigCateItemClickListener);
    }

    private OnItemClickListener mBigCateItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            mBigCatePosition = position;
            gotoUserListScreen();
        }

    };

    private void gotoUserListScreen() {
        Intent intent = new Intent();
        intent.setClass(this, UserListActivity.class);
        intent.putExtra(Const.EXTRA_CITY_ID, mCurrentCityId);
        intent.putExtra(Const.EXTRA_AREA_POSITION, mAreaPosition);
        intent.putExtra(Const.EXTRA_BIGCATE_POSITION, mBigCatePosition);
        this.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        DataUpdater.unregisterDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_AREAES, this);
        DataUpdater.unregisterDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_BIGCATES, this);

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

    private void updateSpinner(int status) {
        Log.d(TAG, "updateSpinner: status = " + status);
        switch (status) {
            case DataUpdater.DATA_UPDATE_STATUS_LOADING:
                mLoadingBar.setVisibility(View.VISIBLE);
                break;
            case DataUpdater.DATA_UPDATE_STATUS_READY:
                mLoadingBar.setVisibility(View.GONE);
                mAreaList = DataModel.getAreaListItems(Integer.valueOf(mCurrentCityId));
                for (Area area : mAreaList) {
                    mAreaNameList.add(area.mName);
                }
                mAreaAdapter.notifyDataSetChanged();
                break;
            case DataUpdater.DATA_UPDATE_STATUS_ERROR:
                mLoadingBar.setVisibility(View.GONE);
                break;
        }
    }

    private void updateBigCateListView(int status) {
        Log.d(TAG, "updateBigCateListView: status = " + status);
        switch (status) {
            case DataUpdater.DATA_UPDATE_STATUS_LOADING:
                mLoadingBar.setVisibility(View.VISIBLE);
                break;
            case DataUpdater.DATA_UPDATE_STATUS_READY:
                mLoadingBar.setVisibility(View.GONE);
                mBigCategories = DataModel.getBigCatesListItems();
                mBigCateAdapter.setBigCategoryList(mBigCategories);
                mBigCateAdapter.notifyDataSetChanged();
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
                            updateSpinner(status);
                            break;
                        case DataUpdater.DATA_UPDATE_TYPE_BIGCATES:
                            updateBigCateListView(status);
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
