package com.net.bootstrap;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SeverRunTest {

	public static void main(String[] args) throws IOException {
		ServerBootStrap serverBootStrap = new ServerBootStrap();

		ExecutorService bossExecute = Executors.newCachedThreadPool();
		ExecutorService workerExecute = Executors.newCachedThreadPool();
		
		serverBootStrap.setBossExecute(bossExecute);
		serverBootStrap.setWorkCount(10);
		serverBootStrap.setWorkerExecute(workerExecute);

		InetSocketAddress socketAddress = new InetSocketAddress("localhost", 8083);
		serverBootStrap.bind(socketAddress);

		System.out.println("finish");

	}

}
