package com.monitor.main;

public class RaspberryPi {

	private int piId;
	private int zipcode;
	private String streetAddress;
	private char type;
	
	public RaspberryPi(int piId,int zipcode,String streetAddress,char type){
		this.piId=piId;
		this.zipcode=zipcode;
		this.streetAddress=streetAddress;
		this.type=type;
	}
	
	public int getPiId() {
		return piId;
	}

	public void setPiId(int piId) {
		this.piId = piId;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public int getZipcode() {
		return zipcode;
	}

	public void setZipcode(int zipcode) {
		this.zipcode = zipcode;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
}
