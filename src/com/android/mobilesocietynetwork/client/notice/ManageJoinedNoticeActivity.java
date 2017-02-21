package com.android.mobilesocietynetwork.client.notice;

import java.io.IOException;
import java.util.ArrayList;

import com.android.mobilesocietynetwork.client.MyActivity;
import com.android.mobilesocietynetwork.client.chat.DialogActivity;
import com.android.mobilesocietynetwork.client.info.NoticeInfo;
import com.android.mobilesocietynetwork.client.info.User;
import com.android.mobilesocietynetwork.client.packet.CancelNoticePacket;
import com.android.mobilesocietynetwork.client.packet.MyJoinedNoticePacket;
import com.android.mobilesocietynetwork.client.tool.ReceiveNoticeIQTool;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.ImgHelper;
import com.android.mobilesocietynetwork.client.R;




import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ManageJoinedNoticeActivity extends MyActivity {
	 
	private ListView lvJoinedNotices;
	private JoinedNoticeAdapter mAdapter;
	private int count=0;
	//private boolean joinFlag = true;

	private ArrayList<NoticeInfo> joinedNotices = new ArrayList<NoticeInfo>();
    private ProgressDialog pd;  
    private boolean success = true;//��ǰ�����Ƿ�ɹ��ı�־
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_manage_joined_notice);
		lvJoinedNotices = (ListView) findViewById(R.id.lvJoinedNotices);
		initData();
	}
	
	//��ʼ�����ݣ�����ʾ
	private void initData() {
		// TODO Auto-generated method stub
		//����ұ����Ļ	
		//ReceiveNoticeIQTool.init();
		MyJoinedNoticePacket myJoinedNoticeIQ = new MyJoinedNoticePacket();
		XmppTool.getConnection().sendPacket(myJoinedNoticeIQ);
		ReceiveNoticeIQTool.resettIsReceiveJoin();
	  //����һ��List<noticeInfo>���͵Ļ�б�
	      pd = ProgressDialog.show(ManageJoinedNoticeActivity.this, "Hint", "loading����");  
	        /* ����һ�����̣߳������߳���ִ�н��� */  
	        new Thread(new Runnable() {  
	            @Override  
	            public void run() {  
	            	if(receiveJoined())// �����Ƽ��Ļ,������ܲ�Ϊ�գ�������Ϣ
	                handler.sendEmptyMessage(101);  // ִ�к�ʱ�ķ���֮��������handler  
	            	else 
	            		handler.sendMessage(handler.obtainMessage(100, "no data")); 
	            }  	  
	        }).start();  
		return;
	}
	
	
	public class JoinedNoticeAdapter extends BaseAdapter{
		private ArrayList<NoticeInfo> list;
		private Context context;
		public JoinedNoticeAdapter(Context context, ArrayList<NoticeInfo> list) {
			this.list = list;
			this.context = context;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		//��ȡ�����б��View����
		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) 
		{
			@SuppressWarnings("unchecked")
	        View layoutNoticeChild = arg1;  
	        final ViewHolder vh;  
	        if (layoutNoticeChild == null) {  
	        	LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        	layoutNoticeChild = layoutInflater.inflate(R.layout.exlistview_notice_child, null);    
	            vh = new ViewHolder();  
	            vh.image = (ImageView)layoutNoticeChild.findViewById(R.id.displayImage);
	            vh. title= (TextView)layoutNoticeChild.findViewById(R.id.tvItemName);
	            vh. distance= (TextView)layoutNoticeChild.findViewById(R.id. tvItemDistance);
	            vh. content= (TextView)layoutNoticeChild.findViewById(R.id.tvItemContent);
	            vh. informer= (TextView)layoutNoticeChild.findViewById(R.id.tvItemInformer);
	            vh. startTime= (TextView)layoutNoticeChild.findViewById(R.id.tvItemStartTime);
	            vh. location= (TextView)layoutNoticeChild.findViewById(R.id.tvItemLocation);
	            vh. limit = (TextView)layoutNoticeChild.findViewById(R.id.tvItemLimit);
	            vh.peopleNum = (TextView)layoutNoticeChild.findViewById(R.id.tvItemPeopleNum);
	            vh.label1 = (TextView)layoutNoticeChild.findViewById(R.id.tvItemLabel1);
	            vh.label2 = (TextView)layoutNoticeChild.findViewById(R.id.tvItemLabel2);
	            vh.label3 = (TextView)layoutNoticeChild.findViewById(R.id.tvItemLabel3);
	            vh.label4 = (TextView)layoutNoticeChild.findViewById(R.id.tvItemLabel4);
	            vh.label5 = (TextView)layoutNoticeChild.findViewById(R.id.tvItemLabel5);
	            vh.labels.add(vh.label1);
	            vh.labels.add(vh.label2);
	            vh.labels.add(vh.label3);
	            vh.labels.add(vh.label4);
	            vh.labels.add(vh.label5);
	            vh.contact = (LinearLayout)layoutNoticeChild.findViewById(R.id.btContact);
	            vh.comment = (LinearLayout)layoutNoticeChild.findViewById(R.id.btComment);
	            vh.join =  (LinearLayout)layoutNoticeChild.findViewById(R.id.btJoin);
	            vh.joinText = (TextView)layoutNoticeChild.findViewById(R.id.tvItemJoinText);
	            layoutNoticeChild.setTag(vh);  
	        } else {  
	            vh = (ViewHolder)layoutNoticeChild.getTag();  
	        } 
	        //holder�и�Ԫ��������Ӧ������
	    	final NoticeInfo child = (NoticeInfo) list.get(arg0);
	    	ImgHelper imgHelper=new ImgHelper();
	    	if(child.getImageString()!=null && child.getImageString().length() > 0){   
		    	try {
		    		Bitmap imageBitmap = ImgHelper.bytesToBitmap(ImgHelper.decode(child.getImageString()));
						    vh.image.setImageBitmap(imageBitmap); 
						    vh.image.setVisibility(View.VISIBLE);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    	}
			vh.title.setText(child.getTitle());
			vh.distance.setText(child.getDistance()+"km");
			vh.distance.setVisibility(View.GONE);
			vh.content.setText(child.getContent());
			vh.informer.setText(child.getInformer());
			vh.startTime.setText(child.getStartTime());
			vh. location.setText(child.getLocation());
			vh. limit.setText(child.getLimit());
			vh.peopleNum.setText(child.getPeopleNum());
			//����Ѿ��������������İ�ťֱ�ӱ�Ϊȡ����
			if(Integer.parseInt(child.getIsJoined())==1){
			    vh.joinText.setText("Cancel");
			}
			for(int i=0;i<child.getLabels().size()&&i<5;i++)
			{
              vh.labels.get(i).setText(child.getLabels().get(i));
              vh.labels.get(i).setVisibility(View.VISIBLE);
			}
				
	        vh.contact.setOnClickListener(new OnClickListener() {  
	            @Override  
	            public void onClick(View v) {  
	            	//�����ϵ��ť֮�����Ӧ,������ҳ��
					User user = new User();
					user.setName(child.getInformer());
					Intent intend5 = new Intent(ManageJoinedNoticeActivity.this,DialogActivity.class);
					intend5.putExtra("user", user);
					startActivity(intend5);
	           //Toast.makeText(NoticeActivity.this,"�������ϵ", Toast.LENGTH_SHORT).show();  
	            }  
	        }); 
	        vh.comment.setOnClickListener(new View.OnClickListener() {  
	            @Override  
	            public void onClick(View v) {  
	            	//������۰�ť֮�����Ӧ
	        		Intent intent4 = new Intent(ManageJoinedNoticeActivity.this, CommentNoticeActivity.class);
	        		//����һ������
	        		//Bundle bund = new Bundle();
					//bund.putSerializable("NoticeID", child.getId());
					intent4.putExtra("NoticeID", child.getId());
					startActivity(intent4);		
	            }  
	        }); 
	        vh.join.setOnClickListener(new View.OnClickListener() {  
	            @Override  
	            public void onClick(View v) {  
	            	//����μӰ�ť֮�����Ӧ�����ж���Ҫ�μӻ�Ҫȡ��
	            	int intPeopleNum = Integer.parseInt(child.getPeopleNum());
	            	   //�����ȡ������
	            	 	if(sendPacket(child.getId())){
	            	   		//�������ɹ�,ȡ���μ��ϵ����ֱ�Ϊ����������������1
	            	         vh.joinText.setText("Join");
	            	         vh.peopleNum.setText(String.valueOf(intPeopleNum -1));
	            	         child.setPeopleNum(String.valueOf(intPeopleNum - 1));
	            	   	}
        
	            }  
	        });      
			return layoutNoticeChild;
		}	
	}
	
	private boolean sendPacket(String id) {
		//�����ȡ������������ȡ�����������ݰ������շ��������͵ķ���
		ReceiveNoticeIQTool.init();
		CancelNoticePacket cancelNoticeIQ = new CancelNoticePacket();
		cancelNoticeIQ.setNoticeID(id);
		XmppTool.getConnection().sendPacket(cancelNoticeIQ);
	    pd = ProgressDialog.show(ManageJoinedNoticeActivity.this, "Hint", "canceling����");  
	    new Thread(new Runnable() {  
	        @Override  
	        public void run() {  
	        	if(receiveResult()) 
	        		handler.sendEmptyMessage(102);
	        	else
	        		{
	        		handler.sendEmptyMessage(103); 
	        	    success = false;
	        		}
	        }  	  
	    }).start();  
	    return success;
	}
	
	static class ViewHolder
	{

		public ImageView image;
		public TextView title;
		public TextView distance;
		public TextView content;
		public TextView informer;
		public TextView startTime;
		public TextView location;
		public TextView limit;	
		public TextView joinText;	
		public ArrayList<TextView> labels = new ArrayList<TextView>();
	    public TextView label1;
		public TextView label2;
		public TextView label3;
		public TextView label4;
		public TextView label5;
		public TextView peopleNum;
		public LinearLayout contact;
		public LinearLayout comment;
		public LinearLayout join;
	}
	
	//���߳���ִ�еĺ�ʱ����  
    private boolean receiveJoined() {  
  	  //����һ��List<noticeInfo>���͵Ļ�б�
    	count = 0;
		joinedNotices = ReceiveNoticeIQTool.getJoinedList();
  		while((!ReceiveNoticeIQTool.getIsReceiveJoin())&&count<60){
  		//while(joinedNotices.size()==0&&count<20){
  		try {
  			Thread.sleep(300);
  			count++;
  		} catch (InterruptedException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}		
  		}
  		joinedNotices = ReceiveNoticeIQTool.getJoinedList();
		ReceiveNoticeIQTool.resettIsReceiveJoin();
  		return joinedNotices.size()!=0;
    }  
    
    private boolean receiveResult() {  
    	  //����һ��List<noticeInfo>���͵Ļ�б�
    	count = 0;
		while(ReceiveNoticeIQTool.getResult()==null && count <30){
		try {
			Thread.sleep(300);
			count++;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		if("1".equals(ReceiveNoticeIQTool.getResult()))
		{
		ReceiveNoticeIQTool.resetResult();
		return true;	
		}
	else{
		ReceiveNoticeIQTool.resetResult();
		return false;
	} 		
    		
      }   
    
    Handler handler=new Handler()  
    {   
    	@Override  
        public void handleMessage(Message msg)  // handler���յ���Ϣ��ͻ�ִ�д˷��� 
        {     
    		super.handleMessage(msg);
             switch(msg.what)  
            {  
            case 101:  //������Ƽ��
            	  pd.dismiss();// �ر�ProgressDialog 
          		//��ʾ����
          	    mAdapter = new JoinedNoticeAdapter(ManageJoinedNoticeActivity.this, joinedNotices);
          		lvJoinedNotices.setAdapter(mAdapter);
                break;  
            case 102:  //����ǽ���ɹ�
            	 pd.dismiss();// �ر�ProgressDialog
            	Toast.makeText(ManageJoinedNoticeActivity.this,"Succeed", Toast.LENGTH_SHORT).show();   
            	break;  
            case 103:  //����ǽ������ʧ��
           	 pd.dismiss();// �ر�ProgressDialog
           	Toast.makeText(ManageJoinedNoticeActivity.this,"Failed", Toast.LENGTH_SHORT).show();
              break;  
            case 104:  //����Ǹ��½���
          	  pd.dismiss();// �ر�ProgressDialog 
          	mAdapter.notifyDataSetChanged();
              break;  
            case 100:  //���û������
          	  pd.dismiss();// �ر�ProgressDialog 
          	 mAdapter = new JoinedNoticeAdapter(ManageJoinedNoticeActivity.this, joinedNotices);
       		lvJoinedNotices.setAdapter(mAdapter);
           	Toast.makeText(ManageJoinedNoticeActivity.this,"No data", Toast.LENGTH_SHORT).show();
              break;  
            default:  
                break;        
            }  
        }  
    };
	
	


}
