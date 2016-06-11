package com.monitor.gui;

import java.util.TimerTask;

import com.monitor.main.AlertInfo;
import com.monitor.main.MonitorMain;

public class RefreshGUI extends TimerTask {

	@Override
	public void run() {
		int rowLamp = 0, rowLoad = 0;
		System.out.println("Inside GUi Refresher");
		for (AlertInfo info : MonitorMain.infoQueueLamp) {
			if (rowLamp<MonitorGUI.modelLamp.getRowCount()) {
				MonitorGUI.modelLamp.setValueAt(info.getDateTime(), rowLamp, 0);
				MonitorGUI.modelLamp.setValueAt(info.getPi().getStreetAddress(), rowLamp, 1);
				MonitorGUI.modelLamp.setValueAt(info.getPi().getZipcode(),rowLamp, 2);
				MonitorGUI.modelLamp.setValueAt(info.getSensorId(), rowLamp, 3);
				
			}else{
				MonitorGUI.modelLamp.addRow(new Object[]{info.getPi().getStreetAddress(),info.getPi().getZipcode(),info.getSensorId()});
			}
			rowLamp += 1;
		}

		for (AlertInfo info : MonitorMain.infoQueueLoad) {
			if (rowLamp < MonitorGUI.modelLoad.getRowCount()) {
				MonitorGUI.modelLoad.setValueAt(info.getDateTime(), rowLoad, 0);
				MonitorGUI.modelLoad.setValueAt(info.getPi().getStreetAddress(), rowLoad, 1);
				MonitorGUI.modelLoad.setValueAt(info.getPi().getZipcode(),rowLoad, 2);
				MonitorGUI.modelLoad.setValueAt(info.getSensorId(), rowLoad, 3);
				MonitorGUI.modelLoad.setValueAt(info.getValue(), rowLoad, 4);
			}else{
				MonitorGUI.modelLoad.addRow(new Object[]{info.getPi().getStreetAddress(),info.getPi().getZipcode(),info.getSensorId(),info.getValue()});
			}
			System.out.println("Inside GUI" + info.getSensorId());
			rowLamp += 1;
		}
	}

}
