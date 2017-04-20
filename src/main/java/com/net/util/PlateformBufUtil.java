package com.net.util;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import sun.misc.Unsafe;

public class PlateformBufUtil {

	private static long ADRESS_FIELD_OFFSET;

	private static Unsafe unsafe = getUnsafe();

	static {
		Field addressField;
		try {
			addressField = Buffer.class.getDeclaredField("address");
			ADRESS_FIELD_OFFSET = unsafe.objectFieldOffset(addressField);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

	}

	public static long getBaseAddress(Buffer buffer) {
		return unsafe.getLong(buffer, ADRESS_FIELD_OFFSET);
	}

	public static void main(String[] args) {

		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);

		long baseAddress=getBaseAddress(byteBuffer);
		
		int offset = 512;
		int length = 512;
		int limit = offset + length;

		byteBuffer.clear().position(offset).limit(limit);

		byteBuffer.put("t".getBytes());

		byte[] dst = new byte[getReadLength(byteBuffer, offset)];
		initPositionAndLimitForRead(byteBuffer, offset);
		byteBuffer.get(dst);
		System.out.println(new String(dst));
		
		@SuppressWarnings("restriction")
		byte res=unsafe.getByte(baseAddress);
		byte res2=unsafe.getByte(baseAddress+offset);
		
		System.out.println((char)res2);
	}

	private static int getReadLength(ByteBuffer byteBuffer, int offset) {
		return byteBuffer.position() - offset;
	}

	private static void initPositionAndLimitForRead(ByteBuffer byteBuffer, int offset) {
		byteBuffer.limit(byteBuffer.position());
		byteBuffer.position(offset);
	}

	private static void setBufPosition(ByteBuffer byteBuffer, int position) {
		byteBuffer.position(position);
	}

	private static Unsafe getUnsafe() {
		try {
			Field f = Unsafe.class.getDeclaredField("theUnsafe"); // Internal
			f.setAccessible(true);
			Unsafe unsafe = (Unsafe) f.get(null);
			return unsafe;
		} catch (Exception e) {

		}

		return null;
	}

}
