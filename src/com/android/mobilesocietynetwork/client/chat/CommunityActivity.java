package com.android.mobilesocietynetwork.client.chat;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.MultiUserChat;

import com.android.mobilesocietynetwork.client.ActivityManager;
import com.android.mobilesocietynetwork.client.MyActivity;
import com.android.mobilesocietynetwork.client.R;
import com.android.mobilesocietynetwork.client.database.CommunityListDB;
import com.android.mobilesocietynetwork.client.database.FriendListDB;
import com.android.mobilesocietynetwork.client.database.MultiUserChatDB;
import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.info.User;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class CommunityActivity extends MyActivity {

	//private static XMPPConnection connection;
	private MultiUserChatDB mutiUserChatDB;
	LinearLayout comPageActionLayout;
	LinearLayout comPageItems;
	LinearLayout comPageCreation;
	LinearLayout comPageSearch;
	LinearLayout comPageRecommend;
	EditText comPageInput;
	TextView textview;
	TextView communityItemTextView;
	View communityItemView;
	LayoutInflater inflater;
	private SharePreferenceUtil util;
	private TextView comNameTextView;
	private TextView passwordTextView;
	private RadioGroup mRadioGroup;
	private boolean isPersistentroom = false;
	private IntentFilter mIntentFilter;
	private CommunityReceiver mCommunityReceiver;
	
	private CommunityListDB communityListDB;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_community);
		ActivityManager exitM = ActivityManager.getInstance();
		exitM.addActivity(CommunityActivity.this);
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		//connection = XmppTool.getConnection();
		//mutiUserChatDB = MultiUserChatDB.getInstance();
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.community, new PlaceholderFragment()).commit();
		}
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("com.android.mobilesocietynetwork.client.CREATE_COMMUNITY_SUCCESSFULLY");
		mCommunityReceiver = new CommunityReceiver();
		registerReceiver(mCommunityReceiver, mIntentFilter);
		initUI();
		initData();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		initData();
	}

	public void initData() {
		inflater = LayoutInflater.from(this);
	//	ArrayList<String> existingCom = mutiUserChatDB.getAllName();
		//11-3 从数据库获得数据
		communityListDB = CommunityListDB.getInstance(CommunityActivity.this);
		ArrayList<String> existingCom = communityListDB.qureyCommunity(util.getName());
		comPageItems.removeAllViews();
		for (int i = 0; i < existingCom.size(); i++) {
			communityItemView = inflater.inflate(R.layout.community_item, null);
			communityItemTextView = (TextView) communityItemView
					.findViewById(R.id.item_text);
			communityItemTextView.setText(existingCom.get(i));
			communityItemTextView.setOnLongClickListener(new textlonglister()); // 长按事件
			comPageItems.addView(communityItemView);
		}
	}

	public void initUI() {
		comPageCreation = (LinearLayout) findViewById(R.id.community_action_creation);
		comPageSearch = (LinearLayout) findViewById(R.id.community_action_search);
		comPageRecommend = (LinearLayout) findViewById(R.id.community_action_recommend);
		comPageItems = (LinearLayout) findViewById(R.id.community_items);
		// 设置community内的listener
		MyOnclickListener mListener = new MyOnclickListener();
		comPageCreation.setOnClickListener(mListener);
		comPageSearch.setOnClickListener(mListener);
		comPageRecommend.setOnClickListener(mListener);
		comNameTextView = (TextView) findViewById(R.id.tvComName);
		passwordTextView = (TextView) findViewById(R.id.tvPassWord);
		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
	}

	class MyOnclickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			
			case R.id.community_action_creation:
				if( XmppTool.getConnection().isConnected()){
				Intent intent = new Intent(CommunityActivity.this,
				 CreateComFirstActivity.class);
				 startActivity(intent);
				//ShowDialogCreatCommunity();
				 }else
			    	{	
			    		Toast.makeText(CommunityActivity.this, "Service unavailable", Toast.LENGTH_SHORT).show();
			    	}
				break;
			case R.id.community_action_search:
				if( XmppTool.getConnection().isConnected()){
				Intent intent1 = new Intent(CommunityActivity.this,
						QueryCommunityActivity.class);
				startActivity(intent1);
			 }else
		    	{	
		    		Toast.makeText(CommunityActivity.this, "Service unavailable", Toast.LENGTH_SHORT).show();
		    	}
				break;
			// 添加点击"推荐社团"后的响应
			case R.id.community_action_recommend:
				if( XmppTool.getConnection().isConnected()){
				Intent intent2 = new Intent(CommunityActivity.this,
						RecommendComActivity.class);
				startActivity(intent2);
				 }else
			    	{	
			    		Toast.makeText(CommunityActivity.this, "Service unavailable", Toast.LENGTH_SHORT).show();
			    	}
				break;
			}
		}
	}

