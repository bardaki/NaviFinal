package com.example.finalproject.classes;

import java.io.Serializable;


public class Route implements Serializable{
	private String source;
	private String destination;
	private int endLatitude;
	private int endLongitude;
	private int duration;
	private String durationText;

	public Route(String source, String destination, String duration, String durationText, int endLatitude, int endLongitude){
		this.source = source;
		this.destination = destination;
		this.duration = Integer.parseInt(duration);
		this.durationText = durationText;
		this.endLatitude = endLatitude;
		this.endLongitude = endLongitude;
	}

	public String getSource(){
		return source;
	}

	public String getDestination(){
		return destination;
	}

	public int getDuration(){
		return duration;
	}

	public int getLatitude(){
		return endLatitude;
	}

	public int getLongitude(){
		return endLongitude;
	}

	public String toString(){
		String[] src = source.split(",");
		String[] dst = destination.split(",");

		return src[0] + "- " + dst[0] + "\n          " + durationText;
	}
}

