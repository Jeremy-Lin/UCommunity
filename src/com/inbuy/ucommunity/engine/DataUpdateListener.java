package com.inbuy.ucommunity.engine;

/**
 * A listener for the data update notification.
 */

public interface DataUpdateListener {

	public void onUpdate(int status, int dataType, int id, Object arg);
}
