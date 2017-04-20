package com.net.constants;

/**
 * 内存使用情况
 * 
 * @author zhaozhenzuo
 *
 */
public interface MemUseFlag {

	/**
	 * 未分配过
	 */
	public static final byte FRESH = 0;

	/**
	 * 部分分配
	 */
	public static final byte PART_USED = 1;

	/**
	 * 全部已分配
	 */
	public static final byte ALL_USED = 2;

}
