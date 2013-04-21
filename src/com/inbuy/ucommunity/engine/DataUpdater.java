
package com.inbuy.ucommunity.engine;

import android.util.Log;

import com.inbuy.ucommunity.data.Area;
import com.inbuy.ucommunity.data.BigCategory;
import com.inbuy.ucommunity.data.City;
import com.inbuy.ucommunity.util.JSONUtil;
import com.inbuy.ucommunity.util.NetUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Hashtable;

public class DataUpdater {
    private static final String TAG = "DataUpdater";

    // Recommend life cycle of caching data (from the last data reloading to
    // now) to help app decide if it's necessary to reload data from server now.
    public final static long TIME_DURATION_FOR_DATA_RELOAD_HIGH = 15 * 60 * 1000; // 15
                                                                                  // min

    public final static long TIME_DURATION_FOR_DATA_RELOAD_MED = 60 * 60 * 1000; // 60
                                                                                 // min

    public final static long TIME_DURATION_FOR_DATA_RELOAD_LOW = 180 * 60 * 1000; // 180

    public final static int DATA_UPDATE_STATUS_ERROR = -1;

    public final static int DATA_UPDATE_STATUS_LOADING = 0;

    public final static int DATA_UPDATE_STATUS_READY = 1;

    public final static int DATA_UPDATE_TYPE_MIN = 0;

    public final static int DATA_UPDATE_TYPE_CITIES = 0;

    public final static int DATA_UPDATE_TYPE_AREAES = 1;

    public final static int DATA_UPDATE_TYPE_BIGCATES = 2;

    public final static int DATA_UPDATE_TYPE_MAX = 3;

    // <request id, ServerDataRequest>
    private static Hashtable<Integer, ServerDataRequest> mServerDataRequests = new Hashtable<Integer, ServerDataRequest>();

    // Listeners registered by UI to get notification when data changes
    // <DATA_UPDATE_TYPE, DataUpdateListeners array for this type>
    private static Hashtable<Integer, ArrayList<DataUpdateListener>> sDataUpdateListeners = new Hashtable<Integer, ArrayList<DataUpdateListener>>();

    private static ServerResponseListener mServerResponseListener = new ServerResponseListener();

    private static ServerConnector mServerConnector = null;

    public static void init() {
        mServerConnector = ServerConnector.getInstance();
    }

    public static ServerConnector getServerConnector() {
        return mServerConnector;
    }

    public static ServerResponseListener getServerResponseListener() {
        return mServerResponseListener;
    }

    /**
     * register DataUpdateListener for get notification of data updates/changes
     * on related type
     * 
     * @param dataType
     * @param listener
     */
    public static void registerDataUpdateListener(int dataType, DataUpdateListener listener) {

        if (dataType < DATA_UPDATE_TYPE_MIN || dataType > DATA_UPDATE_TYPE_MAX) {
            // the data type is invalid
            return;
        }

        if (listener == null) {
            return;
        }

        synchronized (sDataUpdateListeners) {
            ArrayList<DataUpdateListener> updatelisteners = sDataUpdateListeners.get(dataType);
            if (updatelisteners == null) {
                updatelisteners = new ArrayList<DataUpdateListener>();
                updatelisteners.add(listener);

                sDataUpdateListeners.put(dataType, updatelisteners);
            } else {
                if (!updatelisteners.contains(listener)) {
                    updatelisteners.add(listener);
                }
            }
        }
    }

    /**
     * unregister DataUpdateListener for ignore notification of data
     * updates/changes on related type
     * 
     * @param dataType
     * @param listener
     */
    public static void unregisterDataUpdateListener(int dataType, DataUpdateListener listener) {
        if (dataType < DATA_UPDATE_TYPE_MIN || dataType > DATA_UPDATE_TYPE_MAX) {
            // the data type is invalid
            return;
        }

        if (listener == null) {
            return;
        }

        synchronized (sDataUpdateListeners) {
            ArrayList<DataUpdateListener> updatelisteners = sDataUpdateListeners.get(dataType);
            if (updatelisteners != null && updatelisteners.contains(listener)) {
                updatelisteners.remove(listener);
            }
        }
    }

    public static int requestDataUpdate(int dataType, Object arg) {
        Log.d(TAG, "requestDataUpdate(), dataType=" + dataType + ", arg=" + arg);

        if (dataType < DATA_UPDATE_TYPE_MIN || dataType > DATA_UPDATE_TYPE_MAX) {
            // the data type is invalid
            return DATA_UPDATE_STATUS_ERROR;
        }

        if (mServerConnector == null) {
            return DATA_UPDATE_STATUS_ERROR;
        }

        int requestId = 0;
        int winId = 0;
        String uuid = null;

        switch (dataType) {
            case DATA_UPDATE_TYPE_CITIES: {
                requestId = mServerConnector.httpGet(NetUtil.getCityUrl(), dataType,
                        mServerResponseListener);
            }
                break;
            case DATA_UPDATE_TYPE_AREAES: {
                if (arg != null && arg instanceof String) {
                    requestId = mServerConnector.httpGet(NetUtil.getAreaUrl((String) arg),
                            dataType, mServerResponseListener);
                }
            }
                break;
            case DATA_UPDATE_TYPE_BIGCATES: {
                requestId = mServerConnector.httpGet(NetUtil.getBigCateUrl(), dataType,
                        mServerResponseListener);
            }
                break;
            default:
                break;

        }

        synchronized (mServerDataRequests) {
            if (requestId != 0) {
                mServerDataRequests.put(requestId, new ServerDataRequest(requestId, dataType));

                // notify all data listeners the data loading status
                synchronized (sDataUpdateListeners) {
                    ArrayList<DataUpdateListener> lisenters = sDataUpdateListeners.get(dataType);
                    if (lisenters != null) {
                        for (DataUpdateListener l : lisenters) {
                            l.onUpdate(DATA_UPDATE_STATUS_LOADING, dataType, winId, null);
                        }
                    }
                }
                return DATA_UPDATE_STATUS_LOADING;
            } else {
                return DATA_UPDATE_STATUS_ERROR;
            }
        }
    }

