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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.net.util.SocketUtil;

public class ServerSocketTest {

	private static final Logger logger = LoggerFactory.getLogger(ServerSocketTest.class);

	public static void main(String[] args) {

		try {
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);

			Selector selector = Selector.open();

			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			SocketAddress socketAddress = new InetSocketAddress("localhost", 8083);
			serverSocketChannel.bind(socketAddress);

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
					} else if (selectionKey.isReadable()) {
						// read event
						processRead(selectionKey);
					}

				}

			}

		} catch (IOException e) {
			logger.error("err", e);
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
//				channel.close();
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
		SocketChannel socketChannel = serverSocketChannel.accept();
		socketChannel.configureBlocking(false);
		socketChannel.register(selector, SelectionKey.OP_READ);
	}

}
