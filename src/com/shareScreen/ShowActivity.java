package com.shareScreen;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.android.mobilesocietynetwork.client.R;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class ShowActivity extends Activity{
	CODE code=new CODE();
	ImageView img;
	boolean isstop=true;
	
	int w,h;
	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			img.setImageBitmap(BitmapFactory.decodeFile("/sdcard/hulei.bmp"));
			Log.i("client", "show");
		}

	};
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show);
		final Button server=(Button)findViewById(R.id.server);
		img=(ImageView)findViewById(R.id.show);

		server.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Display display = getWindowManager().getDefaultDisplay(); 
				w=display.getWidth();
				h=display.getHeight();
				if(isstop){
					server.setText("ֹͣ");	
					isstop=false;
					//getpic.getPicFromFrameBuffer(width, height);
					//img.setImageBitmap(BitmapFactory.decodeFile("/sdcard/b.bmp"));
					new ClientThread().start();
				}else{
					
					server.setText("��ʾ");	
					isstop=true;
				}
			}
		});
	}
	class ClientThread extends Thread{
		boolean a=true;
    	Socket socket;	
    	DataOutputStream s_out;
    	DataInputStream s_in;
    	File file=new File("/sdcard/mypic.h264");
    	String s;
    	private Handler mHandler;  
    	private final Object mSync = new Object(); 
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			
			//getpic.getPicFromFrameBuffer(width, height);
			socket=new Socket();
			
			try {
				socket.connect(new InetSocketAddress("192.168.49.1", 8123), 8000);
				Log.i("client", "connect");
				s_out=new DataOutputStream(socket.getOutputStream());
				s_in=new DataInputStream(socket.getInputStream());
				s_out.writeUTF("id hulei");  //дid
				s_out.flush();
				int len,m=0;
				byte buf[] = new byte[1024];
				try{
				while(!isstop)
				{
					
					s_out.writeUTF("msg continue");
					DataOutputStream out=new DataOutputStream(new FileOutputStream("/sdcard/mypic.h264"));
					Log.i("11", "abc");
					//copyFile(in, out);
					/*int w=s_in.readInt();
					int h=s_in.readInt();*/
					Log.i("width height", "w="+w+",h="+h);
					int sum=s_in.readInt();
					//width=in.readInt();
					//height=in.readInt();
					Log.i("11", "sum="+sum);
			        try {
			            while ((len = s_in.read(buf)) != -1) {
			            	Log.i("11", "length="+len);
			            	m+=len;
			            	Log.i("11", "m="+m);
			            	if(m==sum){
			            		m=0;
			            		break;
			            	}
			                out.write(buf, 0, len);
			            }
			  
			            //out.close();
			           // inputStream.close();
			        } catch (IOException e) {
			        	  Log.i("11", "exception");
			        }
					out.close();
					Log.i("11", "jni");
					code.decode(w, h);
					
					handler.sendEmptyMessage(1);
					
					Log.i("11", "END");
				}
				s_out.writeUTF("hulei stop");
				s_in.close();
				s_out.close();
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			super.run();
	}
	}
	

}


