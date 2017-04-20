package com.net.mem;

import java.nio.ByteBuffer;

import com.net.constants.MemUseFlag;

/**
 * 内存管理chunk<br/>
 * 一个chunk =16M,由2048个page组成<br/>
 * 一个page有8K<br/>
 * 具体根据内存块需求，分成512字节的tinyMem组，及256的smallMem组<br/>
 * 内存中会维护一个二叉树，记录相应page的使用情况，叶子结点是page结点，非叶子结点记录子结点使用情况<br/>
 * eg:申请内存为8M，则定位到8M这层，看这两个结点是否有空闲空间<br/>
 * 
 * @author zhaozhenzuo
 *
 */
public class PoolChunk {

	/**
	 * 底层实际存储内存
	 */
	private ByteBuffer mem;

	/**
	 * 页大小
	 */
	private int pageSize;

	/**
	 * chunk大小
	 */
	private int chunkSize;

	private int pageNums;

	/**
	 * 用数组实现二叉树，记录每个结点的相应内存使用情况<br/>
	 */
	private byte[] memBitArr;

	/**
	 * 叶子结点深度
	 */
	private int leaveDeep;

	private int max2Mi;

	/**
	 * 释放对应标记
	 * 
	 * @param idx
	 */
	public void free(int idx) {
		memBitArr[idx] = MemUseFlag.FRESH;
		this.updateParentToPartUsed(idx);
	}

	/**
	 * 根据idx获取到对应物理内存相对地址
	 * 
	 * @param idx
	 */
	public int getOffset(int idx) {
		int deep = log2(idx);
		int firstNodeIdx = getFirstNodeIndexForDeep(deep);

		// 当前层，内存单位
		int memLength = 1 << (max2Mi - deep);

		int offset = (idx - firstNodeIdx) * memLength;
		return offset;
	}

	public static void main(String[] args) {
		PoolChunk poolChunk = new PoolChunk(twoMi(24), twoMi(13));

		int res = poolChunk.allocate(twoMi(13));
		System.out.println(res);

		int res2 = poolChunk.allocate(twoMi(13));
		System.out.println(res2);

		int offset = poolChunk.getOffset(res2);
		System.out.println("offset:" + offset);
	}

	private static int twoMi(int x) {
		return (int) Math.pow(2, x);
	}

	public PoolChunk(int chnunkSize, int pageSize) {
		this.chunkSize = chnunkSize;
		this.pageSize = pageSize;

		pageNums = chnunkSize / pageSize;

		// 初始化内存占用二叉数组
		this.initMemBitArr();

		// 这里向操作系统申请直接内存
		mem = ByteBuffer.allocate(chnunkSize);
	}

	/**
	 * 分配一个page
	 * 
	 * @param reqSize
	 * @return 当前memBitArr的索引位置
	 */
	public int allocate(int reqSize) {
		/**
		 * 1.映射到当前需要分配在哪一层
		 */
		int deepToSearch = max2Mi - log2(reqSize);

		/**
		 * 找到一个未分配的结点
		 */
		int idx = -1;
		int deepIdx = this.getFirstNodeIndexForDeep(deepToSearch);
		int nextDeepIdx = this.getFirstNodeIndexForDeep(deepToSearch + 1);
		boolean searchFlag = false;
		idx = deepIdx;
		while (idx < nextDeepIdx) {
			if (memBitArr[idx] == MemUseFlag.FRESH) {
				searchFlag = true;
				memBitArr[idx] = MemUseFlag.ALL_USED;
				break;
			}
			idx = idx + 1;
		}

		if (!searchFlag) {
			return -1;
		}

		// 设置父结点为部分分配
		this.updateParentToPartUsed(idx);
		return idx;
	}

	/**
	 * 设置所有父结点为部分分配
	 * 
	 * @param idx
	 */
	private void updateParentToPartUsed(int idx) {
		int parent;
		while (idx > 1) {
			parent = idx >>> 1;
			int brotherNodeIdx = idx ^ 1;
			int brotherNodeValue = value(brotherNodeIdx);

			int idxNodeValue = value(idx);

			if (idxNodeValue == MemUseFlag.ALL_USED && brotherNodeValue == MemUseFlag.ALL_USED) {
				memBitArr[parent] = MemUseFlag.ALL_USED;
			} else if (idxNodeValue == MemUseFlag.FRESH && brotherNodeValue == MemUseFlag.FRESH) {
				memBitArr[parent] = MemUseFlag.FRESH;
			} else {
				memBitArr[parent] = MemUseFlag.PART_USED;
			}

			idx = parent;
		}

	}

	public int value(int idx) {
		return memBitArr[idx];
	}

	/**
	 * 获取到当前层次第一个结点的index
	 * 
	 * @param deep
	 * @return
	 */
	private int getFirstNodeIndexForDeep(int deep) {
		return 1 << deep;
	}

	private int log2(int reqSize) {
		int res = -1;
		int temp = reqSize;
		while (temp != 0) {
			temp = temp >>> 1;
			res++;
		}

		return res;
	}

	private void initMemBitArr() {
		leaveDeep = -1;

		int temp = pageNums;
		while (temp != 0) {
			temp = temp >>> 1;
			leaveDeep++;
		}

		max2Mi = -1;
		int chunkSizeTemp = chunkSize;
		while (chunkSizeTemp != 0) {
			chunkSizeTemp = chunkSizeTemp >>> 1;
			max2Mi++;
		}

		/**
		 * 整个二叉树是一个完全二叉树，结点总数为：2^(leaveDeep+1)-1
		 */
		int nodesNum = getNodeNumsByTreeDeep(leaveDeep);

		/**
		 * 这里数组初始化成结点个数加1,因为存储要从1开始
		 */
		memBitArr = new byte[nodesNum + 1];

		for (int i = 0; i < memBitArr.length; i++) {
			memBitArr[i] = MemUseFlag.FRESH;
		}
	}

	private int getNodeNumsByTreeDeep(int deep) {
		int res = twoMi(deep + 1) - 1;
		return res;
	}

	public ByteBuffer getMem() {
		return mem;
	}

	public String toString() {
		StringBuilder buffer = new StringBuilder(memBitArr.length);
		for (byte b : memBitArr) {
			buffer.append(b);
		}
		return buffer.toString();
	}

}
