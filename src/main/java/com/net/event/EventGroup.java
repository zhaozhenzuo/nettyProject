package com.net.event;

import java.nio.channels.Channel;

import com.net.channel.ChannelFuture;

public interface EventGroup extends EventLoop {

	/**
	 * 注册channel到具体某个eventLoop中
	 * 
	 * @param channel
	 * @return
	 */
	ChannelFuture registerChannel(Channel channel);

	EventLoop next();

}
