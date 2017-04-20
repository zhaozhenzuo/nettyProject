package com.net.channel;

/**
 * 用于给用户设置初始化pipeLine<br/>
 * 只用于接受到连接后的处理,eg:reaad或write
 * 
 * @author zhaozhenzuo
 *
 */
public interface ChannelPipeLineFactory {

	ChannelPipeLine getPipleLine();

}
