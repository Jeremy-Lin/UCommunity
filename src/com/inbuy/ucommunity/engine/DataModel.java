
package com.inbuy.ucommunity.engine;

import com.inbuy.ucommunity.data.Area;
import com.inbuy.ucommunity.data.BigCategory;
import com.inbuy.ucommunity.data.City;
import com.inbuy.ucommunity.data.User;

import java.util.ArrayList;
import java.util.Hashtable;

public class DataModel {
    private static final String TAG = "DataModel";

    // local data caching
    private static DataCachingItem sCityListItems = new DataCachingItem(
            DataUpdater.DATA_UPDATE_TYPE_CITIES);

    private static DataCachingItem sBigCateListItems = new DataCachingItem(
            DataUpdater.DATA_UPDATE_TYPE_BIGCATES);

    private static DataCachingItem sUserListItems = new DataCachingItem(
            DataUpdater.DATA_UPDATE_TYPE_USERS);

    private static Hashtable<Integer, DataCachingItem> sAreaListItems = new Hashtable<Integer, DataCachingItem>();

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

    public static ArrayList<User> getUserListItems() {
        if (sUserListItems == null) {
            sUserListItems = new DataCachingItem(DataUpdater.DATA_UPDATE_TYPE_USERS);
        }

        synchronized (sUserListItems) {
            ArrayList<User> data = (ArrayList<User>) sUserListItems.getData();

            if (data == null) {
                DataUpdater.requestDataUpdate(DataUpdater.DATA_UPDATE_TYPE_USERS, null);
            }

            if (data != null) {
                ArrayList<User> output = new ArrayList<User>();
                for (User a : data) {
                    output.add(a);
                }
                return output;
            } else {
                return new ArrayList<User>();
            }
        }
    }

    /**
     * Setter method of user list
     */
    public static void setUserListItems(ArrayList<User> data) {
        if (data == null) {
            return;
        }

        if (sUserListItems == null) {
            sUserListItems = new DataCachingItem(data, DataUpdater.DATA_UPDATE_TYPE_USERS);
        } else {
            synchronized (sUserListItems) {
                sUserListItems.updateData(data);
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
     * Getter method of area list
     */
    public static ArrayList<Area> getAreaListItems(int id) {
        if (sAreaListItems == null) {
            sAreaListItems = new Hashtable<Integer, DataCachingItem>();
        }

        synchronized (sAreaListItems) {
            DataCachingItem data = sAreaListItems.get(id);

            if (data == null || checkDataOutOfDateStatus(data)) {
                String uid = String.valueOf(id);
                DataUpdater.requestDataUpdate(DataUpdater.DATA_UPDATE_TYPE_AREAES, uid);
            }
            if (data != null) {
                return new ArrayList<Area>((ArrayList<Area>) data.getData());
            } else {
                return new ArrayList<Area>();
            }
        }
    }

    /**
     * Setter method of area list
     */
    public static void setAreaListItems(int id, ArrayList<Area> data) {
        if (data == null) {
            return;
        }

        synchronized (sAreaListItems) {
            DataCachingItem items = new DataCachingItem(data, DataUpdater.DATA_UPDATE_TYPE_AREAES);
            if (items != null)
                sAreaListItems.put(id, items);
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
