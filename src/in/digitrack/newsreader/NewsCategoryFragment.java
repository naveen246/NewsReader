package in.digitrack.newsreader;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

//https://raw.githubusercontent.com/naveen246/news_sources/master/news_data.json

public class NewsCategoryFragment extends Fragment {
	public static final String CATEGORY = "in.digitrack.newsreader.category";
	public static final String DEFAULT_CATEGORY = "India";
	private static final String NEWS_DATA_URL = "https://raw.githubusercontent.com/naveen246/news_sources/master/news_data.json";
	
	
	private Spinner categoryDropDown;
	private ListView newsSourceList;
	private String mCurrentCategory;
	private NewsData mNewsData;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNewsData = NewsData.get(getActivity());
		new NewsDataDownload().execute();
		setRetainInstance(true);
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_news_category, container, false);
		
		categoryDropDown = (Spinner)v.findViewById(R.id.category_dropdown);
		categoryDropDown.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mCurrentCategory = (String) parent.getItemAtPosition(position);
				updateNewsSources(mCurrentCategory);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
		
		newsSourceList = (ListView)v.findViewById(R.id.news_source_list);
		newsSourceList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String newsSource = (String) parent.getItemAtPosition(position);
				String url = mNewsData.getFeedUrl(mCurrentCategory, newsSource);
				Log.d("newsreader", url);
				Intent i = new Intent(getActivity(), NewsListActivity.class);
				i.putExtra(NewsListFragment.NEWS_SOURCE, newsSource);
				i.putExtra(NewsListFragment.FEED_URL, url);
				startActivity(i);
			}
		});
		
		updateUI();
		
		return v;
	}
	
	private void updateUI() {
		if(mNewsData.isDataAvailable()) {
			ArrayList<String> categories = mNewsData.getNewsCategories();
			CategoryAdapter categoryAdapter = new CategoryAdapter(categories);
			categoryDropDown.setAdapter(categoryAdapter);
			
			String category = categoryDropDown.getSelectedItem().toString();
			updateNewsSources(category);
		}
	}
	
	private void updateNewsSources(String category) {
		ArrayList<String> newsSources = mNewsData.getNewsSources(category);
		NewsSourceAdapter newsAdapter = new NewsSourceAdapter(newsSources);
		newsSourceList.setAdapter(newsAdapter);
	}
	
	private class NewsSourceAdapter extends ArrayAdapter<String> {

		public NewsSourceAdapter(ArrayList<String> newsSources) {
			super(getActivity(), android.R.layout.simple_list_item_1, newsSources);
		}
	}
	
	private class CategoryAdapter extends ArrayAdapter<String> {

		public CategoryAdapter(ArrayList<String> categories) {
			super(getActivity(), android.R.layout.simple_list_item_1, categories);
		}
	}
	
	private class NewsDataDownload extends AsyncTask<Void, Void, String> {
		ProgressDialog dialog;
		@Override
		protected String doInBackground(Void... arg0) {
			String resultString = null;
			try {
				resultString = new FeedFetchr().getResultString(NEWS_DATA_URL);
			} catch (IOException e) { }
			return resultString;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if(dialog.isShowing())
				dialog.dismiss();
			if(result != null) {
				mNewsData.loadJSONData(result);
				updateUI();
			}
		}
		
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(getActivity());
			dialog.setMessage("Loading data");
			dialog.show();
		}
	}
}
