package in.digitrack.newsreader;

import android.app.Fragment;

public class NewsPageActivity extends SingleFragmentActivity {
	
	@Override
	protected Fragment createFragment() {
		return new NewsPageFragment();
	}

}
