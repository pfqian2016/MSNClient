package com.shareScreen;

import java.io.File;



import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mobilesocietynetwork.client.R;
import com.shareScreen.DeviceListFragment.DeviceActionListener;

public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {

    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    private MyDeviceInfo deviceInfo=MyDeviceInfo.getInstance();
    ProgressDialog progressDialog = null;
    Intent share,flieService;
    Intent fileCheck;
    static{
		System.loadLibrary("avutil-54");
		System.loadLibrary("avcodec-56");
		System.loadLibrary("swresample-1");
		System.loadLibrary("avformat-56");
		System.loadLibrary("swscale-3");
		System.loadLibrary("avfilter-5");
		System.loadLibrary("avdevice-56");
		System.loadLibrary("GetPicUsingJni");	
		
	}
   /* static Handler handler=new Handler()
    {
    	 public void handleMessage(Message msg) 
    	 {
    		 Bundle bundle=new Bundle();
    		 String string=bundle.getString("IP");
    		 Toast.makeText(null, string, 50000).show();
    	 }
    };*/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.wifidirect_device_detail, null);
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                if(deviceInfo.getIsHopeBeOwner()){
                	config.groupOwnerIntent=15;
                }else{
                	config.groupOwnerIntent=0;
                }
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true
//                        new DialogInterface.OnCancelListener() {
//
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
//                            }
//                        }
                        );
                ((DeviceActionListener)getActivity()).connect(config);

            }
        });
      
        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceActionListener) getActivity()).disconnect();
                        if(info.isGroupOwner){
                        	ServerThread.getInstance().setIsStop(true);
                        	ServerDB.getInstance().clear();
                        	if(share!=null){
                        		getActivity().stopService(share);
                        	}
                        	if(flieService!=null){
                        		getActivity().stopService(flieService);
                        	}
                        	Button b= (Button)mContentView.findViewById(R.id.btn_start_client);
                            b.setClickable(true);
                            Button b1= (Button)mContentView.findViewById(R.id.file);
                            b1.setClickable(true);
                        }else{
                        	FileCheckThread.getInstance().close();
                        	getActivity().stopService(fileCheck);
                        }
                    }
                });
        final Button share_button=(Button) mContentView.findViewById(R.id.btn_start_client);
        share_button.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // Allow user to pick an image from Gallery or other
                        // registered apps
