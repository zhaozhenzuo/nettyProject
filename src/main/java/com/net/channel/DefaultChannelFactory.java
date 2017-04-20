package com.net.channel;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;

import com.net.nio.NioClientSocketChannel;
import com.net.nio.NioServerSocketChannel;

public class DefaultChannelFactory implements ChannelFactory{
	
	private Class channelClass;

	public Channel newChannel() throws IOException {
		if(channelClass.isAssignableFrom(NioServerSocketChannel.class)){
			return new NioServerSocketChannel();
		}else if(channelClass.isAssignableFrom(NioClientSocketChannel.class)){
			return new NioClientSocketChannel();
		}else{
			throw new IllegalArgumentException("unsurport");
		}
	}
	
	

}
