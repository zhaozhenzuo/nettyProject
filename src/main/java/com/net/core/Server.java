package com.net.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
	
	public static void main(String[] args) throws IOException {
		startServer(8083);
	}
	
	public static void startServer(int port) throws IOException{
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(port));

        Selector selector = Selector.open();    // 打开selector
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int readyChannels = selector.select();  // 阻塞直到有IO事件就绪
            if (readyChannels <= 0) {
                continue;
            }

            Set<SelectionKey> selectorKeySet = selector.selectedKeys(); // 得到就绪的选择键
            Iterator<SelectionKey> iterator = selectorKeySet.iterator();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (iterator.hasNext()) {    // 遍历选择键
                SelectionKey key = iterator.next(); 
                if (key.isAcceptable()) {   // 处理accept事件
                    SocketChannel socketChannel = serverChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {  // 处理read事件
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    buffer.clear();
                    socketChannel.read(buffer);
                    buffer.flip();
                    socketChannel.write(buffer);
                    
                    System.out.println("read");
                }
                iterator.remove();
            }
            
            selectorKeySet.clear();
        }
    }

}
