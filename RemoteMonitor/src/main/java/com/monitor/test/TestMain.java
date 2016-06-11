package com.monitor.test;

import java.util.Timer;
import java.util.concurrent.TimeUnit;


public class TestMain {

	public static void main(String[] args) {
		Timer timer = new Timer();
		timer.schedule(new TimerTaskTest(),0,5000);

	}

}
