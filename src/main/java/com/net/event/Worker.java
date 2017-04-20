package com.net.event;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.net.util.SocketUtil;

public class Worker implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(Worker.class);

	// reactor selector
	private Selector selector;

	private Thread thread;

	private LinkedBlockingQueue<RegisterTask> registerTaskQueue;

	private LinkedBlockingQueue<WriteTask> writeTaskQueue;

	public Worker() throws IOException {
		selector = Selector.open();

		// TODO，大小
		writeTaskQueue = new LinkedBlockingQueue<Worker.WriteTask>();
		registerTaskQueue = new LinkedBlockingQueue<Worker.RegisterTask>();
	}

	public void register(SocketChannel clientSocketChannel) throws IOException {
		RegisterTask registerTask = new RegisterTask(clientSocketChannel);
		registerTaskQueue.add(registerTask);
	}

	class RegisterTask implements Runnable {

		SocketChannel clientSocketChannel;

		public RegisterTask(SocketChannel clientSocketChannel) {
			this.clientSocketChannel = clientSocketChannel;
		}

		public void run() {
			try {
				clientSocketChannel.configureBlocking(false);
				clientSocketChannel.register(selector, SelectionKey.OP_READ, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void processRegister() {
		for (;;) {
			RegisterTask registerTask = registerTaskQueue.poll();
			if (registerTask == null) {
				break;
			}
			registerTask.run();
		}

	}

	public void run() {
		thread = Thread.currentThread();

		while (true) {
			int cnt;
			try {
				cnt = selector.select(500);

				// 处理注册消息
				this.processRegister();

				// 下面处理io读写
				if (cnt <= 0) {
					continue;
				}

				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectionKeys.iterator();

				while (iterator.hasNext()) {
					SelectionKey selectionKey = iterator.next();
					iterator.remove();
					if (selectionKey.isReadable()) {
						// read event
						processRead(selectionKey);
					} else {
						throw new IllegalArgumentException("key类型不支持");
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	class WriteTask implements Runnable {

		public void run() {

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

}
