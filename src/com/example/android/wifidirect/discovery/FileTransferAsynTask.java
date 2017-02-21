package com.example.android.wifidirect.discovery;

import java.io.InputStream;
import java.io.OutputStream;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class FileTransferAsynTask extends AsyncTask<Void, Void, Boolean> {
        
        private InputStream inputStream;
        private OutputStream outputStream;
        public FileTransferAsynTask(InputStream inputStream, OutputStream outputStream) {
                this.inputStream = inputStream;
                this.outputStream = outputStream;
        }

        @Override
        protected Boolean doInBackground(Void... params) { 
                Log.d("test doInBackground ", "start transfile doInBackground");
                byte buf[] = new byte[1024*4];
                int len = 0;
                Log.d("test", "start transfile doInBackground");
                try {
                        Log.d("doInBackground", "start transfile doInBackground");
                        while ((len = inputStream.read(buf)) != -1) {
                                outputStream.write(buf, 0, len);                        
                        } 
                        Log.d("@@@@doInBackground", "file transfer finish");
                        //Toast.makeText(FileTransferAsynTask, "File Transfer Over", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                        Log.d("@@@@@@@@@@", "test transfile error");
                }
                return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if(result){
                	//Toast.makeText(context, "Download succeeded", Toast.LENGTH_SHORT).show();
                }else{
                	//Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show();
                }
                Log.d("$$$$doInBackground", "test FileTransferAsynTask finish");
                
        } 
        
        
}
