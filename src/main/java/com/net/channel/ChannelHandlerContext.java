package com.net.channel;

import com.net.handler.Handler;

public interface ChannelHandlerContext {

	Channel channel();

	Handler handler();

	ChannelHandlerContext pre();

	ChannelHandlerContext next();

	ChannelHandlerContext fireRead(Object msg);

	void setPre(ChannelHandlerContext channelHandlerContext);

	void setNext(ChannelHandlerContext channelHandlerContext);

}
