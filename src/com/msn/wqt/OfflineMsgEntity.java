package com.msn.wqt;

import java.io.Serializable;

public class OfflineMsgEntity implements Serializable{
	private String source;
	private String destnation;
	private String date;
	private String msgContent;
	
	public OfflineMsgEntity(){
		
	}
	public OfflineMsgEntity(String source, String destnation, String date, String msgContent) {
		super();
		this.source = source;
		this.destnation = destnation;
		this.date = date;
		this.msgContent = msgContent;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDestnation() {
		return destnation;
	}
	public void setDestnation(String destnation) {
		this.destnation = destnation;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getMsgContent() {
		return msgContent;
	}
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return source+WqtConstants.SYMBOL_SPILT+destnation+WqtConstants.SYMBOL_SPILT+date+WqtConstants.SYMBOL_SPILT+msgContent;
	}
	
	
}
