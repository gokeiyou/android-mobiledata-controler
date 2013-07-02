package com.example.connectivitycontroler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private final int _maxOnTime=60;
	private final int _maxOffTime=60;
	private final int _timeUnit=1;
	private int _onTimeCount;
	private int _offTimeCount;
	private SeekBar onTimeSeekBar;
	private SeekBar offTimeSeekBar;
	private TextView onTimeTextView;
	private TextView offTimeTextView;
	private ConnectorManager conManager;
	private Switch trigger;
	private SharedPreferences _state;
	private boolean _isServiceRunning;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		_state=getSharedPreferences(getString(R.string.prefs_name),0);
		_isServiceRunning=_state.getBoolean("service_state", false);
		_onTimeCount=_state.getInt("onTime", 0);
		_offTimeCount=_state.getInt("offTime", 0);
		onTimeSeekBar=(SeekBar)findViewById(R.id.onTimeseekBar);
		offTimeSeekBar=(SeekBar)findViewById(R.id.offTimeseekBar);
		onTimeTextView=(TextView)findViewById(R.id.onTimeTextView);
		offTimeTextView=(TextView)findViewById(R.id.offTimeTextView);
		trigger=(Switch)findViewById(R.id.switch1);
		onTimeSeekBar.setMax((int) _maxOnTime);
		onTimeSeekBar.setProgress(_onTimeCount);
		onTimeSeekBar.setOnSeekBarChangeListener(new onTimeSeekBarChangeListener());
		offTimeSeekBar.setMax(_maxOffTime);
		offTimeSeekBar.setProgress(_offTimeCount);
		offTimeSeekBar.setOnSeekBarChangeListener(new offTimeSeekBarChangeListener());
		trigger.setOnCheckedChangeListener(new switchListener());
		setTimeCount(_onTimeCount,onTimeTextView);
		setTimeCount(_offTimeCount,offTimeTextView);
		//onTimeTextView.setText("00小时 00分");
		//offTimeTextView.setText("00小时 00分");
		conManager=new ConnectorManager(this);
		if(_isServiceRunning){
			trigger.setChecked(true);
		}
		/*
		if(isServiceRunning("com.example.connectivitycontroler.CMServer")){
			Toast.makeText(this, "running", Toast.LENGTH_LONG).show();
			trigger.setChecked(true);
		}
		*/
	}
	private class onTimeSeekBarChangeListener implements OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			_onTimeCount=progress;
			setTimeCount(progress,onTimeTextView);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class offTimeSeekBarChangeListener implements OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			_offTimeCount=progress;
			setTimeCount(progress,offTimeTextView);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
	}
	private class switchListener implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1){
				onTimeSeekBar.setEnabled(false);
				offTimeSeekBar.setEnabled(false);
				SharedPreferences.Editor editor=_state.edit();
				editor.putInt("onTime", _onTimeCount);
				editor.putInt("offTime",_offTimeCount);
				editor.putInt("timeUnit", _timeUnit);
				editor.commit();
				if(_isServiceRunning!=true){
					Intent i=new Intent();
					i.setClass(MainActivity.this, CMServer.class);
					//i.putExtra("ontime", _onTimeCount);
					//i.putExtra("offtime", _offTimeCount);
					startService(i);  
				}
			}
			else{
				onTimeSeekBar.setEnabled(true);
				offTimeSeekBar.setEnabled(true);
				Intent i=new Intent();
				i.setClass(MainActivity.this, CMServer.class);
				stopService(i);
			}
		}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean isServiceRunning(String name){
		ActivityManager am=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> sl=am.getRunningServices(50);
		for(int i=0;i<sl.size();++i){
			Log.d("service", sl.get(i).service.getClassName());
			if(sl.get(i).service.getClassName().equals(name)){
				return true;
			}
		}
		return false;
		
	}
	
	private void setTimeCount(int count,TextView tv){
		int sum=_timeUnit*count;
		int hours=sum/60;
		int mins=sum%60;
		String s=""+hours+"小时 "+mins+"分";
		tv.setText(s);
	}
	
}
