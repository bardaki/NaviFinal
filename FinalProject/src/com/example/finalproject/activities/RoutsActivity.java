package com.example.finalproject.activities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.finalproject.R;
import com.example.finalproject.bl.DirectionsFetcher;
import com.example.finalproject.classes.Address;
import com.example.finalproject.classes.Navigation;
import com.example.finalproject.classes.Route;
import com.example.finalproject.custom.MyApplication;
import com.example.finalproject.services.LocationService;
import com.example.finalproject.utils.SqliteController;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RoutsActivity extends ActionBarActivity  {
	private SqliteController sqlController = new SqliteController(this);
	private List<Address> placesArray = new ArrayList<Address>();
	private List<Route> routes = new ArrayList<Route>();
	private Navigation nav = new Navigation();
	private boolean resumeHasRun = false;

	Intent intService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_routs);
		intService = new Intent(this, LocationService.class);
		stopService(intService);
		// get global variable
		routes = ((MyApplication) this.getApplication()).getRoutes();
		String[] values = new String[routes.size()];
		nav = ((MyApplication) this.getApplication()).getNavigation();
		for(int i = 0; i< routes.size(); i++){
			values[i] = (routes.get(i).toString());
		}
		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < values.length; ++i) {
			list.add(values[i]);
		}
		CustomAdapter adapter = new CustomAdapter(this, android.R.layout.simple_list_item_1, list);
		//Routes ListView
		ListView lv = (ListView) findViewById(R.id.list);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				intService.putExtra("endLatitude", routes.get(position).getLatitude());
				intService.putExtra("endLongitude", routes.get(position).getLongitude());
				intService.putExtra("routes", (Serializable)routes);  
				intService.putExtra("navObj", (Serializable)nav ); 
				startService(intService);
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + routes.get(position).getDestination()));
				startActivity(intent); 

			}
		});

		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ABFF")));	
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		getSupportActionBar().setCustomView(R.layout.abs_routes);
		Button backBtn = (Button) findViewById(R.id.action_back_to_plane);
		//Back button
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RoutsActivity.this.onBackPressed();				
			}
		});
		//Add to favorites button
		Button addBtn = (Button) findViewById(R.id.action_add);
		addBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addToFavorites();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
		case R.id.action_back_to_plane:
			super.onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		intService.putExtra("endLatitude", routes.get(position).getLatitude());
		intService.putExtra("endLongitude", routes.get(position).getLongitude());
		intService.putExtra("routes", (Serializable)routes);  
		intService.putExtra("navObj", (Serializable)nav ); 
		startService(intService);
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + routes.get(position).getDestination()));
		startActivity(intent);       
	}

	public void backClicked(View v){	
		Intent i = new Intent(RoutsActivity.this, MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.putExtra("navObj", (Serializable)nav ); 
		startActivity(i);
	}

	@Override
	public void onResume(){
		super.onResume();
		stopService(intService);

		if(!resumeHasRun){
			resumeHasRun = true;		
		}
		else{
			boolean calculateAgain = removeStartAddress();
			if(calculateAgain){
				DirectionsFetcher df = new DirectionsFetcher(this);
				df.execute();
			}
		}
	}

	public boolean removeStartAddress(){
		nav = ((MyApplication) this.getApplication()).getNavigation();	
		placesArray = ((MyApplication) this.getApplication()).getPlaces();
		if(nav.getAddresses().contains(routes.get(0).getDestination())){
			nav.addStartAdd(routes.get(0).getDestination());
			nav.getAddresses().remove(routes.get(0).getDestination());
			int loc = getAddressLocation(routes.get(0).getDestination());
			placesArray.remove(loc);
			((MyApplication) this.getApplication()).setPlaces(placesArray);
			return true;
		}
		else{
			showEndNavigationAlert();
			return false;
		}
	}

	public int getAddressLocation(String address){
		int i = 0;
		for(i = 0; i < placesArray.size(); i++){
			if(placesArray.get(i).getName().equals(address))
				break;
		}
		return i;
	}

	public void addToFavorites(){
		nav.setId(java.util.UUID.randomUUID().toString());
		HashMap<String, String> favorite_startEnd = new HashMap<String, String>();
		HashMap<String, String> favorite_addresses = new HashMap<String, String>();
		favorite_startEnd.put("Id", null); 
		favorite_startEnd.put("MainId", nav.getId()); 
		favorite_startEnd.put("Start", nav.getStartAdd());
		favorite_startEnd.put("End", nav.getEndAdd());
		for(int i = 0; i < nav.getAddresses().size(); i++){
			favorite_addresses.put("Id" + i, null);
			favorite_addresses.put("MainId" + i, nav.getId());
			favorite_addresses.put("Place" + i, nav.getAddresses().get(i));
		}
		sqlController.insertFavorite(favorite_startEnd, favorite_addresses);
		Toast.makeText(getApplicationContext(), "מסלול נוסף למועדפים", 
				Toast.LENGTH_SHORT).show();
	}

	public void showEndNavigationAlert(){
		new AlertDialog.Builder(this)
		.setTitle(R.string.endNavigation)
		.setMessage(R.string.finish)
		.setNegativeButton(R.string.home, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
				Intent i = new Intent(RoutsActivity.this, MainActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 	
				startActivity(i);
			}
		})
		.setIcon(android.R.drawable.ic_dialog_alert)
		.show();
	}

	public class CustomAdapter extends ArrayAdapter<String> {

		public CustomAdapter(Context context, int resID, ArrayList<String> items) {
			super(context, resID, items);                       
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = super.getView(position, convertView, parent);
			((TextView) v).setTextColor(Color.BLACK); 
			((TextView) v).setTextSize(18);
			return v;
		}
	}

	public void routesClicked(View v){
		Intent i = new Intent(RoutsActivity.this, AddressesActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
	}

	public void favoritesClicked(View v){
		Intent i = new Intent(RoutsActivity.this, FavoritesActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
	}

	public void mapClicked(View v){
		Intent i = new Intent(RoutsActivity.this, MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
	}
	
	public void helpClicked(View v){
		Intent i = new Intent(RoutsActivity.this, HelpActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
	}
}
