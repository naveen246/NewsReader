package in.digitrack.newsreader;

public class News {
	private String mTitle;
	private String mLink;
	private String mPublishedDate;
	private String mContentSnippet;
	
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String title) {
		mTitle = title;
	}
	public String getLink() {
		return mLink;
	}
	public void setLink(String link) {
		mLink = link;
	}
	public String getPublishedDate() {
		return mPublishedDate;
	}
	public void setPublishedDate(String publishedDate) {
		mPublishedDate = publishedDate;
	}
	public String getContentSnippet() {
		return mContentSnippet;
	}
	public void setContentSnippet(String contentSnippet) {
		mContentSnippet = contentSnippet;
	}
}
