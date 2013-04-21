
package com.inbuy.ucommunity.engine;

import com.inbuy.ucommunity.data.Area;
import com.inbuy.ucommunity.data.BigCategory;
import com.inbuy.ucommunity.data.City;

import java.util.ArrayList;

public class DataModel {
    private static final String TAG = "DataModel";

    // local data caching
    private static DataCachingItem sCityListItems = new DataCachingItem(
            DataUpdater.DATA_UPDATE_TYPE_CITIES);

    private static DataCachingItem sAreaListItems = new DataCachingItem(
            DataUpdater.DATA_UPDATE_TYPE_AREAES);

    private static DataCachingItem sBigCateListItems = new DataCachingItem(
            DataUpdater.DATA_UPDATE_TYPE_BIGCATES);

    public static boolean checkDataOutOfDateStatus(DataCachingItem data) {
        if (data == null) {
            return true;
        }

        boolean isOutOfDate = false;
        int dataType = data.getDataType();
        long updateDuration = System.currentTimeMillis() - data.getUpdateTimeStamp();

        switch (dataType) {
            case DataUpdater.DATA_UPDATE_TYPE_CITIES:
                // reload in high frequency
                if (updateDuration >= DataUpdater.TIME_DURATION_FOR_DATA_RELOAD_HIGH) {
                    isOutOfDate = true;
                }
                break;
            case DataUpdater.DATA_UPDATE_TYPE_AREAES:
            case DataUpdater.DATA_UPDATE_TYPE_BIGCATES:
                if (updateDuration >= DataUpdater.TIME_DURATION_FOR_DATA_RELOAD_HIGH) {
                    isOutOfDate = true;
                }
                break;
            default:
                isOutOfDate = false;
                break;
        }

        return isOutOfDate;
    }

    public static ArrayList<City> getCityListItems() {
        if (sCityListItems == null) {
            sCityListItems = new DataCachingItem(DataUpdater.DATA_UPDATE_TYPE_CITIES);
        }

        synchronized (sCityListItems) {
            ArrayList<City> data = (ArrayList<City>) sCityListItems.getData();

            if (data == null || checkDataOutOfDateStatus(sCityListItems)) {
                DataUpdater.requestDataUpdate(DataUpdater.DATA_UPDATE_TYPE_CITIES, null);
            }

            if (data != null) {
                ArrayList<City> output = new ArrayList<City>();
                for (City a : data) {
                    output.add(a);
                }
                return output;
            } else {
                return new ArrayList<City>();
            }
        }
    }

    public static ArrayList<Area> getAreaListItems(String cityId) {
        if (sAreaListItems == null) {
            sAreaListItems = new DataCachingItem(DataUpdater.DATA_UPDATE_TYPE_AREAES);
        }

        synchronized (sAreaListItems) {
            ArrayList<Area> data = (ArrayList<Area>) sAreaListItems.getData();

            if (data == null || checkDataOutOfDateStatus(sAreaListItems)) {
                DataUpdater.requestDataUpdate(DataUpdater.DATA_UPDATE_TYPE_AREAES, cityId);
            }

            if (data != null) {
                ArrayList<Area> output = new ArrayList<Area>();
                for (Area a : data) {
                    output.add(a);
                }
                return output;
            } else {
                return new ArrayList<Area>();
            }
        }
    }

    public static ArrayList<BigCategory> getBigCatesListItems() {
        if (sBigCateListItems == null) {
            sBigCateListItems = new DataCachingItem(DataUpdater.DATA_UPDATE_TYPE_BIGCATES);
        }

        synchronized (sBigCateListItems) {
            ArrayList<BigCategory> data = (ArrayList<BigCategory>) sBigCateListItems.getData();

            if (data == null || checkDataOutOfDateStatus(sBigCateListItems)) {
                DataUpdater.requestDataUpdate(DataUpdater.DATA_UPDATE_TYPE_BIGCATES, null);
            }

            if (data != null) {
                ArrayList<BigCategory> output = new ArrayList<BigCategory>();
                for (BigCategory a : data) {
                    output.add(a);
                }
                return output;
            } else {
                return new ArrayList<BigCategory>();
            }
        }
    }

    /**
     * Setter method of City list
     */
    public static void setCityListItems(ArrayList<City> data) {
        if (data == null) {
            return;
        }

        if (sCityListItems == null) {
            sCityListItems = new DataCachingItem(data, DataUpdater.DATA_UPDATE_TYPE_CITIES);
        } else {
            synchronized (sCityListItems) {
                sCityListItems.updateData(data);
            }
        }
    }

    /**
     * Setter method of Area list
     */
    public static void setAreaListItems(ArrayList<Area> data) {
        if (data == null) {
            return;
        }

        if (sAreaListItems == null) {
            sAreaListItems = new DataCachingItem(data, DataUpdater.DATA_UPDATE_TYPE_AREAES);
        } else {
            synchronized (sAreaListItems) {
                sAreaListItems.updateData(data);
            }
        }
    }

    /**
     * Setter method of BigCategory list
     */
    public static void setBigCateListItems(ArrayList<BigCategory> data) {
        if (data == null) {
            return;
        }

        if (sBigCateListItems == null) {
            sBigCateListItems = new DataCachingItem(data, DataUpdater.DATA_UPDATE_TYPE_BIGCATES);
        } else {
            synchronized (sBigCateListItems) {
                sBigCateListItems.updateData(data);
            }
        }
    }

    /*
     * Keep data caching and update time stamp
     */
    static class DataCachingItem {
        private long mLastUpdateTimeStamp;

        private Object mData;

        private int mDataType;

        DataCachingItem(int dataType) {
            mDataType = dataType;
        }

        DataCachingItem(Object data, int dataType) {
            updateData(data);
            mDataType = dataType;
        }

        public void updateData(Object data) {
            mLastUpdateTimeStamp = System.currentTimeMillis();
            mData = data;
        }

        public void clearData() {
            mLastUpdateTimeStamp = 0;
            mData = null;
            mDataType = 0;
        }

        public long getUpdateTimeStamp() {
            return mLastUpdateTimeStamp;
        }

        public Object getData() {
            return mData;
        }

        public int getDataType() {
            return mDataType;
        }
    }
}
