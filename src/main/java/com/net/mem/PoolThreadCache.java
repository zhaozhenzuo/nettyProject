package com.net.mem;

/**
 * 每个线程独有的threadLocal<br/>
 * 用于存储相应缓存块，避免多线程竞争
 * 
 * @author zhaozhenzuo
 *
 */
public class PoolThreadCache {

	private ThreadLocal<MemCache> cacheThreadLocal = new ThreadLocal<MemCache>();

	public MemCache getFromCache() {
		return cacheThreadLocal.get();
	}

	public void putCahceForCurrentThread(MemCache memCache) {
		cacheThreadLocal.set(memCache);
	}

}
