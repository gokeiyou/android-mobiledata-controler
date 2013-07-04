package com.example.connectivitycontroler;

import java.util.List;



import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
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
	private Switch trigger;
	private SharedPreferences _state;
	private boolean _isServiceRunning;
	private ConnectorManager _connManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		_state=getSharedPreferences(getString(R.string.prefs_name),0);
		_isServiceRunning=_state.getBoolean("service_state", false);
		
		
		onTimeSeekBar=(SeekBar)findViewById(R.id.onTimeseekBar);
		offTimeSeekBar=(SeekBar)findViewById(R.id.offTimeseekBar);
		onTimeTextView=(TextView)findViewById(R.id.onTimeTextView);
		offTimeTextView=(TextView)findViewById(R.id.offTimeTextView);
		trigger=(Switch)findViewById(R.id.switch1);
		onTimeSeekBar.setMax((int) _maxOnTime);
		onTimeSeekBar.setOnSeekBarChangeListener(new onTimeSeekBarChangeListener());
		offTimeSeekBar.setMax(_maxOffTime);
		offTimeSeekBar.setOnSeekBarChangeListener(new offTimeSeekBarChangeListener());
		trigger.setOnCheckedChangeListener(new switchListener());
		_onTimeCount=_state.getInt("onTime", 0);
		onTimeSeekBar.setProgress(_onTimeCount);
		_offTimeCount=_state.getInt("offTime", 0);	
		offTimeSeekBar.setProgress(_offTimeCount);
		setTimeCountTitle(_onTimeCount,onTimeTextView);
		setTimeCountTitle(_offTimeCount,offTimeTextView);
		_connManager=new ConnectorManager(this);
		if(isServiceRunning("com.example.connectivitycontroler.CMServer")){
			//Toast.makeText(this, "running", Toast.LENGTH_LONG).show();
			_isServiceRunning=true;
			trigger.setChecked(true);
		}
		else{
			_isServiceRunning=false;
		}
		//Log.d("activity", "oncreare");
		
	}

	private class onTimeSeekBarChangeListener implements OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			_onTimeCount=progress;
			SharedPreferences.Editor editor=_state.edit();
			editor.putInt("onTime", _onTimeCount);
			editor.commit();
			setTimeCountTitle(progress,onTimeTextView);
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
			SharedPreferences.Editor editor=_state.edit();
			editor.putInt("offTime", _offTimeCount);
			editor.commit();
			setTimeCountTitle(progress,offTimeTextView);
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
				//onTimeSeekBar.setEnabled(false);
				//offTimeSeekBar.setEnabled(false);
				//Log.d("activity", "switchon");
				SharedPreferences.Editor editor=_state.edit();
				editor.putInt("onTime", _onTimeCount);
				editor.putInt("offTime",_offTimeCount);
				editor.putInt("timeUnit", _timeUnit);
				editor.commit();
				if(_isServiceRunning!=true){
					//Log.d("activity", "new service");
					Intent i=new Intent();
					i.setClass(MainActivity.this, CMServer.class);
					//i.putExtra("ontime", _onTimeCount);
					//i.putExtra("offtime", _offTimeCount);
					startService(i);  
				}
			}
			else{
				//onTimeSeekBar.setEnabled(true);
				//offTimeSeekBar.setEnabled(true);
				//Log.d("activity", "switchoff");
				Intent i=new Intent();
				i.setClass(MainActivity.this, CMServer.class);
				stopService(i);
				exitDialog();
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
	
	private void setTimeCountTitle(int count,TextView tv){
		int sum=_timeUnit*count;
		int hours=sum/60;
		int mins=sum%60;
		String s=""+hours+"小时 "+mins+"分";
		tv.setText(s);
	}
	private void serviceStateInit(){
		_state=getSharedPreferences(getString(R.string.prefs_name),0);
		_isServiceRunning=_state.getBoolean("service_state", false);
		
		if(isServiceRunning("com.example.connectivitycontroler.CMServer")){
			//Toast.makeText(this, "running", Toast.LENGTH_LONG).show();
			_isServiceRunning=true;
			trigger.setChecked(true);
		}
		else{
			_isServiceRunning=false;
		}
	}
	
	private void UIInit() {
		// TODO Auto-generated method stub
		onTimeSeekBar=(SeekBar)findViewById(R.id.onTimeseekBar);
		offTimeSeekBar=(SeekBar)findViewById(R.id.offTimeseekBar);
		onTimeTextView=(TextView)findViewById(R.id.onTimeTextView);
		offTimeTextView=(TextView)findViewById(R.id.offTimeTextView);
		trigger=(Switch)findViewById(R.id.switch1);
		onTimeSeekBar.setMax((int) _maxOnTime);
		onTimeSeekBar.setOnSeekBarChangeListener(new onTimeSeekBarChangeListener());
		offTimeSeekBar.setMax(_maxOffTime);
		offTimeSeekBar.setOnSeekBarChangeListener(new offTimeSeekBarChangeListener());
		trigger.setOnCheckedChangeListener(new switchListener());
	}
	private void exitDialog(){
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(getString(R.string.mobiledata_keepornot));
		builder.setTitle(getString(R.string.exit));
		builder.setPositiveButton("是", new exitDialogPosButtonListener());
		builder.setNegativeButton("否", new exitDialogNegButtonListener());
		builder.create().show();
	}
	private class exitDialogPosButtonListener implements DialogInterface.OnClickListener{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			_connManager.toggleMobileData(true);
		}
		
	}
	private class exitDialogNegButtonListener implements DialogInterface.OnClickListener{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			_connManager.toggleMobileData(false);
		}
		
	}
}
