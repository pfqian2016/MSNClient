package com.android.mobilesocietynetwork.client.packet.hybrid;

import java.util.ArrayList;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;

import com.android.mobilesocietynetwork.client.util.Constants;

/**
 * DirectScfPacket类某用户向服务器发送由用户A传递给用户B的数据，由服务器直接投递给B
 * @author ShiJie
 *
 */
public class DirectScfPacket extends IQ  {
	private static final String NAME="scf";
	private static final String NAMESPACE="com.msn.hybrid.scf";
	private static final String TYPE="direct";
	private static final String SERVERNAME = Constants.SERVER_NAME;
	
	private String throwid ;
	private String source ;
	private String destination ;
	private String value ;
	//change long to String
	private String starttime ;
	//add
	private String msgcontent;
	private String life ;
	private ArrayList<String> passList = new ArrayList<String>();
	
	public DirectScfPacket()
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
	
	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}
	
	public String getDestination()
	{
		return destination;
	}

	public void setDestination(String destination)
	{
		this.destination = destination;
	}
	
	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}
	public String getStarttime()
	{
		return starttime;
	}

	public void setStarttime(String starttime)
	{
		this.starttime = starttime;
	}
	public String getMsgcontent() {
		return msgcontent;
	}

	public void setMsgcontent(String msgcontent) {
		this.msgcontent = msgcontent;
	}

	public String getLife()
	{
		return life;
	}

	public void setLife(String life)
	{
		this.life = life;
	}
	
	public ArrayList<String> getPassList()
	{
		return passList;
	}


	public void setPassList(ArrayList<String> passList)
	{
		this.passList = passList;
	}
	
	public void addPass(String pass)
	{
		this.passList.add(pass);
	}
	
	
	@Override
	public String getChildElementXML()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<").append(NAME).append(" xmlns=\"").append(NAMESPACE).append("\" type=\"").append(TYPE).append("\"").append(">");
		 buf.append("<throwid>").append(throwid).append("</throwid>"); 
		 buf.append("<source>").append(source).append("</source>"); 
		 buf.append("<destination>").append(destination).append("</destination>"); 
		 buf.append("<value>").append(value).append("</value>"); 
		 buf.append("<starttime>").append(starttime).append("</starttime>"); 
		 buf.append("<life>").append(life).append("</life>");
		 buf.append("<msgcontent>").append(msgcontent).append("</msgcontent>");
		 buf.append("<passes>");
		 for(int i=0;i<passList.size();i++)
		 {
		 buf.append("<pass>").append(passList.get(i)).append("</pass>");
	 }
		 buf.append("</passes>");
		 buf.append("</").append(NAME).append(">");		 
		return buf.toString();
	}
	



}

