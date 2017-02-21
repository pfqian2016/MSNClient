package com.android.mobilesocietynetwork.client.packet;

import java.util.ArrayList;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;

import com.android.mobilesocietynetwork.client.util.Constants;

public class SearchPacket extends IQ
{
	private static final String ITEM="item";
	private static final String SERVERNAME = Constants.SERVER_NAME;
	private String name;
	private String namespace;
	private ArrayList<String> userList;
	private ArrayList<String> conditionList;
	
	public SearchPacket(String name,String namespace)
	{
		super();
		setTo();
		this.name=name;
		this.namespace = namespace;
		conditionList=new ArrayList<String>();
	}


	public String getNamespace()
	{
		return namespace;
	}


	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
	}


	@Override 
	public void setType(Type type)       
	{
		super.setType(type);
	}
	
	public void setTo()
	{
		//super.setTo("wss-pc");
		//super.setTo("confid");
		super.setTo(SERVERNAME);
	}

	
	
	public ArrayList<String> getUserList()
	{
		return userList;
	}


	public void setUserList(ArrayList<String> userList)
	{
		this.userList = userList;
	}
	
	public void addUser(String user)
	{
		this.userList.add(user);
	}
	
	public void addList(ArrayList<String> list){
		conditionList=list;
	}

	@Override
	public String getChildElementXML()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<").append(name).append(" xmlns=\"").append(namespace).append("\">");
		// TODO Auto-generated method stub
		buf.append("</").append(name).append(">");
		return buf.toString();
	}
	
	
}
