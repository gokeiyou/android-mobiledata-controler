package com.example.connectivitycontroler;



import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import android.net.wifi.WifiManager;



public  class CMServer extends Service {
	private int _onTimeCount;
	private int _offTimeCount;
	private int _timeUnit;
	private SharedPreferences _state;
	private ConnectorManager _connManager;
	private boolean _on=true;
	private static final int _notificationID=10101;
	private Handler _timerHandler=new Handler();
	private Runnable _timer = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//Toast.makeText(CMServer.this, "hello", Toast.LENGTH_LONG).show(); 
			long time=0;
			int wifistate=0;
			updateTimeCount();
			if(_on==true){
				time=60000*_onTimeCount*_timeUnit;
				switch(_connManager.wifiSate()){
				/*当wifi工作时不打开数据链路*/
				case WifiManager.WIFI_STATE_DISABLED:
				case WifiManager.WIFI_STATE_DISABLING:
				case WifiManager.WIFI_STATE_UNKNOWN:
					wifistate=0;
					break;
				default:
					wifistate=1;
				}
				if(time>0&&wifistate==0&&_connManager.mobileDataState()==0){
					_connManager.toggleMobileData(_on);
				}
				//Toast.makeText(CMServer.this, "hello"+time, Toast.LENGTH_SHORT).show();
				_on=false;
			}
			else{
				time=60000*_offTimeCount*_timeUnit;
				if(time>0&&_connManager.mobileDataState()!=0){
					_connManager.toggleMobileData(_on);
				}
				//Toast.makeText(CMServer.this, "world"+time, Toast.LENGTH_SHORT).show();
				_on=true;
			}
			_timerHandler.postDelayed(this, time>0?time:1000);
			 
		}};  
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
    public void onCreate() {
		_state=getSharedPreferences(getString(R.string.prefs_name),0);
		_connManager=new ConnectorManager(this);
    }

	@Override
    public void onDestroy() {
		//Toast.makeText(this, "My Service Stopped"+_onTimeCount, Toast.LENGTH_LONG).show(); 
		SharedPreferences.Editor editor=_state.edit();
		editor.putBoolean("service_state", false);
		editor.commit();
		_timerHandler.removeCallbacks(_timer);
		//exitDialog();
		clearNotification();
    }

	@Override
    public void onStart(Intent intent, int startid) {
		//_onTimeCount=intent.getIntExtra("ontime", 0);
		//_offTimeCount=intent.getIntExtra("offtime", 0);
		_timeUnit=_state.getInt("timeUnit", 1);
		updateTimeCount();
		SharedPreferences.Editor editor=_state.edit();
		editor.putBoolean("service_state", true);
		editor.commit();
		//Toast.makeText(this, "My Service Started "+_offTimeCount+_onTimeCount, Toast.LENGTH_LONG).show();
		_timerHandler.post(_timer);
		showNotification();
		//Toast.makeText(this, "My Service Started "+_offTimeCount+_onTimeCount, Toast.LENGTH_LONG).show(); 
    }
	
	private void updateTimeCount(){
		_onTimeCount=_state.getInt("onTime", 0);
		_offTimeCount=_state.getInt("offTime",0);
	}
	
	private void showNotification(){
		NotificationManager notificationManager=(NotificationManager)
				getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		Notification notification=new Notification(R.drawable.ic_launcher_my,
				"移动网络定时开关",System.currentTimeMillis());
		notification.flags|=Notification.FLAG_ONGOING_EVENT;
		notification.flags|=Notification.FLAG_NO_CLEAR;
		CharSequence contentTitle ="移动网络定时开关"; // 通知栏标题   
        CharSequence contentText ="点击进行跳转"; // 通知栏内容  
        Intent notificationIntent=new Intent(this,MainActivity.class);
        PendingIntent contentIntent=PendingIntent.getActivity(CMServer.this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
        notificationManager.notify(_notificationID, notification);
	}
	private void clearNotification(){
		NotificationManager notificationManager=(NotificationManager)
				getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(_notificationID);
	}
	
}
