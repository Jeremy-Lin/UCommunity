
package com.inbuy.ucommunity.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.inbuy.ucommunity.R;
import com.inbuy.ucommunity.engine.DataUpdateListener;
import com.inbuy.ucommunity.engine.DataUpdater;

public class SplashActivity extends Activity implements DataUpdateListener {
    private static final String TAG = "SplashActivity";

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_splash);
        DataUpdater.registerDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_CITIES, this);
        DataUpdater.init();
        DataUpdater.requestDataUpdate(DataUpdater.DATA_UPDATE_TYPE_CITIES, null);
    }

    @Override
    public void onUpdate(int status, int dataType, int id, Object arg) {
        Log.d(TAG, "onUpdate: status = " + status + " dataType = " + dataType);

        if (dataType == DataUpdater.DATA_UPDATE_TYPE_CITIES
                && status == DataUpdater.DATA_UPDATE_STATUS_READY) {

            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    SplashActivity.this.startActivity(intent);
                    SplashActivity.this.finish();
                }

            }, 1000);

        }
    }

    @Override
    protected void onDestroy() {
        DataUpdater.unregisterDataUpdateListener(DataUpdater.DATA_UPDATE_TYPE_CITIES, this);
        super.onDestroy();
    }

}
