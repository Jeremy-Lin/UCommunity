package com.inbuy.ucommunity.engine;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;

import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.inbuy.ucommunity.util.ErrorCode;

public class ServerConnector implements Runnable {
	private final static String TAG = "ServerConnector";

	final static boolean DEBUG = true;
	final static boolean TRACK_PERFORMANCE = false;

	private final static int MSG_HTTPGET = 0;
	private final static int MSG_HTTPPOST = 1;
	private final static int MSG_QUIT = 2;

	private Handler mHandler;

	private int mNextRqtId = 1000;
	private ExecutorService mPool = Executors.newCachedThreadPool();
	private Thread mThread;

	private static ServerConnector mInstance;

	private ServerConnector() {
		Log.d(TAG, "ServerConnector: begin");
		mThread = new Thread(this);
		mThread.setDaemon(true);
		mThread.start();
		// Make sure that the mHandler is set.
		if (mHandler == null) {
			synchronized (mThread) {
				Log.d(TAG, "ServerConnector: mThread wait");
				try {
					mThread.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static ServerConnector getInstance() {
		if (mInstance == null) {
			mInstance = new ServerConnector();
		}
		return mInstance;
	}

	public int httpGet(String url, int dataType, ResponseListener listener) {
		// Should be atomically get next request ID.
		int requestId = mNextRqtId++;

		Log.d(TAG, "httpGet: url = " + url);

		Object[] obj = { url, new Integer(requestId), new Integer(dataType),
				listener };
		Message msg = mHandler.obtainMessage(MSG_HTTPGET, obj);
		mHandler.sendMessage(msg);
		return requestId;
	}

	class ConnectorHandler extends Handler {
		/**
		 * This internal method handles the actual request/response and it
		 * should be run in a thread.
		 */
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "ConnectorHandler handleMessage msg.what = " + msg.what);
			int errCode;
			switch (msg.what) {
			case MSG_HTTPGET:
				mPool.execute(new HttpGetTask(msg));
				break;
			case MSG_QUIT:
				Looper.myLooper().quit();
			}
		}
	}

	class HttpGetTask implements Runnable {
		private Object[] mArgs;

		public HttpGetTask(Message msg) {
			// The msg object is reusable by other threads, so cannot save it.
			mArgs = (Object[]) msg.obj;
		}

		@Override
		public void run() {
			String url = (String) mArgs[0];
			int requestId = (Integer) mArgs[1];
			int dataType = (Integer) mArgs[2];
			ResponseListener listener = (ResponseListener) mArgs[3];

			sHttpGet(url, requestId, dataType, listener);
		}
	}

	// Synchronous HTTP Get.
	void sHttpGet(String url, int requestId, int dataType,
			ResponseListener listener) {
		Log.d(TAG, "sHttpGet: url " + url + " requestId = " + requestId
				+ ", listener = " + listener);
		long t1, t2;
		if (TRACK_PERFORMANCE) {
			t1 = System.currentTimeMillis();
		}

		String result = null;
		int ecode = 0;
		HttpGet getMethod = new HttpGet(url);
		Log.d(TAG, "do the getRequest,url=" + url + "");
		// DefaultHttpClient client = new DefaultHttpClient(new
		// BasicHttpParams());

		AndroidHttpClient client = AndroidHttpClient
				.newInstance("Android-2.2.3");
		try {
			// getMethod.setHeader("User-Agent", USER_AGENT);
			HttpResponse httpResponse = client.execute(getMethod);
			// statusCode == 200 正常
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.d(TAG, "statuscode = " + statusCode);

			if (statusCode != 200) {
				ecode = statusCode;
				Log.e(TAG, "Request ID=" + requestId + ", status=" + statusCode
						+ ", url=" + url);
			} else {
				// 处理返回的httpResponse信息
				result = retrieveInputStream(httpResponse.getEntity());
			}
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
			ecode = ErrorCode.INTERNAL_ERROR;
		} catch (IllegalStateException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
			ecode = ErrorCode.INTERNAL_ERROR;
		} catch (IOException e) {
			ecode = ErrorCode.INTERNAL_ERROR;
			e.printStackTrace();
			Log.e(TAG, "Request ID=" + requestId + ", url=" + url);
		} finally {
			getMethod.abort();
		}
		if (TRACK_PERFORMANCE) {
			t2 = System.currentTimeMillis();
			Log.d("PERF", "httpGet=" + (t2 - t1));
		}
		Log.d(TAG, "reponse result = " + result);
		JSONArray obj = null;
		if (result != null) {
			try {
				obj = new JSONArray(result);
			} catch (JSONException e) {
				ecode = ErrorCode.INTERNAL_ERROR;
				e.printStackTrace();
				Log.e(TAG, "Request ID=" + requestId + ", url=" + url
						+ ", error message = " + e.getMessage());
			}
		}

		if (listener != null) {
			listener.onReceive(requestId, dataType, ecode, obj);
		}
	}

	/**
	 * 处理httpResponse信息,返回String
	 * 
	 * @param httpEntity
	 * @return String
	 */
	protected static String retrieveInputStream(HttpEntity httpEntity)
			throws UnsupportedEncodingException, IllegalStateException,
			IOException {

		int length = (int) httpEntity.getContentLength();
		// the number of bytes of the content, or a negative number if unknown.
		// If the content length is known but exceeds Long.MAX_VALUE, a negative
		// number is returned.
		// length==-1，下面这句报错，println needs a message
		if (length < 0) {
			Log.e(TAG, "length = " + length);
			length = 10000;
		}
		StringBuffer stringBuffer = new StringBuffer(length);
		InputStreamReader inputStreamReader = new InputStreamReader(
				httpEntity.getContent(), HTTP.UTF_8);
		char buffer[] = new char[length];
		int count;
		while ((count = inputStreamReader.read(buffer, 0, length - 1)) > 0) {
			stringBuffer.append(buffer, 0, count);
		}
		return stringBuffer.toString();
	}

	@Override
	public void run() {
		Log.d(TAG, "run: begin");
		Looper.prepare();
		mHandler = new ConnectorHandler();
		synchronized (mThread) {
			mThread.notify();
		}
		Looper.loop();
	}

	public synchronized void destroy() {
		if (mHandler != null) {
			mHandler.sendEmptyMessage(MSG_QUIT);
			mHandler = null;
		}

		mThread = null;
	}
}
