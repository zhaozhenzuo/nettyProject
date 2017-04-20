package com.net.mem;

import java.nio.ByteBuffer;

/**
 * 缓存操作内存块抽象类<br/>
 * 内部会有一个实际存储块：可以是heap也可以是direct<br/>
 * 这个类的职责：<br/>
 * 封装对于缓存块的操作，因为分配内存时会分配一个大块，然后对于每个请求只会用到这个大块的一部分<br/>
 * 这个类就会维护当前可操作的这个大块的区域地址
 * 
 * @author zhaozhenzuo
 *
 */
public abstract class AbstractPoolBuf implements PoolBuf {

	/**
	 * 底层实际物理内存
	 */
	private PoolChunk chunk;

	/**
	 * 对应哪个bitMem数组下标
	 */
	private int idx;

	/**
	 * 实际物理位置开始位置
	 */
	private int offset;

	/**
	 * 能够读取的长度
	 */
	private int length;

	private ByteBuffer tmpBuffer;

	public void free() {
		// 让chunk先恢复当前位置的内存占用标记，及父结点祖先等
		chunk.free(idx);
		
		System.out.println(chunk.toString());

		// 回置各个状态标记
		idx = 0;
		offset = -1;
		length = -1;
		tmpBuffer = null;
	}

	public void init(PoolChunk chunk, int offset, int length, int idx) {
		this.chunk = chunk;
		this.offset = offset;
		this.length = length;
		tmpBuffer = chunk.getMem();
		this.idx = idx;
	}

	public void writeBytesToBuf(byte[] bytes) {
		if (bytes == null || bytes.length <= 0) {
			return;
		}

		tmpBuffer.clear().position(offset).limit(offset + length);
		tmpBuffer.put(bytes);
	}

	public void resetPositionAndLimit() {
		tmpBuffer.position(offset).limit(offset + length);
	}

	public String readBytesAsString() {
		int readLength = tmpBuffer.position() - offset;

		// 开始读取，需要重置postion及limit
		resetPositionAndLimit();

		byte[] dst = new byte[readLength];
		tmpBuffer.get(dst);
		return new String(dst);
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public long getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

}
