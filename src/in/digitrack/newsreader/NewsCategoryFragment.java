package in.digitrack.newsreader;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

//https://raw.githubusercontent.com/naveen246/news_sources/master/news_data.json

public class NewsCategoryFragment extends Fragment {
	public static final String CATEGORY = "in.digitrack.newsreader.category";
	public static final String DEFAULT_CATEGORY = "Top Stories";
	private static final String NEWS_DATA_URL = "https://raw.githubusercontent.com/naveen246/news_sources/master/news_data.json";
	
	private DrawerLayout mDrawerLayout;
	private Button showCategoryBtn;
	private ListView mCategoryDrawer;
	private ListView newsSourceList;
	private String mCurrentCategory;
	private NewsData mNewsData;
	private TextView categoryTxtView;
	
	private SharedPreferences prefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNewsData = NewsData.get(getActivity());
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		new NewsDataDownload().execute();
		mCurrentCategory = prefs.getString(CATEGORY, null);
		setRetainInstance(true);
	}
	
	public void startNewsListActivity(String newsSource, String url) {
		prefs.edit().putString(CATEGORY, mCurrentCategory).commit();
		Intent i = new Intent(getActivity(), NewsListActivity.class);
		i.putExtra(NewsListFragment.NEWS_SOURCE, newsSource);
		i.putExtra(NewsListFragment.FEED_URL, url);
		startActivity(i);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_news_category, container, false);
		
		mCategoryDrawer = (ListView)v.findViewById(R.id.category_listView);
		newsSourceList = (ListView)v.findViewById(R.id.news_source_list);
		mDrawerLayout = (DrawerLayout) v.findViewById(R.id.drawer_layout);
		showCategoryBtn = (Button)v.findViewById(R.id.show_drawer_button);
		categoryTxtView = (TextView)v.findViewById(R.id.category_textView);
		
		showCategoryBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDrawerLayout.openDrawer(mCategoryDrawer);
			}
		});
		
		categoryTxtView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDrawerLayout.openDrawer(mCategoryDrawer);
			}
		});
		
		mCategoryDrawer.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mCurrentCategory = (String) parent.getItemAtPosition(position);
				categoryTxtView.setText(mCurrentCategory);
				updateNewsSources(mCurrentCategory);
				mDrawerLayout.closeDrawer(mCategoryDrawer);
			}
		});
		
		newsSourceList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String newsSource = (String) parent.getItemAtPosition(position);
				String url = mNewsData.getFeedUrl(mCurrentCategory, newsSource);
				startNewsListActivity(newsSource, url);
			}
		});
		
		updateUI();
		
		return v;
	}
	
	private void updateUI() {
		if(mNewsData.isDataAvailable()) {
			ArrayList<String> categories = mNewsData.getNewsCategories();
			CategoryAdapter categoryAdapter = new CategoryAdapter(categories);
			mCategoryDrawer.setAdapter(categoryAdapter);
			if(mCurrentCategory == null) {
				mCurrentCategory = DEFAULT_CATEGORY;
			}
			categoryTxtView.setText(mCurrentCategory);
			updateNewsSources(mCurrentCategory);
		}
	}
	
	private void updateNewsSources(String category) {
		ArrayList<String> newsSources = mNewsData.getNewsSources(category);
		NewsSourceAdapter newsAdapter = new NewsSourceAdapter(newsSources);
		newsSourceList.setAdapter(newsAdapter);
	}
	
	private class NewsSourceAdapter extends ArrayAdapter<String> {

		public NewsSourceAdapter(ArrayList<String> newsSources) {
			super(getActivity(), 0, newsSources);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
			}

			if(position % 2 == 0) {
				convertView.setBackgroundColor(getResources().getColor(R.color.dark_shade));
			} 
			
			TextView newsSourceTxtView = (TextView)convertView;
			newsSourceTxtView.setText(getItem(position));
			
			return convertView;
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
			if(dialog.isShowing()) {
				dialog.dismiss();
			}
			if(result != null) {
				mNewsData.loadJSONData(result);
				updateUI();
				prefs.edit().putString(NewsData.TOP_STORIES, mNewsData.getTopStoriesData()).commit();
			}
		}
		
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(getActivity());
			dialog.setMessage("Loading data");
			dialog.setCancelable(true);
			dialog.show();
		}
	}
}
