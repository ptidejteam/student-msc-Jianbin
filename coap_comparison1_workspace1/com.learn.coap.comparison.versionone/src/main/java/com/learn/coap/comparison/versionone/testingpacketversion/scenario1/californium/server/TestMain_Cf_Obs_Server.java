package com.learn.coap.comparison.versionone.testingpacketversion.scenario1.californium.server;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.elements.config.Configuration;

/**
 * 
 * @author laipl
 *
 */
public class TestMain_Cf_Obs_Server {
	public static void main(String[] args) {
		
		//CoapServer server = new CoapServer(5683);
		
		//ref:https://www.rfc-editor.org/rfc/rfc7641#section-4.5
		// A server that transmits notifications mostly in non-confirmable
		//  messages MUST send a notification in a confirmable message instead of
		//  a non-confirmable message at least every 24 hours.  This prevents a
		//  client that went away or is no longer interested from remaining in
		//  the list of observers indefinitely.
		//如果不这样设置, 如果使用的是non-confirmable message, 它默认每100条 就会发送一个CON(带内容)
		//这样会影响我测试NON,所以我改成999, 方便测试, 淡然你也可以改成其他数字
		Configuration configuration = Configuration.getStandard();
		configuration.set(CoapConfig.NOTIFICATION_CHECK_INTERVAL_COUNT, 999);
		
		CoapServer server = new CoapServer(configuration,5683);

		Cf_ObserverResource myobResc1 = new Cf_ObserverResource("Resource1");		//new resource
		myobResc1.setStatusUpdateMaxTimes(200);
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