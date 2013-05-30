package com.njzk2.twitterbrowser;

import java.util.HashMap;
import java.util.Map;

import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

final class TokenRequest extends StringRequest {
	TokenRequest(int method, String url, Listener<String> listener, ErrorListener errorListener) {
		super(method, url, listener, errorListener);
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		Map<String, String> params = new HashMap<String, String>();
		params.put("grant_type", "client_credentials");
		return params;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = new HashMap<String, String>();
		String auth = "Basic "
				+ Base64.encodeToString(
						(TwitterValues.CONSUMER_KEY + ":" + TwitterValues.CONSUMER_SECRET).getBytes(),
						Base64.NO_WRAP);
		Log.d(MainActivity.TAG, "Auth " + auth);
		headers.put("Authorization", auth);
		return headers;
	}
}