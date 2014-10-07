package in.digitrack.newsreader;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;

public class NewsData {
	private static NewsData sNewsData;
	private Context mAppContext;
	
	private JSONObject mJSONObj;

	private NewsData(Context appContext) {
		mAppContext = appContext;
	}
	
	public static NewsData get(Context c) {
		if(sNewsData == null) {
			sNewsData = new NewsData(c.getApplicationContext());
		}
		return sNewsData;
	}
	
	public boolean isDataAvailable() {
		return mJSONObj != null;
	}
	
	public ArrayList<String> getNewsSources(String category) {
		if(mJSONObj != null) {
			ArrayList<String> newsSources = new ArrayList<String>();
			try {
				if(mJSONObj.has(category)) {
					JSONObject obj = mJSONObj.getJSONObject(category);
					JSONArray arr = obj.names();
					for(int i = 0; i < arr.length(); i++) {
						newsSources.add(arr.getString(i));
					}
				}
				
			} catch(Exception e) { }
			Collections.sort(newsSources);
			return newsSources;
		} else {
			return null;
		}
	}

	public ArrayList<String> getNewsCategories() {
		if(mJSONObj != null) {
			ArrayList<String> categories = new ArrayList<String>();
			JSONArray arr = mJSONObj.names();
			for(int i = 0; i < arr.length(); i++) {
				try {
					categories.add(arr.getString(i));
				} catch (JSONException e) { }
			}
			Collections.sort(categories);
			return categories;
		} else {
			return null;
		}
	}
	
	public String getFeedUrl(String category, String source) {
		String url = null;
		try {
			if(mJSONObj.has(category)) {
				JSONObject obj = mJSONObj.getJSONObject(category);
				if(obj.has(source)) {
					url = obj.getString(source);
				}
			}
		} catch(Exception e) { }
		return url;
	}
	
	public void loadJSONData(String jsonString) {
		try {
			mJSONObj = (JSONObject) new JSONTokener(jsonString).nextValue();
		} catch(Exception e) {}
	}
}
