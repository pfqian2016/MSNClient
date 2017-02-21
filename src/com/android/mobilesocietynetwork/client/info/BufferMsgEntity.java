package com.android.mobilesocietynetwork.client.info;

import java.util.ArrayList;

public class BufferMsgEntity {
	private String throwid; 
	private String destination; // ��ϢĿ�ķ�
	private String source; // ��Ϣ���ͷ�
	private String date;// ��Ϣ����
	private String life;//��Ϣ��������
	private String message;// ��Ϣ����
	private ArrayList<String> passList = new ArrayList<String>();//��Ϣת����
	
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
