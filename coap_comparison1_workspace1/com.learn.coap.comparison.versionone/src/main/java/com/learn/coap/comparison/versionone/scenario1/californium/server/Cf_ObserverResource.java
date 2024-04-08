package com.learn.coap.comparison.versionone.scenario1.californium.server;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class Cf_ObserverResource extends CoapResource {
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
			if (this.statusUpdate <= this.statusUpdateMaxTimes - 1) {
				this.statusUpdate = this.statusUpdate + 1;
				changed(); // Notify all observers
			} else {
				rescDone = true;
			}
		}

		public boolean isMyDone() {
			return this.rescDone;
		}
	}

	private Timer timer;
	private UpdateTask updateTask = null;

	public Cf_ObserverResource(String name) {
		super(name);
		this.setObservable(true);
		this.setObserveType(Type.NON);
		this.getAttributes().setObservable();

		timer = new Timer();
		updateTask = new UpdateTask();

	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.CONTENT, "Hello World!" + updateTask.getStatusUpdate(),MediaTypeRegistry.TEXT_PLAIN);
		//exchange.respond(ResponseCode.CONTENT, "",MediaTypeRegistry.TEXT_PLAIN);
		//exchange.respond("");
	}

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