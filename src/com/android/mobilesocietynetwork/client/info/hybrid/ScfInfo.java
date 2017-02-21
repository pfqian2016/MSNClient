package com.android.mobilesocietynetwork.client.info.hybrid;

import java.util.ArrayList;

public class ScfInfo {
	private String throwid;
	private String value;
	private String source;
	private String destination;
	private String starttime;
	private String life;
	private ArrayList<String> passes =new ArrayList<String>() ;
	
	
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
	public void setSource(String source) {
		this.source = source;
	}
	
	public String getSource() {
		return source;
	}
	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getLife() {
		return life;
	}

	public void setLife(String life) {
		this.life = life;
	}
	public ArrayList<String> getPasses() {
		return passes;
	}

	public void setPasses(ArrayList<String> passes) {
		this.passes = passes;
	}
	
	public void addPass(String pass) {
		passes.add(pass);
	}



}
