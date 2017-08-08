### 深入理解Jvm ch3 CG
* CG的三个部分
    * 回收的对象【判断对象是否不可得】
    * 回收的时机【什么时候回收】
    * 回收的方法【如何回收】
    
* 对象不可达策略 
    * 引用计数算法 【计数器】
        * 被引用+1 否则-1 以counter 是否 等于0判断是否死亡
    * 可达性分析算法 【CG Roots】
        * 维护以一系列 "CG Roots" 为根节点的树形结构 如果从CG Roots 出发 无法到达 就是不可达对象
        
* 引用计数 和 可达性算法 本质是 对象是否被【引用】 引在【堆】和在【方法区的判断也不一样】
* Java堆中的引用 【4】种
    * 强引用 代码明确的引用 例如 Object obj=new Object();  CG 不会回收
    * 软引用 SoftReference 有用但是非必须的引用 当系统内存溢出之前，CG会将这些对象进行二次回收，如果还没有足够的内存
    才会抛出内存溢出的异常
    * 弱引用 WeakReference 非必须对象，只能生存到下次CG，也就是必须被回收
    * 虚引用 最弱的一种引用关系 是否存在对系统没有任何影响 无法获得一个对象的实例 PhantomReference
    
* 从标记不可达到回收，需要被标记【2】次才算是真正的不可到达，也就是到达弱引用一下
当标记了1次之后只能算是软引用，不一定非要回收

* Java方法区的引用
    * 方法区 也就是 JDK 中的 永久代 
    * 主要包括 废弃的常量 和 无用的类
    * 无用类的判断
        * 所有类实例已经被回收
        * 这个类的classloader已经被回收
        * 这个类的class对象没有任何引用，也就是无法通过反射访问这个类
        

* HotSpot垃圾回收的【方法论】【如何回收】
    * 垃圾回收的算法 略
    * 可达性分析 需要暂停当前所有线程【Stop The World】 快速枚举CG Roots和所有的对象引用
        * 使用 OopMap 记录对象和引用关系 CG 实际上就是扫描这个OopMap获得可达性分析
        * 维护OopMap 只在【安全带safePoint】进行 一般在方法调用、 循环跳转、 异常跳转等 以保障正常的代码执行
        * 维护OopMap2 safeRegion 安全区域

* HotSpot垃圾回收器【具体实现】【如何回收】
    * 垃圾器有很多种，需要安装具体的需求选择相应的类型
    * 针对 新生代 和 老生代 有不同的垃圾回收器
        * 新生代 Serial、Parallel New、Parallel Scavenge
        * 老生代 Serial Old、Parallel Old、CMS
        * 新型 G1
        
    * 垃圾回收器之间有特定的组合规则 比如 老生代使用 CMS ， 新生代只能是Serial 或者 Parallel New 
    
    * 各类回收器综述
        * Serial 单线程 stop the world 时间长 实现简单 CPU利用率高 适用于桌面应用
        * Parallel 多线程 stw 时间较短 可与CMS配合 ParNew + CMS 为服务器常见组合
            * Parallel Scavenge 注重吞吐量
        * CMS 并发执行 swt 时间最短、仅存于标记阶段 适用于Web应用
        * G1 优先级分区 未来新型的垃圾回收
     
    * ParNew + CMS
        * ParNew
            * 控制参数 -XX:SurvivorRatio 新生代 Eden:Survivor:Survivor
            * 采用【复制算法】 暂停所有用户线程 多线程并行垃圾回收
            
        * CMS Concurrent Mark Sweep 并发低停顿收集器
            * 停顿时间最短 适合Web应用
            * Mark Sweep 也就是基于 【标记-清除】
            * 在标记阶段stw 清除阶段和用户线程并发
                * 初始标记、并发标记、重新标记、并发清除 4个阶段 【1、3】stw 【2、4】并发执行
            * 不足
                * 对于CPU资源敏感 降低吞吐量
                * 浮动垃圾可能触发 Concurrent Mode Failure 使用默认的Serial Old 垃圾回收 反而降低性能
                * 【标记-清除】带来的碎片化需要额外处理 
                    * 碎片整理 或者 FullGC
    * G1
        * 面向服务端的垃圾收集器
        * 并行并发 尽可能缩短、消除stw
        * 分代收集 G1可以独立管理，不需要与其他收集器配合
        * 标记整理 整体采用【标记-整理】避免碎片化的
        * 可预测停顿 可以指定停顿参数
        * 相关概念
            * G1 虽然保留 新生代、老生代 但是却是按照把整个堆分为等大小的若干区域【region】
            * G1 对于每个区域计算一个价值优先级，优先回收价值最高的区域 保证手机效率
            
* 调优参数
    * UseSerialGC client模式默认 使用 Serial+Serial Old 
    * UseParNewGC ParNew+SerialOld
    * UseConcMarkSweepGC ParNew+CMS+Serial Old
    * SurvivorRatio Eden:survivor:survivor 默认 8:1:1
    * ParallelGCThreads 并行GC时内存回收的线程数
    * CMSInitialingOccupancyFraction CMS当老年代到达多少后出发GC 默认68%
    * UseCMSCompactAtFullCollection CMS完成垃圾回收后 是否进行碎片整理
    * CMSFullGCsBeforeCompaction 设置CMS经过多少次垃圾回收再启动一次fullGC
    * PrintGCDetails 打印日志参数
    
* 回收时机
    * Minor GC 新生代GC 频繁且速度快
    * Major GC 老生代GC 不频繁且速度慢 一般伴随着一次 Minor GC
    * 对象优先在 新生代分配空间 如果不够 将触发一次Minor GC 
    如果Minor GC任然不够 就会提前进入老年代
    * -XX:PretenureSizeThreshold 设置直接进入老年代的阈值 也就是避免大对象进入新生代而带来的复制消费
    * -XX:MaxTennuringThreshold 15 默认经过15次minor gc之后任然存活的对象 进入老年代
