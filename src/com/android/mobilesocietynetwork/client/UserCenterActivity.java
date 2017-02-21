package com.android.mobilesocietynetwork.client;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;


public class UserCenterActivity extends Activity {
	
	private TextView userInfoName;
	private TextView userInfoAge;
	private TextView userInfoSex;
	private TextView userInfoSite;
	private TextView userInfoEmail;
	private TextView userInfoTel;
	private TextView userInfoLabel;
	private ImageView userInfoImage;
	private Button userInfoBack;
	private Button userInfoEdit;
	private Button logout;
	private SharePreferenceUtil util;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_center);
		initView();
		initData();
		initControl();
	}
	private void initView() {
		// TODO Auto-generated method stub
		userInfoName = (TextView) findViewById(R.id.tv_userInfoName);
		userInfoSex = (TextView) findViewById(R.id.tv_userInfoSex);
		userInfoAge = (TextView) findViewById(R.id.tv_userInfoAge);
		userInfoSite = (TextView) findViewById(R.id.tv_userInfoSite);
		userInfoEmail = (TextView) findViewById(R.id.tv_userInfoEmail);
		userInfoTel = (TextView) findViewById(R.id.tv_userInfoTel);
		userInfoLabel = (TextView) findViewById(R.id.tv_userInfoLabel);
		userInfoImage = (ImageView)findViewById(R.id.iv_userImage);
	   userInfoBack = (Button)findViewById(R.id.bt_backUserInfo);
	   userInfoEdit = (Button)findViewById(R.id.bt_editUserInfo);  
	   logout = (Button)findViewById(R.id.bt_logout);  
	}
	
	private void initData() {
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		userInfoImage.setImageResource(Constants.IMGS[util.getImg()]);
		userInfoName.setText(util.getName());
		userInfoSex.setText(util.getSex());
		userInfoAge.setText(util.getAge());
		userInfoSite.setText(util.getSite());
		userInfoEmail.setText(util.getEmail());
		userInfoTel.setText(util.getTel());
		Set<String> set = new HashSet<String>();
		set = util.getLabel();
		if(set!=null&&set.size()>0){
		String[] temp = new String[set.size()];
		int i = 0;
		for(Iterator it = set.iterator(); it.hasNext();i++)
		{
		    temp[i] = (String)it.next();
		}
		StringBuilder sb=new StringBuilder("");
		for (i = 0; i < temp.length; i++) {
		sb.append(temp[i]);
		}
		userInfoLabel.setText(sb.toString());
		}
	}
	private void initControl() {
		
		userInfoBack.setOnClickListener(new OnClickListener() { 
	            @Override 
	            public void onClick(View v) { 
	                finish(); 
	            } 
	        });
		userInfoEdit.setOnClickListener(new OnClickListener(){
		    	
		    	@Override
		    	public void onClick(View v){
		    		
				Intent intent = new Intent(UserCenterActivity.this, UserCenterEditActivity.class);
				startActivity(intent);		
		    	}
		    });
		
		logout.setOnClickListener(new OnClickListener() { 
            @Override 
            public void onClick(View v) { 
    			// 确定退出的提示
    			AlertDialog.Builder builder = new AlertDialog.Builder(
    					UserCenterActivity.this);
    			builder.setTitle("hint").setMessage("Sure to quit:");
    			builder.setPositiveButton("Yes",
    					new DialogInterface.OnClickListener() {

    						@Override
    						public void onClick(DialogInterface dialog, int which) {
    							// TODO Auto-generated method stub
    							util.setStatus(0);
    							util.setName("");
    							util.setPasswd("");
    							//XmppTool.closeConnection();
    							ActivityManager.getInstance().exit();
    							/*Intent exitIntent = new Intent(UserCenterActivity.this,
    									LoginActivity.class);
    							startActivity(exitIntent);*/
    							//finish();
    						}
    					});
    			builder.setNegativeButton("Cancel",
    					new DialogInterface.OnClickListener() {

    						@Override
    						public void onClick(DialogInterface dialog, int which) {
    							// TODO Auto-generated method stub
    							dialog.dismiss();
    						}
    					});
    			builder.create().show();
            } 
        });

	}
}
