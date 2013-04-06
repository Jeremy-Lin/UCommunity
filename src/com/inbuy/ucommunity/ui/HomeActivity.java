package com.inbuy.ucommunity.ui;

import java.util.ArrayList;

import com.inbuy.ucommunity.R;
import com.inbuy.ucommunity.R.id;
import com.inbuy.ucommunity.R.layout;
import com.inbuy.ucommunity.R.menu;
import com.inbuy.ucommunity.data.Category;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.widget.GridView;
import android.widget.SearchView;

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
    }
	
	private void initViewsRes() {
		mSearchView = (SearchView) this.findViewById(R.id.searchview_merchant);
		mGridView = (GridView) this.findViewById(R.id.grid_categories);
	}

	private ArrayList<Category> getCategoryData() {
		ArrayList<Category> categories = new ArrayList<Category>();
		int[] nameIds = {R.string.all_merchant, R.string.nearby_merchant, R.string.search, R.string.recommand_merchant, R.string.popular_merchant, R.string.about};
		int[] logoIds = {R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher,};
		
		Resources res = this.getResources();
		
		Category category = null;
		String name = null;
		Drawable logo = null;
		for(int i = 0; i < nameIds.length; i ++) {
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
