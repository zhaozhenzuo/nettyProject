package com.net.channel;

/**
 * 通道,低层有nioServerSocketChannel及nioClientSocketChannel实现
 * 
 * @author zhaozhenzuo
 *
 */
public interface Channel {

	ChannelFuture wirte(Object obj);

	ChannelFuture close();

	void setChannelPipeLine(ChannelPipeLine channelPipeLine);

	ChannelPipeLine getChannelPipeLine();

}
