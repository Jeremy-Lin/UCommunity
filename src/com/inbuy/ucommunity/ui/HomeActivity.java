
package com.inbuy.ucommunity.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SearchView;

import com.inbuy.ucommunity.R;
import com.inbuy.ucommunity.data.Category;
import com.inbuy.ucommunity.engine.DataModel;
import com.inbuy.ucommunity.engine.DataUpdater;

import java.util.ArrayList;

public class HomeActivity extends Activity {
    private SearchView mSearchView;
    private GridView mGridView;
    private HomeGridAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViewsRes();
        initGridView();

        DataUpdater.init();
        DataModel.getCityListItems();
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
        Intent intent = new Intent();
        intent.setClass(this, BigCategoryActivity.class);
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

}
