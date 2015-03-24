package com.example.finalproject.bl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;

import com.example.finalproject.activities.AddressesActivity;
import com.example.finalproject.activities.RoutsActivity;
import com.example.finalproject.classes.Navigation;
import com.example.finalproject.classes.Route;
import com.example.finalproject.custom.MyApplication;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

@SuppressLint("RtlHardcoded")
public class DirectionsFetcher extends AsyncTask<URL, Integer, List<LatLng> > implements LocationListener {
	private final String SERVER_URL = "http://naviserver.azurewebsites.net/api/Navigation/Complex";
	private Navigation nav = new Navigation();
	private Context context;
	private final ProgressDialog ringProgressDialog;
	private List<Route> optimizeRoute;
	private boolean doInBg;

	public DirectionsFetcher(Context context) {
		this.context = context;
		this.ringProgressDialog = ProgressDialog.show(context, "אנא המתן...", "מחשב מסלול אופטימלי...", true);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		this.doInBg = true;
		this.nav = ((MyApplication) ((Activity) context).getApplication()).getNavigation();
		ringProgressDialog.getWindow().setGravity(Gravity.RIGHT);
		ringProgressDialog.setCancelable(false);
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);
		Location location = locationManager.getLastKnownLocation(provider);		
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		List<Address> addresses;
		//Add start & end addresses if empty
		try {
			if(nav.getStartAdd().equals("")){
				addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
				nav.addStartAdd(addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getAddressLine(1) + "," + addresses.get(0).getAddressLine(2));
			}
			if(nav.getEndAdd().equals("")){
				addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
				nav.addEndAdd(addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getAddressLine(1) + "," + addresses.get(0).getAddressLine(2));	
			}
		} catch (IOException e) {
			doInBg = false;			
		}
	}

	@SuppressLint("NewApi") 
	protected List<LatLng> doInBackground(URL... urls) {
		if(doInBg){
			Gson gson = new Gson();	 
			String json = gson.toJson(nav);
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(SERVER_URL);
			StringEntity se;
			//Request best route from server
			try {
				se = new StringEntity(json, "UTF-8");
				httpPost.setEntity(se);
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-type", "application/json");
				httpPost.setHeader(
						"Authorization",
						"Bearer TokenRemovedBecauseUseless");
				org.apache.http.HttpResponse httpResponse = httpclient.execute(httpPost);
				if(httpResponse.getStatusLine().getStatusCode() != 200)
					doInBg = false;	
				else{
					String jsonn = EntityUtils.toString(httpResponse.getEntity());
					TypeToken<List<Route>> token = new TypeToken<List<Route>>(){};
					optimizeRoute = gson.fromJson(jsonn, token.getType());
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}



	protected void onPostExecute(List<LatLng> result) {   
		ringProgressDialog.dismiss();
		((MyApplication) ((Activity) context).getApplication()).setDoInBg(doInBg);
		if(doInBg){		
			//set global variable
			((MyApplication) ((Activity) context).getApplication()).setRoutes(optimizeRoute);
			Intent i = new Intent(context, RoutsActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);	
			context.startActivity(i);
		}
		else{
			ringProgressDialog.dismiss();
			Intent i = new Intent(context, AddressesActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);	
			context.startActivity(i);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}
}
