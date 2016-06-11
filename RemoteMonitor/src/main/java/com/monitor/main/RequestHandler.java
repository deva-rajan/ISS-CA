package com.monitor.main;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.monitor.util.Mail;

public class RequestHandler extends AbstractHandler{
	
	private void pushInfoLamp(RaspberryPi obj,int sensorId,int value,String dateTime){
		if(MonitorMain.infoQueueLamp.size()>10){
			MonitorMain.infoQueueLamp.remove();
		}
		AlertInfo alertObj = new AlertInfo(sensorId,value,obj,dateTime);
		MonitorMain.infoQueueLamp.add(alertObj);
		System.out.println("Adding into stacklamp:"+alertObj.getSensorId());
		MonitorMain.infoListLamp.add(alertObj);
	}
	
	private void pushInfoLoad(RaspberryPi obj,int sensorId,int value,String dateTime){
		if(MonitorMain.infoQueueLoad.size()>10){
			MonitorMain.infoQueueLoad.remove();
		}
		AlertInfo alertObj = new AlertInfo(sensorId,value,obj,dateTime);
		MonitorMain.infoQueueLoad.add(alertObj);
		Mail.sendMailLoad(alertObj.getPi().getStreetAddress()+","+alertObj.getPi().getZipcode()+","+alertObj.getSensorId()+","+alertObj.getValue());
		System.out.println("Adding into stackload:"+alertObj.getSensorId());
	}
	
	public void handle(String arg0, Request arg1, HttpServletRequest arg2,
			HttpServletResponse arg3) throws IOException, ServletException {
	   
		//Get raspberry pi id
		int id=Integer.parseInt(arg1.getParameter("id"));
		int sensorId = Integer.parseInt(arg1.getParameter("sensorId"));
		int value=Integer.parseInt(arg1.getParameter("value"));
		char label=MonitorMain.piMap.get(id).getType();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dateInst = new Date();
		String dateTime = sdf.format(dateInst);
		if(label=='l')
			pushInfoLamp(MonitorMain.piMap.get(id),sensorId,value,dateTime);
		else
			pushInfoLoad(MonitorMain.piMap.get(id),sensorId,value,dateTime);
		System.out.println("id:"+id+" sensorid:"+sensorId+" value:"+value);
	}

}