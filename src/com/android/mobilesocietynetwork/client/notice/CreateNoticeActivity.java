package com.android.mobilesocietynetwork.client.notice;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.jivesoftware.smack.Roster;
//import org.jivesoftware.smack.XmppConnection;
import org.jivesoftware.smack.packet.IQ.Type;

import com.android.mobilesocietynetwork.client.MyActivity;
import com.android.mobilesocietynetwork.client.packet.CreateNoticePacket;
import com.android.mobilesocietynetwork.client.tool.ReceiveNoticeIQTool;
import com.android.mobilesocietynetwork.client.tool.XmppTool;
import com.android.mobilesocietynetwork.client.util.Constants;
import com.android.mobilesocietynetwork.client.util.DateTimePickDialogUtil;
import com.android.mobilesocietynetwork.client.util.ImgHelper;
import com.android.mobilesocietynetwork.client.R;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

















import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.util.Log;



public class CreateNoticeActivity extends MyActivity {
	public static final int REQUEST_CODE_PHOTO_PICKED = 1;
	public static final int REQUEST_CODE_VIDEO_PICKED = 2;
	private EditText etTitle;
	private EditText etStartTime;
	private EditText etLocation;
	private EditText etContent;
	private EditText etLimit;
	private ImageView imgNoticePic;
	private ImageView imgFullScreen;
	private ListView elNoticeType;
	private Button btSubmit;
	private Button btQuit;
	private Button btChooseImage;
	//add video
	//private Button btChooseVideo;
	//private VideoView videoNotice;
	private MyAdapter adapter;
	private boolean[][] LabelSelect = new boolean[5][5];
	// 百度地图
	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();
	private double mlongitude;
	private double mlatitude;
	//private Uri videoUri;
	
