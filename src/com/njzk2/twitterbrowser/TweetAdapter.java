package com.njzk2.twitterbrowser;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;

public class TweetAdapter extends ArrayAdapter<JSONObject> {

	private int threshold = Integer.MAX_VALUE;

	protected static final String TAG = TweetAdapter.class.getSimpleName();

	private static final String LIST_URL = "https://api.twitter.com/1.1/statuses/user_timeline.json";

	private TweetJsonListener mDownListener = new TweetJsonListener();
	private JSONObject loadingObject = new JSONObject();

	private String screenName;

	public TweetAdapter(Context context, String screenName) {
		super(context, 0);
		this.screenName = screenName;
		loadDown();
	}

	private void loadDown() {
		add(loadingObject);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("screen_name", screenName));
		if (getCount() > 1) {
			try {
				long maxId = (getItem(getCount() - 1).getLong("id") - 1);
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position >= threshold) {
			threshold = Integer.MAX_VALUE;
			loadDown();
		}
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.twitter_item, null);
			holder = new ViewHolder();
			holder.imageView = (NetworkImageView) convertView.findViewById(R.id.profile_image);
			holder.nameView = (TextView) convertView.findViewById(R.id.name);
			holder.textView = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		JSONObject currentItem = getItem(position);
		if (currentItem.length() == 0) {
			holder.nameView.setText(R.string.loading);
			holder.textView.setText("");
			holder.imageView.setImageResource(R.drawable.ic_launcher);
		} else {
			try {
				JSONObject user = currentItem.getJSONObject("user");
				holder.nameView.setText("#" + position + " " + user.optString("name", "<No name>"));
				holder.imageView.setImageUrl(user.getString("profile_image_url"), MainActivity.mImageLoader);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			holder.textView.setText(currentItem.optString("text", "<No text>"));
		}
		return convertView;
	}

	private static class ViewHolder {
		private NetworkImageView imageView;
		private TextView nameView;
		private TextView textView;
	}

	private class TweetJsonListener implements Listener<JSONArray> {

		@Override
		public void onResponse(JSONArray response) {
			Log.d(TAG, "received " + response.length() + " items");
			JSONObject jsonObject = null;
			for (int i = 0; i < response.length(); i++) {
				jsonObject = response.optJSONObject(i);
				if (jsonObject != null) {
					add(jsonObject);
				}
			}
			remove(loadingObject);
			threshold = getCount() - 1;
		}
	}
}
