package com.inbuy.ucommunity.engine;

import java.util.ArrayList;

import com.inbuy.ucommunity.data.City;
import com.inbuy.ucommunity.util.NetUtil;

public class DataModel {
	private static final String TAG = "DataModel";

	// local data caching
	private static DataCachingItem sCityListItems = new DataCachingItem(
			DataUpdater.DATA_UPDATE_TYPE_CITIES);

	public static boolean checkDataOutOfDateStatus(DataCachingItem data) {
		if (data == null) {
			return true;
		}

		boolean isOutOfDate = false;
		int dataType = data.getDataType();
		long updateDuration = System.currentTimeMillis()
				- data.getUpdateTimeStamp();

		switch (dataType) {
		case DataUpdater.DATA_UPDATE_TYPE_CITIES:
			// reload in high frequency
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

	public static void requestCityList() {
		ServerConnector.getInstance().httpGet(NetUtil.getCityUrl(), 0, null);
	}

	public static ArrayList<City> getCityListItems() {
		if (sCityListItems == null) {
			sCityListItems = new DataCachingItem(
					DataUpdater.DATA_UPDATE_TYPE_CITIES);
		}

		synchronized (sCityListItems) {
			ArrayList<City> data = (ArrayList<City>) sCityListItems.getData();

			if (data == null || checkDataOutOfDateStatus(sCityListItems)) {
				DataUpdater.requestDataUpdate(
						DataUpdater.DATA_UPDATE_TYPE_CITIES, null);
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

	/**
	 * Setter method of Alerts list
	 */
	public static void setCityListItems(ArrayList<City> data) {
		if (data == null) {
			return;
		}

		if (sCityListItems == null) {
			sCityListItems = new DataCachingItem(data,
					DataUpdater.DATA_UPDATE_TYPE_CITIES);
		} else {
			synchronized (sCityListItems) {
				sCityListItems.updateData(data);
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