//                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                        intent.setType("image/*");
//                        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
                    	if(info.isGroupOwner){
                    		Display display = ((WiFiDirectActivity)getActivity()).getWindowManager().getDefaultDisplay(); 
                            deviceInfo.setWidth(display.getWidth());  
                            deviceInfo.setHeight(display.getHeight());
                    		share=new Intent();
                    		share.setClass(getActivity(), ServerService.class);
                    		((WiFiDirectActivity)getActivity()).startService(share);
                    		Toast.makeText((WiFiDirectActivity)getActivity(), "开始共享", Toast.LENGTH_SHORT).show();
                    		//Button b=(Button)v;
                    		share_button.setClickable(false);
                    	}else{
                    		Intent intent=new Intent();
                    		intent.setClass(getActivity(), ShowActivity.class);
                    		startActivity(intent);
                    	}
                    }
                });
        final Button fileButton=(Button)mContentView.findViewById(R.id.file);
        fileButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Display display = ((WiFiDirectActivity)getActivity()).getWindowManager().getDefaultDisplay(); 
				CODE code=new CODE();
				code.encode(display.getWidth(), display.getHeight());
				if(info.isGroupOwner){
//					flieService=new Intent();
//					flieService.setClass(getActivity(), FileTransferService.class);
//            		((WiFiDirectActivity)getActivity()).startService(flieService);
					new FileServerThread(((WiFiDirectActivity)getActivity()).getHandler()).start();
            		fileButton.setClickable(false);
				}else{
					
					AlertDialog.Builder builder=new AlertDialog.Builder(getActivity()).setTitle("选择传输方式");
					builder.setMessage("是否手动选择，还是传输数据库？是为手动选择，否是传输数据库文件内容").setPositiveButton("否", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Log.i("ClientFileTransferService", "ClientFileTransferService run...");
							FileCheckThread.getInstance().setStart(true);
							
						}
					}).setNegativeButton("是", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Intent i=new Intent();
							i.setType("*/*");
							i.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(i,0);
						}
					}).show();				
				}
			}
		});
        return mContentView;
    }
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Uri uri;
		if(resultCode==Activity.RESULT_OK)
		{
			switch(requestCode)
			{
				case 0:
					uri=data.getData();
					new FileThread(uri,0).sendmsg();
					break;
				case 1:
					Bitmap photo=null;
					uri=data.getData();
					 if (uri != null) {
						photo = BitmapFactory.decodeFile(uri.getPath());
						new FileThread(uri,0).sendmsg();
					 }
					 if (photo == null) {
						 Bundle bundle = data.getExtras();
						 if (bundle != null) {
							photo = (Bitmap) bundle.get("data");
							new FileThread(photo,1).sendmsg();
						 } else {
							 Toast.makeText((WiFiDirectActivity)getActivity(),"无图片",Toast.LENGTH_SHORT).show();
							 return;
						 }
					 }
				
					break;
			}
		}
	}
    class FileThread
	{
		String path=null;
		Uri uri;
		int form;
		Bitmap photo;
		public FileThread(Uri s,int i)
		{
			uri=s;
			form=i;
			path=getImageAbsolutePath((WiFiDirectActivity)getActivity(),s);
		}
		public FileThread(Bitmap s,int i)
		{
			photo=s;
			form=i;
		}
		public void sendmsg()
		{
			//Packet l = new Packet();
			String s;
			switch(form)
			{
				case 0:
					File file=new File(path);
					s=file.getName();
					Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
					new FileTransferThread(uri, s, getActivity().getApplicationContext()).start();
					break;
				case 1:
					s=System.currentTimeMillis()+".jpg";
					
					break;
			}		
		}	
	}
    /************************************通过Uri获取文件路劲*********************************************/
	@TargetApi(19)  
	public static String getImageAbsolutePath(Activity context, Uri imageUri) {  
	    if (context == null || imageUri == null)  
	        return null;  
	    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {  
	        if (isExternalStorageDocument(imageUri)) {  
	            String docId = DocumentsContract.getDocumentId(imageUri);  
	            String[] split = docId.split(":");  
	            String type = split[0];  
	            if ("primary".equalsIgnoreCase(type)) {  
	                return Environment.getExternalStorageDirectory() + "/" + split[1];  
	            }  
	        } else if (isDownloadsDocument(imageUri)) {  
	            String id = DocumentsContract.getDocumentId(imageUri);  
	            Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));  
	            return getDataColumn(context, contentUri, null, null);  
	        } else if (isMediaDocument(imageUri)) {  
	            String docId = DocumentsContract.getDocumentId(imageUri);  
	            String[] split = docId.split(":");  
	            String type = split[0];  
	            Uri contentUri = null;  
	            if ("image".equals(type)) {  
	                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
	            } else if ("video".equals(type)) {  
	                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;  
	            } else if ("audio".equals(type)) {  
	                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;  
	            }  
	            String selection = MediaStore.Images.Media._ID + "=?";  
	            String[] selectionArgs = new String[] { split[1] };  
	            return getDataColumn(context, contentUri, selection, selectionArgs);  
	        }  
	    } // MediaStore (and general)  
	    else if ("content".equalsIgnoreCase(imageUri.getScheme())) {  
	        // Return the remote address  
	        if (isGooglePhotosUri(imageUri))  
	            return imageUri.getLastPathSegment();  
	        return getDataColumn(context, imageUri, null, null);  
	    }  
	    // File  
	    else if ("file".equalsIgnoreCase(imageUri.getScheme())) {  
	        return imageUri.getPath();  
	    }  
	    return null;  
	}  
	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {  
	    Cursor cursor = null;  
	    String column = MediaStore.Images.Media.DATA;  
	    String[] projection = { column };  
	    try {  
	        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);  
	        if (cursor != null && cursor.moveToFirst()) {  
	            int index = cursor.getColumnIndexOrThrow(column);  
	            return cursor.getString(index);  
	        }  
	    } finally {  
	        if (cursor != null)  
	            cursor.close();  
	    }  
	    return null;  
	}  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is ExternalStorageProvider. 
	 */  
	public static boolean isExternalStorageDocument(Uri uri) {  
	    return "com.android.externalstorage.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is DownloadsProvider. 
	 */  
	public static boolean isDownloadsDocument(Uri uri) {  
	    return "com.android.providers.downloads.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is MediaProvider. 
	 */  
	public static boolean isMediaDocument(Uri uri) {  
	    return "com.android.providers.media.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is Google Photos. 
	 */  
	public static boolean isGooglePhotosUri(Uri uri) {  
	    return "com.google.android.apps.photos.content".equals(uri.getAuthority());  
	} 
    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);
        deviceInfo.setP2PInfo(info);
        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)
                + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
                        : getResources().getString(R.string.no)));

        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
//            new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text))
//                    .execute();
        	Button button=(Button) mContentView.findViewById(R.id.btn_start_client);
        	button.setText("屏幕共享");
        	button.setVisibility(View.VISIBLE);
        	Button file_button=(Button) mContentView.findViewById(R.id.file);
        	file_button.setText("接收文件");
        	file_button.setVisibility(View.VISIBLE);
        
    		Toast.makeText((WiFiDirectActivity)getActivity(), "文件接收服务开启", Toast.LENGTH_SHORT).show();
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case, we enable the
            // get file button.
        	Button button=(Button) mContentView.findViewById(R.id.btn_start_client);
        	button.setText("屏幕接收");
        	button.setVisibility(View.VISIBLE);
        	Button file_button=(Button) mContentView.findViewById(R.id.file);
        	file_button.setText("发送文件");
        	file_button.setVisibility(View.VISIBLE);
            //mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
                    .getString(R.string.client_text));
            fileCheck=new Intent(getActivity(),ClientFileTransferService.class);
            Log.i("service", "start ClientFileTansferService");
            getActivity().startService(fileCheck);
        }

        // hide the connect button
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
    }

    /**
     * Updates the UI with device data
     * 
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());

    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
    }

}

