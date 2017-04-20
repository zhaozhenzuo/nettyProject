package com.net.channel;

import java.util.concurrent.Future;

/**
 * 定义channel异步结果
 * 
 * @author zhaozhenzuo
 *
 * @param <V>
 */
public interface ChannelFuture extends Future<Void> {

	/**
	 * 是否成功
	 * 
	 * @return
	 */
	public boolean isSuccess();

}
