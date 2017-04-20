package com.net.nio;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.net.channel.Channel;
import com.net.channel.ChannelFuture;
import com.net.channel.ChannelPipeLine;

public class NioClientSocketChannel implements Channel{
	
	private SocketChannel clientSocketChannel;
	
	public NioClientSocketChannel() throws IOException{
		clientSocketChannel=SocketChannel.open();
	}

	public ChannelFuture wirte(Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	public ChannelFuture close() {
		// TODO Auto-generated method stub
		return null;
	}

	public SocketChannel getClientSocketChannel() {
		return clientSocketChannel;
	}

	public void setClientSocketChannel(SocketChannel clientSocketChannel) {
		this.clientSocketChannel = clientSocketChannel;
	}

	public void setChannelPipeLine(ChannelPipeLine channelPipeLine) {
		
	}

	public ChannelPipeLine getChannelPipeLine() {
		return null;
	}

}
