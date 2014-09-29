package in.digitrack.newsreader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
		loadJSONData();
	}
	
	public static NewsData get(Context c) {
		if(sNewsData == null) {
			sNewsData = new NewsData(c.getApplicationContext());
		}
		return sNewsData;
	}
	
	public ArrayList<String> getNewsSources(String category) {
		if(mJSONObj == null)
			loadJSONData();
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
	}

	public ArrayList<String> getNewsCategories() {
		if(mJSONObj == null)
			loadJSONData();
		ArrayList<String> categories = new ArrayList<String>();
		JSONArray arr = mJSONObj.names();
		for(int i = 0; i < arr.length(); i++) {
			try {
				categories.add(arr.getString(i));
			} catch (JSONException e) { }
		}
		Collections.sort(categories);
		return categories;
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
	
	private void loadJSONData() {
		BufferedReader reader = null;
		try {
			InputStream in = mAppContext.getResources().openRawResource(R.raw.news_data);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line = new String();
			while((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
			
			mJSONObj = (JSONObject) new JSONTokener(jsonString.toString()).nextValue();
			
			if(reader != null)
				reader.close();
		} catch(Exception e) {}
	}
	
}
