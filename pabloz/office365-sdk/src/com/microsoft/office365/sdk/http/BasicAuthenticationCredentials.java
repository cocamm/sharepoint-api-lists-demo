package com.microsoft.office365.sdk.http;

import com.microsoft.office365.sdk.Credentials;
import com.microsoft.office365.sdk.Platform;

import android.util.Base64;

/**
 * Credentials implementation for HTTP Basic Authentication
 */
public class BasicAuthenticationCredentials implements Credentials {
	private String mUsername;
	private String mPassword;
	private Base64Encoder mEncoder;
	
	/**
	 * Creates a BasicAuthenticationCredentials instance to be used on Android
	 * @throws InvalidPlatformException Throws an exception when invoked on a non-android platform
	 */
	public BasicAuthenticationCredentials() {
		this("", "");
	}
	
	/**
	 * Creates a BasicAuthenticationCredentials instance with a username and password to be used on Android
	 * @param username The username for the credentials
	 * @param password The password for the credentials
	 */
	public BasicAuthenticationCredentials(String username, String password) {
		if (!Platform.isAndroid()) {
			throw new InvalidPlatformException();
		}
		
		initialize(username, password, new Base64Encoder() {
			@Override
			public String encodeBytes(byte[] bytes) {
				return Base64.encodeToString(bytes, Base64.DEFAULT);
			}
		});
	}
	
	/**
	 * Creates a BasicAuthenticationCredentials instance with a username, password and an encoder
	 * @param username The username for the credentials
	 * @param password The password for the credentials
	 * @param encoder The Base64 encoder to use
	 */
	public BasicAuthenticationCredentials(String username, String password, Base64Encoder encoder) {
		initialize(username, password, encoder);
	}
	
	/**
	 * Initializes a BasicAuthenticationCredentials instance with a username and a password
	 * @param username The username for the credentials
	 * @param password The password for the credentials
	 * @param encoder The Base64 encoder to use
	 */
	private void initialize(String username, String password, Base64Encoder encoder) {
		mUsername = username;
		mPassword = password;
		mEncoder = encoder;
		
		if (encoder == null) {
			throw new IllegalArgumentException("encoder");
		}
	}
	
	/**
	 * Returns the username for the credentials
	 */
	public String getUsername() {
		return mUsername;
	}
	
	/**
	 * Sets the username for the credentials
	 * @param username username to set
	 */
	public void setUsername(String username) {
		mUsername = username;
	}
	
	/**
	 * Returns the password for the credentials
	 */
	public String getPassword() {
		return mPassword;
	}
	
	/**
	 * Sets the password for the credentials
	 * @param password password for the credentials
	 */
	public void setPassword(String password) {
		mPassword = password;
	}

	@Override
	public void prepareRequest(Request request) {
		String headerValue = mUsername + ":" + mPassword;
		
		headerValue = mEncoder.encodeBytes(headerValue.getBytes()).trim();
		
		request.addHeader("Authorization", "Basic " + headerValue);
	}
	
	/**
	 * Represents a Base64Encoder
	 */
	public interface Base64Encoder {
		/**
		 * Encodes a byte array
		 * @param bytes Bytes to encode
		 * @return The encoded bytes
		 */
		public String encodeBytes(byte[] bytes);
	}
	
	public class InvalidPlatformException extends RuntimeException {
		private static final long serialVersionUID = 1975952258601813204L;
	}
}
