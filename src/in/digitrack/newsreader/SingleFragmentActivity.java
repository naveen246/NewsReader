package in.digitrack.newsreader;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public abstract class SingleFragmentActivity extends Activity {
	
	protected abstract Fragment createFragment();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		
		FragmentManager fm = getFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
		
		if(fragment == null) {
			fragment = createFragment();
			fm.beginTransaction()
			  .add(R.id.fragmentContainer, fragment)
			  .commit();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.news_activity_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.notification_settings);
		if(PollService.isServiceAlarmOn(getApplicationContext())) {
			item.setTitle(R.string.disable_notification);
		} else {
			item.setTitle(R.string.enable_notification);
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.notification_settings:
			if(PollService.isServiceAlarmOn(getApplicationContext())) {
				PollService.setServiceAlarm(getApplicationContext(), false);
			} else {
				PollService.setServiceAlarm(getApplicationContext(), true);
			}
			invalidateOptionsMenu();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
