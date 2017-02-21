package com.android.mobilesocietynetwork.client.packet.hybrid;

import java.util.ArrayList;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;

import com.android.mobilesocietynetwork.client.util.Constants;

/**
 * NetworkSpeedPacket类用于发送网速信息
 * @author ShiJie
 *
 */
public class NetworkSpeedPacket extends IQ  {
	private static final String NAME="networkspeed";
	private static final String NAMESPACE="com.msn.hybrid.networkspeed";
	private static final String SERVERNAME = Constants.SERVER_NAME;
	
	private long speed ;
	
	public NetworkSpeedPacket()
	{
		super();
		setTo();
		setType(Type.SET);
	}

	public void setTo()
	{
		super.setTo(SERVERNAME);
	}

	public long getSpeed()
	{
		return speed;
	}

	public void setSpeed(long speed)
	{
		this.speed = speed;
	}
	
	@Override
	public String getChildElementXML()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<").append(NAME).append(" xmlns=\"").append(NAMESPACE).append("\"").append(">");
		 buf.append("<speed>").append(speed).append("</speed>"); 
		 buf.append("</").append(NAME).append(">");		 
		return buf.toString();
	}
	



}


