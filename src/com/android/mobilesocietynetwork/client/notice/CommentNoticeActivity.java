package com.android.mobilesocietynetwork.client.notice;


import java.util.ArrayList;
import java.util.HashMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;

import com.android.mobilesocietynetwork.client.MyActivity;
import com.android.mobilesocietynetwork.client.database.SharePreferenceUtil;
import com.android.mobilesocietynetwork.client.info.CommentInfo;
import com.android.mobilesocietynetwork.client.packet.AskCommentsPacket;
import com.android.mobilesocietynetwork.client.packet.SendCommentPacket;
import com.android.mobilesocietynetwork.client.tool.ReceiveNoticeIQTool;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;
import com.android.mobilesocietynetwork.client.util.ImgHelper;
import com.android.mobilesocietynetwork.client.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;


public class CommentNoticeActivity extends Activity {
	
	public static final int REQUEST_CODE_PHOTO_PICKED = 1;
	public static final int REQUEST_CODE_VIDEO_PICKED = 2;
 
	private ListView lvComments;
	private EditText etComment;
	private Button btSend;
	private Button btBack;
	private Button btChooseImage;
	private ImageView imgNoticePic;
	private ImageView commentFullImg;
	private String currentImage;
	private String pub_name;
	private String pub_date;
	private String content;
	private String image_string;
	
