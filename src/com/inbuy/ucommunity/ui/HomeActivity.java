
package com.inbuy.ucommunity.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inbuy.ucommunity.R;
import com.inbuy.ucommunity.data.Category;
import com.inbuy.ucommunity.data.City;
import com.inbuy.ucommunity.engine.DataModel;
import com.inbuy.ucommunity.engine.DataUpdateListener;
import com.inbuy.ucommunity.engine.DataUpdater;
import com.inbuy.ucommunity.util.Const;
import com.inbuy.ucommunity.util.Util;

import java.util.ArrayList;

public class HomeActivity extends Activity implements DataUpdateListener {
    private static final String TAG = "HomeActivity";
    private static final int MSG_DATA_UPDATE = 0;
    private static final int MSG_CITY_CHANGED = 1;

    private EditText mSearchView;
    private GridView mGridView;
    private HomeGridAdapter mAdapter;
    private ArrayList<City> mCityList;
    private City mCurrentCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_CITIES, this);

        mCityList = DataModel.getCityListItems();

        setupActionbar();

        initViewsRes();
        initGridView();
    }

    private void setupActionbar() {
        ActionBar actionbar = this.getActionBar();
        // actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        int flag = actionbar.getDisplayOptions() ^ ActionBar.DISPLAY_SHOW_HOME
                ^ ActionBar.DISPLAY_SHOW_TITLE;
        actionbar.setDisplayOptions(flag);
        setupActionbarSpinner();
    }

    private void setupActionbarSpinner() {
        if (mCityList == null) {
            return;
        }

        View mCustomView = this.getLayoutInflater().inflate(R.layout.home_actionbar_spinner, null);

        ActionBar actionbar = this.getActionBar();

        actionbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));

        actionbar.setCustomView(mCustomView, new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        ActionBar.LayoutParams lp = (ActionBar.LayoutParams) mCustomView.getLayoutParams();
        lp.gravity &= Gravity.CENTER;
        actionbar.setCustomView(mCustomView, lp);

        mCustomView.setOnClickListener(mActionbarTitleListener);

        actionbar.setDisplayShowCustomEnabled(true);

        int size = mCityList.size();
        String titleFormat = getResources().getString(R.string.home_title);
        String name = "";
        if (mCityList != null && size != 0) {
            mCurrentCity = mCityList.get(0);

            String[] cities = new String[size];
            for (int i = 0; i < size; i++) {
                name = ((City) mCityList.get(i)).getmName();
                cities[i] = String.format(titleFormat, name);
            }

            TextView titleView = (TextView) mCustomView.findViewById(R.id.txt_city_title);

            titleView.setText(cities[0]);

            ImageView downView = (ImageView) mCustomView.findViewById(R.id.img_down);
            downView.setOnClickListener(mActionbarTitleListener);

            // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            // R.layout.home_actionbar_spinner_item, cities);

            // Spinner spinner = (Spinner)
            // mCustomView.findViewById(R.id.spinner);
            // spinner.setAdapter(adapter);
            //
            // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //
            // spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            //
            // @Override
            // public void onItemSelected(AdapterView<?> arg0, View arg1, int
            // position, long arg3) {
            // Log.d(TAG, "OnItemSelectedListener: onItemSelected position = " +
            // position);
            // if (position >= mCityList.size() || position < 0) {
            // Log.e(TAG, "onItemSelected: itemPosition is " + position
            // + ", out of bound.");
            // return;
            // }
            // mCurrentCity = mCityList.get(position);
            // }
            //
            // @Override
            // public void onNothingSelected(AdapterView<?> arg0) {
            // // TODO Auto-generated method stub
            //
            // }
            //
            // });

        }
    }

    private OnClickListener mActionbarTitleListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Toast.makeText(HomeActivity.this, "Go to city selector page.", Toast.LENGTH_SHORT)
                    .show();

        }

    };

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        Util.deleteAllFiles(Util.getDataPhotoPath(), null);
        Util.deleteAllFiles(Util.getDataPhotoPath(), Util.WINDATA_DOWNLOAD_SUFFIX);
        super.onDestroy();
    }

    private void initViewsRes() {
        mSearchView = (EditText) this.findViewById(R.id.edit_search_user);
        mSearchView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.equals(mSearchView)) {
                    gotoSearchActivity();
                }
            }

        });

        mGridView = (GridView) this.findViewById(R.id.grid_categories);

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                switch (position) {
                    case 0:
                        gotoBigCategoryActivity();
                        break;
                    case 1:
                        gotoNearbyActivity();
                        break;
                    case 2:
                        gotoSearchActivity();
                        break;
                    case 3:
                        gotoRecommandActivity();
                        break;
                    case 4:
                        gotoPopulateActivity();
                        break;
                    case 5:
                        gotoAboutActivity();
                        break;
                    default:
                        break;
                }

            }

        });
    }

    private void gotoNearbyActivity() {
        if (mCurrentCity == null) {
            return;
        }

        if (!Util.isNetworkEnabled(this)) {
            Toast.makeText(this, getResources().getString(R.string.error_network),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Util.isGPSEnabled(this)) {
            Toast.makeText(this, getResources().getString(R.string.error_gps), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        Intent intent = new Intent();
        intent.setClass(this, UserListActivity.class);
        intent.putExtra(Const.EXTRA_CITY_ID, mCurrentCity.getmId());
        intent.putExtra(UserListActivity.EXTRA_TYPE, UserListActivity.TYPE_NEARBY);
        this.startActivity(intent);
    }

    private void gotoBigCategoryActivity() {
        if (mCurrentCity == null) {
            return;
        }

        if (!Util.isNetworkEnabled(this)) {
            Toast.makeText(this, getResources().getString(R.string.error_network),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.setClass(this, BigCategoryActivity.class);
        intent.putExtra(Const.EXTRA_CITY_ID, mCurrentCity.getmId());
        this.startActivity(intent);
    }

    private void gotoRecommandActivity() {
        if (mCurrentCity == null) {
            return;
        }

        if (!Util.isNetworkEnabled(this)) {
            Toast.makeText(this, getResources().getString(R.string.error_network),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.setClass(this, UserListActivity.class);
        intent.putExtra(Const.EXTRA_CITY_ID, mCurrentCity.getmId());
        intent.putExtra(UserListActivity.EXTRA_TYPE, UserListActivity.TYPE_RECOMMAND);
        this.startActivity(intent);
    }

    private void gotoPopulateActivity() {
        if (mCurrentCity == null) {
            return;
        }

        if (!Util.isNetworkEnabled(this)) {
            Toast.makeText(this, getResources().getString(R.string.error_network),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.setClass(this, UserListActivity.class);
        intent.putExtra(Const.EXTRA_CITY_ID, mCurrentCity.getmId());
        intent.putExtra(UserListActivity.EXTRA_TYPE, UserListActivity.TYPE_POPULATE);
        this.startActivity(intent);
    }

    private void gotoSearchActivity() {
        if (mCurrentCity == null) {
            return;
        }

        if (!Util.isNetworkEnabled(this)) {
            Toast.makeText(this, getResources().getString(R.string.error_network),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.setClass(this, SearchActivity.class);
        intent.putExtra(Const.EXTRA_CITY_ID, mCurrentCity.getmId());
        this.startActivity(intent);
    }

    private void gotoAboutActivity() {

        if (!Util.isNetworkEnabled(this)) {
            Toast.makeText(this, getResources().getString(R.string.error_network),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.setClass(this, AboutActivity.class);
        this.startActivity(intent);
    }

    private ArrayList<Category> getCategoryData() {
        ArrayList<Category> categories = new ArrayList<Category>();
        int[] nameIds = {
                R.string.all_merchant, R.string.nearby_merchant, R.string.search,
                R.string.recommand_merchant, R.string.popular_merchant, R.string.about
        };
        int[] logoIds = {
                R.drawable.ic_all_user_selector, R.drawable.ic_nearby_user_selector,
                R.drawable.ic_search_user_selector, R.drawable.ic_recommand_user_selector,
                R.drawable.ic_popu_user_selector, R.drawable.ic_about_selector,
        };

        Resources res = this.getResources();

        Category category = null;
        String name = null;
        for (int i = 0; i < nameIds.length; i++) {
            name = res.getString(nameIds[i]);
            category = new Category(logoIds[i], name);
            categories.add(category);
        }

        return categories;
    }

    private void initGridView() {
        ArrayList<Category> categories = getCategoryData();
        mAdapter = new HomeGridAdapter(this, categories);
        mGridView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    private void refreshActionBar(int status) {
        switch (status) {
            case DataUpdater.DATA_UPDATE_STATUS_READY:
                mCityList = DataModel.getCityListItems();
                setupActionbarSpinner();
                break;
        }
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            int what = msg.what;
            switch (what) {
                case MSG_DATA_UPDATE:
                    refreshActionBar(msg.arg1);
                    break;
                case MSG_CITY_CHANGED:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    @Override
    public void onUpdate(int status, int dataType, int id, Object arg) {
        Log.d(TAG, "onUpdate: status = " + status + " dataType = " + dataType);

    }

}
