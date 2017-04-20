package com.net.event;

import java.net.SocketAddress;

/**
 * 输入事件,eg:读事件
 * 
 * @author zhaozhenzuo
 *
 */
public interface InboundEvent extends Event {

	/**
	 * bind时的地址
	 * 
	 * @return
	 */
	SocketAddress socketAddress();

}
