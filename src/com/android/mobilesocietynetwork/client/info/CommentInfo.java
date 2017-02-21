package com.android.mobilesocietynetwork.client.info;

public class CommentInfo {
	/**
	 * 评论的实体，用来存活动内容 包括：发布者、发布时间、内容
	 */
	private String pubName;
	private String pubDate;
	private String content;
	private String imageString;

	public String getPubName() {
		return pubName;
	}

	public void setPubName(String pubName) {
		this.pubName = pubName;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}
	
	public String getImageString(){
		return imageString;
	}

	public void setImageString(String imageString){
		this.imageString=imageString;
	}

}
