package com.android.mobilesocietynetwork.client.packet.hybrid;

import java.util.ArrayList;

import org.jivesoftware.smack.packet.IQ;

import com.android.mobilesocietynetwork.client.util.Constants;

/**
 * BeginAskOnlinePacket类是某用户向服务器发送online代价值查询报文
 * @author ShiJie
 *
 */
public class BeginAskOnlinePacket extends IQ  {
	private static final String NAME="askonline";
	private static final String NAMESPACE="com.msn.hybrid.askonline";
	private static final String TYPE="begin";
	private static final String SERVERNAME = Constants.SERVER_NAME;
	
	private String throwid ;
	private String destination ;
	private String size ;
	
	public BeginAskOnlinePacket()
	{
		super();
		setTo();
		setType(Type.SET);
	}

	public void setTo()
	{
		super.setTo(SERVERNAME);
	}

	public String getThrowid()
	{
		return throwid;
	}

	public void setThrowid(String string)
	{
		this.throwid = string;
	}
	
	public String getDestination()
	{
		return destination;
	}

	public void setDestination(String destination)
	{
		this.destination = destination;
	}
	
	public String getSize()
	{
		return size;
	}

	public void setSize(String size)
	{
		this.size = size;
	}
	
	@Override
	public String getChildElementXML()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<").append(NAME).append(" xmlns=\"").append(NAMESPACE).append("\" type=\"").append(TYPE).append("\"").append(">");
		 buf.append("<throwid>").append(throwid).append("</throwid>"); 
		 buf.append("<destination>").append(destination).append("</destination>"); 
		 buf.append("<size>").append(size).append("</size>"); 
		 buf.append("</").append(NAME).append(">");		 
		return buf.toString();
	}
	



}


