package com.example.SCFData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class TXTLog extends Service{
        
        final String  FILE_NAME  =  "/example1.bin";  
        // 定义Onbinder方法返回的对象
        private MyBinder binder = new MyBinder();
        // ͨ通过继承Binder来实现IBinder类 
        
        public class MyBinder extends Binder { 
                
                public  void  write(String content) {
                        try {
                                
                                if  (Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED) ) {
                                        File  sdCardDir  =  Environment.getExternalStorageDirectory () ;
                                        File  targetFile   =  new File( sdCardDir.getCanonicalPath () + FILE_NAME );
                                        RandomAccessFile raf  =  new  RandomAccessFile(targetFile,  "rw" );
                                        raf.seek( targetFile.length() );
                                        raf.write( content.getBytes() );                                   
                                        //Log.d("write test", ""+ targetFile.length());
                                        raf.close();
                                }
                        } catch (Exception e) { 
                                e.printStackTrace();
                                Log.d("test", "write failed" );
                        }
                }
                
                public String  read ()  {
                        
                        try {
                                
                                if (Environment.getExternalStorageState().equals(
                                                Environment.MEDIA_MOUNTED) ) {
                                        File  sdCardDir  =  Environment.getExternalStorageDirectory ();
                                        FileInputStream fis  =  new  FileInputStream(sdCardDir.getCanonicalPath() + FILE_NAME);
                                        BufferedReader  br  =  new  BufferedReader(new InputStreamReader(fis) );
                                        StringBuilder sb  =  new StringBuilder ("");
                                        String line = null;
                                        while ( ( line  = br.readLine() ) !=  null ) {
                                                sb.append(line);                                
                                        }
                                        br.close();
                                        return  sb.toString();
                                }
                        } catch (Exception e) { 
                                e.printStackTrace ();
                        }
                        return null;            
                } 
        }
        // 必须实现的方法，绑定改service时回调该方法
        @Override
        public IBinder onBind(Intent intent) {
                System.out.println("Service is Binded");
                Log.d("binder service", "Service is Binded"); 
                return binder;
        }
        // Service被断开链接时回电该方法
        @Override
        public boolean onUnbind(Intent intent) {
                System.out.println("Service is Unbinded");
                return true;
        }
        // Service被关闭之前回调该方法
        @Override
        public void onDestroy() {
                super.onDestroy(); 
                System.out.println("Service is Destroyed");
        }
}
