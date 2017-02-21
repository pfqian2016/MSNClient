package com.shareScreen;

public class User {
	private String id;
	private static User instance;
	private User(){}
	public static User getInstance(){
		if(instance==null){
			instance=new User();
		}
		return instance;
	}
	public void setName(String name){
		this.id=name;
	}
	public String  getName(){
		return id;
	}
	

}
