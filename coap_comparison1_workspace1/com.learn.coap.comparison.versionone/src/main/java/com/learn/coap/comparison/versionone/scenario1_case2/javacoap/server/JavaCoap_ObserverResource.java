package com.learn.coap.comparison.versionone.scenario1_case2.javacoap.server;

import java.util.Timer;
import java.util.TimerTask;

import com.mbed.coap.CoapConstants;
import com.mbed.coap.exception.CoapCodeException;
import com.mbed.coap.exception.CoapException;
import com.mbed.coap.observe.AbstractObservableResource;
import com.mbed.coap.observe.NotificationDeliveryListener;
import com.mbed.coap.packet.Code;
import com.mbed.coap.packet.MediaTypes;
import com.mbed.coap.server.CoapExchange;
import com.mbed.coap.server.CoapServer;
/**
 * 
 * @author laipl
 * 
 * 跟scenario1完全一致
 * 
 */
public class JavaCoap_ObserverResource extends AbstractObservableResource{

	private class UpdateTask extends TimerTask {
		private int statusUpdateMaxTimes = 30;
		private int statusUpdate = 0;
		private boolean rescDone = false;
		
		public void setStatusUpdateMaxTimes(int statusUpdateMaxTimes) {
			this.statusUpdateMaxTimes = statusUpdateMaxTimes;
		}

		public int getStatusUpdate() {
			return this.statusUpdate;
		}

		
		@Override
		public void run() {
			// 为了保持 与Mqtt 测量的方式 相同, 当信息更新次数>statusUpdateMaxTimes-1时, 不再发送信息给 client
			if(statusUpdate<=statusUpdateMaxTimes-1) {
				statusUpdate = statusUpdate+1;
				//System.out.println(content+":"+statusUpdate);
				try {
					notifyChange(new String("Hello World!"+statusUpdate).getBytes(CoapConstants.DEFAULT_CHARSET),MediaTypes.CT_TEXT_PLAIN);
				} catch (CoapException e) {
					e.printStackTrace();
				} 
			}
			else {
				rescDone = true;
			}
			// 类比于 mqtt 它每一次信息自己更新
		}
		
		public boolean isMyDone() {
			return this.rescDone;
		}
	}
	
	

	private Timer timer;
	private UpdateTask updateTask = null;
	
	public JavaCoap_ObserverResource(CoapServer coapServer) {
		super(coapServer);
		// TODO Auto-generated constructor stub
		this.setConNotifications(false);					// configure the notification type to NONs, 如果不写这个默认的是 CON
		//
		timer = new Timer();								// schedule a periodic update task, otherwise let events call changed()
		updateTask = new UpdateTask();
		//timer.schedule(myUpdateTask1,0, 500);	//500ms
	}
	
	
	@Override
	public void get(CoapExchange exchange) throws CoapCodeException {
		exchange.setResponseBody("Hello World!"+updateTask.getStatusUpdate());
        exchange.getResponseHeaders().setContentFormat(MediaTypes.CT_TEXT_PLAIN);
        exchange.setResponseCode(Code.C205_CONTENT);
        exchange.sendResponse();
	}
	
	

	//--------------------------------------------------------------------------------
	//----------------------------------  my method ----------------------------------
	// 这里 这个 statusUpdateMaxTimes 不可以用final来修饰
	public void setStatusUpdateMaxTimes(int statusUpdateMaxTimes) {
		this.updateTask.setStatusUpdateMaxTimes(statusUpdateMaxTimes);
	}
	public int getStatusUpdate() {
		return this.updateTask.getStatusUpdate();
	}

	public void startResource() {
		this.timer.schedule(updateTask, 0, 500);
	}

	public void stopResource() {
		this.timer.cancel();
	}

	public boolean isMyDone() {
		return updateTask.isMyDone();
	}
}

