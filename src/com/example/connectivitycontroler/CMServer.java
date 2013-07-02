package com.example.connectivitycontroler;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;




public  class CMServer extends Service {
	private int _onTimeCount;
	private int _offTimeCount;
	private SharedPreferences _state;
	
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
		Toast.makeText(this, "My Service Started "+_offTimeCount+_onTimeCount, Toast.LENGTH_LONG).show(); 
    }
	


}
