package in.digitrack.newsreader;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class NewsPageFragment extends Fragment {
	private String mUrl;
	private WebView mWebView;
	private ProgressBar mProgressBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mUrl = getActivity().getIntent().getData().toString();
		Intent intent = getActivity().getIntent();
		if(intent != null) {
			String newsSource = intent.getStringExtra(NewsListFragment.NEWS_SOURCE);
			getActivity().getActionBar().setTitle(newsSource);
		}
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_news_page, null);
		
		mWebView = (WebView)v.findViewById(R.id.webView);
		mProgressBar = (ProgressBar)v.findViewById(R.id.newsPage_progressBar);
		
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setDisplayZoomControls(false);
		
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		});
		
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if(newProgress == 100) {
					mProgressBar.setVisibility(View.INVISIBLE);
				} else {
					mProgressBar.setVisibility(View.VISIBLE);
					mProgressBar.setProgress(newProgress);
				}
			}
		});
		
		mWebView.loadUrl(mUrl);
		
		return v;
	}
}
