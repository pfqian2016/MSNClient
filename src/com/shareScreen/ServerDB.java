package com.shareScreen;

import java.util.HashMap;

public class ServerDB {
	private HashMap<String, ServerOutputThread> all=new HashMap<String, ServerOutputThread>();
	private HashMap<String, ServerOutputThread> share=new HashMap<String, ServerOutputThread>();
	private static ServerDB db=new ServerDB();
	private ServerDB(){
		
	}
	public synchronized static ServerDB getInstance() {
		return db;
	}
	public synchronized HashMap<String, ServerOutputThread> getAll(){
		return all;
	}
	public synchronized HashMap<String, ServerOutputThread> getShare(){
		return share;
	}
	public void clear(){
		all.clear();
	}
}
