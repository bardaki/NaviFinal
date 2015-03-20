package com.example.finalproject.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Navigation implements Serializable{
	private String id;
	private String startAdd = "";
	private List<String> addresses;
	private String endAdd = "";

	public Navigation(){
		addresses = new ArrayList<String>();		
	}
	
	public Navigation(String id, String startAdd, List<String> addresses, String endAdd){
		this.id = id;
		this.startAdd = startAdd;
		this.addresses = addresses;
		this.endAdd = endAdd;
	}

	public void addAddress(String address){
		addresses.add(address);
	}

	public void addStartAdd(String address){
		startAdd = address;
	}

	public void addEndAdd(String address){
		endAdd = address;
	}

	public String getStartAdd(){
		return startAdd;
	}

	public String getEndAdd(){
		return endAdd;
	}

	public List<String> getAddresses(){
		return addresses;
	}

	public void removeFromAddresses(int position){
		addresses.remove(position);
	}
	
	public String toString(){
		String[] src = startAdd.split(",");
		String[] dst = endAdd.split(",");

		return src[0] + " - " + dst[0] + " : " + addresses.size() + " נקודות ביניים";
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}
}
