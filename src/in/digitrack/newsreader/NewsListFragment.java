package in.digitrack.newsreader;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NewsListFragment extends ListFragment {
	
	private FeedDownloader mFeedThread;
	
	public static final String NEWS_SOURCE = "in.digitrack.newsreader.news_source";
	public static final String FEED_URL = "in.digitrack.newsreader.feed_url";

	public static Fragment newInstance(String newsSource, String feedUrl) {
		Bundle args = new Bundle();
		args.putString(NEWS_SOURCE, newsSource);
		args.putString(FEED_URL, feedUrl);
		Fragment fragment = new NewsListFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("newsreader", getArguments().getString(NEWS_SOURCE));
		Log.d("newsreader", getArguments().getString(FEED_URL));
		mFeedThread = new FeedDownloader(new Handler());
		mFeedThread.setListener(new FeedDownloader.Listener() {
			@Override
			public void onFeedDownloaded(ArrayList<News> newsList) {
				NewsAdapter adapter = new NewsAdapter(newsList);
				setListAdapter(adapter);
			}
		});
		mFeedThread.start();
		mFeedThread.getLooper();
		setRetainInstance(true);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		News news = (News)getListAdapter().getItem(position);

		Uri newsPageUri = Uri.parse(news.getLink());
		Intent i = new Intent(getActivity(), NewsPageActivity.class);
		i.setData(newsPageUri);
		startActivity(i);
	}
	
	private class NewsAdapter extends ArrayAdapter<News> {
		public NewsAdapter(ArrayList<News> newsList) {
			super(getActivity(), 0, newsList);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.news_list_item, null);
			}
			TextView newsTitleTxtView = (TextView)convertView.findViewById(R.id.news_title_textView);
			TextView newsDateTxtView = (TextView)convertView.findViewById(R.id.news_date_textView);
			TextView newsSnippetTxtView = (TextView)convertView.findViewById(R.id.news_snippet_textView);
			
			News news = getItem(position);
			newsTitleTxtView.setText(news.getTitle());
			newsDateTxtView.setText(news.getPublishedDate());
			newsSnippetTxtView.setText(news.getContentSnippet());
			
			return convertView;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mFeedThread.queueTask(getArguments().getString(FEED_URL));
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mFeedThread.quit();
	}
	
}
