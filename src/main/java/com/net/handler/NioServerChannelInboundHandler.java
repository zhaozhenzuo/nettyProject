package com.net.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.net.channel.ChannelHandlerContext;
import com.net.event.InboundEvent;

public class NioServerChannelInboundHandler implements InboundHandler {

	private static final Logger logger = LoggerFactory.getLogger(NioServerChannelInboundHandler.class);

	public void handlerRead(ChannelHandlerContext ctx, InboundEvent inboundEvent) {
		logger.info(">handle read event,param[" + inboundEvent.toString() + "]");
	}

	public boolean canHandleRead() {
		return true;
	}

	public boolean canHandleWrite() {
		return false;
	}

}
