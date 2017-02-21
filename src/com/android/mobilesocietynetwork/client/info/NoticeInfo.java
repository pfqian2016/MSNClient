package com.android.mobilesocietynetwork.client.info;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Base64;
import android.graphics.BitmapFactory;



public class NoticeInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 活动通知的实体，用来存活动内容 包括：活动id，发布时间、活动图片、标题、内容、发布者、活动时间、活动地点）
	 */
	private String id;
	private String pubTime;
	private String imageString;
	private String title;
	private String content;
	private String informer;
	private String startTime;
	private String location;
	private String distance;
	private ArrayList<String> labels =new ArrayList<String>() ;
	private String peopleNum;
	private String limit;
	private String isJoined;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPubTime() {
		return pubTime;
	}

	public void setPubTime(String pubTime) {
		this.pubTime = pubTime;
	}

	public String getImageString(){
		return imageString;
	}

	public void setImageString(String imageString){
		this.imageString=imageString;
	}
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setInformer(String informer) {
		this.informer = informer;
	}

	public String getInformer() {
		return informer;
	}
	
	public String getStartTime() {
		return startTime;
	}	

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}
	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}
	
	public ArrayList<String> getLabels() {
		return labels;
	}

	public void setLabels(ArrayList<String> labels) {
		this.labels = labels;
	}
	
	public void addLabel(String label) {
		labels.add(label);
	}
	
	public String getPeopleNum() {
		return peopleNum;
	}

	public void setPeopleNum(String peopleNum) {
		this.peopleNum = peopleNum;
	}
	
	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}
	
	public String getIsJoined() {
		return isJoined;
	}

	public void setIsJoined(String isJoined) {
		this.isJoined = isJoined;
	}
	
}
