package com.android.mobilesocietynetwork.client.packet.hybrid;

import org.jivesoftware.smack.packet.IQ;

import com.android.mobilesocietynetwork.client.util.Constants;

/**
 * HalfAskOnlinePacket类为在线用户向服务器回馈辅助信息
 * @author ShiJie
 *
 */
public class HalfAskOnlinePacket extends IQ  {
	private static final String NAME="askonline";
	private static final String NAMESPACE="com.msn.hybrid.askonline";
	private static final String TYPE="half";
	private static final String SERVERNAME = Constants.SERVER_NAME;
	
	private String throwid ;
	private String towho ;
	private String value ;
	
	public HalfAskOnlinePacket()
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

	public void setThrowid(String throwid)
	{
		this.throwid = throwid;
	}
	
	public String getTowho()
	{
		return towho;
	}

	public void setTowho(String towho)
	{
		this.towho = towho;
	}
	
	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}
	
	@Override
	public String getChildElementXML()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<").append(NAME).append(" xmlns=\"").append(NAMESPACE).append("\" type=\"").append(TYPE).append("\"").append(">");
		 buf.append("<throwid>").append(throwid).append("</throwid>"); 
		 buf.append("<towho>").append(towho).append("</towho>"); 
		 buf.append("<value>").append(value).append("</value>"); 
		 buf.append("</").append(NAME).append(">");		 
		return buf.toString();
	}
	



}


