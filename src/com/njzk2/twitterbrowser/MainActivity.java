package com.njzk2.twitterbrowser;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends ListActivity {

	protected static final String TAG = MainActivity.class.getSimpleName();
	private static final String TOKEN_URL = "https://api.twitter.com/oauth2/token";
	private TweetAdapter tweetAdapter = null;
	public static ImageLoader mImageLoader;
	public static RequestQueue mRequestQueue;
	private TweetLoader tweetLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		mRequestQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mRequestQueue, new BitmapLru(64000));
		StringRequest request = new TokenRequest(Method.POST, TOKEN_URL, new Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject object = new JSONObject(response);
					Log.w(TAG, object.optString("access_token"));
					TwitterValues.ACCESS_TOKEN = object.optString("access_token");

					tweetAdapter = new TweetAdapter(MainActivity.this);
					tweetLoader = new TweetLoader(tweetAdapter, MainActivity.this);
					setListAdapter(tweetAdapter);
					EndlessScrollListener listener = new EndlessScrollListener();
					listener.setOnEndReachedListener(tweetLoader);
					getListView().setOnScrollListener(listener);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "Error during request");
				error.printStackTrace();
			}
		});
		mRequestQueue.add(request);
		onSearchRequested();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			tweetLoader.setQuery(query);
		}
	}
}
