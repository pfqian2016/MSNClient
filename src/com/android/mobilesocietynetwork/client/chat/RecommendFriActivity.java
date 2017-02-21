package com.android.mobilesocietynetwork.client.chat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.widget.SimpleAdapter;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.IQTypeFilter;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.xmlpull.v1.XmlPullParser;

import com.android.mobilesocietynetwork.client.ActivityManager;
import com.android.mobilesocietynetwork.client.MyActivity;
import com.android.mobilesocietynetwork.client.notice.NoticeActivity.NoticeListviewAdapter;
import com.android.mobilesocietynetwork.client.packet.RecommendPacket;
import com.android.mobilesocietynetwork.client.tool.RecommendFriTool;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class RecommendFriActivity extends Activity {

	private ListView mListView;
	private ArrayList<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
	private SimpleAdapter simpleAdapter;
    private ProgressDialog pd;  
	private RecommendFriTool retool = new RecommendFriTool();
	private ArrayList<String> list  = new ArrayList<String>();
	// ������ĳ�Ա�������ڽ���xmlʱ���Ͱ�Recommendlist��ֵ

	// private XMPPConnection con;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_recommend_fri);
		ActivityManager exitM = ActivityManager.getInstance();
		exitM.addActivity(RecommendFriActivity.this);
		mListView = (ListView) findViewById(R.id.listView_recommend_fri);

		retool.SendIQ();
		   pd = ProgressDialog.show(RecommendFriActivity.this, "Hint", "loading����");  
	        new Thread(new Runnable() {  
	            @Override  
	            public void run() {  
	            	if(receiveProcess())// �����Ƽ��Ļ,������ܲ�Ϊ�գ�������Ϣ
	                handler.sendEmptyMessage(1);  // ִ�к�ʱ�ķ���֮��������handler  
	            	else 
	            		handler.sendEmptyMessage(0); //û������ʱ��������Ϣ0
	            }  	  
	        }).start();  
}
	
	//���߳���ִ�еĺ�ʱ����  
    private boolean receiveProcess() {  
  	  //����һ��List<noticeInfo>���͵Ļ�б�
    	int count = 0;
 		while(retool.getRecommendList()==null&&count<10){
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		}
		list = retool.getRecommendList();
  		return list.size()!=0;
    }  
    
    Handler handler=new Handler()  
    {   
    	@Override  
        public void handleMessage(Message msg)  // handler���յ���Ϣ��ͻ�ִ�д˷��� 
        {     
    		super.handleMessage(msg);
             switch(msg.what)  
            {  
            case 1:  //������Ƽ��
            	  pd.dismiss();// �ر�ProgressDialog 
        	   simpleAdapter = new SimpleAdapter(RecommendFriActivity.this, listItems,
        				R.layout.list_view, new String[] { "name" },
        				new int[] { R.id.item_community });
            		for (int i = 0; i < list.size(); i++) {
            			Map<String, Object> listItem = new LinkedHashMap<String, Object>();
            			listItem.put("name", list.get(i));
            			listItems.add(listItem);
            		}
            		mListView.setAdapter(simpleAdapter);
                break;  

            case 0:  //���û������
          	  pd.dismiss();// �ر�ProgressDialog 
    		simpleAdapter = new SimpleAdapter(RecommendFriActivity.this, listItems,
    				R.layout.list_view, new String[] { "name" },
    				new int[] { R.id.item_community });
      		mListView.setAdapter(simpleAdapter);
           	Toast.makeText(RecommendFriActivity.this,"No data", Toast.LENGTH_SHORT).show();
              break;  
            default:  
                break;        
            }  
        }  
    };

}
