package com.net.handler;

import com.net.channel.ChannelHandlerContext;
import com.net.event.InboundEvent;

public interface InboundHandler extends Handler {

	/**
	 * 处理读取事件
	 * 
	 * @param ctx
	 * @param inboundEvent
	 */
	public void handlerRead(ChannelHandlerContext ctx, InboundEvent inboundEvent);

}
