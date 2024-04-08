package com.learn.coap.comparison.versionone.scenario1_case2.californium.server;

import org.eclipse.californium.core.CoapServer;

/**
 * 
 * @author laipl
 * 
 * 跟scenario1完全一致
 * 
 */
public class TestMain_Cf_Obs_Server {
	public static void main(String[] args) {
		CoapServer server = new CoapServer(5656);

		Cf_ObserverResource myobResc1 = new Cf_ObserverResource("Resource1");
		myobResc1.setStatusUpdateMaxTimes(35);
		server.add(myobResc1);
		
		myobResc1.startResource();
		server.start();

		while (!myobResc1.isMyDone()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		myobResc1.stopResource();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// destory 可以结束程序, 但是stop不可以
		//server.destroy();
		//server.stop();
		server.destroy();
	}
}