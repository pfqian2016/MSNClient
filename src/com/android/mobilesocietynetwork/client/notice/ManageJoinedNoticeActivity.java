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
    private boolean success = true;//当前操作是否成功的标志
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_manage_joined_notice);
		lvJoinedNotices = (ListView) findViewById(R.id.lvJoinedNotices);
		initData();
	}
	
	//初始化数据，并显示
	private void initData() {
		// TODO Auto-generated method stub
		//获得我报名的活动	
		//ReceiveNoticeIQTool.init();
		MyJoinedNoticePacket myJoinedNoticeIQ = new MyJoinedNoticePacket();
		XmppTool.getConnection().sendPacket(myJoinedNoticeIQ);
		ReceiveNoticeIQTool.resettIsReceiveJoin();
	  //接收一个List<noticeInfo>类型的活动列表
	      pd = ProgressDialog.show(ManageJoinedNoticeActivity.this, "Hint", "loading……");  
	        /* 开启一个新线程，在新线程里执行接收 */  
	        new Thread(new Runnable() {  
	            @Override  
	            public void run() {  
	            	if(receiveJoined())// 接收推荐的活动,如果接受不为空，发送消息
	                handler.sendEmptyMessage(101);  // 执行耗时的方法之后发送消给handler  
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

		//获取二级列表的View对象
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
	        //holder中各元素设置相应的属性
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
			//如果已经报名过，则报名的按钮直接变为取消，
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
	            	//点击联系按钮之后的响应,打开聊天页面
					User user = new User();
					user.setName(child.getInformer());
					Intent intend5 = new Intent(ManageJoinedNoticeActivity.this,DialogActivity.class);
					intend5.putExtra("user", user);
					startActivity(intend5);
	           //Toast.makeText(NoticeActivity.this,"点击了联系", Toast.LENGTH_SHORT).show();  
	            }  
	        }); 
	        vh.comment.setOnClickListener(new View.OnClickListener() {  
	            @Override  
	            public void onClick(View v) {  
	            	//点击评论按钮之后的响应
	        		Intent intent4 = new Intent(ManageJoinedNoticeActivity.this, CommentNoticeActivity.class);
	        		//传递一捆数据
	        		//Bundle bund = new Bundle();
					//bund.putSerializable("NoticeID", child.getId());
					intent4.putExtra("NoticeID", child.getId());
					startActivity(intent4);		
	            }  
	        }); 
	        vh.join.setOnClickListener(new View.OnClickListener() {  
	            @Override  
	            public void onClick(View v) {  
	            	//点击参加按钮之后的响应，先判断是要参加还要取消
	            	int intPeopleNum = Integer.parseInt(child.getPeopleNum());
	            	   //如果是取消报名
	            	 	if(sendPacket(child.getId())){
	            	   		//如果请求成功,取消参加上的文字变为报名，报名人数加1
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
		//如果是取消报名，发送取消报名的数据包并接收服务器发送的反馈
		ReceiveNoticeIQTool.init();
		CancelNoticePacket cancelNoticeIQ = new CancelNoticePacket();
		cancelNoticeIQ.setNoticeID(id);
		XmppTool.getConnection().sendPacket(cancelNoticeIQ);
	    pd = ProgressDialog.show(ManageJoinedNoticeActivity.this, "Hint", "canceling……");  
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
	
	//在线程中执行的耗时操作  
    private boolean receiveJoined() {  
  	  //接收一个List<noticeInfo>类型的活动列表
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
    	  //接收一个List<noticeInfo>类型的活动列表
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
        public void handleMessage(Message msg)  // handler接收到消息后就会执行此方法 
        {     
    		super.handleMessage(msg);
             switch(msg.what)  
            {  
            case 101:  //如果是推荐活动
            	  pd.dismiss();// 关闭ProgressDialog 
          		//显示数据
          	    mAdapter = new JoinedNoticeAdapter(ManageJoinedNoticeActivity.this, joinedNotices);
          		lvJoinedNotices.setAdapter(mAdapter);
                break;  
            case 102:  //如果是结果成功
            	 pd.dismiss();// 关闭ProgressDialog
            	Toast.makeText(ManageJoinedNoticeActivity.this,"Succeed", Toast.LENGTH_SHORT).show();   
            	break;  
            case 103:  //如果是结果操作失败
           	 pd.dismiss();// 关闭ProgressDialog
           	Toast.makeText(ManageJoinedNoticeActivity.this,"Failed", Toast.LENGTH_SHORT).show();
              break;  
            case 104:  //如果是更新界面
          	  pd.dismiss();// 关闭ProgressDialog 
          	mAdapter.notifyDataSetChanged();
              break;  
            case 100:  //如果没有数据
          	  pd.dismiss();// 关闭ProgressDialog 
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
