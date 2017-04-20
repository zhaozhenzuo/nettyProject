package com.net.event;

import java.nio.channels.Channel;

public interface Event {

	Channel channel();

	Integer eventType();

}
