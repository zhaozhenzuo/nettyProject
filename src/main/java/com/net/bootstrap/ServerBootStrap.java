package com.net.bootstrap;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import com.net.channel.ChannelFactory;
import com.net.channel.ChannelFuture;
import com.net.channel.ChannelPipeLine;
import com.net.channel.ChannelPipeLineFactory;
import com.net.channel.DefaultChannelHandlerContext;
import com.net.channel.DefaultChannelPipeLine;
import com.net.event.Boss;
import com.net.handler.NioServerChannelInboundHandler;
import com.net.nio.NioServerSocketChannel;
import com.net.util.WorkerUtil;

public class ServerBootStrap implements BootStrap {

	private ChannelFactory channelFactory;

	private ChannelPipeLineFactory channelPipeLineFactory;

	private ExecutorService bossExecute;

	private ExecutorService workerExecute;

	private int workCount;

	public ChannelFuture bind(SocketAddress socketAddress) throws IOException {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);

		NioServerSocketChannel nioServerSocketChannel = new NioServerSocketChannel();
		nioServerSocketChannel.setServerSocketChannel(serverSocketChannel);

		// 初始化boss pipeLine
		ChannelPipeLine channelPipeLine = new DefaultChannelPipeLine();

		DefaultChannelHandlerContext headContext = new DefaultChannelHandlerContext(nioServerSocketChannel,
				new NioServerChannelInboundHandler());
		channelPipeLine.addLast(headContext);

		nioServerSocketChannel.setChannelPipeLine(channelPipeLine);

		// 初始化work

		// 启动boss，由bossExecute执行
		WorkerUtil workerUtil = new WorkerUtil(workerExecute, workCount);
		Boss boss = new Boss(bossExecute, serverSocketChannel, workerUtil);
		boss.bind(socketAddress);

		// TODO
		return null;

	}

	public ChannelFactory getChannelFactory() {
		return channelFactory;
	}

	public void setChannelFactory(ChannelFactory channelFactory) {
		this.channelFactory = channelFactory;
	}

	public ChannelPipeLineFactory getChannelPipeLineFactory() {
		return channelPipeLineFactory;
	}

	public void setChannelPipeLineFactory(ChannelPipeLineFactory channelPipeLineFactory) {
		this.channelPipeLineFactory = channelPipeLineFactory;
	}

	public Executor getBossExecute() {
		return bossExecute;
	}

	public ExecutorService getWorkerExecute() {
		return workerExecute;
	}

	public void setWorkerExecute(ExecutorService workerExecute) {
		this.workerExecute = workerExecute;
	}

	public void setBossExecute(ExecutorService bossExecute) {
		this.bossExecute = bossExecute;
	}

	public int getWorkCount() {
		return workCount;
	}

	public void setWorkCount(int workCount) {
		this.workCount = workCount;
	}

}
