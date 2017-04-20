package com.net.util;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import com.net.event.Worker;

public class WorkerUtil {

	private ExecutorService workerExecute;

	private Worker[] workers;

	private int workCount;

	private AtomicInteger nextId = new AtomicInteger(0);

	public WorkerUtil(ExecutorService workerExecute, int workCount) throws IOException {
		this.workerExecute = workerExecute;
		this.workCount = workCount;
		workers = new Worker[workCount];
		this.init();
	}

	public void init() throws IOException {
		for (int i = 0; i < workCount; i++) {
			workers[i] = new Worker();
		}
	}

	public void startWorker() {
		for (Worker worker : workers) {
			workerExecute.submit(worker);
		}
	}

	public Worker nextWork() {
		int id = nextId.getAndIncrement() % workCount;
		return workers[id];
	}

}
