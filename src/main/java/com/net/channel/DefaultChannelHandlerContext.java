package com.net.channel;

import com.net.handler.Handler;

public class DefaultChannelHandlerContext implements ChannelHandlerContext {

	private Channel channel;

	private ChannelHandlerContext pre;

	private ChannelHandlerContext next;

	private Handler handler;

	public DefaultChannelHandlerContext(Channel channel, Handler handler) {
		this.channel = channel;
		this.handler = handler;
		pre = null;
		next = null;
	}

	public Channel channel() {
		return this.channel;
	}

	public Handler handler() {
		return this.handler;
	}

	public ChannelHandlerContext pre() {
		return pre;
	}

	public ChannelHandlerContext next() {
		return next;
	}

	public ChannelHandlerContext fireRead(Object msg) {
		return null;
	}

	public void setPre(ChannelHandlerContext channelHandlerContext) {
		pre = channelHandlerContext;
	}

	public void setNext(ChannelHandlerContext channelHandlerContext) {
		next = channelHandlerContext;
	}

}
