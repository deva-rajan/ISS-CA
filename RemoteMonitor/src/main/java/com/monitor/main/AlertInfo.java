package com.monitor.main;

public class AlertInfo {
	
	private int sensorId;
	private int value;
	private RaspberryPi pi;
	private String dateTime;
	
	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public AlertInfo(int sensorId, int value, RaspberryPi pi,String dateTime) {
		this.sensorId=sensorId;
		this.value=value;
		this.pi=pi;
		this.dateTime=dateTime;
	}

	public int getSensorId() {
		return sensorId;
	}
	public void setSensorId(int sensorId) {
		this.sensorId = sensorId;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public RaspberryPi getPi() {
		return pi;
	}
	public void setPi(RaspberryPi pi) {
		this.pi = pi;
	}
	
	

}
