package com.android.mobilesocietynetwork.client.packet;

import java.util.ArrayList;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;

import com.android.mobilesocietynetwork.client.util.Constants;

public class RecommendLabelPacket extends IQ{
	private static final String ITEM="item";
	private static final String SERVERNAME = Constants.SERVER_NAME;
	
	private String name;
	private String namespace;
	
	private ArrayList<String> userList;
	private String recommendtype;
	private String label;
	
	

	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public RecommendLabelPacket(String name, String namespace) {
		super();
		setTo();
		this.name = name;
		this.namespace = namespace;
	}


	public String getRecommendtype() {
		return recommendtype;
	}


	public void setRecommendtype(String recommendtype) {
		this.recommendtype = recommendtype;
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

	@Override
	public String getChildElementXML()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<").append(name).append(" xmlns=\"").append(namespace).append("\"");
		buf.append(" type=\"").append(recommendtype).append("\">");
		buf.append("<label>").append(label).append("</label>");
		buf.append("</").append(name).append(">");
		return buf.toString();
	}
	
	
}
