package com.android.mobilesocietynetwork.client.packet;

import org.jivesoftware.smack.packet.IQ;

import com.android.mobilesocietynetwork.client.util.Constants;

public class testIQ extends IQ{
	private static final String NAME="test";
	private static final String NAMESPACE="com.msn.test";
	private static final String SERVERNAME = Constants.SERVER_NAME;
	
	
	public testIQ()
	{
		super();
		setTo();
		setType(Type.SET);
	}

	public void setTo()
	{
		super.setTo(SERVERNAME);
	}

	

	@Override
	public String getChildElementXML()
	{
		StringBuffer buf = new StringBuffer();
	//	buf.append("<").append(NAME).append(" xmlns=\"").append(NAMESPACE).append("\">");
		buf.append("<").append(NAME).append(" xmlns=\"").append(NAMESPACE).append("\"").append(">");
		 buf.append("</").append(NAME).append(">");		 
		return buf.toString();
	}
	

}

