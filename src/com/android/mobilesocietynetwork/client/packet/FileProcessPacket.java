package com.android.mobilesocietynetwork.client.packet;

import java.util.ArrayList;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;

import com.android.mobilesocietynetwork.client.util.Constants;

public class FileProcessPacket extends IQ{
	private static final String NAME="query";
	private static final String NAMESPACE="com.msn.fileProcess";
	private static final String SERVERNAME = Constants.SERVER_NAME;
	private String file; 
	private static String source ;
	private static String destination ;
	private static String current;
	private static String total;
	private static String suffix;
	



	public static String getCurrent() {
		return current;
	}

	public static void setCurrent(String current) {
		FileProcessPacket.current = current;
	}

	public static String getTotal() {
		return total;
	}

	public static void setTotal(String total) {
		FileProcessPacket.total = total;
	}

	public static String getSuffix() {
		return suffix;
	}

	public static void setSuffix(String suffix) {
		FileProcessPacket.suffix = suffix;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public FileProcessPacket()
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
		buf.append("<").append(NAME).append(" xmlns=\"").append(NAMESPACE).append("\"").append(">");
		 buf.append("<file>").append(file).append("</file>"); 
		 buf.append("<source>").append(source).append("</source>");
		 buf.append("<destination>").append(destination).append("</destination>");
		 buf.append("<current>").append(current).append("</current>");
		 buf.append("<total>").append(total).append("</total>");
		 buf.append("<suffix>").append(suffix).append("</suffix>");
		 buf.append("</").append(NAME).append(">");		 
		return buf.toString();
	}
	
/*	public static void main(String[] args)
	{
		RecommendNoticePacket createNoticeIQ = new RecommendNoticePacket();
		createNoticeIQ.setLongitude("longitude");
		createNoticeIQ.setLatitude("latitude");
		createNoticeIQ.setDistance("1");
		String buf=createNoticeIQ.getChildElementXML();
	System.out.println(buf);
	}*/
	public static void main(String[] args)
	{
		FileProcessPacket fileProcessIQ=new FileProcessPacket();
		fileProcessIQ.setFile("file");
		fileProcessIQ.setSource(source);
		fileProcessIQ.setDestination(destination);
		String buf=fileProcessIQ.getChildElementXML();
		System.out.println(buf);
	}
	
}
