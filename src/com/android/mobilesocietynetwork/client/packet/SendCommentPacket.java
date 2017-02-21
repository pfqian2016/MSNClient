package com.android.mobilesocietynetwork.client.packet;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;

import com.android.mobilesocietynetwork.client.util.Constants;

public class SendCommentPacket extends IQ  {
	private static final String NAME="activity";
	private static final String NAMESPACE="com.msn.activity";
	private static final String TYPE="post";
	private static final String SERVERNAME = Constants.SERVER_NAME;
	
	private String noticeID;//活动ID
	private String comment;//评论内容
	private String imageString=null;
/*	private String imageName=null;
	
	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}*/

	public String getImageString() {
		return imageString;
	}

	public void setImageString(String imageString) {
		this.imageString = imageString;
	}

	public SendCommentPacket()
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
	
	public String getComment()
	{
		return comment;
	}


	public void setComment(String comment)
	{
		this.comment = comment;
	}
	

	@Override
	public String getChildElementXML()
	{
		StringBuffer buf = new StringBuffer();
	//	buf.append("<").append(NAME).append(" xmlns=\"").append(NAMESPACE).append("\">");
		buf.append("<").append(NAME).append(" xmlns=\"").append(NAMESPACE).append("\" type=\"").append(TYPE).append("\"").append(">");
		 buf.append("<activity_id>").append(noticeID).append("</activity_id>"); 
		 buf.append("<comment>").append(comment).append("</comment>"); 
		 buf.append("<imageString>").append(imageString).append("</imageString>"); 
		// buf.append("<imageName>").append(imageName).append("</imageName>");
		 buf.append("</").append(NAME).append(">");		 
		return buf.toString();
	}
	
	public static void main(String[] args)
	{
		SendCommentPacket createNoticeIQ = new SendCommentPacket();
		createNoticeIQ.setNoticeID("1");
		createNoticeIQ.setComment("test");
		String buf=createNoticeIQ.getChildElementXML();
	System.out.println(buf);
	}
	



}
