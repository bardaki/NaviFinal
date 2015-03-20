package com.example.finalproject.services;

import java.util.ArrayList;
import java.util.List;

import com.example.finalproject.classes.Navigation;
import com.example.finalproject.classes.Route;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


public class LocationService extends Service 
{
	public static final String BROADCAST_ACTION = "Hello World";
	public LocationManager locationManager;
	public MyLocationListener listener;
	public Location previousBestLocation = null;
	@SuppressWarnings("unused")
	private List<Route> routes = new ArrayList<Route>();
	@SuppressWarnings("unused")
	private Navigation nav = new Navigation();
	private Handler handler = new Handler();
	private int endLatitude;
	private int endLongitude;

	Intent intent;
	int counter = 0;

	@Override
	public void onCreate() 
	{
		super.onCreate();
		intent = new Intent(BROADCAST_ACTION);           
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onStart(Intent intent, int startId) 
	{      
		endLatitude = (int) intent.getIntExtra("endLatitude", 0);
		endLongitude = (int) intent.getIntExtra("endLongitude", 0);
		routes = (List<Route>) intent.getSerializableExtra("routes");	 
		nav = (Navigation) intent.getSerializableExtra("navObj");
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		listener = new MyLocationListener();        
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, (LocationListener) listener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, listener);
	}

	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}

	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		double distance = distance(location.getLatitude(), location.getLongitude(), (double)endLatitude / 1E6, (double)endLongitude / 1E6, 'K');
		if(distance < 0.3)
			return true;
		else
			return false;
	}

	private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == 'K') {
			dist = dist * 1.609344;
		} else if (unit == 'N') {
			dist = dist * 0.8684;
		}
		return dist;
	}

	private double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}

	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	@Override
	public void onDestroy() {    
		locationManager.removeUpdates(listener);
		handler.removeCallbacksAndMessages(null);   
		super.onDestroy();
		Log.v("STOP_SERVICE", "DONE");
		locationManager.removeUpdates(listener);  
		listener = null;
		locationManager = null;
		//android.os.Process.killProcess(android.os.Process.myPid());
	}   

	public static Thread performOnBackgroundThread(final Runnable runnable) {
		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					runnable.run();
				} finally {

				}
			}
		};
		t.start();
		return t;
	}

	public void stop(){
		this.stopSelf();
	}


	public class MyLocationListener implements LocationListener
	{

		public void onLocationChanged(final Location loc)
		{
			Log.i("**************************************", "Location changed");
			if(isBetterLocation(loc, previousBestLocation)) 
				stop();                             
		}

		public void onProviderDisabled(String provider)
		{
			Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
		}

		public void onProviderEnabled(String provider)
		{
			Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
		}

		public void onStatusChanged(String provider, int status, Bundle extras)
		{

		}
	}
}