
package com.inbuy.ucommunity.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.inbuy.ucommunity.R;
import com.inbuy.ucommunity.data.Category;
import com.inbuy.ucommunity.data.City;
import com.inbuy.ucommunity.engine.DataModel;
import com.inbuy.ucommunity.engine.DataUpdateListener;
import com.inbuy.ucommunity.engine.DataUpdater;
import com.inbuy.ucommunity.util.Const;

import java.util.ArrayList;

public class HomeActivity extends Activity implements DataUpdateListener {
    private static final String TAG = "HomeActivity";
    private static final int MSG_DATA_UPDATE = 0;
    private static final int MSG_CITY_CHANGED = 1;

    private SearchView mSearchView;
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

        actionbar.setCustomView(mCustomView, new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        ActionBar.LayoutParams lp = (ActionBar.LayoutParams) mCustomView.getLayoutParams();
        lp.gravity &= Gravity.CENTER_HORIZONTAL;
        actionbar.setCustomView(mCustomView, lp);

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

            SpinnerAdapter adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, cities);

            Spinner spinner = (Spinner) mCustomView.findViewById(R.id.spinner);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    Log.d(TAG, "OnItemSelectedListener: onItemSelected position = " + position);
                    if (position >= mCityList.size() || position < 0) {
                        Log.e(TAG, "onItemSelected: itemPosition is " + position
                                + ", out of bound.");
                        return;
                    }
                    mCurrentCity = mCityList.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }

            });

            // actionbar.setListNavigationCallbacks(adapter, new
            // OnNavigationListener() {
            //
            // @Override
            // public boolean onNavigationItemSelected(int itemPosition, long
            // itemId) {
            // // TODO Auto-generated method stub
            // if (itemPosition >= mCityList.size() || itemPosition < 0) {
            // Log.e(TAG, "itemPosition is " + itemPosition +
            // ", out of bound.");
            // return false;
            // }
            // mCurrentCity = mCityList.get(itemPosition);
            // return false;
            // }
            //
            // });
        }
    }

    private void initViewsRes() {
        mSearchView = (SearchView) this.findViewById(R.id.searchview_merchant);
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
                        break;
                }

            }

        });
    }

    private void gotoBigCategoryActivity() {
        if (mCurrentCity == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this, BigCategoryActivity.class);
        intent.putExtra(Const.EXTRA_CITY_ID, mCurrentCity.getmId());
        this.startActivity(intent);
    }

    private ArrayList<Category> getCategoryData() {
        ArrayList<Category> categories = new ArrayList<Category>();
        int[] nameIds = {
                R.string.all_merchant, R.string.nearby_merchant, R.string.search,
                R.string.recommand_merchant, R.string.popular_merchant, R.string.about
        };
        int[] logoIds = {
                R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher,
                R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher,
        };

        Resources res = this.getResources();

        Category category = null;
        String name = null;
        Drawable logo = null;
        for (int i = 0; i < nameIds.length; i++) {
            name = res.getString(nameIds[i]);
            logo = res.getDrawable(logoIds[i]);
            category = new Category(logo, name);
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
        getMenuInflater().inflate(R.menu.activity_main, menu);
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
