package com.learn.coap.comparison.versionone.scenario1_case2.javacoap.client;


import java.io.IOException;

import java.net.InetSocketAddress;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.learn.coap.comparison.versionone.scenario1.californium.client.TestMain_Cf_Obs_Client;
import com.mbed.coap.client.CoapClient;
import com.mbed.coap.client.CoapClientBuilder;
import com.mbed.coap.client.ObservationListener;
import com.mbed.coap.exception.CoapException;
import com.mbed.coap.packet.CoapPacket;
import com.mbed.coap.utils.Callback;

/**
 * @author laipl
 * 
 * 少一个 MyObservationListener, 并且 改成 new String(t.getPayload())
 *
 */
public class TestMain_JavaCoap_Obs_Client {
	private int expectedNumberOfMessages			= 30;
	private int numberOfMessages 					= 0;
	
	private String clientSeq = null;
	
	
	public static void main(String[] args) {
		new TestMain_JavaCoap_Obs_Client().run();
    }
	
	public void run() {
		InetSocketAddress inetSocketAddr = new InetSocketAddress("localhost",5656);		// create client
		CoapClient client=null;
		try {
			client = CoapClientBuilder.newBuilder(inetSocketAddr).build();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// observe

		client.resource("/Resource1").get(new Callback<CoapPacket>() {
			@Override
			public void call(CoapPacket t) {
		    	System.out.println(new String(t.getPayload()));
		    	numberOfMessages = numberOfMessages +1;
			}
			@Override
		    public void callException(Exception ex) {
				System.err.println("Failed");
		    }
		//
		});

        //---------------------------------------------
		// 停留一段时间 让server继续运行
        while(numberOfMessages < expectedNumberOfMessages) {
        	try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        //
		client.close();
	}

}

