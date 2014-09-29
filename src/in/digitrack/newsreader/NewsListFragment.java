package in.digitrack.newsreader;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

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
			public void onFeedDownloaded(String xmlString) {
				Log.d("newsreader", xmlString);
			}
		});
		mFeedThread.start();
		mFeedThread.getLooper();
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
