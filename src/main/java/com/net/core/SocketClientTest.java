package com.net.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketClientTest {

	private static final Logger logger = LoggerFactory.getLogger(SocketClientTest.class);

	public static void main(String[] args) throws IOException {

		SocketChannel socketChannel = SocketChannel.open();

		InetSocketAddress remote = new InetSocketAddress("localhost", 8083);

		boolean connectRes = socketChannel.connect(remote);

		if (!connectRes) {
			logger.error(">error connect");
			return;
		}

		write(socketChannel);
		
		System.out.println("finish");

	}

	private static void write(SocketChannel socketChannel) throws IOException {
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

		String content = "hi";

		byteBuffer.put(content.getBytes());

		socketChannel.write(byteBuffer);
	}

}
