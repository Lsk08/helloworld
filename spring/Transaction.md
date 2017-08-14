### Spring 事务
* 事务的4个性质 ACID
    * 原子性A 事务操作【过程】作为一个原子操作
    * 一致性C 事务的【结果】必须保持一致性 不能只得到一部分结果 破坏现有数据
    * 隔离性I 多个事务同时执行 不能相互干扰 类似于多线程
    * 持久性D 事务结果的持久化 也就是把结果写入数据库
 
* Sping事务的本质是
    * 数据库对于事务的支持  也就是spring本身并不提供事务，只提供接口，底层调用数据库来支持 例如JDBC
    
* @Transaction
   * JDBC事务的过程
       1. 获取连接 Connection con = DriverManager.getConnection()
       2. 设置事务 con.setAutoCommit(true/false);
       3. CRUD
       4. 提交、回滚 con.submit/con.rollback
       5. 关闭连接
    
   * @Transaction 可以省略 2 4 ， 也就是利用注解自动设置事务和提交、回滚
   * 数据库底层通过 数据库日志 binlog或者redo log实现的。
   
* Spring事务框架
    * PlatformTransactionManager  也就是spring为各个平台提供了统一的接口、具体由每个平台实现 例如JDBC、JTA、Hibernate
        >Public interface PlatformTransactionManager()...{  
             // 由TransactionDefinition得到TransactionStatus对象  
             TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException; 
             // 提交  
             Void commit(TransactionStatus status) throws TransactionException;  
             // 回滚  
             Void rollback(TransactionStatus status) throws TransactionException;  
             }
    
    * JDBC 的事务  
        `<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
            <property name="dataSource" ref="dataSource" />
        </bean>`
    * DataSourceTransactionManager 调用 java.sql.Connection.commit/rollback 完成任务
    * TransactionDefinition  事务定义的【5】个级别 事务传播、事务隔离、超时、只读、回滚规则
        >public interface TransactionDefinition {  
             int getPropagationBehavior(); //返回事务的传播行为  
             int&nbsp;getIsolationLevel();//返回事务的隔离级别，事务管理器根据它来控制另外一个事务可以看到本事务内的哪些数据  
             int getTimeout();  // 返回事务必须在多少秒内完成
             boolean isReadOnly();//事务是否只读，事务管理器能够根据这个返回值进行优化，确保事务是只读的
         } 
         
        * 事务的传播行为 当方法被另一个事务调用是，指定事务如何传播
            * Required 如果被另一个事务调用，则正常执行，否则新建一个任务
            * Supported 支持事务 如果被另一个事务调用，则支持事务，否则非事务执行
            * Mandatory 必须被另一个事务调用 否则抛出 IllegalTransactionStateException 异常
            * Required_new 总是开启一个新的事务。如果事务已存在，当前事务会挂起 需要jta支持
            * 略
            
        * 事务的隔离程度 多个事务并发执行时的相互影响 也就是事务自动隔离非事务，但是事务之间由于有传播行为导致没有隔离
            * 问题 脏读 幻读 不可重复读 本质是事务的传播
                * 脏读 一个事务读取了另一个事务修改但是没有提交的数据，之后又被回滚，那么这个数据就是无效的 
                * 不可重复读 一个事务多次读取的数据不一致，另一个事务可能在这之间修改了数据 
                * 幻读 一个事务读取了一些数据 另一个事务插入了新数据 随后事务1发现又多了一些数据
                    * 不可重复读 和 幻读 的发生情景相似 但是范围不同 也就是 不可重复读是针对一条数据的 而 幻读是一些数据
                    
            * 隔离级别
                * ISOLATION_READ_UNCOMMITTED 脏读+幻读+不可重复读
                * ISOLATION_READ_COMMITTED 阻止脏读  存在 幻读+不可重复读
                * ISOLATION_REPEATABLE_READ 阻止脏读+不可重复读 可以幻读
                * ISOLATION_SERIALIZABLE 阻止所有 但是速度较慢 通过全表锁定实现
                
        * 事务超时
            * 如果超时但是没有完成，那么就会回滚
            
        * 只读
            * 事务的第三个特性是它是否为只读事务。如果事务只对后端的数据库进行该操作，数据库可以利用事务的只读特性来进行一些特定的优化。
            
        * 回滚规则
            * 在何时回滚 默认是抛出运行时异常的时候回滚
            
    * 事务的状态
        >public interface TransactionStatus{  
             boolean isNewTransaction(); // 是否是新的事物
             boolean hasSavepoint(); // 是否有恢复点  
             void setRollbackOnly();  // 设置为只回滚  
             boolean isRollbackOnly(); // 是否为只回滚  
             boolean isCompleted; // 是否已完成  
         } 
         
* 配置 注解式
    >	&lt;!-- (事务管理) --&gt;  
     	<bean id="transactionManager"
     		class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
     		\<property name="dataSource" ref="dynamicDataSource"></property>
     	</bean>  
     	\<!-- 使用annotation定义数据库事务，这样可以在类或方法中直接使用@Transactional注解来声明事务 -->  
     	<tx:annotation-driven transaction-manager="transactionManager" />
