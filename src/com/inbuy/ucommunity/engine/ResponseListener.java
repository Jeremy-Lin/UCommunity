package com.inbuy.ucommunity.engine;

/**
 * A listener for the response from an asynchronous call.
 */
public interface ResponseListener {
	/**
	 * Upon a receipt of the response, this method will be called.
	 * 
	 * @param requestId
	 *            The request ID from the asynchronous call invocation.
	 * @param errorCode
	 *            0 for success, otherwise, an error code.
	 * @param response
	 *            The populated response object to the asynchronous call.
	 */
	public void onReceive(int requestId, int dataType, int errorCode,
			Object response);
}