/*	public void ShowDialogCreatCommunity() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(
				R.layout.creat_community_dialog, null);
		builder.setTitle("创建聊天室");
		builder.setView(textEntryView);
		mRadioGroup = (RadioGroup) textEntryView.findViewById(R.id.radioGroup);
		mRadioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup arg0, int arg1) {
						// TODO Auto-generated method stub
						// 获取变更后的选中项的ID
						if (arg1 == R.id.radioButton1) {
							// Toast.makeText(getApplicationContext(), "永久",
							// 1).show();
							isPersistentroom = true;
						} else if (arg1 == R.id.radioButton2) {
							// Toast.makeText(getApplicationContext(), "暂时",
							// 1).show();
							isPersistentroom = false;
						}
					}
				});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				EditText comName = (EditText) textEntryView
						.findViewById(R.id.etComName);
				EditText password = (EditText) textEntryView
						.findViewById(R.id.etPassWord);
				// RadioGroup mRadioGroup = (RadioGroup)
				// textEntryView.findViewById(R.id.radioGroup);
				// RadioButton
				// r1=(RadioButton)textEntryView.findViewById(R.id.radioButton1);
				// RadioButton
				// r2=(RadioButton)textEntryView.findViewById(R.id.radioButton2);
				String comNametext = comName.getText().toString();
				String passwordtext = password.getText().toString();
				MultiUserChat muc = createRoom(util.getName(), comNametext,
						passwordtext, isPersistentroom);
				if (muc != null) {
					mutiUserChatDB.addMuc(muc);
					ArrayList<String> existingCom = mutiUserChatDB.getAllName();
					comPageItems.removeAllViews();
					for (int i = 0; i < existingCom.size(); i++) {
						communityItemView = inflater.inflate(
								R.layout.community_item, null);
						communityItemTextView = (TextView) communityItemView
								.findViewById(R.id.item_text);
						communityItemTextView.setText(existingCom.get(i));
						comPageItems.addView(communityItemView);
					}
					Toast.makeText(getApplicationContext(), "聊天室创建成功！", 3000)
							.show();
				} else {
					Toast.makeText(getApplicationContext(), "聊天室创建失败！", 3000)
							.show();
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		builder.create().show();
	}*/
	
	// 1104 add
	class CommunityReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			//ArrayList<String> existingCom = mutiUserChatDB.getAllName();
			//更新数据
			communityListDB = CommunityListDB.getInstance(CommunityActivity.this);
			ArrayList<String> existingCom = communityListDB.qureyCommunity(util.getName());
			comPageItems.removeAllViews();
			for (int i = 0; i < existingCom.size(); i++) {
				communityItemView = inflater.inflate(R.layout.community_item, null);
				communityItemTextView = (TextView) communityItemView.findViewById(R.id.item_text);
				communityItemTextView.setText(existingCom.get(i));
				communityItemTextView.setOnLongClickListener(new textlonglister());
				comPageItems.addView(communityItemView);
			}
			Log.d("Community", "update view");
		}

	}


	// xml里的textview设置了点击和长按方式
	public void CommunityItemOnClick(View v) {
		TextView clickedTextView = (TextView) v;
		String selectcName = clickedTextView.getText().toString();
		User u = new User();
		u.setName(selectcName);
		u.setIsCom(true);
		// Toast.makeText(getApplicationContext(),clickedTextView.getText().toString(),3000).show();
		Intent intent = new Intent(CommunityActivity.this, DialogActivity.class);
		intent.putExtra("user", u);
		startActivity(intent);
	}

	// 长按退出社团
	class textlonglister implements OnLongClickListener {
		public boolean onLongClick(View v) {
			String quitcname = communityItemTextView.getText().toString();
			new AlertDialog.Builder(CommunityActivity.this)
					.setTitle("Confirm")
					.setMessage("Are you sure to quit " + quitcname )
					.setPositiveButton(
							"Sure",
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 没有multiuserchat找到怎么退出的
									comPageItems.removeAllViews();
									ArrayList<String> existingCom = mutiUserChatDB
											.getAllName();
									for (int i = 0; i < existingCom.size(); i++) {
										communityItemView = inflater.inflate(
												R.layout.community_item, null);
										communityItemTextView = (TextView) communityItemView
												.findViewById(R.id.item_text);
										communityItemTextView
												.setText(existingCom.get(i));
										comPageItems.addView(communityItemView);
									}
								}
							})
					.setNegativeButton(
							"Cancel",
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 取消删除操作，什么都不做
								}
							}).create().show();
			return false;
		}
	}

	/**
	 * 创建房间
	 * 
	 * @param roomName
	 *            房间名称
	 */
	public MultiUserChat createRoom(String user, String roomName,
			String password, boolean isPersistentroom) {
		if (XmppTool.getConnection() == null)
			return null;

		MultiUserChat muc = null;
		try {
			// 创建一个MultiUserChat
			muc = new MultiUserChat(XmppTool.getConnection(), roomName + "@conference."
					+ XmppTool.getConnection().getServiceName());
			// 创建聊天室
			muc.create(roomName);
			// 获得聊天室的配置表单
			Form form = muc.getConfigurationForm();
			// 根据原始表单创建一个要提交的新表单。
			Form submitForm = form.createAnswerForm();
			// 向要提交的表单添加默认答复
			for (Iterator<FormField> fields = form.getFields(); fields
					.hasNext();) {
				FormField field = (FormField) fields.next();
				if (!FormField.TYPE_HIDDEN.equals(field.getType())
						&& field.getVariable() != null) {
					// 设置默认值作为答复
					submitForm.setDefaultAnswer(field.getVariable());
				}
			}
			// 设置聊天室的新拥有者
			// List<String> owners = new ArrayList<String>();
			// owners.add(connection.getUser());// 用户JID
			// String ss=connection.getUser();
			// submitForm.setAnswer("muc#roomconfig_roomowners", owners);
			// 设置聊天室是持久聊天室，即将要被保存下来
			submitForm.setAnswer("muc#roomconfig_persistentroom",
					isPersistentroom);
			// 房间仅对成员开放
			submitForm.setAnswer("muc#roomconfig_membersonly", false);
			// 允许占有者邀请其他人
			submitForm.setAnswer("muc#roomconfig_allowinvites", true);
			if (!password.equals("")) {
				// 进入是否需要密码
				submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",
						true);
				// 设置进入密码
				submitForm.setAnswer("muc#roomconfig_roomsecret", password);
			}
			// 能够发现占有者真实 JID 的角色
			// submitForm.setAnswer("muc#roomconfig_whois", "anyone");
			// 登录房间对话
			submitForm.setAnswer("muc#roomconfig_enablelogging", true);
			// 仅允许注册的昵称登录
			submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
			// 允许使用者修改昵称
			submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
			// 允许用户注册房间
			submitForm.setAnswer("x-muc#roomconfig_registration", false);
			// 发送已完成的表单（有默认值）到服务器来配置聊天室
			muc.sendConfigurationForm(submitForm);
		} catch (XMPPException e) {
			e.printStackTrace();
			return null;
		}
		return muc;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	@SuppressLint("NewApi")
	public static class PlaceholderFragment extends Fragment {
		public PlaceholderFragment() {

		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
}
