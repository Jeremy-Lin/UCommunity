
package com.inbuy.ucommunity.ui;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.inbuy.ucommunity.engine.DataModel;
import com.inbuy.ucommunity.util.Util;

public class UCommApplication extends Application {
    private static final String TAG = "UCommApplication";

    private static UCommApplication mInstance = null;
    public boolean m_bKeyRight = true;
    BMapManager mBMapManager = null;

    public static final String strKey = "5B837FB7E9C306308BE759F258E293717AB8A600";

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Util.setWinDataPath(this);

        Log.i(TAG, "Application onCreate()");
        initEngineManager(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        Log.i(TAG, "Application onTerminate()");

        DataModel.clearAll();

        if (mBMapManager != null) {
            mBMapManager.destroy();
            mBMapManager = null;
        }
    }

    public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey, new MyGeneralListener())) {
            Toast.makeText(UCommApplication.getInstance().getApplicationContext(),
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
    }

    public static UCommApplication getInstance() {
        return mInstance;
    }

    // 常用事件监听，用来处理通常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {

        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(UCommApplication.getInstance().getApplicationContext(), "您的网络出错啦！",
                        Toast.LENGTH_LONG).show();
            } else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(UCommApplication.getInstance().getApplicationContext(),
                        "输入正确的检索条件！", Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
                // 授权Key错误：
                Toast.makeText(UCommApplication.getInstance().getApplicationContext(),
                        "请在 UCommApplication.java文件输入正确的授权Key！", Toast.LENGTH_LONG).show();
                UCommApplication.getInstance().m_bKeyRight = false;
            }
        }
    }
}
