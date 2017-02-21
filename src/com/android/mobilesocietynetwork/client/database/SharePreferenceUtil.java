package com.android.mobilesocietynetwork.client.database;


import java.util.HashSet;
import java.util.Set;

import com.android.mobilesocietynetwork.client.util.Constants;
import com.shareScreen.User;

import android.content.Context;
import android.content.SharedPreferences;

/*
 * 
 * ���汾���û����ݣ������ڱ����¼�ߵ���Ϣ��
 * 
 * */
public class SharePreferenceUtil
{
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	public SharePreferenceUtil(Context context, String file)
	{
		sp = context.getSharedPreferences(file, context.MODE_PRIVATE);
		editor = sp.edit();
	}

	// �û�������
	public void setPasswd(String passwd)
	{
		editor.putString("passwd", passwd);
		editor.commit();
	}

	public String getPasswd()
	{
		return sp.getString("passwd", "");
	}

	// �û�������
	public void setName(String name)
	{
		editor.putString("name", name);
		editor.commit();
		User.getInstance().setName(name);
	}
	
	public String getName()
	{
		return sp.getString("name", "");
	}

	/**
	 * �û���״̬
	 * @param status 
	 * 1:online
	 * 0:offline
	 */
	public void setStatus(int status)
	{
		editor.putInt("status", status);
		editor.commit();
	}
	
	public int getStatus()
	{
		return sp.getInt("status", 0);
	}
	// �û����Ա�
	public String getSex()
	{
		return sp.getString("sex", "");
	}

	public void setSex(String sex)
	{
		editor.putString("sex", sex);
		editor.commit();
	}
	
	// �û�������
	public String getAge()
	{
		return sp.getString("age", "");
	}

	public void setAge(String age)
	{
		editor.putString("age", age);
		editor.commit();
	}

	// �û�������
	public String getEmail()
	{
		return sp.getString("email", "");
	}

	public void setEmail(String email)
	{
		editor.putString("email", email);
		editor.commit();
	}

	// �û��ĵ绰
	public String getTel()
	{
		return sp.getString("tel", "");
	}

	public void setTel(String tel)
	{
		editor.putString("tel", tel);
		editor.commit();
	}

	// �û��Լ���ͷ��
	public int getImg()
	{
		return sp.getInt("img", 0);
	}

	public void setImg(int i)
	{
		editor.putInt("img", i);
		editor.commit();
	}
	
	// �û��Լ������ڵ�
	public String getSite()
	{
		return sp.getString("site", "");
	}

	public void setSite(String site)
	{
		editor.putString("img", site);
		editor.commit();
	}
	
	// �û���Ȥ��ǩ
	public Set<String> getLabel()
	{
		return sp.getStringSet("lable",null);
	}

	public void setLabel(Set<String> label)
	{
		Set<String> labelSet = label;
		editor.putStringSet("label", labelSet);
		editor.commit();
	}
	
	// ip
	public void setIp(String ip)
	{
		editor.putString("ip", ip);
		editor.commit();
	}

	public String getIp()
	{
		return sp.getString("ip", Constants.SERVER_IP);
	}

	// �˿�
	public void setPort(int port)
	{
		editor.putInt("port", port);
		editor.commit();
	}

	public int getPort()
	{
		return sp.getInt("port", Constants.SERVER_PORT);
	}

	// �Ƿ��ں�̨���б��
	public void setIsStart(boolean isStart)
	{
		editor.putBoolean("isStart", isStart);
		editor.commit();
	}

	public boolean getIsStart()
	{
		return sp.getBoolean("isStart", false);
	}

	// �Ƿ��һ�����б�Ӧ��
	public void setIsFirst(boolean isFirst)
	{
		editor.putBoolean("isFirst", isFirst);
		editor.commit();
	}

	public boolean getisFirst()
	{
		return sp.getBoolean("isFirst", true);
	}
}
