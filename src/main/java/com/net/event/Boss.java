package com.net.event;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.net.nio.NioServerSocketChannel;
import com.net.util.WorkerUtil;

/**
 * boss主reactor处理类<br/>
 * 职责:<br/>
 * 1.接受连接<br/>
 * 2.分配具体worker处理后续当前接受到连接的读写事件
 * 
 * @author zhaozhenzuo
 *
 */
public class Boss implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(Boss.class);

	/**
	 * boss线程池
	 */
	private Executor executor;

	private Selector selector;

	private NioServerSocketChannel serverSocketChannel;

	private WorkerUtil workerUtil;

	public Boss(Executor executor, ServerSocketChannel serverSocketChannel, WorkerUtil workerUtil) throws IOException {
		if (executor == null || serverSocketChannel == null || workerUtil == null) {
			throw new IllegalArgumentException("executor,serverSocketChannel,workerUtil不能为空");
		}

		this.executor = executor;

		this.serverSocketChannel = new NioServerSocketChannel();
		this.serverSocketChannel.setServerSocketChannel(serverSocketChannel);

		this.workerUtil = workerUtil;

		// open selector
		selector = Selector.open();

		// register
		boolean register = false;
		try {
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, null);
			register = true;
		} finally {
			if (!register) {
				selector.close();
			}
		}

	}

	public void bind(SocketAddress socketAddress) throws IOException {
		serverSocketChannel.getServerSocketChannel().bind(socketAddress);

		// execute reacotr task
		executor.execute(this);

		// start workers
		workerUtil.startWorker();
	}

	public void run() {
		try {
			while (true) {
				int cnt = selector.select();

				if (cnt <= 0) {
					continue;
				}

				Set<SelectionKey> selectionKeys = selector.selectedKeys();

				Iterator<SelectionKey> iterator = selectionKeys.iterator();

				while (iterator.hasNext()) {
					SelectionKey selectionKey = iterator.next();
					iterator.remove();
					if (selectionKey.isAcceptable()) {
						// 连接
						processAcceptNewConnection(serverSocketChannel, selector, selectionKey);
					} else {
						logger.error(">unsupport operType");
					}

				}

			}
		} catch (Exception e) {
			logger.error(">run err", e);
		}

	}

	private void processAcceptNewConnection(NioServerSocketChannel serverSocketChannel, Selector selector,
			SelectionKey selectionKey) throws IOException {
		SocketChannel clientChannel = serverSocketChannel.getServerSocketChannel().accept();
		Worker worker = workerUtil.nextWork();
		worker.register(clientChannel);
	}

}
