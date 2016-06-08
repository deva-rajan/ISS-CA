package com.monitor.gui;

import java.util.TimerTask;

import com.monitor.main.AlertInfo;
import com.monitor.main.MonitorMain;

public class RefreshGUI extends TimerTask {

	@Override
	public void run() {
		int rowLamp=0,rowLoad=0;
		System.out.println("Inside GUi Refresher");
		for(AlertInfo info:MonitorMain.infoStackLamp){
			MonitorGUI.modelLamp.setValueAt(info.getPi().getStreetAddress(),rowLamp,0);
			MonitorGUI.modelLamp.setValueAt(info.getPi().getZipcode(),rowLamp,1);
		    MonitorGUI.modelLamp.setValueAt(info.getSensorId(),rowLamp,2);
		    rowLamp+=1;
		}
		
		for(AlertInfo info:MonitorMain.infoStackLoad){
			MonitorGUI.modelLoad.setValueAt(info.getPi().getStreetAddress(),rowLoad,0);
			MonitorGUI.modelLoad.setValueAt(info.getPi().getZipcode(),rowLoad,1);
		    MonitorGUI.modelLoad.setValueAt(info.getSensorId(),rowLoad,2);
		    MonitorGUI.modelLoad.setValueAt(info.getSensorId(),rowLoad,3);
		    System.out.println("Inside GUI"+info.getSensorId());
		    rowLamp+=1;
		}
	}

}
