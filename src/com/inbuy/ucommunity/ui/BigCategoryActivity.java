
package com.inbuy.ucommunity.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.inbuy.ucommunity.R;
import com.inbuy.ucommunity.data.BigCategory;

import java.util.ArrayList;

public class BigCategoryActivity extends Activity {
    private static final String TAG = "BigCategoryActivity";
    private static final int MSG_UPDATE_LIST = 0;
    private static final int MSG_ACTION = 1;

    private ListView mBigCategoryList;
    private Spinner mCategorySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_big_category);
        setupActionbar();
        mBigCategoryList = (ListView) this.findViewById(R.id.list_users);
        mCategorySpinner = (Spinner) this.findViewById(R.id.spinner);

        setupSpinner();
        setupListview();
    }

    private void setupActionbar() {
        ActionBar actionbar = this.getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle(R.string.title_users_category);
    }

    private void setupSpinner() {
        // FIXME to get areas from the server.
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.areas,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(adapter);

        mCategorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private ArrayList<BigCategory> getBigCategoryList() {
        // For test.
        ArrayList<BigCategory> categories = new ArrayList<BigCategory>();
        String[] cateNames = {
                "教育培训", "母婴早教", "婚礼婚庆", "驾照、汽车", "运动健身", "旅游", "美容美体", "健康医疗"
        };

        BigCategory category = null;
        for (int i = 0; i < cateNames.length; i++) {
            category = new BigCategory(String.valueOf(i), cateNames[i]);
            categories.add(category);
        }

        return categories;
    }

    private void setupListview() {
        ArrayList<BigCategory> mBigCategories = getBigCategoryList();
        Log.d(TAG, "setupListview: mBigCategories = " + mBigCategories);
        BigCategoryListAdapter adapter = new BigCategoryListAdapter(this, mBigCategories, mHanlder);
        mBigCategoryList.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
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
                default:
                    break;
            }

            super.handleMessage(msg);
        }

    };

}
