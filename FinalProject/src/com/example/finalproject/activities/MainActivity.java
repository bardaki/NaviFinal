package com.example.finalproject.activities;

import com.example.finalproject.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends ActionBarActivity implements OnItemClickListener{
	private GoogleMap googleMap;
	Dialog dialogAddresses;
	Dialog dialogEndAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {
			// Loading map
			initilizeMap();
			googleMap.setMyLocationEnabled(true);
			Location myLocation = getMyLocation();
			LatLng currentCoordinates = new LatLng(
					myLocation.getLatitude(),
					myLocation.getLongitude());
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentCoordinates, 15));

		} catch (Exception e) {
			e.printStackTrace();
		}

		//ActionBar bar = getSupportActionBar();
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ABFF")));
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		getSupportActionBar().setCustomView(R.layout.abs_main);
	}

	private Location getMyLocation() {
		// Get location from GPS if it's available
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		// Location wasn't found, check the next most accurate place for the current location
		if (myLocation == null) {
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
			// Finds a provider that matches the criteria
			String provider = lm.getBestProvider(criteria, true);
			// Use the provider to get the last known location
			myLocation = lm.getLastKnownLocation(provider);
		}

		return myLocation;
	}

	@SuppressLint("NewApi") 
	private void initilizeMap() {
		if (googleMap == null) {
			SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview);

			googleMap = mapFrag.getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar, menu);

		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void routesClicked(View v){
		Intent i = new Intent(MainActivity.this, AddressesActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
	}

	public void favoritesClicked(View v){
		Intent i = new Intent(MainActivity.this, FavoritesActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
	}
}
