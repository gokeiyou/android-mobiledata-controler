package com.example.connectivitycontroler;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;




public  class CMServer extends Service {
	private int _onTimeCount;
	private int _offTimeCount;
	private int _timeUnit;
	private SharedPreferences _state;
	private ConnectorManager _connManager;
	private boolean _on=true;
	private Handler _timerHandler=new Handler();
	private Runnable _timer = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//Toast.makeText(CMServer.this, "hello", Toast.LENGTH_LONG).show(); 
			long time=0;
			if(_on==true){
				_connManager.toggleConectivity(_on);
				//Toast.makeText(CMServer.this, "hello"+5000, Toast.LENGTH_SHORT).show();
				time=60000*_onTimeCount*_timeUnit;
				_on=false;
			}
			else{
				_connManager.toggleConectivity(_on);
				//Toast.makeText(CMServer.this, "world"+6000, Toast.LENGTH_SHORT).show();
				time=60000*_offTimeCount*_timeUnit;;
				_on=true;
			}
			_timerHandler.postDelayed(this, time);
			 
		}};  
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
    public void onCreate() {
		_state=getSharedPreferences(getString(R.string.prefs_name),0);
    }

	@Override
    public void onDestroy() {
		//Toast.makeText(this, "My Service Stopped"+_onTimeCount, Toast.LENGTH_LONG).show(); 
		SharedPreferences.Editor editor=_state.edit();
		editor.putBoolean("service_state", false);
		editor.commit();
		_timerHandler.removeCallbacks(_timer);
    }

	@Override
    public void onStart(Intent intent, int startid) {
		//_onTimeCount=intent.getIntExtra("ontime", 0);
		//_offTimeCount=intent.getIntExtra("offtime", 0);
		_onTimeCount=_state.getInt("onTime", 0);
		_offTimeCount=_state.getInt("offTime",0);
		SharedPreferences.Editor editor=_state.edit();
		editor.putBoolean("service_state", true);
		editor.commit();
		
		_timerHandler.post(_timer);
		//Toast.makeText(this, "My Service Started "+_offTimeCount+_onTimeCount, Toast.LENGTH_LONG).show(); 
    }
	


}
