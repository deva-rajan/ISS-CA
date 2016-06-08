package com.monitor.main;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class RequestHandler extends AbstractHandler{
	
	private void pushInfoLamp(RaspberryPi obj,int sensorId,int value){
		if(MonitorMain.infoStackLamp.size()>10){
			MonitorMain.infoStackLamp.pop();
		}
		AlertInfo alertObj = new AlertInfo(sensorId,value,obj);
		MonitorMain.infoStackLamp.push(alertObj);
		System.out.println("Adding into stacklamp:"+alertObj.getSensorId());
		MonitorMain.infoListLamp.add(alertObj);
	}
	
	private void pushInfoLoad(RaspberryPi obj,int sensorId,int value){
		if(MonitorMain.infoStackLoad.size()>10){
			MonitorMain.infoStackLoad.pop();
		}
		AlertInfo alertObj = new AlertInfo(sensorId,value,obj);
		MonitorMain.infoStackLoad.push(alertObj);
		System.out.println("Adding into stackload:"+alertObj.getSensorId());
	}
	
	public void handle(String arg0, Request arg1, HttpServletRequest arg2,
			HttpServletResponse arg3) throws IOException, ServletException {
	   
		//Get raspberry pi id
		int id=Integer.parseInt(arg1.getParameter("id"));
		int sensorId = Integer.parseInt(arg1.getParameter("sensorId"));
		int value=Integer.parseInt(arg1.getParameter("value"));
		char label=MonitorMain.piMap.get(id).getType();
		if(label=='l')
			pushInfoLamp(MonitorMain.piMap.get(id),sensorId,value);
		else
			pushInfoLoad(MonitorMain.piMap.get(id),sensorId,value);
		System.out.println("id:"+id+" sensorid:"+sensorId+" value:"+value);
	}

}