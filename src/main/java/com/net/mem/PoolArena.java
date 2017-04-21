package com.net.mem;

/**
 * 内存块分配器<br/>
 * 主要职责:<br/>
 * 优先从threadLocal分配缓存,没有则从全局内存池获取一块缓存<br/>
 * 这个poolArena每个线程独有
 * 
 * @author zhaozhenzuo
 *
 */
public class PoolArena {

	private int pageSize;

	private int chunkSize;

	private Thread currentThread;

	// 默认chunk大小：16M
	private static final int DEFAULT_CHUNK_SIZE = (int) Math.pow(2, 24);

	// 默认page大小：8Ｋ
	private static final int DEFAULT_PAGE_SIZE = (int) Math.pow(2, 13);

	private MemCache memCache;

	public PoolArena(int chunkSize, int pageSize) {
		this.pageSize = pageSize;
		this.chunkSize = chunkSize;
		currentThread = Thread.currentThread();
	}

	public PoolBuf allocateDirect(int reqSize) {
		/**
		 * 1.将请求大小，转成2的幂次方
		 */
		int normalSize = this.reqSizeToNormalSize(reqSize);

		/**
		 * 2.分配内存
		 */
		PoolBuf res = null;
		if (this.isTiny(normalSize)) {
			if (memCache != null) {
				/**
				 * 有线程缓存，从线程缓存中分配一个
				 */
				res = this.allocateFromThreadCacheDirect(memCache, normalSize);
			}

			/**
			 * 如果没有，就先创建一个 TODO
			 */
			if (res == null) {
				this.initMemCache();
				res = this.allocateFromThreadCacheDirect(memCache, normalSize);
			}

		} else {
			/**
			 * 大于一个page，则从共享内存中获取
			 */
		}

		return res;

	}

	private void initMemCache() {
		memCache = new MemCache();

		/**
		 * 1.当前线程分配一个chunk数组，并初始化，先初始化1个，之后优化<br/>
		 * TODO
		 */
		PoolChunk[] poolChunks = new PoolChunk[1];

		for (int i = 0; i < poolChunks.length; i++) {
			poolChunks[i] = new PoolChunk(chunkSize, pageSize);
		}

		memCache.setPoolChunks(poolChunks);
	}

	/**
	 * 小于等于一个page的是tiny，优先从threadCache分配
	 * 
	 * @param normalSize
	 * @return
	 */
	private boolean isTiny(int normalSize) {
		return normalSize <= pageSize;
	}

	private PoolBuf allocateFromThreadCacheDirect(MemCache memCache, int normalSize) {
		if (memCache == null) {
			return null;
		}

		PoolChunk[] poolChunks = memCache.getPoolChunks();
		if (poolChunks == null || poolChunks.length <= 0) {
			return null;
		}

		PoolBuf res = null;

		for (PoolChunk poolChunk : poolChunks) {
			int idx = poolChunk.allocate(normalSize);
			if (idx > 0) {
				res = this.composePoolBuf(poolChunk, idx, normalSize);
				break;
			}
		}

		return res;

	}

	private PoolBuf composePoolBuf(PoolChunk chunk, int idx, int normalSize) {
		PoolBuf poolBuf = new PoolDirectBuf();
		int offset = chunk.getOffset(idx);
		poolBuf.init(chunk, offset, normalSize, idx);
		return poolBuf;
	}

	private int reqSizeToNormalSize(int reqSize) {
		int res = 1;

		while (res < reqSize) {
			res = res << 1;
		}

		return res;
	}

	public static void main(String[] args) {
		PoolArena poolArena = new PoolArena(DEFAULT_CHUNK_SIZE, DEFAULT_PAGE_SIZE);
		PoolBuf poolBuf = poolArena.allocateDirect(8192);

		poolBuf.writeBytesToBuf("aa".getBytes());

		System.out.println(poolBuf.readBytesAsString());

		poolBuf.free();

		PoolBuf poolBuf2 = poolArena.allocateDirect(8192);
		poolBuf2.writeBytesToBuf("ss".getBytes());

		System.out.println(poolBuf2.readBytesAsString());

	}

}
