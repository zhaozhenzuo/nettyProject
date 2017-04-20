package com.net.nio;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

import com.net.channel.Channel;
import com.net.channel.ChannelFuture;
import com.net.channel.ChannelPipeLine;

public class NioServerSocketChannel implements Channel {

	private ServerSocketChannel serverSocketChannel;

	private ChannelPipeLine channelPipeLine;

	public NioServerSocketChannel() throws IOException {
		serverSocketChannel = ServerSocketChannel.open();
	}

	public ChannelFuture wirte(Object obj) {
		return null;
	}

	public ChannelFuture close() {
		return null;
	}

	public ServerSocketChannel getServerSocketChannel() {
		return serverSocketChannel;
	}

	public void setServerSocketChannel(ServerSocketChannel serverSocketChannel) {
		this.serverSocketChannel = serverSocketChannel;
	}

	public void setChannelPipeLine(ChannelPipeLine channelPipeLine) {
		this.channelPipeLine = channelPipeLine;
	}

	public ChannelPipeLine getChannelPipeLine() {
		return channelPipeLine;
	}

}
