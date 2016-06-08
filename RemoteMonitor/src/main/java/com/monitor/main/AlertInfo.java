package com.monitor.main;

public class AlertInfo {
	
	private int sensorId;
	private int value;
	private RaspberryPi pi;
	
	public AlertInfo(int sensorId, int value, RaspberryPi pi) {
		this.sensorId=sensorId;
		this.value=value;
		this.pi=pi;
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
