package com.learn.coap.comparison.versionone.scenario1.javacoap.server;

import java.io.IOException;



import com.mbed.coap.server.CoapServer;


public class TestMain_JavaCoap_Obs_Server {
	
	
	
	
	public static void main(String[] args) {
		// ref:java-coap/coap-core/src/test/java/protocolTests/ObservationTest.java 
		CoapServer server = CoapServer.builder().transport(5683).build();			// create server
		JavaCoap_ObserverResource myobResc1 = new JavaCoap_ObserverResource(server);	// create resource
		myobResc1.setStatusUpdateMaxTimes(50);						// 因为我们想独立的设置次数, 而不想更改构造函数, 所以后面需要独立出来一个startMyResource 
		server.addRequestHandler("/Resource1", myobResc1);								// add resoucre
		
		
		myobResc1.startResource();
		
		try {
			server.start();				// start server
		} catch (IllegalStateException | IOException e1) {
			e1.printStackTrace();
		}																

		
		while (!myobResc1.isMyDone()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		
		// 因为我们的resource用了 timer,
		// 所以我们 destroy 了server以后 , resource还是在运行的
		// in my opinion, we should apply a standard process
		// so we need to stop the resource
		myobResc1.stopResource();
		//
		// 再让Main函数 运行一段时间, 我们可以发现resource没有输出了, 也就意味着 确实结束了
		// 其实 这后面的可以不用, 只是用来判断resource是否结束了,
		// 如果resource 没关掉, 就可以 在这段时间内 发现有resource的输出
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// destroy server
		// because the resource use the timer
		server.stop();
	}

}
