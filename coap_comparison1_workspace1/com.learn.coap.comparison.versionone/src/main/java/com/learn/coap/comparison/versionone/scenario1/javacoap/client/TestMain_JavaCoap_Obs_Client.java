package com.learn.coap.comparison.versionone.scenario1.javacoap.client;


import java.io.IOException;

import java.net.InetSocketAddress;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


import com.mbed.coap.client.CoapClient;
import com.mbed.coap.client.CoapClientBuilder;
import com.mbed.coap.client.ObservationListener;
import com.mbed.coap.exception.CoapException;
import com.mbed.coap.packet.CoapPacket;

/**
 * 多一个 MyObservationListener
 * @author laipl
 *
 */
public class TestMain_JavaCoap_Obs_Client {
	private int expectedNumberOfMessages			= 30;
	private int numberOfMessages 					= 0;
	
	//private String clientSeq = null;
	
	
	public static void main(String[] args) {
		new TestMain_JavaCoap_Obs_Client().run();
    }
	
	public void run() {
		InetSocketAddress inetSocketAddr = new InetSocketAddress("127.0.0.1",5683);		// create client
		CoapClient client=null;
		try {
			client = CoapClientBuilder.newBuilder(inetSocketAddr).build();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// observe
		CompletableFuture<CoapPacket> resp = null;
		try {
			resp = client.resource("/Resource1").observe(new MyObservationListener());
			if(resp != null) {
				//用来获取 第一次得到的数据
				
				if(resp.get().getPayloadString()!=null) {								//防止对面传空 对面传空 直接这么写里面的 会报错的, 所以它和californium 不太一样
					System.out.println(resp.get().getPayloadString().toString());		
				}
				else {
					System.out.println();	
				}
				//System.out.println(resp.get().getPayloadString().toString());
				
				numberOfMessages = numberOfMessages +1;
			}
		} catch (CoapException | InterruptedException | ExecutionException e1) {
			e1.printStackTrace();
		} 

        //---------------------------------------------
		// 停留一段时间 让server继续运行
        while(numberOfMessages < expectedNumberOfMessages) {
        	try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        //
		client.close();
	}
	
	/**
	 * ObservationListener
	 * ref: java-coap/coap-core/src/test/java/protocolTests/ObservationTest.java
	 * 
	 * @author laipl
	 *
	 */
    public class MyObservationListener implements ObservationListener {

        @Override
        public void onObservation(CoapPacket obsPacket) throws CoapException {
            System.out.println(obsPacket.getPayloadString());
            numberOfMessages = numberOfMessages +1;
        }

        @Override
        public void onTermination(CoapPacket obsPacket) throws CoapException {
        	System.out.println("term!!!!!!!"+obsPacket.getPayloadString());
        }
    }
}

