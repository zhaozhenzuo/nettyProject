package com.net.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.net.util.SocketUtil;

public class MultiServerSocketTest {

	private static final Logger logger = LoggerFactory.getLogger(MultiServerSocketTest.class);

	private static final ExecutorService boss = Executors.newCachedThreadPool();

	private static final ExecutorService work = Executors.newCachedThreadPool();

	public static void main(String[] args) {

		try {
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			Selector selector = Selector.open();
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			SocketAddress socketAddress = new InetSocketAddress("localhost", 8083);
			serverSocketChannel.bind(socketAddress);
			
			ServerSocketChannel serverSocketChannel2 = ServerSocketChannel.open();
			serverSocketChannel2.configureBlocking(false);
			Selector selector2 = Selector.open();
			serverSocketChannel.register(selector2, SelectionKey.OP_ACCEPT);
			SocketAddress socketAddress2 = new InetSocketAddress("localhost", 8083);
			serverSocketChannel2.bind(socketAddress2);

			// 启动boss
			boss.submit(new AcceptExecuteTask(selector, serverSocketChannel));
			
			boss.submit(new AcceptExecuteTask(selector, serverSocketChannel));

		} catch (IOException e) {
			logger.error("err", e);
		}

	}

	static class AcceptExecuteTask implements Runnable {

		private Selector selector;

		private ServerSocketChannel serverSocketChannel;

		public AcceptExecuteTask(Selector selector, ServerSocketChannel serverSocketChannel) {
			this.selector = selector;
			this.serverSocketChannel = serverSocketChannel;
		}

		public void run() {
			while (true) {

				try {
					int cnt;
					cnt = selector.select();

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
							logger.error(">unsupport oper type in AcceptTask");
						}

					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		}

	}

	static class ReadWriteExecuteTask implements Runnable {

		private Selector selector;

		public ReadWriteExecuteTask(SocketChannel socketChannel) throws IOException {
			this.selector = Selector.open();
			socketChannel.configureBlocking(false);
			socketChannel.register(selector, SelectionKey.OP_READ);
		}

		public void run() {
			while (true) {

				try {
					int cnt;
					cnt = selector.select();

					if (cnt <= 0) {
						continue;
					}

					Set<SelectionKey> selectionKeys = selector.selectedKeys();

					Iterator<SelectionKey> iterator = selectionKeys.iterator();

					while (iterator.hasNext()) {
						SelectionKey selectionKey = iterator.next();
						iterator.remove();
						if (selectionKey.isReadable()) {
							// 连接
							processRead(selectionKey);
						}else{
							//unsurport
							logger.error(">unsurport oper,for readWriteTask");
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		}

	}

	private static void processRead(SelectionKey selectionKey) {
		if (!selectionKey.isValid()) {
			selectionKey.cancel();
		}

		SocketChannel channel = (SocketChannel) selectionKey.channel();

		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		byteBuffer.clear();

		try {
			int res = channel.read(byteBuffer);
			if (res < 0) {
				// channel.close();
				selectionKey.cancel();
			}

			byteBuffer.flip();

			String str = SocketUtil.decode(byteBuffer);

			logger.info(">res[" + str + "]");
		} catch (IOException e) {
			logger.error(">io exception", e);
		}
	}

	private static void processAcceptNewConnection(ServerSocketChannel serverSocketChannel, Selector selector,
			SelectionKey selectionKey) throws IOException {
		logger.info(">accept,cur thread["+Thread.currentThread().getId()+"]");
		
		SocketChannel socketChannel = serverSocketChannel.accept();
		ReadWriteExecuteTask readWriteExecuteTask = new ReadWriteExecuteTask(socketChannel);
		work.submit(readWriteExecuteTask);
	}
}
