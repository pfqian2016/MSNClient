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
	    		//���������������
	    		ReceiveNoticeIQTool.init();
			    SendCommentPacket commentNoticeIQ = new SendCommentPacket();
			    commentNoticeIQ.setNoticeID(noticeID);//��ȡ���id
			    commentNoticeIQ.setComment(comment);
			    commentNoticeIQ.setImageString(imageString);
			    XmppTool.getConnection().sendPacket(commentNoticeIQ);
				//���������߳�
		        pd = ProgressDialog.show(CommentNoticeActivity.this, "Hint", "Sending����");  
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
		
		//��øû����������
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		AskCommentsPacket askCommentsIQ = new AskCommentsPacket();
		ReceiveNoticeIQTool.init();
		askCommentsIQ.setNoticeID(noticeID);
		XmppTool.getConnection().sendPacket(askCommentsIQ);
		   /* ��ʾProgressDialog */  
        pd = ProgressDialog.show(CommentNoticeActivity.this, "Hint", "loading����");  
        /* ����һ�����̣߳������߳���ִ�н��� */  
        new Thread(new Runnable() {  
            @Override  
            public void run() {  
            	if(receiveComments())// �����Ƽ��Ļ,������ܲ�Ϊ�գ�������Ϣ
            	{
            		initList();
            		handler.sendEmptyMessage(1);  // ִ�к�ʱ�ķ���֮��������handler  
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
					//ѹ��ͼƬ
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
		
		/******************��HashMapʵ��listview*******************/
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
		/******************��HashMapʵ��listview*******************/
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
    	  //����һ��List<noticeInfo>���͵Ļ�б�
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
        public void handleMessage(Message msg)  // handler���յ���Ϣ��ͻ�ִ�д˷��� 
        {     
    		super.handleMessage(msg);
             switch(msg.what)  
            {  
            case 1:  //����鿴����
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
            case 2:  //����ǽ���ɹ�
            	 pd.dismiss();// �ر�ProgressDialog
            	Toast.makeText(CommentNoticeActivity.this,"Comment successfully", Toast.LENGTH_SHORT).show();
			/*	//ͬʱ���������б�
				CommentInfo myComment = new CommentInfo();
				myComment.setPubName(util.getName());
				//��ȡϵͳʱ��
				SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");       
				String date  =  sDateFormat.format(new  java.util.Date());  
				myComment.setPubDate(date);
				myComment.setContent(etComment.getText().toString());
				//myComment.setImageString(currentImage);
				//��ͼƬ
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
            case 3:  //����ǽ������ʧ��
           	 pd.dismiss();// �ر�ProgressDialog
           	Toast.makeText(CommentNoticeActivity.this,"Failed,please try again later", Toast.LENGTH_SHORT).show();
              break;  

            case 0:  //���û������
          	  pd.dismiss();// �ر�ProgressDialog 
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
    
    //ѹ��bitmap
    private Bitmap comp(Bitmap image) {  
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();         
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
        if( baos.toByteArray().length / 1024>1024) {//�ж����ͼƬ����1M,����ѹ������������ͼƬ��BitmapFactory.decodeStream��ʱ���    
            baos.reset();//����baos�����baos  
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//����ѹ��50%����ѹ��������ݴ�ŵ�baos��  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());  
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        //��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��  
        newOpts.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  
        //���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ  
        float hh = 800f;//�������ø߶�Ϊ800f  
        float ww = 480f;//�������ÿ��Ϊ480f  
        //���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��  
        int be = 1;//be=1��ʾ������  
        if (w > h && w > ww) {//�����ȴ�Ļ����ݿ�ȹ̶���С����  
            be = (int) (newOpts.outWidth / ww);  
        } else if (w < h && h > hh) {//����߶ȸߵĻ����ݿ�ȹ̶���С����  
            be = (int) (newOpts.outHeight / hh);  
        }  
        if (be <= 0)  
            be = 1;  
        newOpts.inSampleSize = be;//�������ű���  
        //���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��  
        isBm = new ByteArrayInputStream(baos.toByteArray());  
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
        return compressImage(bitmap);//ѹ���ñ�����С���ٽ�������ѹ��  
    }  
    
    //����ѹ��
    private Bitmap compressImage(Bitmap image) {  
    	  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��  
        int options = 100;  
        while ( baos.toByteArray().length / 1024>100) {  //ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��         
            baos.reset();//����baos�����baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��  
            options -= 10;//ÿ�ζ�����10  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ  
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
