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

	// �������
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
		// ��ʼ������
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
		// ��ȡ�ؼ�
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
				// ��ȡ������ѡ�����ID
				if (arg1 == R.id.radioButtonComOption1) {
					// Toast.makeText(getApplicationContext(), "����",
					// 1).show();
					isPersistentroom = true;
				} else if (arg1 == R.id.radioButtonComOption2) {
					// Toast.makeText(getApplicationContext(), "��ʱ",
					// 1).show();
					isPersistentroom = false;
				}
			}
		});

	}

	// ���ע������Ӧ
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
					    //��ӵ����ݿ�
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

						// ����һ���㲥֪ͨCommunityAcitivity����
						Intent intentBroadcast = new Intent("com.android.mobilesocietynetwork.client.CREATE_COMMUNITY_SUCCESSFULLY");
						sendBroadcast(intentBroadcast);

						// �����Ҵ����ɹ�������һ����
						Intent intent = new Intent(CreateComFirstActivity.this, CreateComSecondActivity.class);
						startActivity(intent);
						
						finish();
						// Toast.makeText(getApplicationContext(), "�����Ҵ����ɹ���",
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
	 * ��������
	 * 
	 * @param roomName
	 *            ��������
	 */
	public MultiUserChat createRoom(String user, String roomName, String password, boolean isPersistentroom) {
		if (connection == null)
			return null;

		MultiUserChat muc = null;
		try {
			// ����һ��MultiUserChat
			muc = new MultiUserChat(connection, roomName + "@conference." + connection.getServiceName());
			// ����������
			muc.create(roomName);
			// ��������ҵ����ñ�
			Form form = muc.getConfigurationForm();
			// ����ԭʼ������һ��Ҫ�ύ���±���
			Form submitForm = form.createAnswerForm();
			// ��Ҫ�ύ�ı����Ĭ�ϴ�
			for (Iterator<FormField> fields = form.getFields(); fields.hasNext();) {
				FormField field = (FormField) fields.next();
				if (!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable() != null) {
					// ����Ĭ��ֵ��Ϊ��
					submitForm.setDefaultAnswer(field.getVariable());
				}
			}
			// ���������ҵ���ӵ����
			// List<String> owners = new ArrayList<String>();
			// owners.add(connection.getUser());// �û�JID
			// String ss=connection.getUser();
			// submitForm.setAnswer("muc#roomconfig_roomowners", owners);
			// �����������ǳ־������ң�����Ҫ����������
			submitForm.setAnswer("muc#roomconfig_persistentroom", isPersistentroom);
			// ������Գ�Ա����
			submitForm.setAnswer("muc#roomconfig_membersonly", false);
			// ����ռ��������������
			submitForm.setAnswer("muc#roomconfig_allowinvites", true);
			if (!password.equals("")) {
				// �����Ƿ���Ҫ����
				submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);
				// ���ý�������
				submitForm.setAnswer("muc#roomconfig_roomsecret", password);
			}
			// �ܹ�����ռ������ʵ JID �Ľ�ɫ
			// submitForm.setAnswer("muc#roomconfig_whois", "anyone");
			// ��¼����Ի�
			submitForm.setAnswer("muc#roomconfig_enablelogging", true);
			// ������ע����ǳƵ�¼
			submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
			// ����ʹ�����޸��ǳ�
			submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
			// �����û�ע�᷿��
			submitForm.setAnswer("x-muc#roomconfig_registration", false);
			// ��������ɵı�����Ĭ��ֵ����������������������
			muc.sendConfigurationForm(submitForm);
		} catch (XMPPException e) {
			e.printStackTrace();
			return null;
		}
		return muc;
	}

	// �����˻�
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
