package com.example.finalproject.classes;

public class Favorite {
	String path = "";
	int id;
	
	public Favorite(String path, int id){
		this.path = path;
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
