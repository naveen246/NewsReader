package in.digitrack.newsreader;

import android.app.Fragment;

public class NewsListActivity extends SingleFragmentActivity {
	
	@Override
	protected Fragment createFragment() {
		String newsSource = getIntent().getStringExtra(NewsListFragment.NEWS_SOURCE);
		String feedUrl = getIntent().getStringExtra(NewsListFragment.FEED_URL);
		return NewsListFragment.newInstance(newsSource, feedUrl);
	}

}
