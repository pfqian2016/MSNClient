package com.android.mobilesocietynetwork.client.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.MultiUserChat;

import com.android.mobilesocietynetwork.client.R;
import com.android.mobilesocietynetwork.client.chat.CommunityActivity.textlonglister;
import com.android.mobilesocietynetwork.client.database.CommunityListDB;
import com.android.mobilesocietynetwork.client.database.MultiUserChatDB;
import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.packet.LabelPacket;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;

import android.os.Bundle;
import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CreateComFirstActivity extends Activity {

	// 定义变量
	public static String comname;
	private EditText comNameEditText;
	private EditText passwordEditText;
	private EditText repasswordEditText;

	private Button nextButton;
	private boolean isPersistentroom = false;
	private RadioGroup mRadioGroup;
	private SharePreferenceUtil util;
	private MultiUserChatDB mutiUserChatDB;
	private static XMPPConnection connection;
	TextView communityItemTextView;
	View communityItemView;
	LinearLayout comPageItems;
	LayoutInflater inflater;
	private CommunityListDB communityListDB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_create_com1);
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		mutiUserChatDB = MultiUserChatDB.getInstance();
		connection = XmppTool.getConnection();
		// 初始化界面
		inflater = LayoutInflater.from(this);
		ArrayList<String> existingCom = mutiUserChatDB.getAllName();
		// comPageItems.removeAllViews();
		// for (int i = 0; i < existingCom.size(); i++) {
		// communityItemView = inflater.inflate(R.layout.community_item, null);
		// communityItemTextView = (TextView) communityItemView
		// .findViewById(R.id.item_text);
		// communityItemTextView.setText(existingCom.get(i));
		// comPageItems.addView(communityItemView);
		// }
		// 获取控件
		comNameEditText = (EditText) findViewById(R.id.EditTextComName);
		passwordEditText = (EditText) findViewById(R.id.EditTextPassWord);
		repasswordEditText = (EditText) findViewById(R.id.EditTextRePassWord);
		nextButton = (Button) findViewById(R.id.ButtonCreateComNext);
		nextButton.setOnClickListener(new nextStepListener());

		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroupCom);
		mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				// 获取变更后的选中项的ID
				if (arg1 == R.id.radioButtonComOption1) {
					// Toast.makeText(getApplicationContext(), "永久",
					// 1).show();
					isPersistentroom = true;
				} else if (arg1 == R.id.radioButtonComOption2) {
					// Toast.makeText(getApplicationContext(), "暂时",
					// 1).show();
					isPersistentroom = false;
				}
			}
		});

	}

	// 点击注册后的响应
	class nextStepListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			comname = comNameEditText.getText().toString();
			String password = passwordEditText.getText().toString();
			String repassword = repasswordEditText.getText().toString();

			if (comname.length() != 0 && password.length() != 0 && repassword.length() != 0) {
				if (password.equals(repassword)) {
					MultiUserChat muc = createRoom(util.getName(), comname, password, isPersistentroom);
					if (muc != null) {
						//mutiUserChatDB.addMuc(muc);
					    //添加到数据库
						// 1104 modify
						communityListDB = CommunityListDB.getInstance(CreateComFirstActivity.this);
						communityListDB.insertCommunity(util.getName(), comname,password);
						/*
						 * ArrayList<String> existingCom =
						 * mutiUserChatDB.getAllName(); for (int i = 0; i <
						 * existingCom.size(); i++) { communityItemView =
						 * inflater.inflate( R.layout.community_item, null);
						 * communityItemTextView = (TextView) communityItemView
						 * .findViewById(R.id.item_text);
						 * communityItemTextView.setText(existingCom.get(i));
						 * 
						 * }
						 */

						// 发送一个广播通知CommunityAcitivity更新
						Intent intentBroadcast = new Intent("com.android.mobilesocietynetwork.client.CREATE_COMMUNITY_SUCCESSFULLY");
						sendBroadcast(intentBroadcast);

						// 聊天室创建成功进入下一步骤
						Intent intent = new Intent(CreateComFirstActivity.this, CreateComSecondActivity.class);
						startActivity(intent);
						
						finish();
						// Toast.makeText(getApplicationContext(), "聊天室创建成功！",
						// 3000)
						// .show();
					} else
						Toast.makeText(getApplicationContext(), "Failed to create group chat", 3000).show();
				}

				else
					Toast.makeText(getApplicationContext(), "Two different passwords are entered", Toast.LENGTH_SHORT).show();

			} else
				Toast.makeText(getApplicationContext(), "account or password invalid", Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * 创建房间
	 * 
	 * @param roomName
	 *            房间名称
	 */
	public MultiUserChat createRoom(String user, String roomName, String password, boolean isPersistentroom) {
		if (connection == null)
			return null;

		MultiUserChat muc = null;
		try {
			// 创建一个MultiUserChat
			muc = new MultiUserChat(connection, roomName + "@conference." + connection.getServiceName());
			// 创建聊天室
			muc.create(roomName);
			// 获得聊天室的配置表单
			Form form = muc.getConfigurationForm();
			// 根据原始表单创建一个要提交的新表单。
			Form submitForm = form.createAnswerForm();
			// 向要提交的表单添加默认答复
			for (Iterator<FormField> fields = form.getFields(); fields.hasNext();) {
				FormField field = (FormField) fields.next();
				if (!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable() != null) {
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
			submitForm.setAnswer("muc#roomconfig_persistentroom", isPersistentroom);
			// 房间仅对成员开放
			submitForm.setAnswer("muc#roomconfig_membersonly", false);
			// 允许占有者邀请其他人
			submitForm.setAnswer("muc#roomconfig_allowinvites", true);
			if (!password.equals("")) {
				// 进入是否需要密码
				submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);
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

	// 创建账户
	private boolean createAccount(String regUserName, String regUserPwd) {
		try {
			XmppTool.getConnection().getAccountManager().createAccount(regUserName, regUserPwd);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public static String getComName() {
		return comname;
	}

}
