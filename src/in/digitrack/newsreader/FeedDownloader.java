package in.digitrack.newsreader;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

public class FeedDownloader extends HandlerThread {

	Handler mHandler;
	Handler mResponseHandler;
	Listener mListener;
	ArrayList<News> mNewsList;
	
	public static final int MESSAGE_DOWNLOAD = 0;
	
	public FeedDownloader(Handler responseHandler) {
		super("");
		mResponseHandler = responseHandler;
	}
	
	public interface Listener {
		public void onFeedDownloaded(ArrayList<News> newsList);
	}
	
	public void setListener(Listener listener) {
		mListener = listener;
	}
	
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onLooperPrepared() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == MESSAGE_DOWNLOAD) {
					String url = FeedFetchr.buildUrl((String)msg.obj);
					try {
						mNewsList = new FeedFetchr().getResult(url);
					} catch (Exception e) { }
					
					mResponseHandler.post(new Runnable() {
						@Override
						public void run() {
							mListener.onFeedDownloaded(mNewsList);
						}
					});
				}
			}
		};
	}
	
	public void queueTask(String url) {
		if(mHandler != null){
			mHandler.obtainMessage(MESSAGE_DOWNLOAD, url).sendToTarget();
		}
	}

}
