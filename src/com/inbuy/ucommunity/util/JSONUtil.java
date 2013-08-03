
package com.inbuy.ucommunity.util;

import android.util.Log;

import com.inbuy.ucommunity.data.Area;
import com.inbuy.ucommunity.data.BigCategory;
import com.inbuy.ucommunity.data.City;
import com.inbuy.ucommunity.data.SmallCategory;
import com.inbuy.ucommunity.data.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONUtil {
    private static final String TAG = "JSONUtil";

    public static ArrayList<City> parseResonseCityList(JSONArray jsonArray) throws JSONException {
        if (jsonArray == null) {
            return null;
        }

        ArrayList<City> cityList = new ArrayList<City>();
        int resultCount = jsonArray.length();
        Log.d(TAG, "parseResonseCityList: resultCount = " + resultCount);
        City city = null;
        for (int i = 0; i < resultCount; i++) {
            JSONObject resultObject = jsonArray.getJSONObject(i);
            String name = resultObject.getString("name");
            String id = resultObject.getString("id");

            Log.d(TAG, "parseResonseCityList: i = " + i + " name = " + name + " id = " + id);
            city = new City(id, name);
            cityList.add(city);
        }

        return cityList;
    }

    public static ArrayList<Area> parseResonseAreaList(JSONArray jsonArray) throws JSONException {
        if (jsonArray == null) {
            return null;
        }

        ArrayList<Area> areaList = new ArrayList<Area>();
        int resultCount = jsonArray.length();
        Log.d(TAG, "parseResonseAreaList: resultCount = " + resultCount);
        Area area = null;
        for (int i = 0; i < resultCount; i++) {
            JSONObject resultObject = jsonArray.getJSONObject(i);
            String name = resultObject.getString("name");
            String id = resultObject.getString("id");

            Log.d(TAG, "parseResonseAreaList: i = " + i + " name = " + name + " id = " + id);
            area = new Area(id, name);
            areaList.add(area);
        }

        return areaList;
    }

    public static ArrayList<BigCategory> parseResonseBigcateList(JSONArray jsonArray)
            throws JSONException {
        if (jsonArray == null) {
            return null;
        }

        ArrayList<BigCategory> bigcateList = new ArrayList<BigCategory>();
        int resultCount = jsonArray.length();
        Log.d(TAG, "parseResonseBigcateList: resultCount = " + resultCount);
        BigCategory bigcate = null;
        for (int i = 0; i < resultCount; i++) {
            JSONObject resultObject = jsonArray.getJSONObject(i);
            String name = resultObject.getString("name");
            String id = resultObject.getString("id");

            Log.d(TAG, "parseResonseBigcateList: i = " + i + " name = " + name + " id = " + id);
            bigcate = new BigCategory(id, name);
            bigcateList.add(bigcate);
        }

        return bigcateList;
    }

    public static ArrayList<SmallCategory> parseResonseSmallcateList(JSONArray jsonArray)
            throws JSONException {
        if (jsonArray == null) {
            return null;
        }

        ArrayList<SmallCategory> smallcateList = new ArrayList<SmallCategory>();
        int resultCount = jsonArray.length();
        Log.d(TAG, "parseResonseBigcateList: resultCount = " + resultCount);
        SmallCategory smallcate = null;
        for (int i = 0; i < resultCount; i++) {
            JSONObject resultObject = jsonArray.getJSONObject(i);
            String name = resultObject.getString("name");
            String id = resultObject.getString("id");

            Log.d(TAG, "parseResonseBigcateList: i = " + i + " name = " + name + " id = " + id);
            smallcate = new SmallCategory(id, name);
            smallcateList.add(smallcate);
        }

        return smallcateList;
    }

    public static ArrayList<User> parseResonseUserList(JSONArray jsonArray) throws JSONException {
        if (jsonArray == null) {
            return null;
        }

        ArrayList<User> userList = new ArrayList<User>();
        int resultCount = jsonArray.length();
        Log.d(TAG, "parseResonseUserList: resultCount = " + resultCount);
        User user = null;
        for (int i = 0; i < resultCount; i++) {
            user = new User();
            JSONObject resultObject = jsonArray.getJSONObject(i);
            user.mName = resultObject.getString("name");
            user.mId = resultObject.getString("id");
            user.mAddress = resultObject.getString("address");
            user.mBigCateId = resultObject.getString("b_cate");
            user.mBank = resultObject.getString("bank");
            user.mSmallCateId = resultObject.getString("s_cate");
            user.mBusId = resultObject.getString("bus");
            user.mCityId = resultObject.getString("city");
            user.mXzId = resultObject.getString("xz");
            user.mStar = resultObject.getString("star");
            user.mDes = resultObject.getString("des");
            user.mTag = resultObject.getString("tag");
            user.mFckCardOne = resultObject.getString("fck_card_one");
            user.mFckCardMore = resultObject.getString("fck_card_more");
            user.mInfo = resultObject.getString("info");
            user.mLng = resultObject.getString("lng");
            user.mLat = resultObject.getString("lat");
            user.mTj = resultObject.getInt("tj");
            user.mRq = resultObject.getInt("rq");
            user.mNew = resultObject.getInt("new");
            user.mInfo = resultObject.getString("info");
            user.mCreateTime = resultObject.getLong("create_time");
            user.mPhone = resultObject.getString("phone");
            user.mCardPhone = resultObject.getString("card_phone");
            user.mImageUrl = resultObject.getString("img");
            user.mThumbImg1 = resultObject.getString("thumb_img1");
            user.mThumbImg2 = resultObject.getString("thumb_img2");

            Log.d(TAG, "parseResonseUserList: i = " + i + " name = " + user.mName + " id = "
                    + user.mId);

            userList.add(user);
        }

        return userList;
    }

    public static User parseResonseUserItem(JSONObject jsonObiect) throws JSONException {
        if (jsonObiect == null) {
            return null;
        }

        Log.d(TAG, "parseResonseUserList: jsonObiect = " + jsonObiect);
        User user = new User();
        JSONObject resultObject = jsonObiect;
        user.mName = resultObject.getString("name");
        user.mId = resultObject.getString("id");
        user.mAddress = resultObject.getString("address");
        user.mBigCateId = resultObject.getString("b_cate");
        user.mBank = resultObject.getString("bank");
        user.mSmallCateId = resultObject.getString("s_cate");
        user.mBusId = resultObject.getString("bus");
        user.mCityId = resultObject.getString("city");
        user.mXzId = resultObject.getString("xz");
        user.mStar = resultObject.getString("star");
        user.mDes = resultObject.getString("des");
        user.mTag = resultObject.getString("tag");
        user.mFckCardOne = resultObject.getString("fck_card_one");
        user.mFckCardMore = resultObject.getString("fck_card_more");
        user.mInfo = resultObject.getString("info");
        user.mLng = resultObject.getString("lng");
        user.mLat = resultObject.getString("lat");
        user.mTj = resultObject.getInt("tj");
        user.mRq = resultObject.getInt("rq");
        user.mNew = resultObject.getInt("new");
        user.mInfo = resultObject.getString("info");
        user.mCreateTime = resultObject.getLong("create_time");

        user.mPhone = resultObject.getString("phone");
        user.mCardPhone = resultObject.getString("card_phone");
        user.mImageUrl = resultObject.getString("img");

        Log.d(TAG, "parseResonseUserList: " + " name = " + user.mName + " id = " + user.mId);

        return user;
    }

}
