package com.android.mobilesocietynetwork.client.info;

import java.io.Serializable;

/**
 * 
 * 客户端的消息传递对象
 * 
 */
public class User implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// private int id;
	private String name;
	private String email;
	private int isOnline;
	private int img;
	private String phone;
	private String sex;
	private String group;
	private boolean isCom = false;

	public String getGroup()
	{
		return group;
	}

	public void setGroup(String group)
	{
		this.group = group;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public int getIsOnline()
	{
		return isOnline;
	}

	public void setIsOnline(int isOnline)
	{
		this.isOnline = isOnline;
	}

	public int getImg()
	{
		return img;
	}

	public void setImg(int img)
	{
		this.img = img;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getSex()
	{
		return sex;
	}

	public void setSex(String sex)
	{
		this.sex = sex;
	}

	public boolean isCom()
	{
		return isCom;
	}

	public void setIsCom(boolean isCom)
	{
		this.isCom = isCom;
	}

	public String toString()
	{
		return "User [name=" + name + ", email=" + email + ", isOnline="
				+ isOnline + ", img=" + img + ", group=" + group + "]";
	}
}
