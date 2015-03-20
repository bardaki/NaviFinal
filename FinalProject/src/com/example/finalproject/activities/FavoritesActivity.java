package com.example.finalproject.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.example.finalproject.R;
import com.example.finalproject.bl.DirectionsFetcher;
import com.example.finalproject.classes.Address;
import com.example.finalproject.classes.Navigation;
import com.example.finalproject.custom.FavoritesAdapter;
import com.example.finalproject.custom.MyApplication;
import com.example.finalproject.custom.SwipeDismissListViewTouchListener;
import com.example.finalproject.utils.SqliteController;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("RtlHardcoded")
public class FavoritesActivity extends ActionBarActivity {
	private SqliteController sqlController = new SqliteController(this);
	private static FavoritesAdapter adapter;
	private List<String> placesArray = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorites);
		//get favorites
		ArrayList<HashMap<String, String>> favorites_startEnd = new ArrayList<HashMap<String, String>>();
		favorites_startEnd = sqlController.getAllFavorites_startEnd();		
		List<Navigation> favoritesList = new ArrayList<Navigation>();
		for(int i = 0; i < favorites_startEnd.size(); i++){
			ArrayList<HashMap<String, String>> favorites_addreses = sqlController.getAllFavorites_addresses(favorites_startEnd.get(i).get("MainId"));
			List<String> addressesList = new ArrayList<String>();
			for(int j = 0; j < favorites_addreses.size(); j++){
				addressesList.add(favorites_addreses.get(j).get("Place"));
			}
			favoritesList.add(new Navigation(favorites_startEnd.get(i).get("Id"), favorites_startEnd.get(i).get("Start"), addressesList,  favorites_startEnd.get(i).get("End")));
		}
		//favorites ListView
		final ListView flv = (ListView) findViewById(R.id.favoriteslistView);
		adapter = new FavoritesAdapter(this, favoritesList);
		flv.setAdapter(adapter);	
		SwipeDismissListViewTouchListener touchListener =
				new SwipeDismissListViewTouchListener(
						flv,
						new SwipeDismissListViewTouchListener.DismissCallbacks() {
							@Override
							public boolean canDismiss(int position) {
								return true;
							}

							@Override
							public void onDismiss(ListView listView, int[] reverseSortedPositions) {
								for (int position : reverseSortedPositions) {
									sqlController.delete(((Navigation) adapter.getItem(position)).getId());
									adapter.remove(adapter.getItem(position));                          	 
								}
								adapter.notifyDataSetChanged();
							}
						});
		flv.setOnTouchListener(touchListener);
		flv.setOnScrollListener(touchListener.makeScrollListener());
		flv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Dialog dialogFavoriteDetails = new Dialog(FavoritesActivity.this);			
				dialogFavoriteDetails.setContentView(R.layout.activity_favorite_details);
				dialogFavoriteDetails.setTitle(R.string.favoriteDetails);
				final ArrayAdapter<String>  adapter;
				final Navigation nav = (Navigation)parent.getItemAtPosition(position);
				TextView txtStart = (TextView) dialogFavoriteDetails.findViewById(R.id.textViewStart);
				txtStart.setText(nav.getStartAdd());
				TextView txtEnd = (TextView) dialogFavoriteDetails.findViewById(R.id.textViewEnd);
				txtEnd.setText(nav.getEndAdd());
				ListView lv = (ListView) dialogFavoriteDetails.findViewById(R.id.listViewAddresses);
				//List of Addresses
				placesArray = new ArrayList<String>();
				for(int j = 0; j < nav.getAddresses().size(); j++){
					placesArray.add(nav.getAddresses().get(j));
				}
				adapter = new ArrayAdapter<String>(FavoritesActivity.this, android.R.layout.simple_list_item_1, placesArray);	
				lv.setAdapter(adapter);	
				TextView titleTextView = (TextView) dialogFavoriteDetails.findViewById(android.R.id.title);
				titleTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
				Button startBtn = (Button) dialogFavoriteDetails.findViewById(R.id.buttonStart);
				startBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						//set global variable
						((MyApplication) FavoritesActivity.this.getApplication()).setNavigation(nav);
						((MyApplication) FavoritesActivity.this.getApplication()).setPlaces(placesArrayTocontext());
						DirectionsFetcher df = new DirectionsFetcher(FavoritesActivity.this);
						df.execute();
						dialogFavoriteDetails.dismiss();
					}
				});	
				dialogFavoriteDetails.show();
			}
		});

		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ABFF")));
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		getSupportActionBar().setCustomView(R.layout.abs_favorites);
		//Remove button
		Button removeBtn = (Button) findViewById(R.id.removeall);
		removeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				adapter.removeAll();                          	 
				sqlController.deleteAll();
				adapter.notifyDataSetChanged();				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar, menu);

		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void routesClicked(View v){
		Intent i = new Intent(FavoritesActivity.this, AddressesActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
	}

	public void mapClicked(View v){
		Intent i = new Intent(FavoritesActivity.this, MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
	}
	
	public void helpClicked(View v){
		Intent i = new Intent(FavoritesActivity.this, HelpActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
	}

	public List<Address> placesArrayTocontext(){
		List<Address> lst = new ArrayList<Address>();
		for(int i = 0; i < placesArray.size(); i++){
			lst.add(new Address(placesArray.get(i), R.drawable.edit));
		}
		return lst;
	}
}
