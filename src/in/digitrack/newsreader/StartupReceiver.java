package in.digitrack.newsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StartupReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean setOn = prefs.getBoolean(PollService.PREF_IS_ALARM_ON, false);
		PollService.setServiceAlarm(context, setOn);
	}

}
