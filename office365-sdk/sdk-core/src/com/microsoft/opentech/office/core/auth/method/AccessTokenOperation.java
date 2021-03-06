package com.microsoft.opentech.office.core.auth.method;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.microsoft.opentech.office.core.action.async.IOperationCallback;
import com.microsoft.opentech.office.core.auth.ISharePointCredentials;
import com.microsoft.opentech.office.core.net.HttpOperation;

/**
 * Operation retrieves access token for OAuth authentication.
 */
class AccessTokenOperation extends HttpOperation {

    private static final String ACCESS_TOKEN_REQUEST_URL_FORMAT = "https://accounts.accesscontrol.windows.net/%s/tokens/OAuth/2";

    private static final String ACCESS_TOKEN_REQUEST_CONTENT = "grant_type=authorization_code&client_id=%s%%40%s&client_secret=%s&code=%s&redirect_uri=%s&resource=00000003-0000-0ff1-ce00-000000000000%%2F%s%%40%s";

    private ISharePointCredentials mCreds;

    /**
     * Creates a new instance of {@link AccessTokenOperation} class.
     * 
     * @param listener Callback to be invoked when operation finishes.
     * @param context Application context.
     * @param creds Authentication credentials.
     */
    public AccessTokenOperation(IOperationCallback<String> listener, Context context, ISharePointCredentials creds) {
        super(listener, context);
        mCreds = creds;
    }

    @Override
    protected Object getPostData() {
        String requestContent = null;
        try {
            URL url = new URL(mCreds.getUrl());
            String sharepointHost = url.getHost();

            requestContent = String.format(ACCESS_TOKEN_REQUEST_CONTENT, encode(mCreds.getClientId()), mCreds.getDomain(),
                    encode(mCreds.getClientSecret()), mCreds.getAccessCode(), mCreds.getRedirectUrl(), sharepointHost, mCreds.getDomain());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestContent;
    }

    @Override
    protected URI getServerUrl() {
        return URI.create(String.format(ACCESS_TOKEN_REQUEST_URL_FORMAT, mCreds.getDomain()));
    }

    @Override
    protected List<Pair<String, String>> getRequestHeaders() throws RuntimeException {
        List<Pair<String, String>> headers = super.getRequestHeaders();
        headers.add(Pair.create(CONTENT_TYPE_HTTP_HEADER_NAME, "application/x-www-form-urlencoded"));
        return headers;
    }

    /**
     * Returns url encoded client id.
     * 
     * @param clientId ID to be encoded.
     * @return Encoded client id.
     * @throws UnsupportedEncodingException when id cannot be encoded in UTF-8 encoding.
     */
    protected static String encode(String clientId) throws UnsupportedEncodingException {
        String encoded = URLEncoder.encode(clientId, "UTF-8");
        return encoded;
    }

    @Override
    protected boolean handleServerResponse(String response) throws IOException {
        if (TextUtils.isEmpty(response)) return false;

        String accessToken = null;
        try {
            JSONObject json = new JSONObject(response);
            accessToken = json.getString("access_token");
        } catch (JSONException e) {
            throw new IOException("Failed to extract a token from server response. Invalid JSON format. 'access_token' item not found.", e);
        }

        mResult = accessToken;

        return true;
    }

}
