package com.njzk2.twitterbrowser;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

public class TweetAdapter extends ArrayAdapter<JSONObject> {

	private static final String TAG = TweetAdapter.class.getSimpleName();

	public TweetAdapter(Context context) {
		super(context, 0);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
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
				holder.nameView.setText(user.optString("name", "<No name>"));
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

}
