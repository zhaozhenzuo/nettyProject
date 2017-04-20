package com.net.conf;

import com.net.channel.ChannelPipeLineFactory;

public class BootConfig {
	
	private Class socketClass;
	
	
	public Class getSocketClass() {
		return socketClass;
	}


	public void setSocketClass(Class socketClass) {
		this.socketClass = socketClass;
	}

}
