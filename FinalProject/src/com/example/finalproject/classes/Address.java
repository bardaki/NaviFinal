package com.example.finalproject.classes;

public class Address {
	String name;
	int urlImage;
	
	public Address(String name, int urlImage){
		this.name = name;
		this.urlImage = urlImage;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int getUrlImage() {
		return urlImage;
	}
	
	public void setUrlImage(int urlImage) {
		this.urlImage = urlImage;
	}
}
