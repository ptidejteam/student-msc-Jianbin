package com.learn.coap.comparison.versionone;


import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

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

import com.learn.coap.comparison.versionone.scenario1.javacoap.client.TestMain_JavaCoap_Obs_Client;
import com.learn.coap.comparison.versionone.scenario1.javacoap.server.TestMain_JavaCoap_Obs_Server;

/**
 * 如果出现这个不用担心
 * java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask@55c0e63a[Not completed, task = java.util.concurrent.Executors$RunnableAdapter@30368daa[Wrapped task = org.eclipse.californium.core.coap.ClientObserveRelation$1@29d7a403]] rejected from java.util.concurrent.ScheduledThreadPoolExecutor@5dbc0f1e[Terminated, pool size = 0, active threads = 0, queued tasks = 0, completed tasks = 0]
	只是因为 server 比如发送 31条, client接受了30条则会多出一条, 这个时候强行关闭 就会出现这个
 * 
 * @author laipl
 *
 */
class test1a {
	long startTime = 0;
	long endTime = 0;
	
	static Map timeRs_testCfObs			= null;
	static Map timeRs_testJavaCoapObs	= null;
	
	static Integer loopClientTmp = 0;
	
	private static final Logger LOGGER = LogManager.getLogger(test1a.class);
	
	@BeforeAll
	static void preparation() {

	}
	
	@AfterAll
	static void toEnd() {
		System.out.println("cf total starttime:"+timeRs_testCfObs.get("startTime")+"/endtime:"+timeRs_testCfObs.get("endTime")+"/usedtime:"+timeRs_testCfObs.get("usedTime")+"/usedtime_sec:"+timeRs_testCfObs.get("usedTime_sec"));
		System.out.println("jc total starttime:"+timeRs_testJavaCoapObs.get("startTime")+"/endtime:"+timeRs_testJavaCoapObs.get("endTime")+"/usedtime:"+timeRs_testJavaCoapObs.get("usedTime")+"/usedtime_sec:"+timeRs_testJavaCoapObs.get("usedTime_sec"));
	}
	
	@BeforeEach
	void beforesomething() {
		startTime			=System.nanoTime(); 
		LOGGER.error("This is an error1 level log message!");
		LOGGER.info("This is an infoooerror level log message!");
	}
	
	
	@AfterEach
	void aftersomething() {
		//Map timeRs1	= MyTimeUtil.countUsedTime(startTime);	
		//System.out.println("total starttime:"+timeRs1.get("startTime")+"/endtime:"+timeRs1.get("endTime")+"/usedtime:"+timeRs1.get("usedTime")+"/usedtime_sec:"+timeRs1.get("usedTime_sec"));
	}
	
	/*
	@Test
	void testCfObs() {
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
		
		//start client
		Runnable client1 = new Runnable() {
			public void run() {
				long clientStartTime			=System.nanoTime();   		//nanoTime 会比 currentTimeMillis更加精确
				TestMain_Cf_Obs_Client.main(null);
				Map timeRs1 = MyTimeUtil.countUsedTime(clientStartTime);
				System.out.println("cf client starttime:"+timeRs1.get("startTime")+"/endtime:"+timeRs1.get("endTime")+"/usedtime:"+timeRs1.get("usedTime")+"/usedtime_sec:"+timeRs1.get("usedTime_sec"));
				
			}
		};
		Thread t2 = new Thread(client1);
		t2.start();
		
		//keep going
		while(t1.isAlive()||t2.isAlive()) {
			//do nothing
		}

		//count time
		timeRs_testCfObs	= MyTimeUtil.countUsedTime(startTime);	
	}
	@Test
	void testJavaCoapObs() {
		//start server
		Runnable server1 = new Runnable() {
			public void run() {
				long serverStartTime			=System.nanoTime();   		//nanoTime 会比 currentTimeMillis更加精确
				TestMain_JavaCoap_Obs_Server.main(null);
				Map timeRs1 = MyTimeUtil.countUsedTime(serverStartTime);
				System.out.println("javacoap server starttime:"+timeRs1.get("startTime")+"/endtime:"+timeRs1.get("endTime")+"/usedtime:"+timeRs1.get("usedTime")+"/usedtime_sec:"+timeRs1.get("usedTime_sec"));
			}
		};
		Thread t1 = new Thread(server1);
		t1.start();
		
		//start client
		Runnable client1 = new Runnable() {
			public void run() {
				long clientStartTime			=System.nanoTime();   		//nanoTime 会比 currentTimeMillis更加精确
				TestMain_JavaCoap_Obs_Client.main(null);
				Map timeRs1 = MyTimeUtil.countUsedTime(clientStartTime);
				System.out.println("javacoap client starttime:"+timeRs1.get("startTime")+"/endtime:"+timeRs1.get("endTime")+"/usedtime:"+timeRs1.get("usedTime")+"/usedtime_sec:"+timeRs1.get("usedTime_sec"));
				
			}
		};
		Thread t2 = new Thread(client1);
		t2.start();
		
		//keep going
		while(t1.isAlive()||t2.isAlive()) {
			//do nothing
		}

		//count time
		timeRs_testJavaCoapObs	= MyTimeUtil.countUsedTime(startTime);	
	}
	*/
	
	
	@Test
	void testCfObs_1Server1Client() {
		int clientNum=1;
		testCfObs_specifyClientNum(clientNum);
	}
	
