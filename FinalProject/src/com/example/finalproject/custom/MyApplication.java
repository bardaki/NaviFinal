package com.example.finalproject.custom;

import java.util.ArrayList;
import java.util.List;
import com.example.finalproject.classes.Address;
import com.example.finalproject.classes.Navigation;
import com.example.finalproject.classes.Route;

import android.app.Application;

public class MyApplication extends Application {

	private List<Route> routes = new ArrayList<Route>();
	private Navigation nav = new Navigation();
	private List<Address> placesArray = new ArrayList<Address>();
	private boolean doInBg = true;

	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}

	public Navigation getNavigation() {
		return nav;
	}

	public void setNavigation(Navigation nav) {
		this.nav = nav;
	}

	public List<Address> getPlaces() {
		return placesArray;
	}

	public void setPlaces(List<Address> placesArray) {
		this.placesArray = placesArray;
	}

	public boolean getDoInBg() {
		return doInBg;
	}

	public void setDoInBg(boolean doInBg) {
		this.doInBg = doInBg;
	}
}