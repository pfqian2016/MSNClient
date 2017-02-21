package com.android.mobilesocietynetwork.client.notice;


import java.util.ArrayList;
import java.text.SimpleDateFormat;

import com.android.mobilesocietynetwork.client.MyActivity;
import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.info.CommentInfo;
import com.android.mobilesocietynetwork.client.packet.AskCommentsPacket;
import com.android.mobilesocietynetwork.client.packet.SendCommentPacket;
import com.android.mobilesocietynetwork.client.tool.ReceiveNoticeIQTool;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;
import com.android.mobilesocietynetwork.client.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class NoticeDetailCommentActivity extends Activity {
 
	private ListView lvComments;
	private EditText etComment;
	private Button btSend;
	private String noticeID;
	private int count = 0;
	private ArrayList<CommentInfo> comments = new ArrayList<CommentInfo>();
	private CommentsAdapter mAdapter;
	private SharePreferenceUtil util;
    private ProgressDialog pd;  
	private ReceiveNoticeIQTool ReceiveNoticeIQTool = new ReceiveNoticeIQTool();
	//private ReceiveNoticeIQTool ReceiveNoticeIQTool2 = new ReceiveNoticeIQTool();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_notice_detail_comment);
		Intent intent = getIntent();
	    noticeID = intent.getStringExtra("NoticeID");  
	    initView();
	    initControl();
	    initData();
	}
	
	private void initView() {
		lvComments = (ListView) findViewById(R.id.lvcomments);
		etComment  = (EditText) findViewById(R.id.etComment );
		btSend = (Button)findViewById(R.id.btSend);
	}

		private void initControl(){
			ReceiveNoticeIQTool.init();
			btSend.setOnClickListener(new OnClickListener(){
		    	
		    	@Override
		    	public void onClick(View v){
		    	if(etComment.getText().toString().equals(""))
		    		{
		    		Toast.makeText(NoticeDetailCommentActivity.this, "empty content", 1).show();
		    		return;
		    		}
		    		String comment = etComment.getText().toString();
		    		//���������������
		    		//ReceiveNoticeIQTool2.init();
				    SendCommentPacket commentNoticeIQ = new SendCommentPacket();
				    commentNoticeIQ.setNoticeID(noticeID);//��ȡ���id
				    commentNoticeIQ.setComment(comment);
					XmppTool.getConnection().sendPacket(commentNoticeIQ);
					//���������߳�
			        pd = ProgressDialog.show(NoticeDetailCommentActivity.this, "Hint", "Commenting����");  
			        new Thread(new Runnable() {  
			            @Override  
			            public void run() {  
			            	if(receiveResult()) 
			            		handler.sendEmptyMessage(2);
			            	else
			            		{
			            		handler.sendEmptyMessage(3); 
			            		}
			            }  	  
			        }).start();  
		    	}
			}
		    	);
		
	}
	
		private void initData(){

			//��øû����������
			util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		//	ReceiveNoticeIQTool.init();
	    	ReceiveNoticeIQTool.resetIsReceive();
			AskCommentsPacket askCommentsIQ = new AskCommentsPacket();
			askCommentsIQ.setNoticeID(noticeID);
			XmppTool.getConnection().sendPacket(askCommentsIQ);
			   /* ��ʾProgressDialog */  
	        pd = ProgressDialog.show(NoticeDetailCommentActivity.this, "Hint", "loading����");  
	        /* ����һ�����̣߳������߳���ִ�н��� */  
	        new Thread(new Runnable() {  
	            @Override  
	            public void run() {  
	            	if(receiveComments())// �����Ƽ��Ļ,������ܲ�Ϊ�գ�������Ϣ
	                handler.sendEmptyMessage(1);  // ִ�к�ʱ�ķ���֮��������handler  
	            	else 
	            		handler.sendMessage(handler.obtainMessage(0, "no data")); 
	            }  	  
	        }).start();  

		}
		
		public class CommentsAdapter extends BaseAdapter{
			private ArrayList<CommentInfo> list;
			private Context ctx;
			public CommentsAdapter(Context ctx, ArrayList<CommentInfo> list) {
				this.list = list;
				this.ctx = ctx;
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

			@Override
			public View getView(final int arg0, View arg1, ViewGroup arg2) {
		        View layoutCreatedNotice  = arg1;  
		        final ViewHolder holder;  
		        if ( layoutCreatedNotice == null) {  
		        	LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        	layoutCreatedNotice = layoutInflater.inflate(R.layout.listview_items_comment, null);    
		            holder = new ViewHolder();  
		            holder.name= (TextView)layoutCreatedNotice.findViewById(R.id.tvName);
		            holder. date= (TextView)layoutCreatedNotice.findViewById(R.id. tvDate);
		            holder. content= (TextView)layoutCreatedNotice.findViewById(R.id.tvContent);
		            layoutCreatedNotice.setTag(holder);  
				} else {
					holder = (ViewHolder)layoutCreatedNotice.getTag();  
				}
		        final CommentInfo info = (CommentInfo)list.get(arg0);
				holder.name.setText(info.getPubName());
				holder.date.setText(info.getPubDate());
				holder.content.setText(info.getContent());

				return layoutCreatedNotice;
			}
		}
		
		static class ViewHolder
		{
			public TextView name;
			public TextView date;
			public TextView content;	
			
		}
	    private boolean receiveComments() {  
	    	  //����һ��List<noticeInfo>���͵Ļ�б�
	      	count = 0;
	  		while((!ReceiveNoticeIQTool.getIsReceive())&&count<20){
	    		//while(comments.size()==0&&count<10){
	    		try {
	    			Thread.sleep(500);
	    			count++;
	    		} catch (InterruptedException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		}
	  		comments = ReceiveNoticeIQTool.getComments();	
	  		ReceiveNoticeIQTool.resetIsReceive();
	    		return comments.size()!=0;
	      }  
	    
	    private boolean receiveResult() {  
	  	count = 0;
			while(ReceiveNoticeIQTool.getResult()==null && count <20){
	  	//while(count <10){
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
	            case 1:  //����鿴����
	            	 pd.dismiss();
	            	 mAdapter = new CommentsAdapter(NoticeDetailCommentActivity.this, comments);
	         		lvComments.setAdapter(mAdapter);
	                break;  
	            case 2:  //����ǽ���ɹ�
	            	 pd.dismiss();// �ر�ProgressDialog
	            	Toast.makeText(NoticeDetailCommentActivity.this,"Succeed", Toast.LENGTH_SHORT).show();
					//ͬʱ���������б�
					CommentInfo myComment = new CommentInfo();
					myComment.setPubName(util.getName());
					//��ȡϵͳʱ��
					SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");       
					String date  =  sDateFormat.format(new  java.util.Date());  
					myComment.setPubDate(date);
					myComment.setContent(etComment.getText().toString());
					comments.add(myComment);
					mAdapter.notifyDataSetChanged();
					etComment.setText("");
	            	break;  
	            case 3:  //����ǽ������ʧ��
	           	 pd.dismiss();// �ر�ProgressDialog
	           	Toast.makeText(NoticeDetailCommentActivity.this,"Failed", Toast.LENGTH_SHORT).show();
	              break;  

	            case 0:  //���û������
	          	  pd.dismiss();// �ر�ProgressDialog 
	         	 mAdapter = new CommentsAdapter(NoticeDetailCommentActivity.this, comments);
	      		lvComments.setAdapter(mAdapter);
	           	Toast.makeText(NoticeDetailCommentActivity.this,"no data", Toast.LENGTH_SHORT).show();
	              break;  
	            default:  
	                break;        
	            }  
	        }  
	    };


	}
