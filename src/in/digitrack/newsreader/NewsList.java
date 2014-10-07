package in.digitrack.newsreader;

import java.util.ArrayList;

public class NewsList {
	private String mTitle;
	private ArrayList<News> newsList;
	
	public NewsList() {
		setNewsList(new ArrayList<News>());
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public ArrayList<News> getNewsList() {
		return newsList;
	}

	public void setNewsList(ArrayList<News> newsList) {
		this.newsList = newsList;
	}
	
	public void add(News news) {
		newsList.add(news);
	}
}
