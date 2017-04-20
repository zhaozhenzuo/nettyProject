package com.net.channel;

/**
 * 默认pipeLine实现
 * 
 * @author zhaozhenzuo
 *
 */
public class DefaultChannelPipeLine implements ChannelPipeLine {

	private ChannelHandlerContext header;

	private ChannelHandlerContext tail;

	public synchronized void addLast(ChannelHandlerContext channelHandlerContext) {
		if (header == null) {
			header = tail = channelHandlerContext;
			return;
		}

		/**
		 * 有header及tail结点，需要设置
		 */
		ChannelHandlerContext pre = tail;
		pre.setNext(channelHandlerContext);

		channelHandlerContext.setPre(tail);
		tail = channelHandlerContext;
	}

}
