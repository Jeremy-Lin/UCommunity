
package com.inbuy.ucommunity.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.TextView;

import com.inbuy.ucommunity.R;

public class AboutActivity extends Activity {
    public static final String ABOUT_URL = "http://www.inbuy360.com/index.php/Info/aboutme1";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.activity_about);

        setupActionbar();

        WebView wv;

        wv = (WebView) findViewById(R.id.wv_about);
        wv.loadUrl(ABOUT_URL);
    }

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
        titleView.setText(R.string.about_title);
        actionbar.setCustomView(customView, new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        ActionBar.LayoutParams lp = (ActionBar.LayoutParams) customView.getLayoutParams();
        lp.gravity &= Gravity.CENTER;
        actionbar.setCustomView(customView, lp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
