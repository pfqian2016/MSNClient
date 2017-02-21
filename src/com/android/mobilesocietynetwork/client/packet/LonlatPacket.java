package com.android.mobilesocietynetwork.client.packet;



import java.util.ArrayList;

import org.jivesoftware.smack.packet.IQ;

import com.android.mobilesocietynetwork.client.util.Constants;

/*
 * 扩展类iq包，用于传输用户的地理位置信息 
 * 
 * */
public class LonlatPacket extends IQ
{
	private LonlatExtensionPacket lonlatExtensionPacket;
	private static final String SERVERNAME = Constants.SERVER_NAME;
	
	public LonlatPacket(String labelName,String labelNamespace){
		super();
		setTo();
		lonlatExtensionPacket = new LonlatExtensionPacket(labelName, labelNamespace);
		

	}
	
	public void addInfo(String user,String longitude,String latitude)
	{
		
		lonlatExtensionPacket.addUser(user);
		lonlatExtensionPacket.addLongitude(longitude);
		lonlatExtensionPacket.addLatitude(latitude);
		
	}
	
	
	public ArrayList<String> getUserList()
	{
		return lonlatExtensionPacket.getUserList();
	}
	
	public ArrayList<String> getLonArrayList()
	{
		return lonlatExtensionPacket.getLongitudeList();
	}
	
	public ArrayList<String> getLatArrayList()
	{
		return lonlatExtensionPacket.getLatitudeList();
	}


	public void setTypeSet(){
		super.setType(Type.SET);
	}
	
	public void setTypeGet(){
		super.setType(Type.GET);
	}
	
	public void setTo()
	{
		super.setTo(SERVERNAME);
	}
	
	@Override
	public String getChildElementXML()
	{
		return lonlatExtensionPacket.toXML();
	}	
}
