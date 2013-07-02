package com.example.connectivitycontroler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;




public class ConnectorManager {
	private Context context;
	private WifiManager wifiManager;
	ConnectivityManager mobileDataManager;
	private Class<?> CMClass=null;// ConnectivityManager类
	private Field CMField = null; // ConnectivityManager类中的字段
	private Object ICMObject = null; // IConnectivityManager类的引用
	private Class<?> ICMClass = null; // IConnectivityManager类
	private Method setMobileDataEnabledMethod = null; // setMobileDataEnabled方法
	public ConnectorManager(Context c){
		context=c;
		mobileDataManagerInit();
		wifiManagerInit();
		
	}
	private void wifiManagerInit() {
		// TODO Auto-generated method stub
		wifiManager=(WifiManager)
				context.getSystemService(Context.WIFI_SERVICE);
	}
	private void mobileDataManagerInit() {
		// TODO Auto-generated method stub
		mobileDataManager=(ConnectivityManager)
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			// 取得ConnectivityManager类
			CMClass = Class.forName(mobileDataManager.getClass().getName());
			// 取得ConnectivityManager类中的对象mService
			CMField = CMClass.getDeclaredField("mService");
			// 设置mService可访问
			CMField.setAccessible(true);
			// 取得mService的实例化类IConnectivityManager
			ICMObject = CMField.get(mobileDataManager);
			// 取得IConnectivityManager类
			ICMClass = Class.forName(ICMObject.getClass().getName());
			// 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
			setMobileDataEnabledMethod = ICMClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			// 设置setMobileDataEnabled方法可访问
			setMobileDataEnabledMethod.setAccessible(true);
			
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		
	}
	public boolean toggleWiFi(boolean enabled) {   
	    return wifiManager.setWifiEnabled(enabled);  

	}
	public void toggleConectivity(boolean enabled){
		try{
		// 调用setMobileDataEnabled方法
		setMobileDataEnabledMethod.invoke(ICMObject, enabled);
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}catch(IllegalAccessException e){
			e.printStackTrace();
		}catch(InvocationTargetException e){
			e.printStackTrace();
		}
	}
}
/*

public boolean toggleWiFi(boolean enabled) {  
    WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);  
    return wm.setWifiEnabled(enabled);  

}
private void toggleMobileData(boolean enabled) {
	  ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

	  Class<?> conMgrClass = null; // ConnectivityManager类
	  Field iConMgrField = null; // ConnectivityManager类中的字段
	  Object iConMgr = null; // IConnectivityManager类的引用
	  Class<?> iConMgrClass = null; // IConnectivityManager类
	  Method setMobileDataEnabledMethod = null; // setMobileDataEnabled方法

	  try {
	   // 取得ConnectivityManager类
	   conMgrClass = Class.forName(conMgr.getClass().getName());
	   // 取得ConnectivityManager类中的对象mService
	   iConMgrField = conMgrClass.getDeclaredField("mService");
	   // 设置mService可访问
	   iConMgrField.setAccessible(true);
	   // 取得mService的实例化类IConnectivityManager
	   iConMgr = iConMgrField.get(conMgr);
	   // 取得IConnectivityManager类
	   iConMgrClass = Class.forName(iConMgr.getClass().getName());
	   // 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
	   setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
	   // 设置setMobileDataEnabled方法可访问
	   setMobileDataEnabledMethod.setAccessible(true);
	   // 调用setMobileDataEnabled方法
	   setMobileDataEnabledMethod.invoke(iConMgr, enabled);
	  } catch (ClassNotFoundException e) {
	   e.printStackTrace();
	  } catch (NoSuchFieldException e) {
	   e.printStackTrace();
	  } catch (SecurityException e) {
	   e.printStackTrace();
	  } catch (NoSuchMethodException e) {
	   e.printStackTrace();
	  } catch (IllegalArgumentException e) {
	   e.printStackTrace();
	  } catch (IllegalAccessException e) {
	   e.printStackTrace();
	  } catch (InvocationTargetException e) {
	   e.printStackTrace();
	  }
	 }
private void toggleGPS() {
	  Intent gpsIntent = new Intent();
	  gpsIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
	  gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
	  gpsIntent.setData(Uri.parse("custom:3"));
	  try {
	   PendingIntent.getBroadcast(this, 0, gpsIntent, 0).send();
	  } catch (CanceledException e) {
	   e.printStackTrace();
	  }
	 }*/