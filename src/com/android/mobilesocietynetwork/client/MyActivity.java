package com.android.mobilesocietynetwork.client;



import com.android.mobilesocietynetwork.client.tool.XmppTool;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * 自定义一个抽象的MyActivity类，每个Activity都继承他
 * @author way
 * 
 */
public abstract class MyActivity extends Activity 
{
	private long exitTime = 0;
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            appExit();
            return false;
        }
        else
        return super.onKeyDown(keyCode, event);
    }
 
    private void appExit() {

        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
        	ActivityManager.getInstance().exit();
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "退出").setIcon(android.R.drawable.ic_menu_delete);
		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "帮助").setIcon( android.R.drawable.ic_menu_help);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
	        case Menu.FIRST + 1:
	        {
	        	Toast.makeText(getApplicationContext(), "删除菜单被点击了", Toast.LENGTH_LONG).show();
	        	XmppTool.closeConnection();
	    		ActivityManager.getInstance().exit(); 
	        }
	        break;
	        case Menu.FIRST + 2:
	        {
	            Toast.makeText(this, "帮助菜单被点击了", Toast.LENGTH_LONG).show();
	        }
	        break;
		}
		return false;
	}
	
	
}