    private ProgressDialog pd;  
   

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_create_notice);
		elNoticeType = null;
		initView();
		initControl();
	}

	private void initControl() {
		// TODO Auto-generated method stub
		MyOnClickListner createNoticeButtonOnclick = new MyOnClickListner();
		btSubmit.setOnClickListener(createNoticeButtonOnclick);
		btQuit.setOnClickListener(createNoticeButtonOnclick);
		btChooseImage.setOnClickListener(createNoticeButtonOnclick);
		//btChooseVideo.setOnClickListener(createNoticeButtonOnclick);
		imgNoticePic.setOnClickListener(createNoticeButtonOnclick);
		imgFullScreen.setOnClickListener(createNoticeButtonOnclick);
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
		mLocationClient.setLocOption(option);
		// 获取经纬度
		mLocationClient.start();
		mLocationClient.stop();
		// 时间选择控件
		etStartTime.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(CreateNoticeActivity.this, "");
				dateTimePicKDialog.dateTimePicKDialog(etStartTime);

			}
		});
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

	private class MyOnClickListner implements OnClickListener {

		public void onClick(View arg0) {
			int buttonID = arg0.getId();
			switch (buttonID) {
		//	case R.id.video_NoticeVideo:
		//		Intent intent=new Intent(Intent.ACTION_VIEW);
		//		intent.setDataAndType(videoUri, "video/mp4");
		//		startActivity(intent);
		//		break;
			case R.id.img_NoticePic:
				imgFullScreen.setVisibility(View.VISIBLE);
				imgFullScreen.setImageBitmap(((BitmapDrawable) imgNoticePic.getDrawable()).getBitmap());
				break;
			case R.id.iv_fullscreen:
				imgFullScreen.setVisibility(View.GONE);
				break;
			case R.id.bt_Submit:
				// 点击提交按钮的响应
				if (etTitle.getText().toString().equals("")) {
					Toast.makeText(CreateNoticeActivity.this, "Please enter title", 1).show();
					break;
				}
				if (etStartTime.getText().toString().equals("")) {
					Toast.makeText(CreateNoticeActivity.this, "Please enter time", 1).show();
					break;
				}
				if (etLocation.getText().toString().equals("")) {
					Toast.makeText(CreateNoticeActivity.this, "Please enter place", 1).show();
					break;
				}
				String title = etTitle.getText().toString();
				String startTime = etStartTime.getText().toString();
				String location = etLocation.getText().toString();
				String content = etContent.getText().toString();
				ImgHelper imgHelper = new ImgHelper();
				String imageString="";
				if(imgNoticePic.getDrawable()!=null)
				{
					imageString = imgHelper
						.bitmapToString(((BitmapDrawable) imgNoticePic.getDrawable()).getBitmap());
				}
				
				int limit = 50;
				limit = Integer.parseInt(etLimit.getText().toString());
				// 获取分类
				ArrayList<String> list = new ArrayList<String>();
				for (int i = 0; i < LabelSelect.length; i++) {
					for (int j = 0; j < LabelSelect[i].length; j++) {
						if (LabelSelect[i][j] == true)
							list.add(Constants.LabelListEN[i][j]);
					}
				}			
				// 发送创建活动的IQ包
							ReceiveNoticeIQTool.init();
							CreateNoticePacket createNoticeIQ = new CreateNoticePacket();
							createNoticeIQ.setTitle(title);
							createNoticeIQ.setTime(startTime);
							createNoticeIQ.setAddress(location);
							createNoticeIQ.setContent(content);
							createNoticeIQ.setLimit(limit);
							createNoticeIQ.setImageString(imageString);
							createNoticeIQ.setLabelList(list);

							String longitude = mlongitude + "";
							String latitude = mlatitude + "";
							createNoticeIQ.setLongitude(longitude);
							createNoticeIQ.setLatitude(latitude);

						XmppTool.getConnection().sendPacket(createNoticeIQ);
				
		        /* 显示ProgressDialog */  
		        pd = ProgressDialog.show(CreateNoticeActivity.this, "Hint", "Creating……");  
		        /* 开启一个新线程，在新线程里执行接收返回结果 */  
		        new Thread(new Runnable() {  
		            @Override  
		            public void run() {  
		            	if(receiveResult())// 接收到成功发送消息
		                handler.sendEmptyMessage(1);  // 执行耗时的方法之后发送消给handler  
		            	else 
		            		 handler.sendEmptyMessage(0);  
		            }  	  
		        }).start();  
				break;
			case R.id.bt_Quit:
				// 点击取消
				CreateNoticeActivity.this.finish();
				break;
			case R.id.bt_ChooseImage:
				// 点击添加图片
				// start Intent
				Intent intentImage = new Intent(Intent.ACTION_GET_CONTENT, null);
				intentImage.setType("image/*");
				startActivityForResult(intentImage, REQUEST_CODE_PHOTO_PICKED);
				break;
		//	case R.id.bt_ChooseVideo:
		//		Intent intentVideo = new Intent(Intent.ACTION_GET_CONTENT, null);
		//		intentVideo.setType("video/*");
		//		startActivityForResult(intentVideo, REQUEST_CODE_VIDEO_PICKED);
		//		break;
			default:
				break;
			}
		}
	}

	private class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			mlatitude = location.getLatitude();
			mlongitude = location.getLongitude();
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		etTitle = (EditText) findViewById(R.id.et_Title);
		etStartTime = (EditText) findViewById(R.id.et_StartTime);
		etLocation = (EditText) findViewById(R.id.et_Location);
		etContent = (EditText) findViewById(R.id.et_Content);
		etLimit = (EditText) findViewById(R.id.et_Limit);
		imgNoticePic = (ImageView) findViewById(R.id.img_NoticePic);
		imgFullScreen = (ImageView) findViewById(R.id.iv_fullscreen);
		//btChooseVideo = (Button) findViewById(R.id.bt_ChooseVideo);
		//videoNotice = (VideoView) findViewById(R.id.video_NoticeVideo);
		elNoticeType = (ListView) findViewById(R.id.noticeTypeList);
		btSubmit = (Button) findViewById(R.id.bt_Submit);
		btQuit = (Button) findViewById(R.id.bt_Quit);
		btChooseImage = (Button) findViewById(R.id.bt_ChooseImage);
		adapter = new MyAdapter(this, R.layout.listview_items_register);
		elNoticeType.setAdapter(adapter);

	}

	private class MyAdapter extends ArrayAdapter<Object> {
		int mTextViewResourceID = 0;
		private Context mContext;

		public MyAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			mTextViewResourceID = textViewResourceId;
			mContext = context;
		}

		public int getCount() {
			return Constants.ListTitle.length;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			TextView title = null;
			CheckBox checkBox1 = null;
			CheckBox checkBox2 = null;
			CheckBox checkBox3 = null;
			CheckBox checkBox4 = null;
			CheckBox checkBox5 = null;

			// if (convertView == null)
			// {
			convertView = LayoutInflater.from(mContext).inflate(mTextViewResourceID, null);
			title = (TextView) convertView.findViewById(R.id.title_textview_listitems_register);
			checkBox1 = (CheckBox) convertView.findViewById(R.id.checkBox1_listitems_register);
			checkBox2 = (CheckBox) convertView.findViewById(R.id.checkBox2_listitems_register);
			checkBox3 = (CheckBox) convertView.findViewById(R.id.checkBox3_listitems_register);
			checkBox4 = (CheckBox) convertView.findViewById(R.id.checkBox4_listitems_register);
			checkBox5 = (CheckBox) convertView.findViewById(R.id.checkBox5_listitems_register);
			title.setText(Constants.ListTitle[position]);
			checkBox1.setText(Constants.LabelListCN[position][0]);
			checkBox2.setText(Constants.LabelListCN[position][1]);
			checkBox3.setText(Constants.LabelListCN[position][2]);
			checkBox4.setText(Constants.LabelListCN[position][3]);
			checkBox5.setText(Constants.LabelListCN[position][4]);
			checkBox1.setChecked(LabelSelect[position][0]);
			checkBox2.setChecked(LabelSelect[position][1]);
			checkBox3.setChecked(LabelSelect[position][2]);
			checkBox4.setChecked(LabelSelect[position][3]);
			checkBox5.setChecked(LabelSelect[position][4]);
			checkBox1.setOnCheckedChangeListener(new CheckBoxListener(position));
			checkBox2.setOnCheckedChangeListener(new CheckBoxListener(position));
			checkBox3.setOnCheckedChangeListener(new CheckBoxListener(position));
			checkBox4.setOnCheckedChangeListener(new CheckBoxListener(position));
			checkBox5.setOnCheckedChangeListener(new CheckBoxListener(position));

			// }
			return convertView;
		}
	}

	private class CheckBoxListener implements OnCheckedChangeListener {

		private int position;

		public CheckBoxListener(int position) {
			// TODO Auto-generated constructor stub
			this.position = position;
		}

		@Override
		public void onCheckedChanged(CompoundButton ButtonView, boolean arg1) {
			// TODO Auto-generated method stub
			switch (ButtonView.getId()) {
			case R.id.checkBox1_listitems_register:
				if (arg1 == true) {
					LabelSelect[position][0] = true;
				} else {
					LabelSelect[position][0] = false;
				}
				break;
			case R.id.checkBox2_listitems_register:
				if (arg1 == true) {
					LabelSelect[position][1] = true;
				} else {
					LabelSelect[position][1] = false;
				}
				break;
			case R.id.checkBox3_listitems_register:
				if (arg1 == true) {
					LabelSelect[position][2] = true;
				} else {
					LabelSelect[position][2] = false;
				}
				break;
			case R.id.checkBox4_listitems_register:
				if (arg1 == true) {
					LabelSelect[position][3] = true;
				} else {
					LabelSelect[position][3] = false;
				}
				break;
			case R.id.checkBox5_listitems_register:
				if (arg1 == true) {
					LabelSelect[position][4] = true;
				} else {
					LabelSelect[position][4] = false;
				}
				break;

			default:
				break;
			}
		}

	}

	// if it is fullscreen,press back to return to createActivity,
	// if not,finish this activity
	@Override
	public void onBackPressed() {
		if (imgFullScreen.getVisibility() == 0) {
			imgFullScreen.setVisibility(View.GONE);
		} else {
			finish();
		}
	}
	
  private boolean receiveResult() {  
    	  //接收一个List<noticeInfo>类型的活动列表
    	int count = 0;
		while(ReceiveNoticeIQTool.getResult()==null && count <20){
		try {
			Thread.sleep(500);
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
	            case 1:  //如果创建成功
	            	  pd.dismiss();// 关闭ProgressDialog 
	            		Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
						CreateNoticeActivity.this.finish();
		                break;  
	            case 0:
	            	pd.dismiss();
	            	Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
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



}
