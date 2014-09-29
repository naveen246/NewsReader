package in.digitrack.newsreader;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

public class FeedDownloader extends HandlerThread {

	Handler mHandler;
	Handler mResponseHandler;
	Listener mListener;
	String xmlString;
	
	public static final int MESSAGE_DOWNLOAD = 0;
	
	public FeedDownloader(Handler responseHandler) {
		super("");
		mResponseHandler = responseHandler;
	}
	
	public interface Listener {
		public void onFeedDownloaded(String xmlString);
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
					try {
						xmlString = new FeedFetchr().getXmlString((String)msg.obj);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mResponseHandler.post(new Runnable() {
						@Override
						public void run() {
							mListener.onFeedDownloaded(xmlString);
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
