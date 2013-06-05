package com.njzk2.twitterbrowser;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.njzk2.twitterbrowser.EndlessScrollListener.OnEndReachedListener;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;

public class TweetLoader implements OnEndReachedListener {

	private static final String LIST_URL = "https://api.twitter.com/1.1/search/tweets.json";
	protected static final String TAG = TweetLoader.class.getSimpleName();
	private ArrayAdapter<JSONObject> tweetAdapter;
	private TweetJsonListener mDownListener = new TweetJsonListener();
	private String query = "freebandnames";
	private Activity activity;

	public TweetLoader(ArrayAdapter<JSONObject> tweetAdapter, Activity activity) {
		this.tweetAdapter = tweetAdapter;
		this.activity = activity;
	}

	public void setQuery(String query) {
		tweetAdapter.clear();
		this.query = query;
		onEndReached();
	}

	@Override
	public void onEndReached() {
		activity.setProgressBarIndeterminate(true);
		activity.setProgressBarIndeterminateVisibility(true);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("q", query));
		if (tweetAdapter.getCount() > 0) {
			try {
				long maxId = (tweetAdapter.getItem(tweetAdapter.getCount() - 1).getLong("id") - 1);
				params.add(new BasicNameValuePair("max_id", "" + maxId));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		MainActivity.mRequestQueue.add(new TweetsRequest(LIST_URL, mDownListener, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.w(TAG, "Error with request");
				error.printStackTrace();
			}
		}, params));
	}

	private class TweetJsonListener implements Listener<JSONObject> {

		@Override
		public void onResponse(JSONObject response) {
			JSONObject jsonObject = null;
			JSONArray tweets = response.optJSONArray("statuses");
			int count = tweets != null ? tweets.length() : 0;
			for (int i = 0; i < count; i++) {
				jsonObject = tweets.optJSONObject(i);
				if (jsonObject != null) {
					tweetAdapter.add(jsonObject);
				}
			}
			activity.setProgressBarIndeterminateVisibility(false);
		}
	}
}
