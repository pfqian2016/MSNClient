package com.example.routedata;

import android.os.Handler;

public class TimeThread extends Thread {
         
        private Handler handler; 
        public TimeThread( Handler handler){ 
                this.handler = handler;
        }
        @Override
        public void run() {  
            try {
                TimeThread.sleep(3000);  
                handler.obtainMessage(1, 12, 123).sendToTarget();  
            } catch (InterruptedException e) { 
                e.printStackTrace(); 
            } 
        }
        
        
}
