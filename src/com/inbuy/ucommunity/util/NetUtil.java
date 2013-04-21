
package com.inbuy.ucommunity.util;

import android.util.Log;

public class NetUtil {
    private static final String TAG = "NetUtil";

    public static final String APP_KEY = "67883000";
    public static final String APP_SECRET = "806986328f227bdd4c3c09c595bb7f14";

    public static final String PARAM_NAME_APPKEY = "AppKey";
    public static final String PARAM_NAME_APPSECRET = "AppSecret";
    public static final String PARAM_NAME_UPID = "upid";
    public static final String PARAM_NAME_BCATE = "b_cate";
    public static final String PARAM_NAME_SCATE = "s_cate";
    public static final String PARAM_NAME_CITY = "city";
    public static final String PARAM_NAME_XZ = "xz";
    public static final String PARAM_NAME_BUS = "bus";
    public static final String PARAM_NAME_KEYWORD = "keyword";
    public static final String PARAM_NAME_LNG = "lng";
    public static final String PARAM_NAME_LAT = "lat";
    public static final String PARAM_NAME_LONG = "long";
    public static final String PARAM_NAME_ID = "id";

    // url and actions
    public static final String URL_API_SERVER = "Http://www.Inbuy360.com/api.php/Api/";
    public static final String ACTION_GET_USER = "getUser";
    public static final String ACTION_GET_CITY = "getCity";
    public static final String ACTION_GET_AREA = "getArea";
    public static final String ACTION_GET_BIGCATE = "getBigCate";
    public static final String ACTION_GET_SMACATE = "getSmaCate";
    public static final String ACTION_SEARCH_USER = "searchUser";
    public static final String ACTION_GET_PERSON = "getPerson";
    public static final String ACTION_GET_CATENAME = "getCateName";
    public static final String ACTION_GET_AREANAME = "getAreaName";

    public static final String DELIMITER = "/";

    public static String getCityUrl() {
        StringBuffer sb = new StringBuffer(URL_API_SERVER);
        sb.append(ACTION_GET_CITY).append(DELIMITER).append(PARAM_NAME_APPKEY).append(DELIMITER)
                .append(APP_KEY).append(DELIMITER).append(PARAM_NAME_APPSECRET).append(DELIMITER)
                .append(APP_SECRET);

        String url = sb.toString();
        Log.d(TAG, "getCityUrl: url = " + url);

        return url;
    }

    public static String getAreaUrl(String cityId) {
        StringBuffer sb = new StringBuffer(URL_API_SERVER);
        sb.append(ACTION_GET_AREA).append(DELIMITER).append(PARAM_NAME_APPKEY).append(DELIMITER)
                .append(APP_KEY).append(DELIMITER).append(PARAM_NAME_APPSECRET).append(DELIMITER)
                .append(APP_SECRET).append(DELIMITER).append(PARAM_NAME_UPID).append(DELIMITER)
                .append(cityId);

        String url = sb.toString();
        Log.d(TAG, "getAreaUrl: url = " + url);

        return url;
    }

    public static String getBigCateUrl() {
        StringBuffer sb = new StringBuffer(URL_API_SERVER);
        sb.append(ACTION_GET_BIGCATE).append(DELIMITER).append(PARAM_NAME_APPKEY).append(DELIMITER)
                .append(APP_KEY).append(DELIMITER).append(PARAM_NAME_APPSECRET).append(DELIMITER)
                .append(APP_SECRET);

        String url = sb.toString();
        Log.d(TAG, "getBigCateUrl: url = " + url);

        return url;
    }

}
