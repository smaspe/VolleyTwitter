package com.njzk2.twitterbrowser;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Menu;

import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends ListActivity {

	protected static final String TAG = MainActivity.class.getSimpleName();
	private static final String TOKEN_URL = "https://api.twitter.com/oauth2/token";
	private TweetAdapter tweetAdapter = null;
	public static ImageLoader mImageLoader;
	public static RequestQueue mRequestQueue;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

					tweetAdapter = new TweetAdapter(MainActivity.this, "Maitre_Eolas");
					setListAdapter(tweetAdapter);

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
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private static class BitmapLru extends LruCache<String, Bitmap> implements ImageCache {

		public BitmapLru(int maxSize) {
			super(maxSize);
		}

		@Override
		public Bitmap getBitmap(String url) {
			return get(url);
		}

		@Override
		public void putBitmap(String url, Bitmap bitmap) {
			put(url, bitmap);
		}

	}
}