	@Test
	void testJavaCoapObs_1Server1Client() {
		int clientNum=1;
		testJavaCoapObs_specifyClientNum(clientNum);
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
		
		//start client
		for(int i=0; i<=clientNum-1; i++) {	
			String seqTmp = String.valueOf(i);
			Runnable client1 = new Runnable() {
				public void run() {
					long clientStartTime			=System.nanoTime();   		//nanoTime 会比 currentTimeMillis更加精确
					TestMain_Cf_Obs_Client.main(null);
					Map timeRs1 = MyTimeUtil.countUsedTime(clientStartTime);
					System.out.println("cf client starttime:"+timeRs1.get("startTime")+"/endtime:"+timeRs1.get("endTime")+"/usedtime:"+timeRs1.get("usedTime")+"/usedtime_sec:"+timeRs1.get("usedTime_sec"));
					LOGGER.info("californium client"+seqTmp+" starttime:"+timeRs1.get("startTime")+"/endtime:"+timeRs1.get("endTime")+"/usedtime:"+timeRs1.get("usedTime")+"/usedtime_sec:"+timeRs1.get("usedTime_sec"));
				}
			};		
			
			Thread t2 = new Thread(client1);
			arr_thdClient.add(t2);
			t2.start();
		}
		
		// 需要稍微等待一下, 再去开启resource 
		// 不然的话 上面resource还没弄好,就启动了 它就会报NullPointer错误
		// 虽然等待的途中 他们会收到 helloworld:0
		// 但是这个并不算是我的计算范围内
		/*
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		//TestMain_Cf_Obs_Server.startResource();
		
		
		//keep going
		boolean someClientRunning = true;
		while(t1.isAlive()==true || someClientRunning==true) {
			int loopTmp = 0;
			for(; loopTmp<=clientNum-1; loopTmp++) {
				if(arr_thdClient.get(loopTmp).isAlive()) {
					break;
				}
			}
			if(loopTmp>clientNum-1) {
				someClientRunning = false;	//all clients finished
			}
			//do nothing
		}

		//count time
		timeRs_testCfObs	= MyTimeUtil.countUsedTime(startTime);	
	}
	
	

	
	void testJavaCoapObs_specifyClientNum(int clientNum) {
		ArrayList<Thread> arr_thdClient = new ArrayList<Thread>();
		
		//start server
		Runnable server1 = new Runnable() {
			public void run() {
				long serverStartTime			=System.nanoTime();   		//nanoTime 会比 currentTimeMillis更加精确
				TestMain_JavaCoap_Obs_Server.main(null);
				Map timeRs1 = MyTimeUtil.countUsedTime(serverStartTime);
				System.out.println("javacoap server starttime:"+timeRs1.get("startTime")+"/endtime:"+timeRs1.get("endTime")+"/usedtime:"+timeRs1.get("usedTime")+"/usedtime_sec:"+timeRs1.get("usedTime_sec"));
			}
		};
		Thread t1 = new Thread(server1);
		t1.start();
		
		//start client
		for(int i=0; i<=clientNum-1; i++) {	
			String seqTmp = String.valueOf(i);
			Runnable runna_client1 = new Runnable() {
				public void run() {
					long clientStartTime			=System.nanoTime();   		//nanoTime 会比 currentTimeMillis更加精确
					TestMain_JavaCoap_Obs_Client.main(null);
					Map timeRs1 = MyTimeUtil.countUsedTime(clientStartTime);
					System.out.println("javacoap client starttime:"+timeRs1.get("startTime")+"/endtime:"+timeRs1.get("endTime")+"/usedtime:"+timeRs1.get("usedTime")+"/usedtime_sec:"+timeRs1.get("usedTime_sec"));
					LOGGER.info("javacoap client"+seqTmp+" starttime:"+timeRs1.get("startTime")+"/endtime:"+timeRs1.get("endTime")+"/usedtime:"+timeRs1.get("usedTime")+"/usedtime_sec:"+timeRs1.get("usedTime_sec"));
				}
			};	
			
			Thread t2 = new Thread(runna_client1);
			arr_thdClient.add(t2);
			t2.start();
		}
		
		//keep going
		boolean someClientRunning = true;
		while(t1.isAlive()==true || someClientRunning==true) {
			int loopTmp = 0;
			for(; loopTmp<=clientNum-1; loopTmp++) {
				if(arr_thdClient.get(loopTmp).isAlive()) {
					break;
				}
			}
			if(loopTmp>clientNum-1) {
				someClientRunning = false;	//all clients finished
			}
			//do nothing
		}

		//count time
		timeRs_testJavaCoapObs	= MyTimeUtil.countUsedTime(startTime);	
	}
	
	
	
	
	
	public void fun1(Class cls1) {
		System.out.println("hellokt");
		//cls1.getClass().
	}

}
