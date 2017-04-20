package com.net.mem;

public interface PoolBuf {

	public void init(PoolChunk chunk, int offset, int length,int idx);

	public void writeBytesToBuf(byte[] bytes);

	public void resetPositionAndLimit();

	public String readBytesAsString();

	public long getOffset();

	public void setOffset(int offset);

	public long getLength();

	public void setLength(int length);
	
	public void free();

}
