package com.android.mobilesocietynetwork.client.notice;


import java.io.IOException;
import java.util.ArrayList;

import com.android.mobilesocietynetwork.client.MyActivity;
import com.android.mobilesocietynetwork.client.R;
import com.android.mobilesocietynetwork.client.info.NoticeInfo;
import com.android.mobilesocietynetwork.client.packet.DeleteNoticePacket;
import com.android.mobilesocietynetwork.client.packet.MyCreatedNoticePacket;
import com.android.mobilesocietynetwork.client.tool.ReceiveNoticeIQTool;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.ImgHelper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ManageCreatedNoticeActivity extends MyActivity {
 
	private ListView lvCreatedNotices;
		private MyCreatedNoticeAdapter mAdapter;
		private int count=0;
		private ArrayList<NoticeInfo> createdNotices = new ArrayList<NoticeInfo>();
	    private ProgressDialog pd;  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_manage_created_notice);
		lvCreatedNotices = (ListView) findViewById(R.id.lvCreatedNotices);
		initData();
	}
	
	//初始化数据，并显示
	private void initData() {
		// TODO Auto-generated method stub
		//获得我创建的活动
		//ReceiveNoticeIQTool.init();
		MyCreatedNoticePacket myCreatedNoticeIQ = new MyCreatedNoticePacket();
		XmppTool.getConnection().sendPacket(myCreatedNoticeIQ);
		ReceiveNoticeIQTool.resettIsReceiveCreate();
	  //接收一个List<noticeInfo>类型的活动列表
        /* 显示ProgressDialog */  
        pd = ProgressDialog.show(ManageCreatedNoticeActivity.this, "Hint", "loading……");  
        /* 开启一个新线程，在新线程里执行接收 */  
        new Thread(new Runnable() {  
            @Override  
            public void run() {  
            	if(receiveCreated())// 接收创建的活动,如果接受不为空，发送消息
            	{
            		handler.sendEmptyMessage(201);  
                	  pd.dismiss();// 关闭ProgressDialog 
            	}
              // 执行耗时的方法之后发送消给handler  
            	else 
            		{
            		handler.sendEmptyMessage(200); 
              	    pd.dismiss();// 关闭ProgressDialog 
            		}
            }  	  
        }).start();  
		return;
	}
	
	public class MyCreatedNoticeAdapter extends BaseAdapter{
		private ArrayList<NoticeInfo> list;
		private Context ctx;
		public MyCreatedNoticeAdapter(Context ctx, ArrayList<NoticeInfo> list) {
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
	        	layoutCreatedNotice = layoutInflater.inflate(R.layout.listview_notice_created, null);    
                holder = new ViewHolder();
	            holder. title= (TextView)layoutCreatedNotice.findViewById(R.id.tvItemName);
	            holder. distance= (TextView)layoutCreatedNotice.findViewById(R.id. tvItemDistance);
	            holder. content= (TextView)layoutCreatedNotice.findViewById(R.id.tvItemContent);
	            holder.image=(ImageView)layoutCreatedNotice.findViewById(R.id.ivItemImage);
	            holder. informer= (TextView)layoutCreatedNotice.findViewById(R.id.tvItemInformer);
	            holder. startTime= (TextView)layoutCreatedNotice.findViewById(R.id.tvItemStartTime);
	            holder. location= (TextView)layoutCreatedNotice.findViewById(R.id.tvItemLocation);
	            holder. limit = (TextView)layoutCreatedNotice.findViewById(R.id.tvItemLimit);
	            holder.peopleNum = (TextView)layoutCreatedNotice.findViewById(R.id.tvItemPeopleNum);
	            holder.label1 = (TextView)layoutCreatedNotice.findViewById(R.id.tvItemLabel1);
	            holder.label2 = (TextView)layoutCreatedNotice.findViewById(R.id.tvItemLabel2);
	            holder.label3 = (TextView)layoutCreatedNotice.findViewById(R.id.tvItemLabel3);
	            holder.label4 = (TextView)layoutCreatedNotice.findViewById(R.id.tvItemLabel4);
	            holder.label5 = (TextView)layoutCreatedNotice.findViewById(R.id.tvItemLabel5);
	            holder.labels.add(holder.label1);
	            holder.labels.add(holder.label2);
	            holder.labels.add(holder.label3);
	            holder.labels.add(holder.label4);
	            holder.labels.add(holder.label5);
	            holder.participant= (LinearLayout)layoutCreatedNotice.findViewById(R.id.btParticipant);
	            holder.comment= (LinearLayout)layoutCreatedNotice.findViewById(R.id.btComment);	           
	            holder.delete= (LinearLayout)layoutCreatedNotice.findViewById(R.id.btDelete);  
	            layoutCreatedNotice.setTag(holder);  
			} else {
				holder = (ViewHolder)layoutCreatedNotice.getTag();  
			}
	        final NoticeInfo info = (NoticeInfo)list.get(arg0);
	        //parse the image into holder
	        ImgHelper imgHelper=new ImgHelper();
	        if(info.getImageString()!=null && info.getImageString().length() > 0){   
		    	try {
						    Bitmap imageBitmap=ImgHelper.bytesToBitmap(ImgHelper.decode(info.getImageString()));
						    holder.image.setImageBitmap(imageBitmap); 
						    holder.image.setVisibility(View.VISIBLE);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        }
	        holder.title.setText(info.getTitle());
			holder.distance.setText(info.getDistance()+"km");
			holder.distance.setVisibility(View.GONE);
			holder.content.setText(info.getContent());
			holder.informer.setText(info.getInformer());
			holder.startTime.setText(info.getStartTime());
			holder. location.setText(info.getLocation());
			holder. limit.setText(info.getLimit());
			holder.peopleNum.setText(info.getPeopleNum());
		    holder.participant.setOnClickListener(new View.OnClickListener() {  
		            @Override  
		            public void onClick(View v) {  
		            	//点击参与者按钮之后的响应
/*		            	Intent intent3 = new Intent(ManageCreatedNoticeActivity.this, ParticipantNoticeActivity.class);
						intent3.putExtra("NoticeID", info.getId());
						startActivity(intent3);		*/
		            }  
		        });      
	        holder.comment.setOnClickListener(new View.OnClickListener() {  
	            @Override  
	            public void onClick(View v) {  
	            	//点击评论按钮之后的响应
	        		Intent intent4 = new Intent( ManageCreatedNoticeActivity.this, CommentNoticeActivity.class);
	        		intent4.putExtra("NoticeID", info.getId());
					startActivity(intent4);			
	            }  
	        }); 
	        holder.delete.setOnClickListener(new View.OnClickListener() {  
	            @Override  
	            public void onClick(View v) {  
	            	//点击删除按钮之后的响应，弹出确认对话框      	
	                AlertDialog.Builder builder = new Builder(ManageCreatedNoticeActivity.this);
		            builder.setMessage("Sure to delete this activity?");
		            builder.setTitle("Hint");
		            builder.setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
		               @Override
		               public void onClick(DialogInterface dialog, int which) {
		                dialog.dismiss();
                         //确认删除后，发送删除包
		                ReceiveNoticeIQTool.init();
			              DeleteNoticePacket deleteNoticeIQ = new DeleteNoticePacket();
			              deleteNoticeIQ.setNoticeID( info.getId());
			              XmppTool.getConnection().sendPacket(deleteNoticeIQ);
			      		//开启接收线程
			              pd = ProgressDialog.show(ManageCreatedNoticeActivity.this, "Hint", "deleting……");  
			              new Thread(new Runnable() {  
			                  @Override  
			                  public void run() {  
			                  	if(receiveResult()) 
			                  	{
			                  	//	handler.sendMessage(handler.obtainMessage(2, arg0));
			                  		handler.sendEmptyMessage(202);
			                  	list.remove(arg0);
			                  	}	        
			                  	else
			                  		handler.sendEmptyMessage(203); 
			                  }  	  
			              }).start();  
			              }
		               });
		            builder.setNegativeButton("No", new  DialogInterface.OnClickListener() {
		            	@Override
		            	public void onClick(DialogInterface dialog, int which) {
		            		dialog.dismiss();
		            		}
		            	});
		            builder.create().show();	 
		            }  
	            });      
	        return layoutCreatedNotice;
		}
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
		public LinearLayout participant;
		public LinearLayout comment;
		public LinearLayout delete;
	}
	
	//在线程中执行的耗时操作  
    private boolean receiveCreated() {  
  	  //接收一个List<noticeInfo>类型的活动列表
    	count = 0;
    	createdNotices = ReceiveNoticeIQTool.getCreatedList();
  		//while(createdNotices.size()==0&&count<20){
  		while((!ReceiveNoticeIQTool.getIsReceiveCreate())&&count<60){
  		try {
  			Thread.sleep(300);
  			count++;
  		} catch (InterruptedException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}	
  		}
  		createdNotices = ReceiveNoticeIQTool.getCreatedList();
		ReceiveNoticeIQTool.resettIsReceiveCreate();
  		return createdNotices.size()!=0;
    }  
    
    private boolean receiveResult() {  
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
            case 201:  //如果是推荐活动
            	//  pd.dismiss();// 关闭ProgressDialog 
            	   mAdapter = new MyCreatedNoticeAdapter(ManageCreatedNoticeActivity.this, createdNotices);
           		lvCreatedNotices.setAdapter(mAdapter);
                break;  
            case 202:  //如果是结果成功
            	 pd.dismiss();// 关闭ProgressDialog
            	Toast.makeText(ManageCreatedNoticeActivity.this,"Succeed", Toast.LENGTH_SHORT).show();
            	//更新本地的列表
            	  mAdapter.notifyDataSetChanged();
            	break;  
            case 203:  //如果是结果操作失败
           	 pd.dismiss();// 关闭ProgressDialog
           	Toast.makeText(ManageCreatedNoticeActivity.this,"Failed", Toast.LENGTH_SHORT).show();
              break;  
            case 200:  //如果没有数据
         // 	  pd.dismiss();// 关闭ProgressDialog 
       	   mAdapter = new MyCreatedNoticeAdapter(ManageCreatedNoticeActivity.this, createdNotices);
      		lvCreatedNotices.setAdapter(mAdapter);
           	Toast.makeText(ManageCreatedNoticeActivity.this,"no data", Toast.LENGTH_SHORT).show();
              break;  
            default:  
                break;        
            }  
        }  
    };
	


}