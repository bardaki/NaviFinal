package com.example.finalproject.activities;

import java.util.ArrayList;
import java.util.List;

import com.example.finalproject.R;
import com.example.finalproject.bl.DirectionsFetcher;
import com.example.finalproject.classes.Address;
import com.example.finalproject.classes.Navigation;
import com.example.finalproject.custom.AddressAdapter;
import com.example.finalproject.custom.MyApplication;
import com.example.finalproject.custom.PlacesAutoCompleteAdapter;
import com.example.finalproject.services.LocationService;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("RtlHardcoded")
public class AddressesActivity extends ActionBarActivity implements OnItemClickListener  {
	private List<Address> placesArray = new ArrayList<Address>();
	private static AddressAdapter adapter;
	private Navigation nav;
	private boolean doInBg;
	
	Intent intService;

	@SuppressLint("ClickableViewAccessibility") @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addresses);
		doInBg = ((MyApplication) this.getApplication()).getDoInBg();
		if(!doInBg){
			showErrorAlert();
		}
		try{
			intService = new Intent(this, LocationService.class);
			stopService(intService);
		} catch(Exception ex){}		
		//get global variable
		nav = ((MyApplication) this.getApplication()).getNavigation();
		//Start Address
		final AutoCompleteTextView autoCompViewStart = (AutoCompleteTextView) findViewById(R.id.autocompleteStart);
		autoCompViewStart.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
		autoCompViewStart.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				String str = (String) adapterView.getItemAtPosition(position);
				nav.addStartAdd(str);				
			}
		});
		autoCompViewStart.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility") @Override
			public boolean onTouch(View v, MotionEvent event) {
				autoCompViewStart.setHint("");
				return false;
			}
		});
		autoCompViewStart.setText(nav.getStartAdd());
		//Addresses
		final AutoCompleteTextView autoCompViewAddresses = (AutoCompleteTextView) findViewById(R.id.autocompleteAddresses);
		autoCompViewAddresses.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
		autoCompViewAddresses.setOnItemClickListener(this);
		autoCompViewAddresses.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				autoCompViewAddresses.setHint("");
				return false;
			}
		});
		//End Address
		final AutoCompleteTextView autoCompViewEnd = (AutoCompleteTextView) findViewById(R.id.autocompleteEnd);
		autoCompViewEnd.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
		autoCompViewEnd.setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("ClickableViewAccessibility") @Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				String str = (String) adapterView.getItemAtPosition(position);
				nav.addEndAdd(str);				
			}
		});
		autoCompViewEnd.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				autoCompViewEnd.setHint("");
				return false;
			}
		});
		autoCompViewEnd.setText(nav.getEndAdd());
		//List of Addresses
		for(int j = 0; j < nav.getAddresses().size(); j++){
			placesArray.add(new Address(nav.getAddresses().get(j) , R.drawable.edit));
		}

		adapter = new AddressAdapter(this, placesArray);	
		ListView places = (ListView) findViewById(R.id.addressesListView);
		places.setAdapter(adapter);	
		//Edit dialog for address
		places.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				final int pos = position;
				Address address = (Address) adapterView.getItemAtPosition(position);
				final Dialog dialogEditAddress = new Dialog(AddressesActivity.this);			
				dialogEditAddress.setContentView(R.layout.activity_address_edit);
				dialogEditAddress.setTitle(address.getName());
				TextView titleTextView = (TextView) dialogEditAddress.findViewById(android.R.id.title);
				titleTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
				Button removeBtn = (Button) dialogEditAddress.findViewById(R.id.buttonRemove);
				removeBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						placesArray.remove(pos);
						nav.removeFromAddresses(pos);
						adapter = new AddressAdapter(AddressesActivity.this, placesArray);	
						ListView places = (ListView) findViewById(R.id.addressesListView);
						places.setAdapter(adapter);
						dialogEditAddress.dismiss();
					}
				}); 
				Button saveBtn = (Button) dialogEditAddress.findViewById(R.id.buttonSave);
				saveBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialogEditAddress.dismiss();
					}
				}); 
				dialogEditAddress.show();				
			}
		});

		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ABFF")));	
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		getSupportActionBar().setCustomView(R.layout.abs_plane_route);
		Button startBtn = (Button) findViewById(R.id.action_startnav);
		//Start navigation button
		startBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startNavClicked();				
			}
		});
	}

	@Override
	public void onResume(){
		super.onResume();
		placesArray = ((MyApplication) this.getApplication()).getPlaces();
		adapter = new AddressAdapter(this, placesArray);	
		ListView places = (ListView) findViewById(R.id.addressesListView);
		places.setAdapter(adapter);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		String str = (String) adapterView.getItemAtPosition(position);
		placesArray.add(new Address(str, R.drawable.edit));
		adapter = new AddressAdapter(this, placesArray);	
		ListView places = (ListView) findViewById(R.id.addressesListView);
		places.setAdapter(adapter);
		nav.addAddress(str);
		AutoCompleteTextView autoCompViewAddreses = (AutoCompleteTextView) findViewById(R.id.autocompleteAddresses);
		autoCompViewAddreses.setText("");
		autoCompViewAddreses.setHint(R.string.addresses);
	}

	public void startNavClicked(){
		//set global variable
		((MyApplication) this.getApplication()).setNavigation(nav);
		DirectionsFetcher df = new DirectionsFetcher(this);
		df.execute();
	}

	public void favoritesClicked(View v){
		Intent i = new Intent(AddressesActivity.this, FavoritesActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
	}

	public void mapClicked(View v){
		Intent i = new Intent(AddressesActivity.this, MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
	}
	
	public void helpClicked(View v){
		Intent i = new Intent(AddressesActivity.this, HelpActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
	}
	
	public void showErrorAlert(){
		new AlertDialog.Builder(this)
		.setTitle(R.string.error)
		.setMessage(R.string.erroMsg)
		.setNegativeButton(R.string._continue, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
				
			}
		})
		.setIcon(android.R.drawable.ic_dialog_alert)
		.show();
	}
}