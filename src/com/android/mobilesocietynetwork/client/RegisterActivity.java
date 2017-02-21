package com.android.mobilesocietynetwork.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.IQ.Type;

import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.packet.LabelPacket;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity
{
	private EditText unameEditText;
	private EditText passwordEditText;
	private EditText repasswordEditText;
	private ListView mlistView;
	private MyAdapter myAdapter;
	private Button okButton;
	private SharePreferenceUtil util;
	
	private String uname;
	private String password;
	private String repassword;
    private ProgressDialog pd;  

	private boolean[][] LabelSelect=new boolean[5][5];
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		
		unameEditText=(EditText)findViewById(R.id.username_edittext_register);
		passwordEditText=(EditText)findViewById(R.id.password_edittext_register);
		repasswordEditText=(EditText)findViewById(R.id.repassword_edittext_register);
		mlistView=(ListView)findViewById(R.id.listView_label_register);
		okButton=(Button)findViewById(R.id.reg_button_register);
		okButton.setOnClickListener(new RegListener());
		
		myAdapter = new MyAdapter(this,R.layout.listview_items_register);
		mlistView.setAdapter(myAdapter);
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
	}

	//点击注册之后
	private class RegListener implements OnClickListener{
		@Override
		public void onClick(View arg0)
		{
			// TODO Auto-generated method stub
			uname=unameEditText.getText().toString();
			password=passwordEditText.getText().toString();
			repassword=repasswordEditText.getText().toString();
			if(uname.length()!=0&&password.length()!=0&&repassword.length()!=0)
			{
				if(password.equals(repassword))
				{
		         	if (createAccount(uname, password))//创建账户成功之后
				      {
					    util.setName(uname);
						util.setImg(1);
						Toast.makeText(getApplicationContext(), "Register successfully,login...", 
								Toast.LENGTH_SHORT).show();
		         		//进入登录线程
					    pd = ProgressDialog.show(RegisterActivity.this, "Hint", "Register successfully,login...");  
				        new Thread(new Runnable() {  
				            @Override  
				            public void run() {  
				            	int result = receiveResult(uname,password);
				            		handler.sendEmptyMessage(result);
				            }  	  
				        }).start();  	
				        
				}
					else
						Toast.makeText(getApplicationContext(), "Sorry,register failed please try again", Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(getApplicationContext(), "Two different passwords are entered！", Toast.LENGTH_SHORT).show();
				}
			}
			else{
				Toast.makeText(getApplicationContext(), "Account or password invalid", Toast.LENGTH_SHORT).show();
			}
		}
		
		
	}
	
	 private int receiveResult(String mNametext,String pwdtext) {  

			try
			{
				// 建立连接并登录
				XmppTool.getConnection().login(mNametext, pwdtext);
				Presence presence = new Presence( // Presence是Packet的一个子类
						Presence.Type.available);
				XmppTool.getConnection().sendPacket(presence);
				return 1;		
			}
			catch (XMPPException e)
			{
				XmppTool.closeConnection();
				return 2;
			}
			catch (IllegalStateException e)
			{

				return 3;
			}		
  }  
	 
	    Handler handler=new Handler()  
	    {   
	    	@Override  
	        public void handleMessage(Message msg)  // handler接收到消息后就会执行此方法 
	        {     
	    		super.handleMessage(msg);
	             switch(msg.what)  
	            {  
	            case 1:  //如果登录成功
	            	//发送标签IQ包
	            	sendLabel();
	            	pd.dismiss();// 关闭ProgressDialog 
	  				Intent intent = new Intent();
					intent.setClass(RegisterActivity.this, MainActivity.class);		
					intent.putExtra("mName", uname);
					startActivity(intent);
					finish();
	                break;  
	            case 2:  //如果用户名密码错误
	          	  pd.dismiss();// 关闭ProgressDialog 
	           	Toast.makeText(RegisterActivity.this,"Failed,please check account and password", Toast.LENGTH_SHORT).show();
	            break; 
	            case 3:  //如果网络连接错误
		          	  pd.dismiss();// 关闭ProgressDialog 
		           	Toast.makeText(RegisterActivity.this,"Can not connect to server", Toast.LENGTH_SHORT).show();
	              break;  
	            default:  
	                break;        
	            }  
	        }  
	    };
	    
	    private void sendLabel(){
			ArrayList<String> list = new ArrayList<String>();
			//写入到本地一份，发送到服务器一份
			Set<String>labelset =  new HashSet<String>();
			for(int i=0;i<LabelSelect.length;i++)
			{
				for(int j=0;j<LabelSelect[i].length;j++)
				{
					if(LabelSelect[i][j]==true)
						list.add(Constants.LabelListEN[i][j]);
					    labelset.add(Constants.LabelListCN[i][j]);
					    util.setLabel(labelset);
						//list.add(Constants.LabelListNUM[i][j]);
				}	
			}
		
			//发送标签的IQ包
			LabelPacket labelPacket = new LabelPacket("setlabel","com.msn.handleLabelPacket",uname);
			labelPacket.addlabelList(list);
			labelPacket.setType(Type.SET);
			XmppTool.getConnection().sendPacket(labelPacket);
	    }
	
	private boolean createAccount(String regUserName, String regUserPwd)
	{
		try
		{
			XmppTool.getConnection().getAccountManager().createAccount(regUserName, regUserPwd);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	
	}
	private class MyAdapter extends ArrayAdapter<Object>{
		int mTextViewResourceID = 0;
		private Context mContext;

		public MyAdapter(Context context, int textViewResourceId)
		{
			super(context, textViewResourceId);
			mTextViewResourceID = textViewResourceId;
			mContext = context;
		}

		public int getCount()
		{
			return Constants.ListTitle.length;
		}

		@Override
		public boolean areAllItemsEnabled()
		{
			return false;
		}

		public Object getItem(int position)
		{
			return position;
		}

		public long getItemId(int position)
		{
			return position;
		}

		public View getView(final int position, View convertView,
		        ViewGroup parent)
		{
			TextView title = null;
			CheckBox checkBox1 = null;
			CheckBox checkBox2 = null;
			CheckBox checkBox3 = null;
			CheckBox checkBox4 = null;
			CheckBox checkBox5 = null;
			
//			if (convertView == null)
//			{
				convertView = LayoutInflater.from(mContext).inflate( mTextViewResourceID, null);
				title = (TextView) convertView.findViewById(R.id.title_textview_listitems_register);
				checkBox1=(CheckBox) convertView.findViewById(R.id.checkBox1_listitems_register);
				checkBox2=(CheckBox) convertView.findViewById(R.id.checkBox2_listitems_register);
				checkBox3=(CheckBox) convertView.findViewById(R.id.checkBox3_listitems_register);
				checkBox4=(CheckBox) convertView.findViewById(R.id.checkBox4_listitems_register);
				checkBox5=(CheckBox) convertView.findViewById(R.id.checkBox5_listitems_register);
				title.setText(Constants.ListTitle[position]);
				checkBox1.setText(Constants.LabelListCN[position][0]);
				checkBox2.setText(Constants.LabelListCN[position][1]);
				checkBox3.setText(Constants.LabelListCN[position][2]);
				checkBox4.setText(Constants.LabelListCN[position][3]);
				checkBox5.setText(Constants.LabelListCN[position][4]);
				checkBox1.setChecked(LabelSelect[position][0]);
				checkBox2.setChecked(LabelSelect[position][1]);
				checkBox3.setChecked(LabelSelect[position][2]);
				checkBox4.setChecked(LabelSelect[position][3]);
				checkBox5.setChecked(LabelSelect[position][4]);
				checkBox1.setOnCheckedChangeListener(new CheckBoxListener(position));
				checkBox2.setOnCheckedChangeListener(new CheckBoxListener(position));
				checkBox3.setOnCheckedChangeListener(new CheckBoxListener(position));
				checkBox4.setOnCheckedChangeListener(new CheckBoxListener(position));
				checkBox5.setOnCheckedChangeListener(new CheckBoxListener(position));
				
//			}
			return convertView;
		}
	}
		
	
	private class CheckBoxListener implements OnCheckedChangeListener{

		private int position;
		public CheckBoxListener(int position)
		{
			// TODO Auto-generated constructor stub
			this.position=position;
		}
		@Override
		public void onCheckedChanged(CompoundButton ButtonView, boolean arg1)
		{
			// TODO Auto-generated method stub
			switch (ButtonView.getId())
			{
				case R.id.checkBox1_listitems_register:
					if(arg1==true)
					{
						LabelSelect[position][0]=true;
					}
					else 
					{
						LabelSelect[position][0]=false;
					}
					break;
				case R.id.checkBox2_listitems_register:
					if(arg1==true)
					{
						LabelSelect[position][1]=true;
					}
					else 
					{
						LabelSelect[position][1]=false;
					}
					break;
				case R.id.checkBox3_listitems_register:
					if(arg1==true)
					{
						LabelSelect[position][2]=true;
					}
					else 
					{
						LabelSelect[position][2]=false;
					}
					break;
				case R.id.checkBox4_listitems_register:
					if(arg1==true)
					{
						LabelSelect[position][3]=true;
					}
					else 
					{
						LabelSelect[position][3]=false;
					}
					break;
				case R.id.checkBox5_listitems_register:
					if(arg1==true)
					{
						LabelSelect[position][4]=true;
					}
					else 
					{
						LabelSelect[position][4]=false;
					}
					break;
				
				default:
					break;
			}
		}
		
	} 
	

}