    // inner class for keeping data request related information
    static class ServerDataRequest {
        int mRequestId;

        int mDataType;

        int mId;

        String mUuid;

        Object mExtra;

        ServerDataRequest(int requestsId, int type) {
            mRequestId = requestsId;
            mDataType = type;
        }

        ServerDataRequest(int requestsId, int type, int id, Object arg) {
            mRequestId = requestsId;
            mDataType = type;
            mId = id;
            mExtra = arg;
        }

        ServerDataRequest(int requestsId, int type, int id, Object arg, String uuid) {
            mRequestId = requestsId;
            mDataType = type;
            mId = id;
            mExtra = arg;
            mUuid = uuid;
        }
    }

    /**
     * notify UI listeners data updates
     * 
     * @param status
     * @param dataType
     * @param id
     */
    protected static void notifyDataChanged(int status, int dataType, int id) {
        ArrayList<DataUpdateListener> lisenters = null;

        synchronized (sDataUpdateListeners) {
            lisenters = sDataUpdateListeners.get(dataType);
        }

        if (lisenters != null) {
            synchronized (lisenters) {
                for (DataUpdateListener l : lisenters) {
                    l.onUpdate(status, dataType, id, null);
                }
            }
        }
    }

    // inner class for receive response from SalesWIN server
    static class ServerResponseListener implements ResponseListener {

        @Override
        public void onReceive(int requestId, int dataType, int errorCode, Object response) {
            Log.d(TAG, "onReceive: requestId = " + requestId + ", dataType = " + dataType
                    + ", errorCode = " + errorCode + ", response = " + response);
            if (!mServerDataRequests.containsKey(requestId)) {
                // Invalid request id
                return;
            }

            ServerDataRequest request = mServerDataRequests.get(requestId);
            Log.d(TAG, "onReceive: receive data from server, dataType=" + request.mDataType
                    + ", errorCode=" + errorCode + ", winId = " + request.mId + ", response = "
                    + response);

            int id = 0;
            Object arg = null;
            boolean successed = (errorCode == 0);

            if (successed) {
                switch (request.mDataType) {
                    case DATA_UPDATE_TYPE_CITIES:
                        if (response != null && response instanceof JSONArray) {
                            ArrayList<City> cityList = null;
                            try {
                                cityList = JSONUtil.parseResonseCityList((JSONArray) response);
                            } catch (JSONException e) {
                                Log.e(TAG, "onReceive: DATA_UPDATE_TYPE_CITIES. e = " + e);
                                e.printStackTrace();
                            }
                            Log.d(TAG, "onReceive: DATA_UPDATE_TYPE_CITIES: cityList size = "
                                    + cityList.size());
                            DataModel.setCityListItems(cityList);
                        }

                        break;
                    case DATA_UPDATE_TYPE_AREAES:
                        if (response != null && response instanceof JSONArray) {
                            ArrayList<Area> areaList = null;
                            try {
                                areaList = JSONUtil.parseResonseAreaList((JSONArray) response);
                            } catch (JSONException e) {
                                Log.e(TAG, "onReceive: DATA_UPDATE_TYPE_AREAES. e = " + e);
                                e.printStackTrace();
                            }
                            Log.d(TAG, "onReceive: DATA_UPDATE_TYPE_AREAES: areaList size = "
                                    + areaList.size());
                            DataModel.setAreaListItems(areaList);
                        }

                        break;
                    case DATA_UPDATE_TYPE_BIGCATES:
                        if (response != null && response instanceof JSONArray) {
                            ArrayList<BigCategory> bigcateList = null;
                            try {
                                bigcateList = JSONUtil
                                        .parseResonseBigcateList((JSONArray) response);
                            } catch (JSONException e) {
                                Log.e(TAG, "onReceive: DATA_UPDATE_TYPE_AREAES. e = " + e);
                                e.printStackTrace();
                            }
                            Log.d(TAG, "onReceive: DATA_UPDATE_TYPE_BIGCATES: bigcateList size = "
                                    + bigcateList.size());
                            DataModel.setBigCateListItems(bigcateList);
                        }

                        break;

                    default:
                        break;
                }
            }
            ArrayList<DataUpdateListener> lisenters = null;

            synchronized (sDataUpdateListeners) {
                lisenters = sDataUpdateListeners.get(request.mDataType);
            }

            if (lisenters != null) {
                synchronized (lisenters) {
                    for (DataUpdateListener l : lisenters) {
                        l.onUpdate(DATA_UPDATE_STATUS_READY, request.mDataType, id, arg);
                    }
                }
            }

            mServerDataRequests.remove(requestId);
        }
    }

}
