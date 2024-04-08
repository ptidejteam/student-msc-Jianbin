package com.learn.coap.comparison.versionone.testingpacketversion.scenario1.californium.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

/**
 * @author laipl
 *
 */
public class TestMain_Cf_Obs_Client {
	private int expectedNumberOfMessages = 100;
	private volatile int  numberOfMessages = 0;

	public static void main(String[] args) {
		new TestMain_Cf_Obs_Client().run();
	}

	private void run() {
		CoapClient client = new CoapClient("coap://192.168.50.55:5683/Resource1");
		
		CoapHandler  myObserveHandler = new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				System.out.println(response.getResponseText());
				numberOfMessages = numberOfMessages + 1;
			}

			@Override
			public void onError() {
			}
		};	
		
		client.observe(myObserveHandler);
		
		while (numberOfMessages < expectedNumberOfMessages) {
			/*
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			*/
		}
		client.shutdown();
	}
}