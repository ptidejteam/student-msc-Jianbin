package com.learn.coap.comparison.versionone.scenario1_case2.californium.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

/**
 * @author laipl
 *
 * 只是修改了 CoapHandler 的变量名
 * client.observer 改成了 client.get 
 *
 */
public class TestMain_Cf_Obs_Client {
	private int expectedNumberOfMessages = 30;
	private int numberOfMessages = 0;

	public static void main(String[] args) {
		new TestMain_Cf_Obs_Client().run();
	}

	private void run() {
		CoapClient client = new CoapClient("coap://localhost:5656/Resource1");
		
		CoapHandler  myCoapHandler1 = new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				System.out.println(response.getResponseText());
				numberOfMessages = numberOfMessages + 1;
			}

			@Override
			public void onError() {
			}
		};	
		
		client.get(myCoapHandler1);
		
		while (numberOfMessages < expectedNumberOfMessages) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		client.shutdown();
	}
}