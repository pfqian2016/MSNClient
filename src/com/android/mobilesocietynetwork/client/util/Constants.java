package com.android.mobilesocietynetwork.client.util;

import android.util.Log;

import com.android.mobilesocietynetwork.client.R;


public class Constants
{
	//public static String SERVER_IP = "119.29.247.210";// ������ip
	//public static String SERVER_NAME = "119.29.247.210";//����������
	//public static String SERVER_IP = "192.168.1.210";// ������ip
	public static String SERVER_IP = "222.197.180.140";
	public static String SERVER_NAME = "openfire";//����������
	public static int SERVER_PORT = 5222;// �������˿�
	public static final String ACTION = "com.way.message";// ���ʽ��Ϣ�㲥action
	public static final String MSGKEY = "message";// ���ʽ��Ϣ��key
	public static final String NORMAL_ACTION = "com.way.normalmessage";//������������Ϣ�㲥
	public static final String NORMAL_MSGKEY = "normalmessage";// ������������Ϣ��key
	public static final String MODE = "com.way.mode";//���ʽģʽѡ��㲥action
	public static final String MODEKEY = "mode";// ��Ϣ��key
	public static final String IP_PORT = "ipPort";// ����ip��port��xml�ļ���
	public static final String SAVE_USER = "saveUser";// �����û���Ϣ��xml�ļ���
	public static final String BACKKEY_ACTION = "com.way.backKey";// ���ؼ����͹㲥��action
	public static final String FRIENDDBNAME = "qqfri.db";// �������ݿ�����
	public static final String FILEDBNAME="qqfile.db";//�ļ����ݿ�����
	public static final String MESSAGEDB = "qqmes.db";// �����¼���ݿ�����
	public static final String BUFFERMESSAGEDB = "buffermes.db";// �����¼���ݿ�����
	public static final String OFFLINE_THROWTIME_DB = "offlinethrowtime.db";// �ֲ�ʽͶ�ݵĴ���ֵ���ݿ�
	public static final String WAIT_TO_REC_DB = "waittoreceive.db";// ������Ϣ��ʱ����������ݿ�
	public static final String ONLINE_THROWTIME_DB = "onlinethrowtime.db";// ����Ͷ�ݵĴ���ֵ���ݿ�
	public static final String OFFLINE_MESSAGE_DB = "offlinemessage.db";//�������ݿ⡢
	public static final String BROADCAST_REQ_GOT_RECORD_DB = "broadcast_req_got_record.db";
	public static final String BROADCAST_RESPONSE_GOT_RECORD_DB = "broadcast_response_got_record.db";
	public static final String COST_WAIT_TO_RESPONSE_DB = "cost_wait_to_response.db";
	public static final String COMMUNITYDB = "qqcom.db";
	public static final String NET_SPEED = "netSpeed";
	public static final String NET_WILLING = "netWilling";
	//��ǩ
	public static final String[] ListTitle={"Gym","TV","Entertaiment","Interest","Focus"};
	public static final String[][] LabelListCN={
			{"basketball","football","swimming","badminton","tabletennis"},
			{"SKTV","ATV","BTV","JTV","CTV"},
			{"movie","cartoon","travel","shopping","KTV"},
			{"reading","writing","drawing","music","dancing"},
			{"S&T","economy","realestate","art","politics"}};
	public static final String[][] LabelListEN={
		{"basketball","football","swimming","badminton","tabletennis"},
		{"SKTV","ATV","BTV","JTV","CTV"},
		{"movie","cartoon","travel","shopping","KTV"},
		{"reading","writing","drawing","music","dancing"},
		{"S&T","economy","realestate","art","politics"}};
	
	public static final int[][] LabelListNUM={
		{1,2,3,4,5},
		{6,7,8,9,10},
		{11,12,13,14,15},
		{16,17,18,19,20},
		{21,22,23,24,25}
		};
	
	public static final int[] IMGS = { R.drawable.icon, R.drawable.f1, R.drawable.f2,
		R.drawable.f3, R.drawable.f4, R.drawable.f5, R.drawable.f6,
		R.drawable.f7, R.drawable.f8, R.drawable.f9 };// ͷ����Դ
	//log���
	public static final String UNKNOWNMSG_ARRIVED = "unknown message arrived";
	public static final String TAG_LOG = "com.llrsunshine.graduationdesign";
	public static final String HEAD_LOG = "LLR_sunshine ----- ";
	public static void logPrint(String log)
	{
		Log.d(TAG_LOG, "LLR_sunshine ----- " + log);
	}
	
	public static final int UPDATE_LIST = 0x1234;
	public static final int SCF_RECEIVE = 0x400 + 1;
	public static final int SCF_SEND = 0x400 + 2;
	public static final int TOASTSHOW = 0x400 + 3;

	// JSON packet �ֶ�
	public static final String PACKET_TYPE = "element";
	public static final String GET_FRIEND = "getFriends";
	public static final String NOTICE_ACCEPT = "noticeAccept";
	public static final String ASK_ONLINECOST = "askOnlineCost";
	public static final String RESPONSE_ONLINECOST = "responseonline";
	public static final String HELP_ONLINECOST = "askonline";
	public static final String DIRECT_SCF = "directSCF";
	public static final String FORWARD_SCF = "forwardSCF";
	public static final String FILE_PROCESS="file";
}
