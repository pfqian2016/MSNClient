package com.android.mobilesocietynetwork.client.packet;

import java.util.ArrayList;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;

import com.android.mobilesocietynetwork.client.util.Constants;

public class RecommendNoticePacket  extends IQ {
	private static final String NAME="activity";
	private static final String NAMESPACE="com.msn.activity";
	private static final String TYPE="askRecommendation";
	private static final String SERVERNAME = Constants.SERVER_NAME;
	
	private String longitude;//经度
	private String latitude;//纬度
	private String distance;//要求距离
	
	public RecommendNoticePacket()
	{
		super();
		setTo();
		setType(Type.SET);
	}

	public void setTo()
	{
		super.setTo(SERVERNAME);
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
	
	public String getDistance()
	{
		return distance;
	}


	public void setDistance(String distance)
	{
		this.distance = distance;
	}

	@Override
	public String getChildElementXML()
	{
		StringBuffer buf = new StringBuffer();
	//	buf.append("<").append(NAME).append(" xmlns=\"").append(NAMESPACE).append("\">");
		buf.append("<").append(NAME).append(" xmlns=\"").append(NAMESPACE).append("\" type=\"").append(TYPE).append("\"").append(">");
		 buf.append("<location").append(" longitude=\"").append(longitude).append("\" latitude=\"").append(latitude).append("\"/>"); 
		 buf.append("<distance>").append(distance).append("</distance>"); 
		 //buf.append("<distance").append(distance).append("/>"); 
		 buf.append("</").append(NAME).append(">");		 
		return buf.toString();
	}
	
	public static void main(String[] args)
	{
		RecommendNoticePacket createNoticeIQ = new RecommendNoticePacket();
		createNoticeIQ.setLongitude("longitude");
		createNoticeIQ.setLatitude("latitude");
		createNoticeIQ.setDistance("1");
		String buf=createNoticeIQ.getChildElementXML();
	System.out.println(buf);
	}
	


}
