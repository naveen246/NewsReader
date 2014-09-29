package in.digitrack.newsreader;

import android.app.Fragment;


public class NewsActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new NewsCategoryFragment();
	}
	
	
}
