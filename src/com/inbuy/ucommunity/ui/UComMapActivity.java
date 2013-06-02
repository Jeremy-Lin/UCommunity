
package com.inbuy.ucommunity.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.inbuy.ucommunity.R;
import com.inbuy.ucommunity.data.User;
import com.inbuy.ucommunity.engine.DataModel;
import com.inbuy.ucommunity.engine.DataUpdateListener;
import com.inbuy.ucommunity.engine.DataUpdater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UComMapActivity extends Activity implements DataUpdateListener {
    private static final String TAG = "UComMapActivity";

    public static final String EXTRA_LOCATION_LAT = "extra_location_lat";
    public static final String EXTRA_LOCATION_LON = "extra_location_lon";
    public static final String EXTRA_LOCATION_RANGE = "extra_location_range";

    static MapView mMapView = null;

    private MapController mMapController = null;

    FrameLayout mMapViewContainer = null;

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    public NotifyLister mNotifyer = null;

    LinearLayout mMapModeLayout = null;

    MyLocationOverlay myLocationOverlay = null;
    LocationData locData = null;

    ArrayList<User> mUserList;

    private double mLatitude;
    private double mLongitude;
    private int mRange;

    OverlayTest ov = null;

    public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Toast.makeText(UComMapActivity.this, "msg:" + msg.what, Toast.LENGTH_SHORT).show();
        };
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UCommApplication app = (UCommApplication) this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            app.mBMapManager
                    .init(UCommApplication.strKey, new UCommApplication.MyGeneralListener());
        }

        setContentView(R.layout.activity_map);

        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_USERS, this);

        getIntentData();
        setupActionBar();
        initOverlayData();
        initMapView();
        initLocationClient();

        mMapModeLayout = (LinearLayout) findViewById(R.id.map_mode);
        OnClickListener clickListener = new OnClickListener() {
            public void onClick(View v) {
                UComMapActivity.this.finish();
            }
        };
        mMapModeLayout.setOnClickListener(clickListener);
    }

    private void getIntentData() {
        mLongitude = getIntent().getDoubleExtra(EXTRA_LOCATION_LON, -1);
        mLatitude = getIntent().getDoubleExtra(EXTRA_LOCATION_LAT, -1);
        mRange = getIntent().getIntExtra(EXTRA_LOCATION_RANGE, -1);
    }

    private void initOverlayData() {
        mUserList = DataModel.getUserListItems();
        if (mUserList == null) {
            return;
        }

        mGeoList.clear();

        for (User user : mUserList) {
            double lat = Double.valueOf(user.mLat);
            double lon = Double.valueOf(user.mLng);
            OverlayItem item = new OverlayItem(new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6)),
                    user.mName, user.mAddress);
            item.setMarker(getResources().getDrawable(R.drawable.icon_marka));
            mGeoList.add(item);
        }
    }

    private void initLocationClient() {
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(500);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

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
        String title = "";
        titleView.setVisibility(View.VISIBLE);
        title = getResources().getString(R.string.title_user_nearby);

        if (title != null) {
            titleView.setText(title);
        }
        actionbar.setCustomView(customView, new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        ActionBar.LayoutParams lp = (ActionBar.LayoutParams) customView.getLayoutParams();
        lp.gravity &= Gravity.CENTER;
        actionbar.setCustomView(customView, lp);
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        DataUpdater.unregisterDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_USERS, this);

        if (mLocClient != null) {
            mLocClient.stop();
        }
        mMapView.destroy();
        UCommApplication app = (UCommApplication) this.getApplication();
        if (app.mBMapManager != null) {
            app.mBMapManager.destroy();
            app.mBMapManager = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "onOptionsItemSelected: id = " + id);
        switch (id) {
            case android.R.id.home:
                UComMapActivity.this.finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMapView.onRestoreInstanceState(savedInstanceState);
    }

    private void initMapView() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapController = mMapView.getController();
        mMapView.setLongClickable(true);
        // mMapController.setMapClickEnable(true);
        // mMapView.setSatellite(false);

        mMapView.getController().setZoom(14);
        mMapView.getController().enableClick(true);

        mMapView.setBuiltInZoomControls(true);
        mMapView.regMapViewListener(UCommApplication.getInstance().mBMapManager, mMapListener);
        myLocationOverlay = new MyLocationOverlay(mMapView);
        locData = new LocationData();
        myLocationOverlay.setData(locData);
        mMapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableCompass();

        Drawable marker = this.getResources().getDrawable(R.drawable.icon_marka);
        ov = new OverlayTest(marker, this, mMapView);

        if (mGeoList != null && mGeoList.size() > 0) {
            ov.addItem(mGeoList);
        }

        mMapView.getOverlays().add(ov);
        mMapView.refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public MKMapViewListener mMapListener = new MKMapViewListener() {

        @Override
        public void onMapMoveFinish() {
            // TODO Auto-generated method stub
        }

        @Override
        public void onClickMapPoi(MapPoi mapPoiInfo) {
            // TODO Auto-generated method stub
            String title = "";
            if (mapPoiInfo != null) {
                title = mapPoiInfo.strText;
                Toast.makeText(UComMapActivity.this, title, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onGetCurrentMap(Bitmap b) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onMapAnimationFinish() {
            // TODO Auto-generated method stub

        }
    };

    /**
     * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;

            locData.latitude = location.getLatitude();
            locData.longitude = location.getLongitude();
            locData.accuracy = location.getRadius();
            locData.direction = location.getDerect();
            myLocationOverlay.setData(locData);
            mMapView.refresh();
            mMapController.animateTo(new GeoPoint((int) (locData.latitude * 1e6),
                    (int) (locData.longitude * 1e6)), mHandler.obtainMessage(1));
        }

        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null) {
                return;
            }
        }
    }

    public class NotifyLister extends BDNotifyListener {
        public void onNotify(BDLocation mlocation, float distance) {
        }
    }

    @Override
    public void onUpdate(int status, int dataType, int id, Object arg) {
        // TODO Auto-generated method stub

    }
}

class OverlayTest extends ItemizedOverlay<OverlayItem> {
    public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
    private Context mContext = null;
    static PopupOverlay pop = null;

    Toast mToast = null;

    public OverlayTest(Drawable marker, Context context, MapView mapView) {
        super(marker, mapView);
        this.mContext = context;
        pop = new PopupOverlay(UComMapActivity.mMapView, new PopupClickListener() {

            @Override
            public void onClickedPopup(int index) {
                if (null == mToast)
                    mToast = Toast.makeText(mContext, "popup item :" + index + " is clicked.",
                            Toast.LENGTH_SHORT);
                else
                    mToast.setText("popup item :" + index + " is clicked.");
                mToast.show();
            }
        });
    }

    protected boolean onTap(int index) {
        System.out.println("item onTap: " + index);

        Bitmap[] bmps = new Bitmap[3];
        if (index % 2 == 0) {
            try {
                bmps[0] = BitmapFactory.decodeStream(mContext.getAssets().open("marker1.png"));
                bmps[1] = BitmapFactory.decodeStream(mContext.getAssets().open("marker2.png"));
                bmps[2] = BitmapFactory.decodeStream(mContext.getAssets().open("marker3.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                bmps[2] = BitmapFactory.decodeStream(mContext.getAssets().open("marker1.png"));
                bmps[1] = BitmapFactory.decodeStream(mContext.getAssets().open("marker2.png"));
                bmps[0] = BitmapFactory.decodeStream(mContext.getAssets().open("marker3.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        pop.showPopup(bmps, getItem(index).getPoint(), 32);
        if (null == mToast)
            mToast = Toast.makeText(mContext, getItem(index).getTitle(), Toast.LENGTH_SHORT);
        else
            mToast.setText(getItem(index).getTitle());
        mToast.show();

        return true;
    }

    public boolean onTap(GeoPoint pt, MapView mapView) {
        if (pop != null) {
            pop.hidePop();
        }
        super.onTap(pt, mapView);
        return false;
    }
}
