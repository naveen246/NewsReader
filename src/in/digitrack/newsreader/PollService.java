package in.digitrack.newsreader;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

public class PollService extends IntentService {
	private static final String TAG = "PollService";
	private static final int POLL_INTERVAL = 1000 * 60 * 60 * 3;
	public static final String  PREF_IS_ALARM_ON = "isAlarmOn";
	
	public PollService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		@SuppressWarnings("deprecation")
		boolean isNetworkAvailable = cm.getBackgroundDataSetting() && cm.getActiveNetworkInfo() != null;
		if(!isNetworkAvailable) return;
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		int lastPolledNewsIndex = prefs.getInt(FeedFetchr.LAST_POLLED_NEWS, 0);
		
		String topStoriesData = prefs.getString(NewsData.TOP_STORIES, null);
		
		JSONObject obj = null;
		JSONArray arr = null;
		ArrayList<String> newsUrl = new ArrayList<String>();
		ArrayList<News> newsData = new ArrayList<News>();
		int newPollNewsIndex = 0;
		String newsSource = null;
		try {
			obj = (JSONObject)new JSONTokener(topStoriesData).nextValue();
			arr = obj.names();
			newPollNewsIndex = (lastPolledNewsIndex + 1) % arr.length();
			prefs.edit().putInt(FeedFetchr.LAST_POLLED_NEWS, newPollNewsIndex).commit();
			for(int i = 0; i < arr.length(); i++) {
				newsUrl.add(obj.getString(arr.getString(i)));
				if(i == newPollNewsIndex) {
					newsSource = arr.getString(i);
				}
			}
			String url = FeedFetchr.buildUrl(newsUrl.get(newPollNewsIndex));
			newsData = new FeedFetchr().getResult(url);
		} catch(Exception e) {}
		
		if(newsData.size() == 0) return;
		
		Intent i = new Intent(this, NewsListActivity.class);
		i.putExtra(NewsListFragment.NEWS_SOURCE, newsSource);
		i.putExtra(NewsListFragment.FEED_URL, newsUrl.get(newPollNewsIndex));
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(NewsListActivity.class);
		stackBuilder.addNextIntent(i);
		// Gets a PendingIntent containing the entire back stack
		PendingIntent pi = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
		
		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
		remoteViews.setTextViewText(R.id.notification_textView, newsData.get(0).getTitle());
		
		Notification notification = new NotificationCompat.Builder(this)
										.setTicker(newsData.get(0).getTitle())
										.setSmallIcon(R.drawable.ic_launcher)
										.setContent(remoteViews)
										.setContentIntent(pi)
										.setAutoCancel(true)
										.build();
		
		NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(0, notification);
	}
	
	public static void setServiceAlarm(Context context, boolean setOn) {
		Intent i = new Intent(context, PollService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		if(setOn) {
			alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), POLL_INTERVAL, pi);
		} else {
			alarmManager.cancel(pi);
			pi.cancel();
		}
		
		PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_IS_ALARM_ON, setOn).commit();
	}
	
	public static boolean isServiceAlarmOn(Context context) {
		Intent i = new Intent(context, PollService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
		return pi != null;
	}

}