	private String noticeID;
	private int count = 0;
	private ArrayList<CommentInfo> comments = new ArrayList<CommentInfo>();
	private ArrayList<HashMap<String,Object>> listItems=new ArrayList<HashMap<String,Object>>();
	//private CommentsAdapter mAdapter ;
	private SimpleAdapter listItemAdapter;
	private SharePreferenceUtil util;
    private ProgressDialog pd;  
    


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_notice_comment);
		Intent intent = getIntent();
	    noticeID = intent.getStringExtra("NoticeID");  
	    initView();
	    initData();
	    initControl();
	    //initList();
	}
	
	private void initView() {
		lvComments = (ListView) findViewById(R.id.lvcomments);
		etComment  = (EditText) findViewById(R.id.etComment );
		btSend = (Button)findViewById(R.id.btSend);
		btBack = (Button)findViewById(R.id.btBack);
		btChooseImage = (Button) findViewById(R.id.bt_ChooseImage);
		imgNoticePic = (ImageView) findViewById(R.id.img_NoticePic);
		commentFullImg=(ImageView) findViewById(R.id.comment_full_img);
	}
	
	
	private void initControl(){
		/************listview click event****************/
		lvComments.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				HashMap<String, Object> map=(HashMap<String, Object>) parent.getAdapter().getItem(position);
				
				Bitmap bitmap=(Bitmap) map.get("image");
				commentFullImg.setVisibility(View.VISIBLE);
				commentFullImg.setImageBitmap(bitmap);
				//ImgHelper imgHelper=new ImgHelper();
				//String imageString=imgHelper.bitmapToString(bitmap);
				
				/*SerializableMap myMap=new SerializableMap();
				myMap.setMap(map);
				Bundle bundle=new Bundle();
				bundle.putSerializable("details", myMap);*/
				
				//Intent intentDetail=new Intent(CommentNoticeActivity.this,CommentDetailActivity.class);
				
				//intentDetail.putExtras(bundle);
				//intentDetail.putExtra("details", imageString);
				//startActivity(intentDetail);
				//String content=(String) map.get("content");
				//Toast.makeText(CommentNoticeActivity.this, content, Toast.LENGTH_SHORT).show();
			}
			
		});
		/************listview click event****************/
		btChooseImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intentImage = new Intent(Intent.ACTION_GET_CONTENT, null);
				intentImage.setType("image/*");
				startActivityForResult(intentImage, REQUEST_CODE_PHOTO_PICKED);
			}
			
		});
		
		commentFullImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(v.getVisibility()==View.VISIBLE){
					v.setVisibility(View.GONE);
				}
			}
		});
		
		
		btSend.setOnClickListener(new OnClickListener(){
	    	
	    	@Override
	    	public void onClick(View v){
	    	if(etComment.getText().toString().equals(""))
	    		{
	    		Toast.makeText(CommentNoticeActivity.this, "Content can not be empty", 1).show();
	    		return;
	    		}
			ImgHelper imgHelper = new ImgHelper();
			String imageString="";
			if(imgNoticePic.getDrawable()!=null)
			{
				imageString = imgHelper
					.bitmapToString(((BitmapDrawable) imgNoticePic.getDrawable()).getBitmap());
			}

	    		String comment = etComment.getText().toString();
	    		//向服务器发送评论
	    		ReceiveNoticeIQTool.init();
			    SendCommentPacket commentNoticeIQ = new SendCommentPacket();
			    commentNoticeIQ.setNoticeID(noticeID);//获取活动的id
			    commentNoticeIQ.setComment(comment);
			    commentNoticeIQ.setImageString(imageString);
			    XmppTool.getConnection().sendPacket(commentNoticeIQ);
				//开启接收线程
		        pd = ProgressDialog.show(CommentNoticeActivity.this, "Hint", "Sending……");  
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
		btBack.setOnClickListener(new OnClickListener(){
	    	
	    	@Override
	    	public void onClick(View v){
	    		CommentNoticeActivity.this.finish();
	    	}});
		
	}
	
	private void initData(){
		
		//获得该活动的评论内容
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		AskCommentsPacket askCommentsIQ = new AskCommentsPacket();
		ReceiveNoticeIQTool.init();
		askCommentsIQ.setNoticeID(noticeID);
		XmppTool.getConnection().sendPacket(askCommentsIQ);
		   /* 显示ProgressDialog */  
        pd = ProgressDialog.show(CommentNoticeActivity.this, "Hint", "loading……");  
        /* 开启一个新线程，在新线程里执行接收 */  
        new Thread(new Runnable() {  
            @Override  
            public void run() {  
            	if(receiveComments())// 接收推荐的活动,如果接受不为空，发送消息
            	{
            		initList();
            		handler.sendEmptyMessage(1);  // 执行耗时的方法之后发送消给handler  
            	}
            	else 
            		handler.sendMessage(handler.obtainMessage(0, "no data")); 
            	
            }  	  
        }).start();  

	}
	
	// get image from media and then set ImageView
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (resultCode != RESULT_OK) {
				return;
			}
			Bitmap bitmap = null;
			Bitmap bitmapCompress = null;
			ContentResolver cr = getContentResolver();
			if (requestCode == REQUEST_CODE_PHOTO_PICKED) {
				try {
					Uri imageUri = data.getData();
					bitmap = BitmapFactory.decodeStream(cr.openInputStream(imageUri));
					//压缩图片
					bitmapCompress=comp(bitmap);
					imgNoticePic.setVisibility(View.VISIBLE);
					imgNoticePic.setImageBitmap(bitmapCompress);
					bitmap.recycle();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		//	if (requestCode == REQUEST_CODE_VIDEO_PICKED) {
		//		   	videoUri = data.getData();
		//			File videoFile = new File(videoUri.toString());
		//			videoNotice.setVisibility(View.VISIBLE);
		//			videoNotice.setVideoPath(videoFile.getPath());
		//	
		//	}

		}
		
		/******************用HashMap实现listview*******************/
		public void initList(){
			for(int i=0;i<comments.size();i++){
				pub_name=comments.get(i).getPubName();
				pub_date=comments.get(i).getPubDate();
				content=comments.get(i).getContent();
				image_string=comments.get(i).getImageString();
				Bitmap imageBitmap=null;
				if(image_string!=null && image_string.length() > 0){   
			    	try {
			    		byte[] imgByte = ImgHelper.decode(image_string);
							    imageBitmap=ImgHelper.bytesToBitmap(imgByte);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    	}
				HashMap<String,Object> map=new HashMap<String,Object>();
				map.put("name", pub_name);
				map.put("date", pub_date);
				map.put("content", content);
				map.put("image", imageBitmap);
				listItems.add(map);
			}
		}
		/******************用HashMap实现listview*******************/
		/*public class CommentsAdapter extends BaseAdapter{
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
	            holder.image = (ImageView)layoutCreatedNotice.findViewById(R.id.displayImage);
	            layoutCreatedNotice.setTag(holder);  
			} else {
				holder = (ViewHolder)layoutCreatedNotice.getTag();  
			}
	        final CommentInfo info = (CommentInfo)list.get(arg0);
	        
	    	if(info.getImageString()!=null && info.getImageString().length() > 0){   
		    	try {
		    		byte[] imgByte = ImgHelper.decode(info.getImageString());
						    Bitmap imageBitmap=ImgHelper.bytesToBitmap(imgByte);
						    holder.image.setImageBitmap(imageBitmap); 
						    holder.image.setVisibility(View.VISIBLE);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	}
	    	
			holder.name.setText(info.getPubName());
			holder.date.setText(info.getPubDate());
			holder.content.setText(info.getContent());

			return layoutCreatedNotice;
		}
	}*/
	
/*	static class ViewHolder
	{
		public TextView name;
		public TextView date;
		public TextView content;	
		public ImageView image;
		
	}*/
	
    private boolean receiveComments() {  
    	  //接收一个List<noticeInfo>类型的活动列表
      	count = 0;
    	//	while(comments.size()==0&&count<10){
  		while((!ReceiveNoticeIQTool.getIsReceive())&&count<20){
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
            case 1:  //如果查看评论
            	 pd.dismiss();
            	/* mAdapter = new CommentsAdapter(CommentNoticeActivity.this, comments);
         		lvComments.setAdapter(mAdapter);*/
            	 listItemAdapter=new SimpleAdapter(CommentNoticeActivity.this,listItems,R.layout.listview_items_comment,new String[]{"name","date","content","image"},new int[]{R.id.tvName,R.id.tvDate,R.id.tvContent,R.id.displayImage});
            	 listItemAdapter.setViewBinder(new ViewBinder() {  
                      
                      @Override  
                      public boolean setViewValue(View view, Object data,  
                              String textRepresentation) {  
                          if(view instanceof ImageView && data instanceof Bitmap){    
                              ImageView i = (ImageView)view;    
                              i.setImageBitmap((Bitmap) data);    
                              return true;    
                          }    
                          return false;  
                      }  
                  });  
            	 lvComments.setAdapter(listItemAdapter);
                break;  
            case 2:  //如果是结果成功
            	 pd.dismiss();// 关闭ProgressDialog
            	Toast.makeText(CommentNoticeActivity.this,"Comment successfully", Toast.LENGTH_SHORT).show();
			/*	//同时更新评论列表
				CommentInfo myComment = new CommentInfo();
				myComment.setPubName(util.getName());
				//获取系统时间
				SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");       
				String date  =  sDateFormat.format(new  java.util.Date());  
				myComment.setPubDate(date);
				myComment.setContent(etComment.getText().toString());
				//myComment.setImageString(currentImage);
				//加图片
				comments.add(myComment);
				mAdapter.notifyDataSetChanged();*/
            	SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");       
				String date  =  sDateFormat.format(new  java.util.Date());  
				ImgHelper imgHelper = new ImgHelper();
				String imageString="";
				Bitmap bitmap=null;
				if(imgNoticePic.getDrawable()!=null)
				{
					bitmap=((BitmapDrawable) imgNoticePic.getDrawable()).getBitmap();
				}
				HashMap<String,Object> myMap=new HashMap<String,Object>();
            	myMap.put("name",util.getName());
            	myMap.put("date",date);
            	myMap.put("content",etComment.getText().toString());
            	myMap.put("image",bitmap);
            	listItems.add(myMap);
            	listItemAdapter.notifyDataSetChanged();
            	imgNoticePic.setVisibility(View.GONE);
				etComment.setText("");
            	break;  
            case 3:  //如果是结果操作失败
           	 pd.dismiss();// 关闭ProgressDialog
           	Toast.makeText(CommentNoticeActivity.this,"Failed,please try again later", Toast.LENGTH_SHORT).show();
              break;  

            case 0:  //如果没有数据
          	  pd.dismiss();// 关闭ProgressDialog 
        /* 	 mAdapter = new CommentsAdapter(CommentNoticeActivity.this, comments);
      		lvComments.setAdapter(mAdapter);*/
          	 listItemAdapter=new SimpleAdapter(CommentNoticeActivity.this,listItems,R.layout.listview_items_comment,new String[]{"name","date","content","image"},new int[]{R.id.tvName,R.id.tvDate,R.id.tvContent,R.id.displayImage});
          	 listItemAdapter.setViewBinder(new ViewBinder() {  
                 
                 @Override  
                 public boolean setViewValue(View view, Object data,  
                         String textRepresentation) {  
                     if(view instanceof ImageView && data instanceof Bitmap){    
                         ImageView i = (ImageView)view;    
                         i.setImageBitmap((Bitmap) data);    
                         return true;    
                     }    
                     return false;  
                 }  
             });  
          	 lvComments.setAdapter(listItemAdapter);
           	Toast.makeText(CommentNoticeActivity.this,"no data", Toast.LENGTH_SHORT).show();
              break;  
            default:  
                break;        
            }  
        }  
    };
    
    //压缩bitmap
    private Bitmap comp(Bitmap image) {  
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();         
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
        if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出    
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());  
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
        newOpts.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为  
        float hh = 800f;//这里设置高度为800f  
        float ww = 480f;//这里设置宽度为480f  
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
        int be = 1;//be=1表示不缩放  
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
            be = (int) (newOpts.outWidth / ww);  
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
            be = (int) (newOpts.outHeight / hh);  
        }  
        if (be <= 0)  
            be = 1;  
        newOpts.inSampleSize = be;//设置缩放比例  
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
        isBm = new ByteArrayInputStream(baos.toByteArray());  
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩  
    }  
    
    //质量压缩
    private Bitmap compressImage(Bitmap image) {  
    	  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100;  
        while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            options -= 10;//每次都减少10  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;  
    }
    
    class SerializableMap implements Serializable{
    	private HashMap<String, Object> map;
    	
    	public HashMap<String, Object> getMap(){
    		return map;
    	}
    	
    	public void setMap(HashMap<String, Object> map){
    		this.map=map;
    	}
    }

}
