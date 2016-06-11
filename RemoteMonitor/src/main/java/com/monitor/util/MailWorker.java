package com.monitor.util;

import java.util.ArrayList;
import java.util.TimerTask;
import com.monitor.main.AlertInfo;
import com.monitor.main.MonitorMain;

public class MailWorker extends TimerTask {
	@Override
	public void run() {
		System.out.println("Inside Mail Worker");
		ArrayList<String> messages = new ArrayList<String>();
		for (AlertInfo info : MonitorMain.infoListLamp) {
			messages.add(info.getPi().getStreetAddress() + ","+ info.getPi().getZipcode() + "," + info.getSensorId()+ "," + info.getValue());
		}
		if (MonitorMain.infoListLamp.size() > 0)
			Mail.sendMailLamp(messages);
		MonitorMain.infoListLamp.clear();
	}
}
