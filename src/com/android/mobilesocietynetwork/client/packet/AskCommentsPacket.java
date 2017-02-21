package com.android.mobilesocietynetwork.client.packet;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;

import com.android.mobilesocietynetwork.client.util.Constants;

public class AskCommentsPacket extends IQ  {
	private static final String NAME="activity";
	private static final String NAMESPACE="com.msn.activity";
	private static final String TYPE="askComments";
	private static final String SERVERNAME = Constants.SERVER_NAME;
	
	private String noticeID;//»î¶¯ID

	
	public AskCommentsPacket()
	{
		super();
		setTo();
		setType(Type.SET);
	}

	public void setTo()
	{
		super.setTo(SERVERNAME);
	}


	public String getNoticeID()
	{
		return noticeID;
	}


	public void setNoticeID(String noticeID)
	{
		this.noticeID = noticeID;
	}
	
	@Override
	public String getChildElementXML()
	{
		StringBuffer buf = new StringBuffer();
	//	buf.append("<").append(NAME).append(" xmlns=\"").append(NAMESPACE).append("\">");
		buf.append("<").append(NAME).append(" xmlns=\"").append(NAMESPACE).append("\" type=\"").append(TYPE).append("\"").append(">");
		 buf.append("<activity_id>").append(noticeID).append("</activity_id>"); 
		 //buf.append("<distance").append(distance).append("/>"); 
		 buf.append("</").append(NAME).append(">");		 
		return buf.toString();
	}
	
	public static void main(String[] args)
	{
		AskCommentsPacket createNoticeIQ = new AskCommentsPacket();
		createNoticeIQ.setNoticeID("1");
		String buf=createNoticeIQ.getChildElementXML();
	System.out.println(buf);
	}
	



}

