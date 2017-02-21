package com.android.mobilesocietynetwork.client.packet;


import java.util.ArrayList;
import java.util.HashMap;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;

import com.android.mobilesocietynetwork.client.util.Constants;

//扩展类IQ包，直接继承自IQ，用于发送和接收标签
public class LabelPacket extends IQ
{
	private static final String ITEM="item";
	private static final String SERVERNAME = Constants.SERVER_NAME;
	private String name;
	private String namespace;
	private String user;
	private ArrayList<String> labelList;
	
	
	public LabelPacket(String name,String namespace,String user)
	{
		super();
		setType(Type.SET);
		setTo();
		this.name=name;
		this.namespace = namespace;
		this.user=user;
		labelList=new ArrayList<String>();
	}


	public String getNamespace()
	{
		return namespace;
	}


	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
	}


	public void addlabelList(ArrayList<String> list){
		labelList=list;
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
	
	@Override
	public String getChildElementXML()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<").append(name).append(" xmlns=\"").append(namespace).append("\">");
		buf.append("<").append(ITEM).append(" user=\"").append(user).append("\"");
		for(int i=1;i<=labelList.size();i++)
		{
			buf.append(" label"+i+"=\"").append(labelList.get(i-1)).append("\"");
		}
		buf.append("/>");
		buf.append("</").append(name).append(">");
		// TODO Auto-generated method stub
		return buf.toString();
	}
	
/* 	public static void main(String[] args)
	{
 		LabelPacket LabelPacket = new LabelPacket("name","namespace","user");
 		ArrayList<String> list = new ArrayList<String>();
 		list.add("t1");
		list.add("t2");
 		LabelPacket.addlabelList(list);
		String buf=LabelPacket.getChildElementXML();
		System.out.println(buf);
	}
*/
}
