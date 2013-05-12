
package com.inbuy.ucommunity.ui;

import android.annotation.SuppressLint;
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
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inbuy.ucommunity.R;
import com.inbuy.ucommunity.data.Area;
import com.inbuy.ucommunity.data.BigCategory;
import com.inbuy.ucommunity.data.PanelListItem;
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
    private ProgressBar mLoadingBar;

    private RelativeLayout mAreaLayout;
    private TextView mAreaNameView;
    private ImageView mAreaIndicator;

    private RelativeLayout mAreaPanel;

    ArrayList<BigCategory> mBigCategories;
    BigCategoryListAdapter mBigCateAdapter;

    ArrayList<String> mAreaNameList = new ArrayList<String>();

    ArrayList<Area> mXzAreaList;
    ArrayList<Area> mBusAreaList;

    ArrayList<PanelListItem> mXzAreaNameList = new ArrayList<PanelListItem>();
    PanelListAdapter mXzAreaAdapter;
    ArrayList<PanelListItem> mBusAreaNameList = new ArrayList<PanelListItem>();
    PanelListAdapter mBusAreaAdapter;

    private ListView mXzList;
    private ListView mBusList;

    private String mCurrentCityId;

    private String mCurrentXzId;
    private String mCurrentBusId;

    private int mBigCatePosition;

    private int mXzAreaPosition;
    private int mBusAreaPosition;

    private int mXzAreaPrePos;
    private int mBusAreaPrePos;

    private Animation mAnimPanelShow = null;
    private Animation mAnimPanelHide = null;

    private boolean mAnimating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_big_category);

        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_AREAE_XZ, this);
        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_BIGCATES, this);
        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_AREAE_BUS, this);

        setupActionbar();
        mBigCategoryList = (ListView) this.findViewById(R.id.list_users);
        mLoadingBar = (ProgressBar) this.findViewById(R.id.loading);

        mXzList = (ListView) this.findViewById(R.id.list_xz);
        mBusList = (ListView) this.findViewById(R.id.list_bus);

        mAreaLayout = (RelativeLayout) this.findViewById(R.id.layout_area);
        mAreaNameView = (TextView) this.findViewById(R.id.txt_area_name);
        mAreaIndicator = (ImageView) this.findViewById(R.id.img_indicator);
        mAreaPanel = (RelativeLayout) this.findViewById(R.id.panel_area);

        mAreaLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mAreaPanel.isShown()) {
                    hideAreaPanel();
                } else {
                    showAreaPanel();
                }
            }

        });

        mCurrentCityId = this.getIntent().getStringExtra(Const.EXTRA_CITY_ID);
        DataUpdater.requestDataUpdate(DataUpdater.DATA_UPDATE_TYPE_AREAE_XZ, mCurrentCityId);

        initAnimation();

        setupListview();

        setupXzListView();
        setupBusListView();

        setAreaNameView();
    }

    @SuppressLint("NewApi")
    private void setupActionbar() {
        ActionBar actionbar = this.getActionBar();
        actionbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));

        actionbar.setHomeButtonEnabled(true);
        actionbar.setIcon(R.drawable.actionbar_back_selector);

        int flag = actionbar.getDisplayOptions() ^ ActionBar.DISPLAY_SHOW_TITLE;
        actionbar.setDisplayOptions(flag);

        actionbar.setDisplayShowCustomEnabled(true);

        View customView = this.getLayoutInflater().inflate(R.layout.actionbar_title_view, null);
        TextView titleView = (TextView) customView.findViewById(R.id.txt_actionbar_title);
        titleView.setText(R.string.title_users_category);
        actionbar.setCustomView(customView, new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        ActionBar.LayoutParams lp = (ActionBar.LayoutParams) customView.getLayoutParams();
        lp.gravity &= Gravity.CENTER;
        actionbar.setCustomView(customView, lp);
    }

    private void initAnimation() {
        if (mAnimPanelShow == null) {
            mAnimPanelShow = AnimationUtils.loadAnimation(this, R.anim.dropdown_activity_topin);

            mAnimPanelShow.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationEnd(Animation arg0) {
                    mAreaPanel.setVisibility(View.VISIBLE);
                    mAnimating = false;
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {

                }

                @Override
                public void onAnimationStart(Animation arg0) {
                }
            });
        }

        if (mAnimPanelHide == null) {
            mAnimPanelHide = AnimationUtils.loadAnimation(this, R.anim.dropdown_activity_topout);

            mAnimPanelHide.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationEnd(Animation arg0) {
                    mAreaPanel.setVisibility(View.INVISIBLE);
                    mAnimating = false;
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {

                }

                @Override
                public void onAnimationStart(Animation arg0) {
                }
            });
        }
    }

    private void showAreaPanel() {
        mAreaIndicator.setImageResource(R.drawable.ic_arrow_up);

        if (mAnimating) {
            return;
        }

        mAreaPanel.clearAnimation();

        mAreaPanel.startAnimation(mAnimPanelShow);
        mAreaPanel.postInvalidate();
        mAnimating = true;
    }

    private void hideAreaPanel() {
        mAreaIndicator.setImageResource(R.drawable.ic_arrow_down);

        if (mAnimating) {
            return;
        }

        mAreaPanel.clearAnimation();

        mAreaPanel.startAnimation(mAnimPanelHide);
        mAreaPanel.postInvalidate();
        mAnimating = true;

    }

    private void setupListview() {
        mBigCategories = DataModel.getBigCatesListItems();
        Log.d(TAG, "setupListview: mBigCategories = " + mBigCategories);
        mBigCateAdapter = new BigCategoryListAdapter(this, mBigCategories, mHanlder);
        mBigCategoryList.setAdapter(mBigCateAdapter);
        mBigCategoryList.setOnItemClickListener(mBigCateItemClickListener);
    }

    private void setAreaNameView() {
        if (mXzAreaPosition == 0 && mBusAreaPosition == 0) {
            mAreaNameView.setText(getResources().getText(R.string.all_area));
        } else if (mBusAreaPosition == 0) {
            mAreaNameView.setText(mXzAreaNameList.get(mXzAreaPosition).mName);
        } else {
            mAreaNameView.setText(mBusAreaNameList.get(mBusAreaPosition).mName);
        }
    }

    private void setupXzListView() {
        PanelListItem pli = new PanelListItem(PanelListItem.TYPE_LEFT, this.getResources()
                .getString(R.string.all_area), false);
        mXzAreaNameList.add(pli);
        // mXzAreaAdapter = new ArrayAdapter<String>(this,
        // android.R.layout.simple_list_item_1,
        // mXzAreaNameList);
        mXzAreaAdapter = new PanelListAdapter(this, mXzAreaNameList, mHanlder);

        mXzList.setAdapter(mXzAreaAdapter);

        mXzList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
                // TODO Auto-generated method stub
                if (mXzAreaNameList == null || mXzAreaNameList.isEmpty() || position < 0
                        || position >= mXzAreaNameList.size()) {
                    return;
                }
                mXzAreaPrePos = mXzAreaPosition;
                mXzAreaPosition = position;
                mXzAreaNameList.get(mXzAreaPrePos).mSelected = false;
                mXzAreaNameList.get(mXzAreaPosition).mSelected = true;
                mXzAreaAdapter.notifyDataSetChanged();

                mBusAreaPosition = 0;

                if (position == 0) {
                    mBusAreaList = null;

                    if (!mBusAreaNameList.isEmpty()) {
                        mBusAreaNameList.clear();
                    }

                    String name = BigCategoryActivity.this.getResources().getString(
                            R.string.all_area);
                    PanelListItem pli = new PanelListItem(PanelListItem.TYPE_RIGHT, name, false);
                    mBusAreaNameList.add(pli);
                    mBusAreaAdapter.notifyDataSetChanged();
                } else if (position > 0) {
                    mCurrentXzId = mXzAreaList.get(position - 1).mId;
                    updateBusAreaList(DataUpdater.DATA_UPDATE_STATUS_READY);
                }
            }

        });
    }

    private void setupBusListView() {
        String name = getResources().getString(R.string.all_area);
        PanelListItem pli = new PanelListItem(PanelListItem.TYPE_RIGHT, name, false);

        mBusAreaNameList.add(pli);

        mBusAreaAdapter = new PanelListAdapter(this, mBusAreaNameList, mHanlder);

        mBusList.setAdapter(mBusAreaAdapter);

        mBusList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                if (mBusAreaNameList == null || mBusAreaNameList.isEmpty() || position < 0
                        || position >= mBusAreaNameList.size()) {
                    return;
                }

                mBusAreaPrePos = mBusAreaPosition;
                mBusAreaPosition = position;
                mBusAreaNameList.get(mBusAreaPrePos).mSelected = false;
                mBusAreaNameList.get(mBusAreaPosition).mSelected = true;
                mBusAreaAdapter.notifyDataSetChanged();

                mBusAreaPosition = position;
                if (position > 0) {
                    mCurrentBusId = mBusAreaList.get(position - 1).mId;
                }

                setAreaNameView();
                hideAreaPanel();

            }

        });
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
        intent.putExtra(Const.EXTRA_XZ_AREA_POSITION, mXzAreaPosition);
        intent.putExtra(Const.EXTRA_BUS_AREA_POSITION, mBusAreaPosition);
        intent.putExtra(Const.EXTRA_BIGCATE_POSITION, mBigCatePosition);
        this.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        DataUpdater.unregisterDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_AREAE_XZ, this);
        DataUpdater.unregisterDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_AREAE_BUS, this);
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

    private void updateXzAreaList(int status) {
        Log.d(TAG, "updateXzAreaList: status = " + status);
        switch (status) {
            case DataUpdater.DATA_UPDATE_STATUS_LOADING:
                mLoadingBar.setVisibility(View.VISIBLE);
                break;
            case DataUpdater.DATA_UPDATE_STATUS_READY:
                mLoadingBar.setVisibility(View.GONE);
                mXzAreaList = DataModel.getXzAreaListItems(Integer.valueOf(mCurrentCityId));
                for (Area area : mXzAreaList) {
                    PanelListItem item = new PanelListItem(PanelListItem.TYPE_LEFT, area.mName,
                            false);
                    mXzAreaNameList.add(item);
                }
                mXzAreaAdapter.notifyDataSetChanged();
                break;
            case DataUpdater.DATA_UPDATE_STATUS_ERROR:
                mLoadingBar.setVisibility(View.GONE);
                break;
        }
    }

    private void updateBusAreaList(int status) {
        Log.d(TAG, "updateBusAreaList: status = " + status);
        switch (status) {
            case DataUpdater.DATA_UPDATE_STATUS_LOADING:
                mLoadingBar.setVisibility(View.VISIBLE);
                break;
            case DataUpdater.DATA_UPDATE_STATUS_READY:
                mLoadingBar.setVisibility(View.GONE);
                if (mCurrentXzId == null) {
                    break;
                }
                mBusAreaList = DataModel.getBusAreaListItems(Integer.valueOf(mCurrentXzId));

                if (!mBusAreaNameList.isEmpty()) {
                    mBusAreaNameList.clear();
                }

                String allBus = this.getResources().getString(R.string.all_bus);
                Area xzSelected = DataModel.getXzAreaById(Integer.valueOf(mCurrentCityId),
                        mCurrentXzId);

                if (xzSelected != null) {
                    allBus = String.format(allBus, xzSelected.mName);
                } else {
                    allBus = String.format(allBus, "");
                }

                PanelListItem pli = new PanelListItem(PanelListItem.TYPE_RIGHT, allBus, false);

                mBusAreaNameList.add(pli);
                for (Area area : mBusAreaList) {
                    pli = new PanelListItem(PanelListItem.TYPE_RIGHT, area.mName, false);
                    mBusAreaNameList.add(pli);
                }
                mBusAreaAdapter.notifyDataSetChanged();
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
                        case DataUpdater.DATA_UPDATE_TYPE_AREAE_XZ:
                            updateXzAreaList(status);
                            break;
                        case DataUpdater.DATA_UPDATE_TYPE_AREAE_BUS:
                            updateBusAreaList(status);
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
