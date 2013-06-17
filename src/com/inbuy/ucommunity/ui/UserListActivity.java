
package com.inbuy.ucommunity.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.inbuy.ucommunity.R;
import com.inbuy.ucommunity.data.Area;
import com.inbuy.ucommunity.data.BigCategory;
import com.inbuy.ucommunity.data.PanelListItem;
import com.inbuy.ucommunity.data.SmallCategory;
import com.inbuy.ucommunity.data.User;
import com.inbuy.ucommunity.engine.DataModel;
import com.inbuy.ucommunity.engine.DataUpdateListener;
import com.inbuy.ucommunity.engine.DataUpdater;
import com.inbuy.ucommunity.util.Const;
import com.inbuy.ucommunity.util.NetUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class UserListActivity extends Activity implements DataUpdateListener, OnQueryTextListener {
    private static final String TAG = "UserListActivity";
    private static final int MSG_UPDATE_LIST = 0;
    private static final int MSG_ACTION = 1;
    private static final int MSG_DATA_UPDATE = 2;

    public static final String EXTRA_TYPE = "extra_type";
    public static final int TYPE_ALL = 0;
    public static final int TYPE_NEARBY = 1;
    public static final int TYPE_RECOMMAND = 2;
    public static final int TYPE_POPULATE = 3;
    public static final int TYPE_SEARCH = 4;

    private String[] mRanges = {
            "0.5", "1", "2", "3", "5"
    };

    private int mType;

    private LinearLayout mSpinnerGroup;

    private ListView mUserListView;

    private TextView mLoadingMoreView;
    private ProgressBar mLoadingMoreBar;

    private ProgressBar mLoadingBar;

    private boolean mIsLoadingMore;

    PanelListAdapter mXzAreaAdapter;
    ArrayList<PanelListItem> mXzAreaNameList = new ArrayList<PanelListItem>();
    ArrayList<Area> mXzAreaList;

    PanelListAdapter mBusAreaAdapter;
    ArrayList<PanelListItem> mBusAreaNameList = new ArrayList<PanelListItem>();
    ArrayList<Area> mBusAreaList;

    private ListView mXzList;
    private ListView mBusList;

    private RelativeLayout mRangeLayout;
    private TextView mRangeNameView;
    private ImageView mRangeIndicator;

    private RelativeLayout mAreaLayout;
    private TextView mAreaNameView;
    private ImageView mAreaIndicator;

    private RelativeLayout mCateLayout;
    private TextView mCateNameView;
    private ImageView mCateIndicator;

    private RelativeLayout mRangePanel;
    private RelativeLayout mAreaPanel;
    private RelativeLayout mCatePanel;

    private LinearLayout mRangeContainer;
    private LinearLayout mAreaContainer;
    private LinearLayout mCateContainer;

    PanelListAdapter mBigCateAdapter;
    ArrayList<PanelListItem> mBigCateNameList = new ArrayList<PanelListItem>();
    ArrayList<BigCategory> mBigCateList;

    PanelListAdapter mSmallCateAdapter;
    ArrayList<PanelListItem> mSmallCateNameList = new ArrayList<PanelListItem>();
    ArrayList<SmallCategory> mSmallCateList;

    private ListView mBigCateListView;
    private ListView mSmallCateListView;

    private ImageButton[] mNodeBtns;
    private TextView[] mNodeNameText;

    ArrayList<User> mUserList;
    UserListAdapter mUserListAdapter;

    private String mCurrentCityId;
    private int mCurAreaPos;

    private int mCurBigCatePos;
    private int mCurSmallCatePos;

    private int mCurXzAreaPos;
    private int mCurBusAreaPos;

    private int mPreXzAreaPos;
    private int mPreBusAreaPos;

    private int mPreBigCatePos;
    private int mPreSmallCatePos;

    private String mCurrentXzId;
    private String mCurrentBusId;

    private String mCurBigCateId;
    private String mCurSmallCateId;

    private String mKeyword;

    private EditText mSearchView;

    private int mLimitIndex = 0;

    private boolean mLoading = false;

    private Animation mAnimRangePanelShow = null;
    private Animation mAnimRangePanelHide = null;

    private boolean mRangePanelAnimating;

    private Animation mAnimAreaPanelShow = null;
    private Animation mAnimAreaPanelHide = null;

    private boolean mAreaPanelAnimating;

    private Animation mAnimCatePanelShow = null;
    private Animation mAnimCatePanelHide = null;

    private boolean mCatePanelAnimating;

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    LocationData locData = null;

    private double mGpsLng = -1;
    private double mGpsLat = -1;
    private String mGpsRange;

    private LocationManager mLocationManager;
    private String mProviderName;

    private LinearLayout mLocationPanel;
    private TextView mLngLatView;
    private TextView mLocationNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_user_list);
        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_AREAE_XZ, this);
        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_AREAE_BUS, this);
        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_BIGCATES, this);
        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_SMALLCATES, this);
        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_USERS, this);
        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_USER_PHOTO, this);
        Intent intent = this.getIntent();

        mType = this.getIntent().getIntExtra(EXTRA_TYPE, TYPE_ALL);

        mCurrentCityId = intent.getStringExtra(Const.EXTRA_CITY_ID);
        mKeyword = intent.getStringExtra(Const.EXTRA_KEYWORD);
        mCurAreaPos = intent.getIntExtra(Const.EXTRA_AREA_POSITION, 0);
        mCurBigCatePos = intent.getIntExtra(Const.EXTRA_BIGCATE_POSITION, 0);
        mCurXzAreaPos = intent.getIntExtra(Const.EXTRA_XZ_AREA_POSITION, 0);
        mCurBusAreaPos = intent.getIntExtra(Const.EXTRA_BUS_AREA_POSITION, 0);

        if (mType == TYPE_ALL) {
            mCurBigCatePos++;
        }
        Log.d(TAG, "onCreate: mCurAreaPos = " + mCurAreaPos + " mCurBigCatePos = " + mCurBigCatePos);
        initViewsRes();
        initAnimation();
        setupActionBar();
        setupSpinnerGroup();
        setupListView();

        if (mType == TYPE_NEARBY) {
            mRangeLayout.setVisibility(View.VISIBLE);
            mAreaLayout.setVisibility(View.GONE);
        } else {
            mRangeLayout.setVisibility(View.GONE);
            mAreaLayout.setVisibility(View.VISIBLE);
        }

        initBDLocation();
    }

    // private void initLocation() {
    // if (mType != TYPE_NEARBY) {
    // return;
    // }
    //
    // mLocationManager = (LocationManager)
    // getSystemService(Context.LOCATION_SERVICE);
    //
    // if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    // {
    // mProviderName = LocationManager.NETWORK_PROVIDER;
    // } else if
    // (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
    // mProviderName = LocationManager.GPS_PROVIDER;
    // } else {
    // Toast.makeText(this, "GPS is not enabled.", Toast.LENGTH_SHORT).show();
    // return;
    //
    // }
    //
    // if (mType == TYPE_NEARBY) {
    // if (!TextUtils.isEmpty(mProviderName)) {
    // mLocationManager.requestLocationUpdates(mProviderName, 0, 0,
    // mLocationListener);
    // }
    // }
    // }

    private void initBDLocation() {
        if (mType == TYPE_NEARBY) {
            UCommApplication app = (UCommApplication) this.getApplication();
            if (app.mBMapManager == null) {
                app.mBMapManager = new BMapManager(this);
                app.mBMapManager.init(UCommApplication.strKey,
                        new UCommApplication.MyGeneralListener());
            }

            mLocClient = new LocationClient(this);
            mLocClient.registerLocationListener(myListener);
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true);// 打开gps
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setScanSpan(500);

            option.setAddrType("all");// 返回的定位结果包含地址信息
            option.disableCache(true);// 禁止启用缓存定位

            mLocClient.setLocOption(option);
            mLocClient.start();
        }
    }

    private void initViewsRes() {
        mSpinnerGroup = (LinearLayout) findViewById(R.id.group_spinners);
        mUserListView = (ListView) findViewById(R.id.list_users);

        mLoadingBar = (ProgressBar) this.findViewById(R.id.loading);

        initFilterPanelRes();

        mLocationPanel = (LinearLayout) this.findViewById(R.id.layout_location);
        mLngLatView = (TextView) this.findViewById(R.id.txt_lng_lat);
        mLocationNameView = (TextView) this.findViewById(R.id.txt_location_name);

        if (mType == TYPE_NEARBY) {
            mLocationPanel.setVisibility(View.VISIBLE);
        } else {
            mLocationPanel.setVisibility(View.GONE);
        }
    }

    private void initFilterPanelRes() {
        // init range layout.
        mRangeLayout = (RelativeLayout) this.findViewById(R.id.layout_range);
        mRangeNameView = (TextView) this.findViewById(R.id.txt_range_name);
        mRangeIndicator = (ImageView) this.findViewById(R.id.img_range_indicator);
        mRangePanel = (RelativeLayout) this.findViewById(R.id.panel_range);

        mRangePanel.setOnClickListener(null);

        mRangeContainer = (LinearLayout) this.findViewById(R.id.panel_range_container);

        initRangeNodeBtn();

        mRangeLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mCatePanel.isShown()) {
                    mCatePanel.setVisibility(View.INVISIBLE);
                }

                if (mAreaPanel.isShown()) {
                    mAreaPanel.setVisibility(View.INVISIBLE);
                }

                if (mRangePanel.isShown()) {
                    hideRangePanel();
                } else {
                    showRangePanel();
                }
            }

        });

        // init area layout.
        mAreaLayout = (RelativeLayout) this.findViewById(R.id.layout_area);
        mAreaNameView = (TextView) this.findViewById(R.id.txt_area_name);
        mAreaIndicator = (ImageView) this.findViewById(R.id.img_area_indicator);
        mAreaPanel = (RelativeLayout) this.findViewById(R.id.panel_area);

        mAreaPanel.setOnClickListener(null);

        mAreaContainer = (LinearLayout) this.findViewById(R.id.panel_area_container);

        mXzList = (ListView) this.findViewById(R.id.list_xz);
        mBusList = (ListView) this.findViewById(R.id.list_bus);

        mAreaLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mCatePanel.isShown()) {
                    mCatePanel.setVisibility(View.INVISIBLE);
                }

                if (mRangePanel.isShown()) {
                    mRangePanel.setVisibility(View.INVISIBLE);
                }

                if (mAreaPanel.isShown()) {
                    hideAreaPanel();
                } else {
                    showAreaPanel();
                }
            }

        });

        // init category layout.
        mCateLayout = (RelativeLayout) this.findViewById(R.id.layout_cate);
        mCateNameView = (TextView) this.findViewById(R.id.txt_cate_name);
        mCateIndicator = (ImageView) this.findViewById(R.id.img_cate_indicator);
        mCatePanel = (RelativeLayout) this.findViewById(R.id.panel_category);

        mCatePanel.setOnClickListener(null);

        mCateContainer = (LinearLayout) this.findViewById(R.id.panel_category_container);

        mBigCateListView = (ListView) this.findViewById(R.id.list_big_cate);
        mSmallCateListView = (ListView) this.findViewById(R.id.list_small_cate);

        mCateLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mAreaPanel.isShown()) {
                    mAreaPanel.setVisibility(View.INVISIBLE);
                }
                if (mRangePanel.isShown()) {
                    mRangePanel.setVisibility(View.INVISIBLE);
                }
                if (mCatePanel.isShown()) {
                    hideCatePanel();
                } else {
                    showCatePanel();
                }
            }

        });
    }

    private void initRangeNodeBtn() {
        LinearLayout nodesContainer = (LinearLayout) this.findViewById(R.id.rlt_range_nodes);
        int count = nodesContainer.getChildCount();
        mNodeBtns = new ImageButton[count];
        mNodeNameText = new TextView[count];
        for (int i = 0; i < count; i++) {
            ViewGroup v = (ViewGroup) nodesContainer.getChildAt(i);
            mNodeBtns[i] = (ImageButton) v.getChildAt(0);
            mNodeNameText[i] = (TextView) v.getChildAt(1);
            mNodeBtns[i].setOnClickListener(mNodesClickListener);
        }

        onResponseNodesClick(0);
    }

    private OnClickListener mNodesClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int index = getNodeBtnIndex(v.getId());

            if (index != -1) {
                onResponseNodesClick(index);
                hideRangePanel();

                mLimitIndex = 0;
                DataModel.clearUserList();
                request();
            }
        }
    };

    private void onResponseNodesClick(int index) {
        if (index == -1) {
            return;
        }
        for (int i = 0; i <= index; i++) {
            mNodeBtns[i].setImageResource(R.drawable.btn_range_pressed);
        }

        for (int j = index + 1; j < mNodeBtns.length; j++) {
            mNodeBtns[j].setImageResource(R.drawable.btn_range_normal);
        }

        mRangeNameView.setText(mNodeNameText[index].getText());

        mGpsRange = mRanges[index];
    }

    private int getNodeBtnIndex(int btnId) {
        int index = -1;

        for (int i = 0; i < mNodeBtns.length; i++) {
            if (btnId == mNodeBtns[i].getId()) {
                index = i;
                break;
            }
        }

        return index;
    }

    @SuppressLint("NewApi")
    private void setupActionBar() {
        ActionBar actionbar = this.getActionBar();
        actionbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
        actionbar.setHomeButtonEnabled(true);
        actionbar.setIcon(R.drawable.actionbar_back_selector);
        int flag = actionbar.getDisplayOptions() ^ ActionBar.DISPLAY_SHOW_TITLE;
        actionbar.setDisplayOptions(flag);
        actionbar.setDisplayShowCustomEnabled(true);

        // Set title view.
        View customView = this.getLayoutInflater().inflate(R.layout.actionbar_title_view, null);
        TextView titleView = (TextView) customView.findViewById(R.id.txt_actionbar_title);
        mSearchView = (EditText) customView.findViewById(R.id.search_view);
        String title = "";
        if (mType == TYPE_RECOMMAND) {
            titleView.setVisibility(View.VISIBLE);
            mSearchView.setVisibility(View.GONE);
            title = getResources().getString(R.string.title_user_new);
        } else if (mType == TYPE_POPULATE) {
            titleView.setVisibility(View.VISIBLE);
            mSearchView.setVisibility(View.GONE);
            title = getResources().getString(R.string.title_user_populate);
        } else if (mType == TYPE_SEARCH) {
            title = null;
            titleView.setVisibility(View.GONE);
            mSearchView.setVisibility(View.VISIBLE);
            mSearchView.setText(mKeyword);
            mSearchView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    gotoSearchActivity();
                }

            });
        } else if (mType == TYPE_NEARBY) {
            titleView.setVisibility(View.VISIBLE);
            mSearchView.setVisibility(View.GONE);
            title = getResources().getString(R.string.title_user_nearby);
        } else {
            titleView.setVisibility(View.VISIBLE);
            mSearchView.setVisibility(View.GONE);
            title = getResources().getString(R.string.title_user_info);
        }

        if (title != null) {
            titleView.setText(title);
        }
        actionbar.setCustomView(customView, new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        ActionBar.LayoutParams lp = (ActionBar.LayoutParams) customView.getLayoutParams();
        lp.gravity &= Gravity.CENTER;
        actionbar.setCustomView(customView, lp);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.user_list_actonbar_menu, menu);

        // MenuItem searchItem = menu.findItem(R.id.action_search);
        // mSearchView = (SearchView) searchItem.getActionView();
        //
        // if (mType == TYPE_SEARCH) {
        // searchItem.setVisible(true);
        // // mSearchView.setOnQueryTextListener(this);
        // mSearchView.setIconifiedByDefault(false);
        // Util.custimizeSearchView(mSearchView);
        // } else {
        // searchItem.setVisible(false);
        // }

        MenuItem locationItem = menu.findItem(R.id.menu_item_location);
        if (mType == TYPE_NEARBY) {
            locationItem.setVisible(true);
        } else {
            locationItem.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void setupSpinnerGroup() {
        if (mType == TYPE_RECOMMAND || mType == TYPE_POPULATE) {
            mSpinnerGroup.setVisibility(View.GONE);
        } else {
            setupXzListView();
            setupBusListView();

            setAreaNameView();

            setupBigCateListView();
            setupSmallCateListView();

            setCateNameView();
        }
    }

    // private void location() {
    // mHanlder.removeCallbacks(mLocationRunnable);
    // mHanlder.postDelayed(mLocationRunnable, 300);
    // }
    //
    // private Runnable mLocationRunnable = new Runnable() {
    //
    // @Override
    // public void run() {
    // Location location = getLocation(UserListActivity.this);
    // if (location == null) {
    // requestLocation();
    // } else {
    // getLngAndLat(location);
    // }
    //
    // }
    // };

    private void request() {
        mHanlder.removeCallbacks(mUsersRequestRunnable);
        mHanlder.postDelayed(mUsersRequestRunnable, 300);
    }

    private Runnable mUsersRequestRunnable = new Runnable() {

        @Override
        public void run() {
            if (mType == TYPE_NEARBY) {
                requestNearbyUserList();
            } else {
                requestUserList();
            }
        }
    };

    private void requestNearbyUserList() {
        if (mLoading || mType != TYPE_NEARBY) {
            return;
        }

        HashMap<String, String> arg = new HashMap<String, String>();

        if (mGpsLng >= 0) {
            arg.put(NetUtil.PARAM_NAME_LNG, String.valueOf(mGpsLng));
        }

        if (mGpsLat >= 0) {
            arg.put(NetUtil.PARAM_NAME_LAT, String.valueOf(mGpsLat));
        }

        if (mGpsRange != null && !mGpsRange.isEmpty()) {
            arg.put(NetUtil.PARAM_NAME_LONG, mGpsRange);
        }

        // if (mLimitIndex != -1) {
        // arg.put(NetUtil.PARAM_NAME_LIMIT, String.valueOf(mLimitIndex));
        // arg.put(NetUtil.PARAM_NAME_COUNT, String.valueOf(Const.USER_COUNT));
        // }

        DataUpdater.requestDataUpdate(DataUpdater.DATA_UPDATE_TYPE_USERS, arg);
    }

    private void requestUserList() {
        if (mLoading) {
            return;
        }

        HashMap<String, String> arg = new HashMap<String, String>();

        arg.put(NetUtil.PARAM_NAME_CITY, mCurrentCityId);

        if (mType == TYPE_RECOMMAND) {
            arg.put(NetUtil.PARAM_NAME_TJ, String.valueOf(1));
        } else if (mType == TYPE_POPULATE) {
            arg.put(NetUtil.PARAM_NAME_RQ, String.valueOf(1));
        } else {

            if (mType == TYPE_SEARCH) {
                arg.put(NetUtil.PARAM_NAME_KEYWORD, mKeyword);
            }

            if (mCurXzAreaPos > 0) {
                arg.put(NetUtil.PARAM_NAME_XZ, mXzAreaList.get(mCurXzAreaPos - 1).mId);
            }

            if (mCurBusAreaPos > 0) {
                arg.put(NetUtil.PARAM_NAME_BUS, mBusAreaList.get(mCurBusAreaPos - 1).mId);
            }

            if (mCurSmallCatePos > 0) {
                arg.put(NetUtil.PARAM_NAME_SCATE, mSmallCateList.get(mCurSmallCatePos - 1).mId);
            } else if (mCurBigCatePos > 0) {
                arg.put(NetUtil.PARAM_NAME_BCATE, mBigCateList.get(mCurBigCatePos - 1).mId);
            }
        }

        if (mLimitIndex != -1) {
            arg.put(NetUtil.PARAM_NAME_LIMIT, String.valueOf(mLimitIndex));
            arg.put(NetUtil.PARAM_NAME_COUNT, String.valueOf(Const.USER_COUNT));
        }

        DataUpdater.requestDataUpdate(DataUpdater.DATA_UPDATE_TYPE_USERS, arg);
    }

    private void setupListView() {
        mUserList = DataModel.getUserListItems();

        DataModel.clearUserList();
        if (mType != TYPE_NEARBY) {
            request();
        }

        mUserListView.setEmptyView(findViewById(android.R.id.empty));

        if (mType != TYPE_NEARBY) {
            View foot = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.user_list_footer, null, false);

            mLoadingMoreView = (TextView) foot.findViewById(R.id.txt_loading_more);
            mLoadingMoreBar = (ProgressBar) foot.findViewById(R.id.progressbar_loading);

            mUserListView.addFooterView(foot);

            foot.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mIsLoadingMore = true;
                    request();
                }

            });
        }
        mUserListAdapter = new UserListAdapter(this, mUserList, mHanlder);
        mUserListView.setAdapter(mUserListAdapter);
        mUserListView.setOnItemClickListener(mUserItemClickListener);

    }

    private OnItemClickListener mUserItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
            Log.d(TAG, "mUserItemClickListener: position = " + position);
            if (position >= 0 && position < mUserList.size()) {
                gotoUserIntroduceScreen(position);
            }
        }

    };

    // private void updateWithNewLocation(Location location) {
    // Log.d(TAG, "updateWithNewLocation: location = " + location);
    // if (location == null) {
    // return;
    // }
    //
    // mGpsLng = location.getLongitude();
    // mGpsLat = location.getLatitude();
    // Log.d(TAG, "updateWithNewLocation: mGpsLng = " + mGpsLng + " mGpsLat = "
    // + mGpsLat);
    //
    // StringBuffer sb = new StringBuffer();
    // sb.append(mGpsLng).append(", ").append(mGpsLat);
    // mLngLatView.setText(sb.toString());
    //
    // request();
    // }

    private void updateWithNewLocation(BDLocation location) {
        Log.v(TAG, "updateWithNewLocation: location = " + location);
        if (location == null) {
            return;
        }

        mGpsLng = location.getLongitude();
        mGpsLat = location.getLatitude();
        String locationName = location.getAddrStr();
        Log.v(TAG, "updateWithNewLocation: mGpsLng = " + mGpsLng + " mGpsLat = " + mGpsLat
                + " locationName = " + location);

        StringBuffer sb = new StringBuffer();
        sb.append(mGpsLng).append(", ").append(mGpsLat);
        mLngLatView.setText(sb.toString());
        mLocationNameView.setText(locationName);

        request();
    }

    // private LocationListener mLocationListener = new LocationListener() {
    //
    // @Override
    // public void onLocationChanged(Location location) {
    // // TODO Auto-generated method stub
    // Log.d(TAG, "mLocationListener/onLocationChanged: location = " +
    // location);
    // updateWithNewLocation(location);
    // mLocationManager.removeUpdates(mLocationListener);
    // }
    //
    // @Override
    // public void onProviderDisabled(String provider) {
    // // TODO Auto-generated method stub
    // Log.d(TAG, "mLocationListener/onProviderDisabled: provider = " +
    // provider);
    // updateWithNewLocation(null);
    //
    // }
    //
    // @Override
    // public void onProviderEnabled(String provider) {
    // // TODO Auto-generated method stub
    // Log.d(TAG, "mLocationListener/onProviderEnabled: provider = " +
    // provider);
    //
    // }
    //
    // @Override
    // public void onStatusChanged(String provider, int status, Bundle extras) {
    // // TODO Auto-generated method stub
    // Log.d(TAG, "mLocationListener/onStatusChanged: provider = " + provider);
    //
    // }
    //
    // };

    public Location getLocation(Context context) {
        LocationManager locMan = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        Location location = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        // if (location == null) {
        // location =
        // locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        // }

        return location;
    }

    private void gotoUserIntroduceScreen(int position) {
        Intent intent = new Intent();
        intent.setClass(this, UserIntroduceActivity.class);
        intent.putExtra(Const.EXTRA_USER_ID, mUserList.get(position).mId);
        this.startActivity(intent);
    }

    private void onBack() {
        if (mRangePanel.isShown()) {
            hideRangePanel();
        } else if (mAreaPanel.isShown()) {
            hideAreaPanel();
        } else if (mCatePanel.isShown()) {
            hideCatePanel();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "onOptionsItemSelected: id = " + id);
        switch (id) {
            case android.R.id.home:
                onBack();
                break;
            case R.id.menu_item_location:
                gotoMapActivity();
                break;
            case R.id.action_search:
                gotoSearchActivity();
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        DataUpdater.unregisterDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_AREAE_XZ, this);
        DataUpdater.unregisterDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_AREAE_BUS, this);
        DataUpdater.unregisterDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_BIGCATES, this);
        DataUpdater.unregisterDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_SMALLCATES, this);
        DataUpdater.unregisterDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_USERS, this);
        DataUpdater.unregisterDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_USER_PHOTO, this);

        if (mLocClient != null)
            mLocClient.stop();

        if (mType == TYPE_NEARBY) {
            mLocClient.unRegisterLocationListener(myListener);
        }

        UCommApplication app = (UCommApplication) this.getApplication();
        if (app.mBMapManager != null) {
            app.mBMapManager.destroy();
            app.mBMapManager = null;
        }
        DataModel.clearUserList();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        if (mType == TYPE_NEARBY) {
            // if (mLocationManager != null) {
            // mLocationManager.removeUpdates(mLocationListener);
            // }

            if (mLocClient.isStarted()) {
                mLocClient.stop();
            }
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        if (mType == TYPE_NEARBY) {
            // if (!TextUtils.isEmpty(mProviderName)) {
            // mLocationManager.requestLocationUpdates(mProviderName, 0, 0,
            // mLocationListener);
            // }

            if (!mLocClient.isStarted()) {
                mLocClient.start();
            }
        }
        super.onResume();
    }

    private void setBigCateNameList() {
        mBigCateList = null;
        mBigCateList = DataModel.getBigCatesListItems();
        if (mBigCateList != null) {
            if (mBigCateNameList == null) {
                mBigCateNameList = new ArrayList<PanelListItem>();
            } else {
                mBigCateNameList.clear();
            }
            PanelListItem pli = new PanelListItem(PanelListItem.TYPE_LEFT, getResources()
                    .getString(R.string.all_category), false);
            mBigCateNameList.add(pli);
            for (BigCategory bigCate : mBigCateList) {
                pli = new PanelListItem(PanelListItem.TYPE_LEFT, bigCate.mName, false);
                mBigCateNameList.add(pli);
            }
        }
    }

    private void filterUserBySmallCateId(String smallCateId) {
        ArrayList<User> userList = DataModel.getUserListItems();
        if (userList == null) {
            return;
        }

        mUserList = new ArrayList<User>();
        for (User user : userList) {
            if (user.mSmallCateId.equals(smallCateId)) {
                mUserList.add(user);
            }
        }
    }

    private void filterUserByBigCateId(String bigCateId) {
        ArrayList<User> userList = DataModel.getUserListItems();
        if (userList == null) {
            return;
        }

        mUserList = new ArrayList<User>();
        for (User user : userList) {
            if (user.mBigCateId.equals(bigCateId)) {
                mUserList.add(user);
            }
        }
    }

    private void setUserList() {
        mUserList = null;

        if (mType == TYPE_NEARBY) {
            if (mCurSmallCatePos > 0) {
                filterUserBySmallCateId(mCurSmallCateId);
            } else if (mCurBigCatePos > 0) {
                filterUserByBigCateId(mCurBigCateId);
            } else {
                mUserList = DataModel.getUserListItems();
            }

        } else {
            mUserList = DataModel.getUserListItems();
        }
        mUserListAdapter.setUserList(mUserList);
    }

    private void updateUserListView(int status) {
        Log.d(TAG, "updateUserListView: status = " + status);
        switch (status) {
            case DataUpdater.DATA_UPDATE_STATUS_LOADING:
                if (mUserList == null || mUserList.size() == 0) {
                    mUserListView.setVisibility(View.INVISIBLE);
                }
                mLoading = true;
                if (mIsLoadingMore) {
                    mLoadingMoreView.setVisibility(View.GONE);
                    mLoadingMoreBar.setVisibility(View.VISIBLE);
                } else {
                    mLoadingBar.setVisibility(View.VISIBLE);
                }
                break;
            case DataUpdater.DATA_UPDATE_STATUS_READY:
                if (mIsLoadingMore) {
                    mLoadingMoreView.setVisibility(View.VISIBLE);
                    mLoadingMoreBar.setVisibility(View.GONE);
                } else {
                    mLoadingBar.setVisibility(View.GONE);
                }
                setUserList();

                if (mUserList == null || mUserList.size() == 0) {

                    // mUserListView.setVisibility(View.INVISIBLE);
                } else {
                    mLimitIndex += Const.USER_COUNT;
                    mUserListView.setVisibility(View.VISIBLE);
                }

                mIsLoadingMore = false;
                mLoading = false;
                mUserListAdapter.notifyDataSetChanged();
                break;
            case DataUpdater.DATA_UPDATE_STATUS_ERROR:
                if (mIsLoadingMore) {
                    mLoadingMoreView.setVisibility(View.VISIBLE);
                    mLoadingMoreBar.setVisibility(View.GONE);
                } else {
                    mLoadingBar.setVisibility(View.GONE);
                }
                mIsLoadingMore = false;
                mLoading = false;
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
                        case DataUpdater.DATA_UPDATE_TYPE_AREAE_XZ:
                            // updateSpinnerLeft(status);
                            updateXzAreaList(status);
                            break;
                        case DataUpdater.DATA_UPDATE_TYPE_AREAE_BUS:
                            updateBusAreaList(status);
                            break;
                        case DataUpdater.DATA_UPDATE_TYPE_BIGCATES:
                            // updateSpinnerMiddle(status);
                            updateBitCateList(status);
                            break;
                        case DataUpdater.DATA_UPDATE_TYPE_SMALLCATES:
                            // updateSpinnerMiddle(status);
                            updateSmallCateList(status);
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

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        mKeyword = intent.getStringExtra(Const.EXTRA_KEYWORD);
        mSearchView.setText(mKeyword);
        mLimitIndex = 0;
        DataModel.clearUserList();
        if (mType != TYPE_NEARBY) {
            request();
        }
        super.onNewIntent(intent);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // TODO Auto-generated method stub
        return false;
    }

    private void gotoSearchActivity() {
        if (mCurrentCityId == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this, SearchActivity.class);
        intent.putExtra(Const.EXTRA_CITY_ID, mCurrentCityId);
        this.startActivity(intent);
    }

    private void gotoMapActivity() {
        Intent intent = new Intent(this, UComMapActivity.class);
        this.startActivity(intent);
    }

    private void setXzAreaNameList() {
        if (mCurrentCityId == null) {
            return;
        }
        mXzAreaList = null;
        mXzAreaList = DataModel.getXzAreaListItems(Integer.valueOf(mCurrentCityId));
        if (mXzAreaList != null) {
            if (mXzAreaNameList == null) {
                mXzAreaNameList = new ArrayList<PanelListItem>();
            } else {
                mXzAreaNameList.clear();
            }

            PanelListItem pli = new PanelListItem(PanelListItem.TYPE_LEFT, getResources()
                    .getString(R.string.all_area), false);

            mXzAreaNameList.add(pli);
            for (Area area : mXzAreaList) {
                pli = new PanelListItem(PanelListItem.TYPE_LEFT, area.mName, false);
                mXzAreaNameList.add(pli);
            }
        }
    }

    private void updateXzAreaList(int status) {
        Log.d(TAG, "updateXzAreaList: status = " + status);
        if (mXzAreaAdapter == null) {
            Log.e(TAG, "updateXzAreaList: mXzAreaAdapter is null.");
            return;
        }

        switch (status) {
            case DataUpdater.DATA_UPDATE_STATUS_LOADING:
                mLoadingBar.setVisibility(View.VISIBLE);
                break;
            case DataUpdater.DATA_UPDATE_STATUS_READY:
                mLoadingBar.setVisibility(View.GONE);
                setXzAreaNameList();
                mXzAreaAdapter.notifyDataSetChanged();
                break;
            case DataUpdater.DATA_UPDATE_STATUS_ERROR:
                mLoadingBar.setVisibility(View.GONE);
                break;
        }
    }

    private void setBusAreaNameList() {
        mBusAreaList = null;
        if (mCurrentXzId != null) {
            mBusAreaList = DataModel.getBusAreaListItems(Integer.valueOf(mCurrentXzId));
        }

        if (mBusAreaNameList == null) {
            mBusAreaNameList = new ArrayList<PanelListItem>();
        } else {
            mBusAreaNameList.clear();
        }
        String allBus = this.getResources().getString(R.string.all_bus);
        if (mBusAreaList != null) {
            Area xzSelected = DataModel
                    .getXzAreaById(Integer.valueOf(mCurrentCityId), mCurrentXzId);

            if (xzSelected != null) {
                allBus = String.format(allBus, xzSelected.mName);
            } else {
                allBus = String.format(allBus, "");
            }
        } else {
            allBus = this.getResources().getString(R.string.all_area);
        }

        PanelListItem pli = new PanelListItem(PanelListItem.TYPE_RIGHT, allBus, false);
        mBusAreaNameList.add(pli);

        if (mBusAreaList != null) {
            for (Area area : mBusAreaList) {
                pli = new PanelListItem(PanelListItem.TYPE_RIGHT, area.mName, false);
                mBusAreaNameList.add(pli);
            }
        }
    }

    private void setSmallCateNameList() {
        if (mCurBigCatePos < 0) {
            return;
        }

        mSmallCateList = null;
        if (mCurBigCateId != null) {
            mSmallCateList = DataModel.getSmallCateListItems(Integer.valueOf(mCurBigCateId));
        }

        if (mSmallCateNameList == null) {
            mSmallCateNameList = new ArrayList<PanelListItem>();
        } else {
            mSmallCateNameList.clear();
        }
        String allCate = this.getResources().getString(R.string.all_category);
        PanelListItem pli = new PanelListItem(PanelListItem.TYPE_RIGHT, allCate, false);
        mSmallCateNameList.add(pli);
        if (mSmallCateList != null) {
            for (SmallCategory cate : mSmallCateList) {
                pli = new PanelListItem(PanelListItem.TYPE_RIGHT, cate.mName, false);
                mSmallCateNameList.add(pli);
            }
        }
    }

    private void updateBusAreaList(int status) {
        Log.d(TAG, "updateBusAreaList: status = " + status);
        if (mBusAreaAdapter == null) {
            Log.e(TAG, "updateBusAreaList: mBusAreaAdapter is null.");
            return;
        }
        switch (status) {
            case DataUpdater.DATA_UPDATE_STATUS_LOADING:
                mLoadingBar.setVisibility(View.VISIBLE);
                break;
            case DataUpdater.DATA_UPDATE_STATUS_READY:
                mLoadingBar.setVisibility(View.GONE);
                setBusAreaNameList();
                mBusAreaAdapter.notifyDataSetChanged();
                break;
            case DataUpdater.DATA_UPDATE_STATUS_ERROR:
                mLoadingBar.setVisibility(View.GONE);
                break;
        }
    }

    private AnimationListener mAnimationListener = new AnimationListener() {
        @Override
        public void onAnimationEnd(Animation ani) {
            if (ani.equals(mAnimAreaPanelShow)) {
                mAreaPanel.setVisibility(View.VISIBLE);
                mAreaPanelAnimating = false;
            } else if (ani.equals(mAnimCatePanelShow)) {
                mCatePanel.setVisibility(View.VISIBLE);
                mCatePanelAnimating = false;
            } else if (ani.equals(mAnimRangePanelShow)) {
                mRangePanel.setVisibility(View.VISIBLE);
                mRangePanelAnimating = false;
            } else if (ani.equals(mAnimAreaPanelHide)) {
                mAreaPanel.setVisibility(View.INVISIBLE);
                mAreaPanelAnimating = false;
            } else if (ani.equals(mAnimCatePanelHide)) {
                mCatePanel.setVisibility(View.INVISIBLE);
                mCatePanelAnimating = false;
            } else if (ani.equals(mAnimRangePanelHide)) {
                mRangePanel.setVisibility(View.INVISIBLE);
                mRangePanelAnimating = false;
            }
        }

        @Override
        public void onAnimationRepeat(Animation arg0) {

        }

        @Override
        public void onAnimationStart(Animation arg0) {
        }
    };

    private void initAreaAnimation() {
        if (mAnimAreaPanelShow == null) {
            mAnimAreaPanelShow = AnimationUtils.loadAnimation(this, R.anim.dropdown_activity_topin);

            mAnimAreaPanelShow.setAnimationListener(mAnimationListener);
        }

        if (mAnimAreaPanelHide == null) {
            mAnimAreaPanelHide = AnimationUtils
                    .loadAnimation(this, R.anim.dropdown_activity_topout);

            mAnimAreaPanelHide.setAnimationListener(mAnimationListener);
        }
    }

    private void initCateAnimation() {
        if (mAnimCatePanelShow == null) {
            mAnimCatePanelShow = AnimationUtils.loadAnimation(this, R.anim.dropdown_activity_topin);

            mAnimCatePanelShow.setAnimationListener(mAnimationListener);
        }

        if (mAnimCatePanelHide == null) {
            mAnimCatePanelHide = AnimationUtils
                    .loadAnimation(this, R.anim.dropdown_activity_topout);

            mAnimCatePanelHide.setAnimationListener(mAnimationListener);
        }
    }

    private void initRangeAnimation() {
        if (mAnimRangePanelShow == null) {
            mAnimRangePanelShow = AnimationUtils
                    .loadAnimation(this, R.anim.dropdown_activity_topin);

            mAnimRangePanelShow.setAnimationListener(mAnimationListener);
        }

        if (mAnimRangePanelHide == null) {
            mAnimRangePanelHide = AnimationUtils.loadAnimation(this,
                    R.anim.dropdown_activity_topout);

            mAnimRangePanelHide.setAnimationListener(mAnimationListener);
        }
    }

    private void initAnimation() {
        initRangeAnimation();
        initAreaAnimation();
        initCateAnimation();
    }

    private void showRangePanel() {
        mRangeIndicator.setImageResource(R.drawable.ic_arrow_up);

        if (mRangePanelAnimating) {
            return;
        }

        mRangePanel.clearAnimation();
        mRangePanel.startAnimation(mAnimRangePanelShow);
        mRangePanel.postInvalidate();
    }

    private void hideRangePanel() {
        mRangeIndicator.setImageResource(R.drawable.ic_arrow_down);

        if (mRangePanelAnimating) {
            return;
        }

        mRangePanel.clearAnimation();
        mRangePanel.startAnimation(mAnimRangePanelHide);
        mRangePanel.postInvalidate();
    }

    private void showAreaPanel() {
        mAreaIndicator.setImageResource(R.drawable.ic_arrow_up);

        if (mAreaPanelAnimating) {
            return;
        }

        mAreaPanel.clearAnimation();
        mAreaPanel.startAnimation(mAnimAreaPanelShow);
        mAreaPanel.postInvalidate();
    }

    private void hideAreaPanel() {
        mAreaIndicator.setImageResource(R.drawable.ic_arrow_down);

        if (mAreaPanelAnimating) {
            return;
        }

        mAreaPanel.clearAnimation();
        mAreaPanel.startAnimation(mAnimAreaPanelHide);
        mAreaPanel.postInvalidate();
    }

    private void showCatePanel() {
        mCateIndicator.setImageResource(R.drawable.ic_arrow_up);

        if (mCatePanelAnimating) {
            return;
        }

        mCatePanel.clearAnimation();
        mCatePanel.startAnimation(mAnimCatePanelShow);
        mCatePanel.postInvalidate();
    }

    private void hideCatePanel() {
        mCateIndicator.setImageResource(R.drawable.ic_arrow_down);

        if (mCatePanelAnimating) {
            return;
        }

        mCatePanel.clearAnimation();
        mCatePanel.startAnimation(mAnimCatePanelHide);
        mCatePanel.postInvalidate();
    }

    private void setupXzListView() {
        setXzAreaNameList();

        mXzAreaAdapter = new PanelListAdapter(this, mXzAreaNameList, mHanlder);

        mXzList.setAdapter(mXzAreaAdapter);

        mXzList.setSelection(mCurXzAreaPos);
        mXzAreaNameList.get(mCurXzAreaPos).mSelected = true;

        if (mCurXzAreaPos > 0) {
            mCurrentXzId = mXzAreaList.get(mCurXzAreaPos - 1).mId;
        }
        mXzList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                if (mXzAreaNameList == null || mXzAreaNameList.isEmpty() || position < 0
                        || position >= mXzAreaNameList.size()) {
                    return;
                }

                mPreXzAreaPos = mCurXzAreaPos;
                mCurXzAreaPos = position;
                mXzAreaNameList.get(mPreXzAreaPos).mSelected = false;
                mXzAreaNameList.get(mCurXzAreaPos).mSelected = true;
                mXzAreaAdapter.notifyDataSetChanged();

                mCurBusAreaPos = 0;

                if (position == 0) {
                    mBusAreaList = null;

                    if (!mBusAreaNameList.isEmpty()) {
                        mBusAreaNameList.clear();
                    }

                    String name = UserListActivity.this.getResources().getString(R.string.all_area);
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

    private void setAreaNameView() {
        if (mCurXzAreaPos == 0 && mCurBusAreaPos == 0) {
            mAreaNameView.setText(getResources().getText(R.string.all_area));
        } else if (mCurBusAreaPos == 0) {
            mAreaNameView.setText(mXzAreaNameList.get(mCurXzAreaPos).mName);
        } else {
            mAreaNameView.setText(mBusAreaNameList.get(mCurBusAreaPos).mName);
        }
    }

    private void setCateNameView() {
        if (mCurBigCatePos == 0 && mCurSmallCatePos == 0) {
            mCateNameView.setText(getResources().getText(R.string.all_category));
        } else if (mCurSmallCatePos == 0) {
            mCateNameView.setText(mBigCateNameList.get(mCurBigCatePos).mName);
        } else {
            mCateNameView.setText(mSmallCateNameList.get(mCurSmallCatePos).mName);
        }
    }

    private void setupBusListView() {
        setBusAreaNameList();

        mBusAreaAdapter = new PanelListAdapter(this, mBusAreaNameList, mHanlder);

        mBusList.setAdapter(mBusAreaAdapter);

        mBusList.setSelection(mCurBusAreaPos);
        mBusAreaNameList.get(mCurBusAreaPos).mSelected = true;

        mBusList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                if (mBusAreaNameList == null || mBusAreaNameList.isEmpty() || position < 0
                        || position >= mBusAreaNameList.size()) {
                    return;
                }
                mPreBusAreaPos = mCurBusAreaPos;
                mCurBusAreaPos = position;
                mBusAreaNameList.get(mPreBusAreaPos).mSelected = false;
                mBusAreaNameList.get(mCurBusAreaPos).mSelected = true;
                mBusAreaAdapter.notifyDataSetChanged();
                if (position > 0) {
                    mCurrentBusId = mBusAreaList.get(position - 1).mId;
                }

                setAreaNameView();
                hideAreaPanel();

                mLimitIndex = 0;
                DataModel.clearUserList();
                request();
            }

        });
    }

    private void setupBigCateListView() {
        setBigCateNameList();

        mBigCateAdapter = new PanelListAdapter(this, mBigCateNameList, mHanlder);

        mBigCateListView.setAdapter(mBigCateAdapter);

        mBigCateListView.setSelection(mCurBigCatePos);
        mBigCateNameList.get(mCurBigCatePos).mSelected = true;

        if (mCurBigCatePos > 0) {
            mCurBigCateId = mBigCateList.get(mCurBigCatePos - 1).mId;
        }
        mBigCateListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                if (mBigCateNameList == null || mBigCateNameList.isEmpty() || position < 0
                        || position >= mBigCateNameList.size()) {
                    return;
                }

                mPreBigCatePos = mCurBigCatePos;
                mCurBigCatePos = position;
                mBigCateNameList.get(mPreBigCatePos).mSelected = false;
                mBigCateNameList.get(mCurBigCatePos).mSelected = true;
                mBigCateAdapter.notifyDataSetChanged();

                mCurSmallCatePos = 0;

                if (position == 0) {
                    mSmallCateList = null;

                    if (!mSmallCateNameList.isEmpty()) {
                        mSmallCateNameList.clear();
                    }

                    String name = UserListActivity.this.getResources().getString(
                            R.string.all_category);
                    PanelListItem pli = new PanelListItem(PanelListItem.TYPE_RIGHT, name, false);
                    mSmallCateNameList.add(pli);
                    mSmallCateAdapter.notifyDataSetChanged();
                } else if (position > 0) {
                    mCurBigCateId = mBigCateList.get(position - 1).mId;
                    updateSmallCateList(DataUpdater.DATA_UPDATE_STATUS_READY);
                }

            }

        });
    }

    private void setupSmallCateListView() {
        setSmallCateNameList();

        mSmallCateAdapter = new PanelListAdapter(this, mSmallCateNameList, mHanlder);

        mSmallCateListView.setAdapter(mSmallCateAdapter);

        mSmallCateListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                if (mSmallCateNameList == null || mSmallCateNameList.isEmpty() || position < 0
                        || position >= mSmallCateNameList.size()) {
                    return;
                }
                mPreSmallCatePos = mCurSmallCatePos;
                mCurSmallCatePos = position;
                mSmallCateNameList.get(mPreSmallCatePos).mSelected = false;
                mSmallCateNameList.get(mCurSmallCatePos).mSelected = true;
                mSmallCateAdapter.notifyDataSetChanged();

                if (position > 0) {
                    mCurSmallCateId = mSmallCateList.get(position - 1).mId;
                }

                setCateNameView();
                hideCatePanel();

                mLimitIndex = 0;
                DataModel.clearUserList();
                request();
            }
        });
    }

    private void updateBitCateList(int status) {
        Log.d(TAG, "updateBitCateList: status = " + status);
        if (mBigCateAdapter == null) {
            Log.e(TAG, "updateBitCateList: mBigCateAdapter is null.");
            return;
        }
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

    private void updateSmallCateList(int status) {
        Log.d(TAG, "updateSmallCateList: status = " + status);
        if (mSmallCateAdapter == null) {
            Log.e(TAG, "updateSmallCateList: mSmallCateAdapter is null.");
            return;
        }
        switch (status) {
            case DataUpdater.DATA_UPDATE_STATUS_LOADING:
                mLoadingBar.setVisibility(View.VISIBLE);
                break;
            case DataUpdater.DATA_UPDATE_STATUS_READY:
                mLoadingBar.setVisibility(View.GONE);
                setSmallCateNameList();
                mSmallCateAdapter.notifyDataSetChanged();
                break;
            case DataUpdater.DATA_UPDATE_STATUS_ERROR:
                mLoadingBar.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.d(TAG, "MyLocationListenner/onReceiveLocation: location = " + location);
            if (location == null)
                return;

            updateWithNewLocation(location);

            // locData.latitude = location.getLatitude();
            // locData.longitude = location.getLongitude();
            // locData.accuracy = location.getRadius();
            // locData.direction = location.getDerect();
            // myLocationOverlay.setData(locData);
            // mMapView.refresh();
            // mMapController.animateTo(new GeoPoint((int) (locData.latitude *
            // 1e6),
            // (int) (locData.longitude * 1e6)), mHandler.obtainMessage(1));
        }

        public void onReceivePoi(BDLocation poiLocation) {
            Log.d(TAG, "MyLocationListenner/onReceivePoi: poiLocation = " + poiLocation);
            if (poiLocation == null) {
                return;
            }
        }
    }

}
