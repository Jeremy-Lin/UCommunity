
package com.inbuy.ucommunity.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.inbuy.ucommunity.R;
import com.inbuy.ucommunity.util.Const;
import com.inbuy.ucommunity.util.Util;

import java.util.HashSet;
import java.util.Set;

public class SearchActivity extends Activity implements OnQueryTextListener {
    private static final String TAG = "SearchActivity";
    public static final String KEY_KEYWORDS = "key_keywords";

    private SearchView mSearchView;
    private ListView mListView;
    private ProgressBar mLoadingBar;

    private String mCurrentCityId;

    private Set<String> mKeywordsSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);
        setupActionbar();

        mCurrentCityId = getIntent().getStringExtra(Const.EXTRA_CITY_ID);

        mLoadingBar = (ProgressBar) this.findViewById(R.id.loading);

        setupListView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        setupListView();
        super.onNewIntent(intent);
    }

    @SuppressLint("NewApi")
    private void setupActionbar() {
        ActionBar actionbar = this.getActionBar();
        actionbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));

        actionbar.setHomeButtonEnabled(true);
        actionbar.setIcon(R.drawable.actionbar_back_selector);

        int flag = actionbar.getDisplayOptions() ^ ActionBar.DISPLAY_SHOW_TITLE;
        actionbar.setDisplayOptions(flag);
    }

    private String[] getKeywords() {
        SharedPreferences sp = this.getSharedPreferences(Const.NAME_SHARED_PREF_UCOMM,
                this.MODE_PRIVATE);
        mKeywordsSet = sp.getStringSet(KEY_KEYWORDS, mKeywordsSet);
        if (mKeywordsSet != null) {
            String[] keywords = new String[mKeywordsSet.size()];
            mKeywordsSet.toArray(keywords);
            return keywords;
        }

        return null;
    }

    private void setupListView() {
        mListView = (ListView) this.findViewById(R.id.list_keywords);

        String[] keywords = getKeywords();
        mLoadingBar.setVisibility(View.GONE);

        if (keywords == null) {
            mListView.setVisibility(View.GONE);
        } else {
            mListView.setVisibility(View.VISIBLE);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, keywords);

            mListView.setAdapter(adapter);

            mListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    // TODO Auto-generated method stub
                    if (mKeywordsSet != null) {
                        String[] keywords = new String[mKeywordsSet.size()];
                        mKeywordsSet.toArray(keywords);
                        gotoSearchResult(keywords[position]);
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_actonbar_menu, menu);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.requestFocus();

        Util.custimizeSearchView(mSearchView);

        return true;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
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
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // TODO Auto-generated method stub
        return false;
    }

    private void saveKeywords(String query) {
        // Save key words searched.
        SharedPreferences sp = this.getSharedPreferences(Const.NAME_SHARED_PREF_UCOMM,
                this.MODE_PRIVATE);

        if (mKeywordsSet == null) {
            mKeywordsSet = new HashSet<String>();
        }

        mKeywordsSet.add(query);
        Editor editor = sp.edit();
        editor.putStringSet(KEY_KEYWORDS, mKeywordsSet);
        editor.commit();
    }

    private void gotoSearchResult(String query) {
        Intent intent = new Intent();
        intent.setClass(this, UserListActivity.class);
        intent.putExtra(Const.EXTRA_CITY_ID, mCurrentCityId);
        intent.putExtra(Const.EXTRA_KEYWORD, query);
        intent.putExtra(UserListActivity.EXTRA_TYPE, UserListActivity.TYPE_SEARCH);
        this.startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // TODO Auto-generated method stub
        if (!TextUtils.isEmpty(query)) {
            // Save key words searched.
            saveKeywords(query);
            // Go to search result activity.
            gotoSearchResult(query);
        }

        mSearchView.setQuery("", false);
        finish();
        return false;
    }

}
