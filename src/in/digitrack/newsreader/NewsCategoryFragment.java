package in.digitrack.newsreader;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Intent;
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

public class NewsCategoryFragment extends Fragment {
	public static final String CATEGORY = "in.digitrack.newsreader.category";
	public static final String DEFAULT_CATEGORY = "India";
	
	private ListView newsSourceList;
	private String mCurrentCategory;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		//setListAdapter(adapter);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_news_category, container, false);
		
		ArrayList<String> categories = NewsData.get(getActivity()).getNewsCategories();
		CategoryAdapter categoryAdapter = new CategoryAdapter(categories);
		Spinner s = (Spinner)v.findViewById(R.id.category_dropdown);
		s.setAdapter(categoryAdapter);
		s.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mCurrentCategory = (String) parent.getItemAtPosition(position);
				updateNewsSources(mCurrentCategory);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		newsSourceList = (ListView)v.findViewById(R.id.news_source_list);
		updateNewsSources(DEFAULT_CATEGORY);
		newsSourceList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String newsSource = (String) parent.getItemAtPosition(position);
				String url = NewsData.get(getActivity()).getFeedUrl(mCurrentCategory, newsSource);
				Log.d("newsreader", url);
				Intent i = new Intent(getActivity(), NewsListActivity.class);
				i.putExtra(NewsListFragment.NEWS_SOURCE, newsSource);
				i.putExtra(NewsListFragment.FEED_URL, url);
				startActivity(i);
			}
		});
		return v;
	}
	
	private void updateNewsSources(String category) {
		ArrayList<String> newsSources = NewsData.get(getActivity()).getNewsSources(category);
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
}
