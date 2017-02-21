package com.android.mobilesocietynetwork.client.info;

import java.util.ArrayList;

public class BufferMsgEntity {
	private String throwid; 
	private String destination; // 消息目的方
	private String source; // 消息发送方
	private String date;// 消息日期
	private String life;//消息的生命期
	private String message;// 消息内容
	private ArrayList<String> passList = new ArrayList<String>();//消息转发者
	
public BufferMsgEntity(){
	super();
}
	public BufferMsgEntity( String throwid,String destination, String source,String date, String life,String text, ArrayList<String> passList)
	{
		super();
		this.throwid = throwid;
		this.destination = destination;
		this.source = source;
		this.date = date;
		this.message = text;
		this.passList=passList;
	}


	public String getThrowid() {
		return throwid;
	}
	public void setThrowid(String throwid) {
		this.throwid = throwid;
	}
	public String getDestination() {
		return destination;
	}


	public void setDestination(String destination) {
		this.destination = destination;
	}


	public String getSource() {
		return source;
	}


	public void setSource(String source) {
		this.source = source;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}

	public String getLife() {
		return life;
	}
	
	public void setLife(String life) {
		this.life = life;
	}

	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
	
	public ArrayList<String> getPassList()
	{
		return passList;
	}


	public void setPassList(ArrayList<String> passList)
	{
		this.passList = passList;
	}



}
