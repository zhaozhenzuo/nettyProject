netty框架实现的简化版本<br/>
V1.0.0<br/>

目前实现功能：<br/>
1.server启动boss及work线程监听客户端连接及处理读写操作<br/>
2.direct内存池<br/>
  小块内存请求，优先从threadCache分配。<br/>
  有一个大的chunk，默认大小为16M。<br/>
  chunk由n个page构成。<br/>
  
  内存分配设计跟netty类似，由一个数组构造出一棵完全二叉树。<br/>
  叶子结点对应具体page结点的内存使用情况，非叶子结点记录了对应它的所有子结点的使用情况。<br/>
  内存使用情况有三种状态：0-未分配 1-部分分配 2-已全部分配<br/>
  
  整个内存池设计涉及类：<br/>
  PoolChunk:<br/>
    一个完整的chunk类，职责是维护物理内存，以及这个chunk内page的分配逻辑。<br/>
  PoolBuf:<br/>
    用户申请内存后，得到的是这个对象。目前只有direct实现。这个对象最终读写是跟chunk对应的物理内存块交互。这个类只是维护了逻辑位置。<br/>
   
  PoolArena:<br/>
    内存分配类，职责是：<br/>
    小于等于一个page大小的内存请求优先从threadCache分配，大于一个page的从公共内存池获取（目前未实现）。<br/>
    线程独有。
  MemCache:<br/>
    线程缓存的内存对象，内存有多个chunk实例对象，每个线程独有。<br/>
    
  PoolThreadCache:<br/>
    实现poolArena线程独立存储<br/>
