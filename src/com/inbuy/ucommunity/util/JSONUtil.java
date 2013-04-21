
package com.inbuy.ucommunity.util;

import android.util.Log;

import com.inbuy.ucommunity.data.Area;
import com.inbuy.ucommunity.data.BigCategory;
import com.inbuy.ucommunity.data.City;

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

}
