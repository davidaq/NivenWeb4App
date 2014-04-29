package cn.niven.web4app;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RequestQueue implements Runnable {

	private Executor executors = Executors.newSingleThreadExecutor();

	private LinkedList<Request<?>> queue = new LinkedList<Request<?>>();

	public synchronized void enque(Request<?> request) {
		queue.addLast(request);
		executors.execute(this);
	}

	@Override
	public void run() {
		Request<?> request = null;
		synchronized (queue) {
			if (!queue.isEmpty()) {
				request = queue.getFirst();
				queue.removeFirst();
			}
		}
		if (request != null
				&& (request.listenResult == (null != request.resultHandler && null != request.resultHandler
						.get()))) {
			try {
				Composition.requestor.sendRequest(request);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
