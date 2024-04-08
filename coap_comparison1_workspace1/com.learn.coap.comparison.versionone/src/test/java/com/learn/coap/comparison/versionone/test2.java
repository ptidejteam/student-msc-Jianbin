package com.learn.coap.comparison.versionone;
/**
 * 暂时不用
 */
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.learn.coap.comparison.versionone.myutil.MyTimeUtil;
import com.learn.coap.comparison.versionone.scenario1.californium.client.TestMain_Cf_Obs_Client;
import com.learn.coap.comparison.versionone.scenario1.californium.server.TestMain_Cf_Obs_Server;

class test2 {
	long startTime = 0;
	long endTime = 0;

	static Map timeRs_testCfObs			= null;
	static Map timeRs_testJavaCoapObs	= null;
	
	static Integer loopClientTmp = 0;
	
	private static final Logger LOGGER = LogManager.getLogger(test2.class);
	
	@BeforeAll
	static void preparation() {

	}
	
	@AfterAll
	static void toEnd() {
		//System.out.println("cf total starttime:"+timeRs_testCfObs.get("startTime")+"/endtime:"+timeRs_testCfObs.get("endTime")+"/usedtime:"+timeRs_testCfObs.get("usedTime")+"/usedtime_sec:"+timeRs_testCfObs.get("usedTime_sec"));
		//System.out.println("jc total starttime:"+timeRs_testJavaCoapObs.get("startTime")+"/endtime:"+timeRs_testJavaCoapObs.get("endTime")+"/usedtime:"+timeRs_testJavaCoapObs.get("usedTime")+"/usedtime_sec:"+timeRs_testJavaCoapObs.get("usedTime_sec"));
	}
	
	@BeforeEach
	void beforesomething() {
		startTime			=System.nanoTime(); 
		LOGGER.error("This is an error1 level log message!");
		LOGGER.info("This is an infoooerror level log message!");
	}
	
	
	@AfterEach
	void aftersomething() {
	}
	
	@Test
	void testCfObs_1Server1Client() {
		int clientNum=1;
		testCfObs_specifyClientNum(clientNum);
	}
	

	void testCfObs_specifyClientNum(int clientNum) {
		ArrayList<Thread> arr_thdClient = new ArrayList<Thread>();
		
		//start server
		Runnable server1 = new Runnable() {
			public void run() {
				long serverStartTime			=System.nanoTime();   		//nanoTime 会比 currentTimeMillis更加精确
				TestMain_Cf_Obs_Server.main(null);
				Map timeRs1 = MyTimeUtil.countUsedTime(serverStartTime);
				System.out.println("cf server starttime:"+timeRs1.get("startTime")+"/endtime:"+timeRs1.get("endTime")+"/usedtime:"+timeRs1.get("usedTime")+"/usedtime_sec:"+timeRs1.get("usedTime_sec"));
			}
		};
		Thread t1 = new Thread(server1);
		t1.start();
		
		Runnable client1 = new Runnable() {
			public void run() {
				long clientStartTime			=System.nanoTime();   		//nanoTime 会比 currentTimeMillis更加精确
				TestMain_Cf_Obs_Client.main(null);
				Map timeRs1 = MyTimeUtil.countUsedTime(clientStartTime);
				System.out.println("cf client starttime:"+timeRs1.get("startTime")+"/endtime:"+timeRs1.get("endTime")+"/usedtime:"+timeRs1.get("usedTime")+"/usedtime_sec:"+timeRs1.get("usedTime_sec"));
				//LOGGER.info("californium client"+seqTmp+" starttime:"+timeRs1.get("startTime")+"/endtime:"+timeRs1.get("endTime")+"/usedtime:"+timeRs1.get("usedTime")+"/usedtime_sec:"+timeRs1.get("usedTime_sec"));
				
			}
		};	
		Thread t2 = new Thread(client1);
		t2.start();
		
		//keep going
		boolean someClientRunning = true;
		while(t1.isAlive()==true) {

		}

		//count time
		timeRs_testCfObs	= MyTimeUtil.countUsedTime(startTime);	
	}
	
	
	
	
}
