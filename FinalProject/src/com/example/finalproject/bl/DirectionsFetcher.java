package com.example.finalproject.bl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
import android.util.Log;
import android.view.Gravity;

import com.example.finalproject.activities.RoutsActivity;
import com.example.finalproject.classes.Navigation;
import com.example.finalproject.classes.OptimizeRoute;
import com.example.finalproject.classes.Route;
import com.example.finalproject.custom.MyApplication;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.json.JSONException;
import org.json.JSONObject;
//import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.loopj.android.http.*;

public class DirectionsFetcher extends AsyncTask<URL, Integer, List<LatLng> > implements LocationListener {
	private static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private Navigation nav = new Navigation();
	private Context context;
	private List<Route> routes = new ArrayList<Route>();
	private List<List<Route>> navigationOptions = new ArrayList<List<Route>>();
	private int minTimeIndex = 0;
	private final ProgressDialog ringProgressDialog;
	private List<Route> optimizeRoute;

	public DirectionsFetcher(Context context) {
		this.context = context;
		this.ringProgressDialog = ProgressDialog.show(context, "אנא המתן...", "מחשב מסלול אופטימלי...", true);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		this.nav = ((MyApplication) ((Activity) context).getApplication()).getNavigation();
		ringProgressDialog.getWindow().setGravity(Gravity.RIGHT);
		ringProgressDialog.setCancelable(false);
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);
		Location location = locationManager.getLastKnownLocation(provider);		
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		List<Address> addresses;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi") 
	protected List<LatLng> doInBackground(URL... urls) {
		ObjectMapper mapper = new ObjectMapper();
		Gson gson = new Gson();	 
		String json = gson.toJson(nav);
		HttpClient httpclient = new DefaultHttpClient();
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		HttpPost httpPost = new HttpPost("http://naviserver.azurewebsites.net/api/Navigation/Complex");
		StringEntity se;
		try {
			se = new StringEntity(json, "UTF-8");

			httpPost.setEntity(se);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			httpPost.setHeader(
					"Authorization",
					"Bearer TokenRemovedBecauseUseless");
			org.apache.http.HttpResponse httpResponse = httpclient.execute(httpPost);
			String jsonn = EntityUtils.toString(httpResponse.getEntity());
			TypeToken<List<Route>> token = new TypeToken<List<Route>>(){};
			optimizeRoute = gson.fromJson(jsonn, token.getType());
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
		
		//main algorithm to find the best route
		//				if(nav.getAddresses().size() != 0){
		//					//Get all pairs options
		//					getAllPairs();
		//					//Get start routes
		//					getStartRoutes();
		//					//Get routes
		//					while(navigationOptions.get(0).size() < nav.getAddresses().size()){
		//						navigationOptions = getRoutes();
		//					}
		//					//Get end routes
		//					getEndRoutes();	
		//					minTimeIndex = getShortestRoute();
		//				}
		//				else{
		//					String urlString = "https://maps.googleapis.com/maps/api/directions/json?origin=" + nav.getStartAdd() + "&destination=" + nav.getEndAdd() + "&region=il";
		//					List<Route> onlyRoute = new ArrayList<Route>();
		//					Route r = new Route(nav.getStartAdd(), nav.getEndAdd(), getJSONFromUrl(urlString).get(0), getJSONFromUrl(urlString).get(1), getLocationFromAddress(nav.getEndAdd()).getLatitudeE6(), getLocationFromAddress(nav.getEndAdd()).getLongitudeE6());
		//					onlyRoute.add(r);
		//					navigationOptions.add(onlyRoute);			
		//				}	
		return null;
	}

//	//Get all pairs options
//	public void getAllPairs(){
//		for(int i = 0; i < nav.getAddresses().size(); i++){
//			//Add routes from start address
//			String urlString = "https://maps.googleapis.com/maps/api/directions/json?origin=" + nav.getStartAdd() + "&destination=" + nav.getAddresses().get(i) + "&region=il";			
//			Route rt = new Route(nav.getStartAdd(), nav.getAddresses().get(i), getJSONFromUrl(urlString).get(0), getJSONFromUrl(urlString).get(1), getLocationFromAddress(nav.getAddresses().get(i)).getLatitudeE6(), getLocationFromAddress(nav.getAddresses().get(i)).getLongitudeE6());
//			routes.add(rt);
//			//Add all routes without start & end addresses
//			if(i != nav.getAddresses().size() - 1){
//				for(int j = i + 1; j < nav.getAddresses().size(); j++){
//					urlString = "https://maps.googleapis.com/maps/api/directions/json?origin=" + nav.getAddresses().get(i) + "&destination=" + nav.getAddresses().get(j) + "&region=il";
//					rt = new Route(nav.getAddresses().get(i), nav.getAddresses().get(j), getJSONFromUrl(urlString).get(0), getJSONFromUrl(urlString).get(1), getLocationFromAddress(nav.getAddresses().get(j)).getLatitudeE6(), getLocationFromAddress(nav.getAddresses().get(j)).getLongitudeE6());
//					urlString = "https://maps.googleapis.com/maps/api/directions/json?origin=" + nav.getAddresses().get(i) + "&destination=" + nav.getAddresses().get(j) + "&region=il";
//					Route rt2 = new Route(nav.getAddresses().get(j), nav.getAddresses().get(i), getJSONFromUrl(urlString).get(0), getJSONFromUrl(urlString).get(1), getLocationFromAddress(nav.getAddresses().get(i)).getLatitudeE6(), getLocationFromAddress(nav.getAddresses().get(i)).getLongitudeE6());
//					routes.add(rt);
//					routes.add(rt2);
//				}
//			}
//			try {
//				TimeUnit.SECONDS.sleep(1);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			//Add routes to end address
//			urlString = "https://maps.googleapis.com/maps/api/directions/json?origin=" + nav.getAddresses().get(i) + "&destination=" + nav.getEndAdd() + "&region=il";
//			rt = new Route(nav.getAddresses().get(i), nav.getEndAdd(), getJSONFromUrl(urlString).get(0), getJSONFromUrl(urlString).get(1), getLocationFromAddress(nav.getEndAdd()).getLatitudeE6(), getLocationFromAddress(nav.getEndAdd()).getLongitudeE6());
//			routes.add(rt);
//		}
//	}
//
//	//Add start address routes
//	public void getStartRoutes(){
//		for(int i = 0; i < routes.size(); i++){
//			List<Route> list = new ArrayList<Route>();
//			if(routes.get(i).getSource() == nav.getStartAdd()){
//				list.add(routes.get(i));
//				navigationOptions.add(list); 
//			}
//		}
//	}
//
//	//Add end address routes
//	public void getEndRoutes(){
//		for(int i = 0; i < navigationOptions.size(); i++){
//			for(int j = 0; j < routes.size(); j++){
//				if(navigationOptions.get(i).get(navigationOptions.get(i).size() - 1).getDestination() == routes.get(j).getSource() &&
//						routes.get(j).getDestination() == nav.getEndAdd()){
//					navigationOptions.get(i).add(routes.get(j));
//				}
//			}
//		}
//	}
//
//	public List<List<Route>> getRoutes(){
//		int sum = 0;
//		int minTime = Integer.MAX_VALUE;
//		//Add routes
//		List<List<Route>> navigationOptions2 = new ArrayList<List<Route>>();
//		for(int i = 0; i < navigationOptions.size(); i++){
//			for(int j = 0; j < routes.size(); j++){
//				if(navigationOptions.get(i).get(navigationOptions.get(i).size() - 1).getDestination() == routes.get(j).getSource() &&
//						navigationOptions.get(i).get(navigationOptions.get(i).size() - 1).getSource() != routes.get(j).getDestination() && routes.get(j).getDestination() != nav.getEndAdd()){
//					if(!contains(navigationOptions.get(i), routes.get(j).getDestination())){
//						List<Route> list = new ArrayList<Route>();
//						for(int k = 0; k < navigationOptions.get(i).size(); k++){
//							list.add(navigationOptions.get(i).get(k));
//							sum += navigationOptions.get(i).get(k).getDuration();
//							if(sum >= minTime)
//								break;
//						}
//						if(sum < minTime){
//							minTime = sum;
//							//indexMin = i;
//							list.add(routes.get(j));
//							navigationOptions2.add(list);
//						}
//						sum = 0;
//						//						list.add(routes.get(j));
//						//						navigationOptions2.add(list);
//					}
//				}
//			}
//		}
//		return navigationOptions2;
//	}
//
//	public boolean contains(List<Route> list, String address){
//		for(int i = 0; i < list.size(); i++){
//			if(list.get(i).getSource() == address || list.get(i).getDestination() == address)
//				return true;
//		}
//		return false;
//	}
//
//	public int getShortestRoute(){
//		int i = 0;
//		int sum = 0;
//		int minTime = Integer.MAX_VALUE;
//		int indexMin = 0;
//		for(i = 0; i < navigationOptions.size(); i++){
//			for(int j = 0; j < navigationOptions.get(i).size(); j++){
//				sum += navigationOptions.get(i).get(j).getDuration();
//				if(sum >= minTime)
//					break;
//			}
//			if(sum < minTime){
//				minTime = sum;
//				indexMin = i;
//			}
//			sum = 0;
//		}
//		return indexMin;
//	}
//
//	public List<String> getJSONFromUrl(String urll) {
//		List<String> result = new ArrayList<String>();
//		// Making HTTP request
//		try {
//			HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
//				@Override
//				public void initialize(HttpRequest request) {
//					request.setParser(new JsonObjectParser(JSON_FACTORY));
//				}
//			});
//			urll.replaceAll("\\s","");
//			GenericUrl url = new GenericUrl(urll);
//			url.put("sensor",false);
//
//			HttpRequest request = requestFactory.buildGetRequest(url);
//			HttpResponse httpResponse = request.execute();
//			DirectionsResult directionsResult = httpResponse.parseAs(DirectionsResult.class);
//			int duration = directionsResult.routes.get(0).legs.get(0).duration.value;
//			String durationText = directionsResult.routes.get(0).legs.get(0).duration.text;
//			result.add(String.valueOf(duration));
//			result.add(durationText);
//
//			return result;
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return null;
//	}
//
//	public String getAddressFromUrl(String url){
//		// Making HTTP request
//		try {
//			HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
//				@Override
//				public void initialize(HttpRequest request) {
//					request.setParser(new JsonObjectParser(JSON_FACTORY));
//				}
//			});
//			url.replaceAll("\\s","");
//			GenericUrl urll = new GenericUrl(url);
//			urll.put("sensor",false);
//
//			HttpRequest request = requestFactory.buildGetRequest(urll);
//			HttpResponse httpResponse = request.execute();
//			Result result = httpResponse.parseAs(Result.class);
//
//			return result.formatted_address;
//		}
//		catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return null;
//	}

	protected void onPostExecute(List<LatLng> result) {   
		ringProgressDialog.dismiss();
		//set global variable
		((MyApplication) ((Activity) context).getApplication()).setRoutes(optimizeRoute);
		Intent i = new Intent(context, RoutsActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);	
		context.startActivity(i);
	}

//	public GeoPoint getLocationFromAddress(String strAddress){
//		Geocoder coder = new Geocoder(context);
//		List<Address> address;
//		GeoPoint p1 = null;
//		try {
//			address = coder.getFromLocationName(strAddress, 5);
//			if (address == null) {
//				return null;
//			}
//			Address location = address.get(0);
//			location.getLatitude();
//			location.getLongitude();
//			p1 = new GeoPoint((int) (location.getLatitude() * 1E6),
//					(int) (location.getLongitude() * 1E6));
//
//			return p1;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}    

//	public static class DirectionsResult {
//		@Key("routes")
//		public List<Route_pl> routes;
//	}
//
//	public static class Route_pl {
//		@Key("overview_polyline")
//		public OverviewPolyLine overviewPolyLine;
//		@Key("legs")
//		public List<Leg> legs;
//	}
//
//	public static class OverviewPolyLine {
//		@Key("points")
//		public String points;
//	}
//
//	public static class Leg {
//		@Key("duration")
//		public Duration duration;
//	}
//
//	public static class Duration {
//		@Key("text")
//		public String text;
//		@Key("value")
//		public int value;
//	}
//
//	public class Result
//	{
//		@Key("formatted_address")
//		public String formatted_address;
//	}
//
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
