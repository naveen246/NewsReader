package in.digitrack.newsreader;

import android.app.Fragment;

public class NewsListActivity extends SingleFragmentActivity {
	
	@Override
	protected Fragment createFragment() {
		return NewsListFragment.newInstance(getIntent().getExtras());
	}

}
