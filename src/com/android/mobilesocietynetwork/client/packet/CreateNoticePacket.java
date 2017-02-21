package com.android.mobilesocietynetwork.client.packet;

import java.util.ArrayList;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;

import com.android.mobilesocietynetwork.client.util.Constants;


public class CreateNoticePacket extends IQ
{
	private static final String NAME="activity";
	private static final String NAMESPACE="com.msn.activity";
	private static final String TYPE="createActivity";
	private static final String SERVERNAME = Constants.SERVER_NAME;
	
	private String title;
	private String content;
	private String time;
	private String imageString=null;
	private String longitude;//¾­¶È
	private String latitude;//Î³¶È
	private String address;
	private int limit;
	private ArrayList<String> labelList = new ArrayList<String>();
	
	
	public CreateNoticePacket()
	{
		super();
		setTo();
		setType(Type.SET);
	}

	public void setTo()
	{
		super.setTo(SERVERNAME);
	}


	public String getTitle()
	{
		return title;
	}


	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getContent()
	{
		return content;
	}


	public void setContent(String content)
	{
		this.content = content;
	}

	public String getTime()
	{
		return time;
	}


	public void setTime(String time)
	{
		this.time = time;
	}
	
	public String getImageString(){
		return imageString;
	}
	
	public void setImageString(String imageString){
		this.imageString=imageString;
	}
	
	public String getLongitude()
	{
		return longitude;
	}


	public void setLongitude(String longitude)
	{
		this.longitude = longitude;
	}
	
	public String getLatitude()
	{
		return latitude;
	}


	public void setLatitude(String latitude)
	{
		this.latitude = latitude;
	}
	
	public String getAddress()
	{
		return address;
	}


	public void setAddress(String address)
	{
		this.address = address;
	}
	
	public int getLimit()
	{
		return limit;
	}


	public void setLimit(int limit)
	{
		this.limit = limit;
	}
	
	public ArrayList<String> getLabelList()
	{
		return labelList;
	}


	public void setLabelList(ArrayList<String> labelList)
	{
		this.labelList = labelList;
	}
	

	@Override
	public String getChildElementXML()
	{
		StringBuffer buf = new StringBuffer();
	//	buf.append("<").append(NAME).append(" xmlns=\"").append(NAMESPACE).append("\">");
		buf.append("<").append(NAME).append(" xmlns=\"").append(NAMESPACE).append("\" type=\"").append(TYPE).append("\"").append(">");
    //	 buf.append("<type>").append(TYPE).append("</type>");  
		 buf.append("<title>").append(title).append("</title>");  
		 buf.append("<imageString>").append(imageString).append("</imageString>"); 
		 buf.append("<content>").append(content).append("</content>"); 
		 buf.append("<time>").append(time).append("</time>"); 
		 buf.append("<location").append(" longitude=\"").append(longitude).append("\" latitude=\"").append(latitude).append("\"/>"); 
		 buf.append("<address>").append(address).append("</address>"); 		
		 buf.append("<labels>");
		 for(int i=0;i<labelList.size();i++)
		 {
		 buf.append("<label>").append(labelList.get(i)).append("</label>");
	 }
		 buf.append("</labels>");
		 buf.append("<limit>").append(limit).append("</limit>"); 		
		 buf.append("</").append(NAME).append(">");		 
		return buf.toString();
	}
	

public static void main(String[] args)
	{
		CreateNoticePacket createNoticeIQ = new CreateNoticePacket();
	   createNoticeIQ.setTitle("title");
		createNoticeIQ.setTime("startTime");
		createNoticeIQ.setAddress("location");
		createNoticeIQ.setContent("content");
		createNoticeIQ.setLimit(1);
		createNoticeIQ.setLongitude("longitude");
		createNoticeIQ.setLatitude("latitude");
		String buf=createNoticeIQ.getChildElementXML();
		System.out.println(buf);
	}
	
}
