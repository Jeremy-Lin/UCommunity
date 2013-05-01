
package com.inbuy.ucommunity.ui;

import android.app.Application;
import android.util.Log;

import com.inbuy.ucommunity.engine.DataModel;
import com.inbuy.ucommunity.util.Util;

public class UCommApplication extends Application {
    private static final String TAG = "UCommApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        Util.setWinDataPath(this);

        Log.i(TAG, "Application onCreate()");

    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        Log.i(TAG, "Application onTerminate()");

        DataModel.clearAll();

        DataModel.clearAll();
    }
}
