package com.learn.coap.comparison.versionone.testingpacketversion.scenario1.javacoap.server;

import java.util.Timer;
import java.util.TimerTask;

import com.mbed.coap.CoapConstants;
import com.mbed.coap.exception.CoapCodeException;
import com.mbed.coap.exception.CoapException;
import com.mbed.coap.observe.AbstractObservableResource;

import com.mbed.coap.packet.Code;
import com.mbed.coap.packet.MediaTypes;
import com.mbed.coap.server.CoapExchange;
import com.mbed.coap.server.CoapServer;

public class JavaCoap_ObserverResource extends AbstractObservableResource{

	private class UpdateTask extends TimerTask {
		private int statusUpdateMaxTimes = 200;
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
					//notifyChange(new String("Hello World!"+statusUpdate).getBytes(CoapConstants.DEFAULT_CHARSET),MediaTypes.CT_TEXT_PLAIN);
					notifyChange(new String("Hi!"+ String.format("%07d", statusUpdate)).getBytes(CoapConstants.DEFAULT_CHARSET),MediaTypes.CT_TEXT_PLAIN);
					
					//notifyChange(new String("").getBytes(),MediaTypes.CT_TEXT_PLAIN); //不可以设置成null.getBytes
					//notifyChange(null,MediaTypes.CT_TEXT_PLAIN); // 运行会错误
					//notifyChange(null,null); 					// 运行会错误
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
		//super(coapServer);
		
		//ref:https://www.rfc-editor.org/rfc/rfc7641#section-4.5
		// A server that transmits notifications mostly in non-confirmable
		//  messages MUST send a notification in a confirmable message instead of
		//  a non-confirmable message at least every 24 hours.  This prevents a
		//  client that went away or is no longer interested from remaining in
		//  the list of observers indefinitely.
		//如果不这样设置, 如果使用的是non-confirmable message, 它默认每20条 就会发送一个CON(带内容)
		//这样会影响我测试NON,所以我改成999, 方便测试, 淡然你也可以改成其他数字
		super(coapServer,true,999);
		
		
		this.setConNotifications(false);					// configure the notification type to NONs, 如果不写这个默认的是 CON
		//this.setConNotifications(true);					// configure the notification type to NONs, 如果不写这个默认的是 CON
		
		//
		timer = new Timer();								// schedule a periodic update task, otherwise let events call changed()
		updateTask = new UpdateTask();
		//timer.schedule(myUpdateTask1,0, 500);	//500ms
	}
	
	
	@Override
	public void get(CoapExchange exchange) throws CoapCodeException {
		exchange.setResponseBody("Hi!"+ String.format("%07d", updateTask.getStatusUpdate()));
		//exchange.setResponseBody("");
        exchange.getResponseHeaders().setContentFormat(MediaTypes.CT_TEXT_PLAIN);		//这个会影响到server to client的 ACK的包中 是否会有 option 关于content-format
        exchange.setResponseCode(Code.C205_CONTENT);									//虽然默认有 , 为了统一和谐
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
		//this.timer.schedule(updateTask, 0, 5000);			//为了测试packet的
	}

	public void stopResource() {
		this.timer.cancel();
	}

	public boolean isMyDone() {
		return updateTask.isMyDone();
	}
}

