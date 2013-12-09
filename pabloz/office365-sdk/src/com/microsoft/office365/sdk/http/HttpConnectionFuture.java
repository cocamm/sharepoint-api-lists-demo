package com.microsoft.office365.sdk.http;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.microsoft.office365.sdk.ErrorCallback;
import com.microsoft.office365.sdk.OfficeFuture;

/**
 * A SinglaRFuture for Http operations
 */
public class HttpConnectionFuture extends OfficeFuture<Response> {
	
	private Queue<Throwable> mTimeoutQueue = new ConcurrentLinkedQueue<Throwable>();	
	private ErrorCallback mTimeoutCallback;
	private Object mTimeoutLock = new Object();
	
	/**
	 * Handles the timeout for an Http operation
	 * @param errorCallback The handler
	 */
	public void onTimeout(ErrorCallback errorCallback) {
		synchronized (mTimeoutLock) {
			mTimeoutCallback = errorCallback;

			while (!mTimeoutQueue.isEmpty()) {
				if (mTimeoutCallback != null) { 
					mTimeoutCallback.onError(mTimeoutQueue.poll());
				}
			}
		}
	}

	/**
	 * Triggers the timeout error
	 * @param error The error
	 */
	public void triggerTimeout(Throwable error) {
		synchronized (mTimeoutLock) {
			if (mTimeoutCallback != null) {
				mTimeoutCallback.onError(error);
			} else {
				mTimeoutQueue.add(error);
			}
		}
	}
	
	/**
	 * Represents the callback to invoke when a response is returned after a request
	 */
	public interface ResponseCallback {
		/**
		 * Callback invoked when a response is returned by the request
		 * @param response The returned response
		 */
		public void onResponse(Response response) throws Exception;
	}
	
}
