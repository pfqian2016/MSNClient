package com.example.routedata;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DealUserRouteTable implements Runnable{
 

        public static final String TAG1 = "@@";
        public static final int DEALROUTEINFO = 0x400 + 13;
        private RouteInfoDBHelper aRouteInfo;
        private SQLiteDatabase db;
        private Cursor cursor; 
        private String userName;
        private String commNameAndNum;
        private String interest;
        private String routeInfo;
        private int owner;
        private int ownerNumber;
        private boolean hadComm = false;
        private int commNumber = 0;
        public DealUserRouteTable(Context context,String  routeInfo) { 
                this.routeInfo = routeInfo;
                this.aRouteInfo = new RouteInfoDBHelper(context, "routeInfo.db");
        }
        
        
        
        @Override
        public void run() { 
                                
                init(); 
                db = aRouteInfo.getWritableDatabase(); 
               cursor = db.rawQuery("select * from UserRouteTable where userName = ? ", new String[]{userName});
               Log.d("test count", "test getCount : " + cursor.getCount());
               
                //userName is old
                if (cursor.getCount() != 0) {
                        
                        String commName = commNameAndNum.split("#")[0];                        
                        while(cursor.moveToNext()) {
                                //已经进行过通信的情况下更新数据表,且该点已经有过通信 
                                String oldName = cursor.getString(cursor.getColumnIndex("communicateNumber")).split("#")[0]; 
                                if (commName.equals(oldName)){                                        
                                        commNumber = Integer.parseInt( (cursor.getString(cursor.getColumnIndex("communicateNumber"))).split("#")[1] );                                         
                                        commNumber = commNumber + 1;
                                        owner = cursor.getInt(cursor.getColumnIndex("OwnerNumber"));             
                                        int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
                                       
                                        db.execSQL("update UserRouteTable set communicateNumber = ?, OwnerNumber = ? where id = ?", 
                                                        new Object[]{ oldName + "#" + commNumber, owner + ownerNumber, id});  
                                        hadComm = true;
                                        break;
                                }    
                        }
                        //还没有与该节点进行过通信
                         if (hadComm == false ) { 
                                 db.execSQL("insert into UserRouteTable (id, userName, communicateNumber, OwnerNumber,  interest) values ( null, ?, ? , ?, ? )",
                                                 new Object[] {userName, commNameAndNum, owner, interest} );
                                  
                        }  
                } else { //userName is new 
                        db.execSQL("insert into UserRouteTable (id, userName, communicateNumber, OwnerNumber,  interest) values ( null, ?, ? , ?, ? )", 
                                        new Object[] {userName, commNameAndNum, ownerNumber, interest} );
                       /* Log.d("new userName", "userName :" +  userName + " ,communicateName : " + commNameAndNum + 
                                                                                                "  ,ownerNumber : " + ownerNumber + "  ,interest : " + interest);*/                      
                }    
                cursor.close();
                db.close();
        }
        
        public void init() {
                this.userName = routeInfo.split(TAG1)[0];
                this.commNameAndNum = routeInfo.split(TAG1)[1];
                this.ownerNumber = Integer.parseInt(routeInfo.split(TAG1)[2]);
                this.interest = routeInfo.split(TAG1)[3];   
        }
}
