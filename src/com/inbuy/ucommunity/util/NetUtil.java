
package com.inbuy.ucommunity.util;

import android.util.Log;

import java.util.HashMap;

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
    public static final String PARAM_NAME_LIMIT = "limit";
    public static final String PARAM_NAME_COUNT = "count";
    public static final String PARAM_NAME_TJ = "tj";
    public static final String PARAM_NAME_RQ = "rq";

    // url and actions
    public static final String URL_SERVER = "Http://www.Inbuy360.com/";
    public static final String URL_API_SERVER = "Http://www.Inbuy360.com/api.php/Api/";
    public static final String ACTION_GET_USER = "getUser";
    public static final String ACTION_GET_CITY = "getCity";
    public static final String ACTION_GET_AREA = "getArea";
    public static final String ACTION_GET_BIGCATE = "getBigCate";
    public static final String ACTION_GET_SMALLCATE = "getSmaCate";
    public static final String ACTION_SEARCH_USER = "searchUser";
    public static final String ACTION_GET_PERSON = "getPerson";
    public static final String ACTION_GET_CATENAME = "getCateName";
    public static final String ACTION_GET_AREANAME = "getAreaName";

    public static final String DELIMITER = "/";

    public static String getPhotoUrl(String imgUrl) {
        StringBuffer sb = new StringBuffer(URL_SERVER);
        sb.append(imgUrl);
        return sb.toString();
    }

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

    public static String getSmallCateUrl(String bigCateId) {
        StringBuffer sb = new StringBuffer(URL_API_SERVER);
        sb.append(ACTION_GET_SMALLCATE).append(DELIMITER).append(PARAM_NAME_APPKEY)
                .append(DELIMITER).append(APP_KEY).append(DELIMITER).append(PARAM_NAME_APPSECRET)
                .append(DELIMITER).append(APP_SECRET).append(DELIMITER).append(PARAM_NAME_UPID)
                .append(DELIMITER).append(bigCateId);

        String url = sb.toString();
        Log.d(TAG, "getSmallCateUrl: url = " + url);

        return url;
    }

    public static String getPersonUrl(String uid) {
        StringBuffer sb = new StringBuffer(URL_API_SERVER);
        sb.append(ACTION_GET_PERSON).append(DELIMITER).append(PARAM_NAME_APPKEY).append(DELIMITER)
                .append(APP_KEY).append(DELIMITER).append(PARAM_NAME_APPSECRET).append(DELIMITER)
                .append(APP_SECRET).append(DELIMITER).append(PARAM_NAME_ID).append(DELIMITER)
                .append(uid);

        String url = sb.toString();
        Log.d(TAG, "getPersonUrl: url = " + url);

        return url;
    }

    public static String getUsersListUrl(HashMap<String, String> ids) {
        StringBuffer sb = new StringBuffer(URL_API_SERVER);
        sb.append(ACTION_SEARCH_USER).append(DELIMITER).append(PARAM_NAME_APPKEY).append(DELIMITER)
                .append(APP_KEY).append(DELIMITER).append(PARAM_NAME_APPSECRET).append(DELIMITER)
                .append(APP_SECRET);

        if (ids == null) {
            return sb.toString();
        }

        String cityId = ids.get(PARAM_NAME_CITY);

        if (cityId != null && !cityId.isEmpty()) {
            sb.append(DELIMITER).append(PARAM_NAME_CITY).append(DELIMITER).append(cityId);
        }

        String keyword = ids.get(PARAM_NAME_KEYWORD);
        if (keyword != null && !keyword.isEmpty()) {
            sb.append(DELIMITER).append(PARAM_NAME_KEYWORD).append(DELIMITER).append(keyword);
        }

        String xzId = ids.get(PARAM_NAME_XZ);
        if (xzId != null && !xzId.isEmpty()) {
            sb.append(DELIMITER).append(PARAM_NAME_XZ).append(DELIMITER).append(xzId);
        }

        String busId = ids.get(PARAM_NAME_BUS);
        if (busId != null && !busId.isEmpty()) {
            sb.append(DELIMITER).append(PARAM_NAME_BUS).append(DELIMITER).append(busId);
        }

        String bigCateId = ids.get(PARAM_NAME_BCATE);
        if (bigCateId != null && !bigCateId.isEmpty()) {
            sb.append(DELIMITER).append(PARAM_NAME_BCATE).append(DELIMITER).append(bigCateId);
        }

        String smallCateId = ids.get(PARAM_NAME_SCATE);
        if (smallCateId != null && !smallCateId.isEmpty()) {
            sb.append(DELIMITER).append(PARAM_NAME_SCATE).append(DELIMITER).append(smallCateId);
        }

        String tj = ids.get(PARAM_NAME_TJ);
        if (tj != null && !tj.isEmpty()) {
            sb.append(DELIMITER).append(PARAM_NAME_TJ).append(DELIMITER).append(tj);
        }

        String rq = ids.get(PARAM_NAME_RQ);
        if (rq != null && !rq.isEmpty()) {
            sb.append(DELIMITER).append(PARAM_NAME_TJ).append(DELIMITER).append(rq);
        }

        String limit = ids.get(PARAM_NAME_LIMIT);
        if (limit != null && !limit.isEmpty()) {
            sb.append(DELIMITER).append(PARAM_NAME_LIMIT).append(DELIMITER).append(limit);
        }

        String count = ids.get(PARAM_NAME_COUNT);
        if (count != null && !count.isEmpty()) {
            sb.append(DELIMITER).append(PARAM_NAME_COUNT).append(DELIMITER).append(count);
        }

        String url = sb.toString();
        Log.d(TAG, "searchUserUrl: url = " + url);

        return url;
    }
}
