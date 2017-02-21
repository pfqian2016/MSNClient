
package com.android.mobilesocietynetwork.client.packet.hybrid;

import java.util.ArrayList;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;

import com.android.mobilesocietynetwork.client.util.Constants;

/**
 * NoticeAcceptPacket类为数据包送达用户B后，B在联网的时候向服务器通告
 * @author ShiJie
 *
 */
public class NoticeAcceptPacket extends IQ  {
	private static final String NAME="notice";
	private static final String NAMESPACE="com.msn.hybrid.notice";
	private static final String SERVERNAME = Constants.SERVER_NAME;
	
	private String throwid ;
	private String source ;
	private long arrivetime ;
	private ArrayList<String> passList = new ArrayList<String>();
	
	public NoticeAcceptPacket()
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
	
	public long getArrivetime()
	{
		return arrivetime;
	}

	public void setArrivetime(long l)
	{
		this.arrivetime = l;
	}
	
	public ArrayList<String> getPassList()
	{
		return passList;
	}


	public void setPassList(ArrayList<String> passList)
	{
		this.passList = passList;
	}
	
	@Override
	public String getChildElementXML()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<").append(NAME).append(" xmlns=\"").append(NAMESPACE).append("\"").append(">");
		 buf.append("<throwid>").append(throwid).append("</throwid>"); 
		 buf.append("<arrivetime>").append(arrivetime).append("</arrivetime>"); 
		 buf.append("<source>").append(source).append("</source>"); 
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


