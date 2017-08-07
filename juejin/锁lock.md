### 锁 java.util.concurrent.locks
* 主要的接口 Lock (ReentrantLock)、ReadWriteLock(ReentrantReadWriteLock) (另有一个Condition)
* 接口Lock:  
`public interface Lock{  `   
`void lock();`  
`void lockInterruptibly() throws InterruptedException;`  
`boolean tryLock();`  
`boolean tryLock(long time, TimeUnit unit) throws InterruptedException;`  
`void unlock();`  
`Condition newCondition();`  
`}`
    * lock/unlock 普通的阻塞式获取锁
    * lockInterruptibly 可以被中断 抛出 InterruptedException
    * tryLock 非阻塞式获得锁 失败就立即返回
    * newCondition 绑定锁条件
* 实现原理 CAS +　LockSupport
* LockSupport 的基本方法有 park/unpark(Thread t)
    * park CPU放弃当前线程的调度，使当前线程进入WAITING状态 底层是Unsafe.park();
    * unpark(t) 让指定线程恢复调度
        * park区别于yield是当前线程不可以在被调用，除非unpark 而yield是可以再次被调度
* AQS java为ReentranceLock、ReentrantReadWriteLock、Semaphore、CountDownLatch 提供的并发工具抽象类
AbstractQueuedSynchronizer
    * AQS　封装了一个state状态  
        * int,表示当前lock被持有的次数 
        * 利用CAS操作计数
        * 每次执行lock.lock/unlock时 +1/-1  
    * AQS 封装了当前线程        
        * 表示持有这个lock的线程
        * CAS操作比较该线程是否是持有线程 决定是将 state+1 还是 加入等待队列
    * AQS 封装了一个等待队列    
        * 维护阻塞等待的线程
        * fair和unfair分别维护
        
 * 公平性一般默认false 原因 公平锁会让等待时间最久的线程执行 而 引发许多线程上下文的切换
 
 ### Lock和Sync: 
 * Lock不仅包括sync的所有功能 还可以实现非阻塞锁、中断响应、定时等功能
 * 但是sync代码简单 是一种声明式编程 一切由jvm处理 而 lock是命令式编程 需要使用者熟练掌握所有细节
 * 于性能，在较新版本的JVM上，ReentrantLock和synchronized的性能是接近的，但Java编译器和虚拟机可以
    不断优化synchronized的实现，比如，自动分析synchronized的使用，对于没有锁竞争的场景，自动省略对锁获取/释放的调用。
 ### 所以一般能用sync就不要用lock
