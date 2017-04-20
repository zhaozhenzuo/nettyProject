package com.net.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class SocketUtil {
    private static Charset charset=Charset.forName("GBK");
    
    public static final String FILE_SEPE=",";
    
    public static PrintWriter getWriter(Socket socket) throws IOException{
        PrintWriter printWriter=new PrintWriter(socket.getOutputStream(),true);
        return printWriter;
    }
    
    public static BufferedReader getReader(Socket socket) throws IOException{
        BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return br;
    }
    
    public static ByteBuffer encode(String s){
        ByteBuffer buffer=charset.encode(s);
        return buffer;
    }
    
    public static String decode(ByteBuffer byteBuffer){
        CharBuffer charBuffer=charset.decode(byteBuffer);
        return charBuffer.toString();
    }
}
