package com.monitor.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Server;

import com.monitor.gui.MonitorGUI;
import com.monitor.gui.RefreshGUI;
import com.monitor.util.MailWorker;

public class MonitorMain {
	
	public static HashMap<Integer,RaspberryPi> piMap = new HashMap<Integer,RaspberryPi>();
	public static Queue<AlertInfo> infoQueueLamp = new LinkedList<AlertInfo>();
	public static List<AlertInfo> infoListLamp = Collections.synchronizedList(new ArrayList<AlertInfo>());
	public static Queue<AlertInfo> infoQueueLoad = new LinkedList<AlertInfo>();
	
	private static void buildPiMap(){
		RaspberryPi pi1 = new RaspberryPi(1001,120274,"Clementi WestStreet2",'l');
		RaspberryPi pi2 = new RaspberryPi(1001,120275,"Clementi WestStreet3",'l');
		RaspberryPi pi3 = new RaspberryPi(1001,120274,"Clementi WestStreet4",'l');
		piMap.put(1001,pi1);
		piMap.put(1001, pi2);
		piMap.put(1001, pi3);
		
		RaspberryPi pi4 = new RaspberryPi(1005,120274,"Clementi WestStreet2",'e');
		RaspberryPi pi5 = new RaspberryPi(1005,120275,"Clementi WestStreet3",'e');
		RaspberryPi pi6 = new RaspberryPi(1005,120274,"Clementi WestStreet4",'e');
		piMap.put(1005,pi4);
		piMap.put(1005, pi5);
		piMap.put(1005, pi6);
	}
	
	
	public static void init() throws Exception{
		buildPiMap();
		//Start Mail Workers
        Timer timer = new Timer();
        timer.schedule(new MailWorker(),0,TimeUnit.MINUTES.toMillis(5));
        
        //Start GUI refresher	
        Timer guiTimer = new Timer();
        guiTimer.schedule(new RefreshGUI(), 0,TimeUnit.SECONDS.toMillis(5));
		//Start Jetty Server
		Server server = new Server(8080);
        server.setHandler(new RequestHandler()); 
        server.start();
        server.join();
        
        
	}

	public static void main(String[] args) throws Exception {
		
		MonitorGUI instance = new MonitorGUI();
		instance.configureFrame();
		init();
		
	}

}
