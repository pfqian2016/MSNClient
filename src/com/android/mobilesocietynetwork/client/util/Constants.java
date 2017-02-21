package com.android.mobilesocietynetwork.client.util;

import android.util.Log;

import com.android.mobilesocietynetwork.client.R;


public class Constants
{
	//public static String SERVER_IP = "119.29.247.210";// 服务器ip
	//public static String SERVER_NAME = "119.29.247.210";//服务器域名
	//public static String SERVER_IP = "192.168.1.210";// 服务器ip
	public static String SERVER_IP = "222.197.180.140";
	public static String SERVER_NAME = "openfire";//服务器域名
	public static int SERVER_PORT = 5222;// 服务器端口
	public static final String ACTION = "com.way.message";// 混合式消息广播action
	public static final String MSGKEY = "message";// 混合式消息的key
	public static final String NORMAL_ACTION = "com.way.normalmessage";//正常的聊天消息广播
	public static final String NORMAL_MSGKEY = "normalmessage";// 正常的聊天消息的key
	public static final String MODE = "com.way.mode";//混合式模式选择广播action
	public static final String MODEKEY = "mode";// 消息的key
	public static final String IP_PORT = "ipPort";// 保存ip、port的xml文件名
	public static final String SAVE_USER = "saveUser";// 保存用户信息的xml文件名
	public static final String BACKKEY_ACTION = "com.way.backKey";// 返回键发送广播的action
	public static final String FRIENDDBNAME = "qqfri.db";// 好友数据库名称
	public static final String FILEDBNAME="qqfile.db";//文件数据库名称
	public static final String MESSAGEDB = "qqmes.db";// 聊天记录数据库名称
	public static final String BUFFERMESSAGEDB = "buffermes.db";// 聊天记录数据库名称
	public static final String OFFLINE_THROWTIME_DB = "offlinethrowtime.db";// 分布式投递的代价值数据库
	public static final String WAIT_TO_REC_DB = "waittoreceive.db";// 发送消息的时间和类型数据库
	public static final String ONLINE_THROWTIME_DB = "onlinethrowtime.db";// 在线投递的代价值数据库
	public static final String OFFLINE_MESSAGE_DB = "offlinemessage.db";//离线数据库、
	public static final String BROADCAST_REQ_GOT_RECORD_DB = "broadcast_req_got_record.db";
	public static final String BROADCAST_RESPONSE_GOT_RECORD_DB = "broadcast_response_got_record.db";
	public static final String COST_WAIT_TO_RESPONSE_DB = "cost_wait_to_response.db";
	public static final String COMMUNITYDB = "qqcom.db";
	public static final String NET_SPEED = "netSpeed";
	public static final String NET_WILLING = "netWilling";
	//标签
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
		R.drawable.f7, R.drawable.f8, R.drawable.f9 };// 头像资源
	//log相关
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

	// JSON packet 字段
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
