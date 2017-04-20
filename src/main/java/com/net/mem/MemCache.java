package com.net.mem;

/**
 * 内存池缓存对象
 * 
 * @author zhaozhenzuo
 *
 */
public class MemCache {

	private PoolChunk[] poolChunks;

	public PoolChunk[] getPoolChunks() {
		return poolChunks;
	}

	public void setPoolChunks(PoolChunk[] poolChunks) {
		this.poolChunks = poolChunks;
	}

}
