package in.digitrack.newsreader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.net.Uri;
import android.util.Log;

public class FeedFetchr {
	
	private static final String ENDPOINT = "https://ajax.googleapis.com/ajax/services/feed/load";
	private static final String VERSION = "2.0";
	private static final String NUMBER_OF_ENTRIES = "20";
	
	public static String buildUrl(String feedUrl) {
		String url = Uri.parse(ENDPOINT).buildUpon()
				.appendQueryParameter("v", VERSION)
				.appendQueryParameter("q", feedUrl)
				.appendQueryParameter("num", NUMBER_OF_ENTRIES)
				.build().toString();
		return url;
	}
	
	public String getResultString(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		try {
			InputStream in = connection.getInputStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return null;
			}
			
			byte[] buffer = new byte[1024];
			int bytesRead = 0;
			while((bytesRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
			}
			out.close();
			return new String(out.toByteArray());
		} finally {
			connection.disconnect();
		}
	}
	
	private ArrayList<News> parseResult(String jsonString) throws JSONException {
		JSONObject json = (JSONObject)new JSONTokener(jsonString).nextValue();
		ArrayList<News> newsList = new ArrayList<News>();
		JSONArray jsonArr = json.getJSONObject("responseData").getJSONObject("feed").getJSONArray("entries");
		for(int i = 0; i < jsonArr.length(); i++) {
			News news = new News();
			JSONObject obj = jsonArr.getJSONObject(i);
			news.setTitle(obj.getString("title"));
			news.setLink(obj.getString("link"));
			news.setPublishedDate(obj.getString("publishedDate"));
			news.setContentSnippet(obj.getString("contentSnippet"));
			newsList.add(news);
		}
		return newsList;
	}

	public ArrayList<News> getResult(String url) throws IOException, JSONException {
		String resultString = getResultString(url);
		return parseResult(resultString);
	}
}
