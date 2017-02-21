package com.android.mobilesocietynetwork.client.info.hybrid;

import java.io.Serializable;

/**
 * 解析接收到的askonline报文时使用，将报文内的信息解析出来放入到BeginAskOnlineInfo
 * @author ShiJie
 *
 */
public class BeginAskOnlineInfo implements Serializable {

	private String throwid;
	private String value;
	private String type;
	private String forwarder;
	private String fromwho;
	private String destination;
	private String size;
	
	public String getThrowid() {
		return throwid;
	}

	public void setThrowid(String throwid) {
		this.throwid = throwid;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public void setForwarder(String forwarder) {
		this.forwarder = forwarder;
	}
	
	public String getForwarder() {
		return forwarder;
	}
	
	public String getFromwho() {
		return fromwho;
	}

	public void setFromwho(String fromwho) {
		this.fromwho = fromwho;
	}
	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}



}
