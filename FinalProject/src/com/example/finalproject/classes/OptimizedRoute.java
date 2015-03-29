package com.example.finalproject.classes;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class OptimizedRoute implements Serializable{
	public List<Route> optimizedRoute;
	public int totalTime;

	public int getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}

	public List<Route> getOptimizeRoute() {
		return optimizedRoute;
	}

	public void setOptimizeRoute(List<Route> optimizeRoute) {
		this.optimizedRoute = optimizeRoute;
	}
	
}
