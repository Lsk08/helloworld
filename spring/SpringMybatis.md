### Mybatis
* 整合Spring 
    * 核心配置 SqlSessionFactoryBean = dataSource + mapperLocations 
    * 扫描Mapper.java配置 MapperScannerConfigurer = sqlSessionFactoryBeanName + basePackage
    * 事务配置配置 DataSourceTransactionManager = dataSource  transactionManager只有一个dataSource说明这只需要dataSource的支持
>  
    <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
          <property name="driverClassName" value="${driver}" />  
          <property name="url" value="${url}" />  
          <property name="username" value="${username}" />  
          <property name="password" value="${password}" />  
          <!-- 初始化连接大小 -->  
          <property name="initialSize" value="${initialSize}"></property>  
          <!-- 连接池最大数量 -->  
          <property name="maxActive" value="${maxActive}"></property>  
          <!-- 连接池最大空闲 -->  
          <property name="maxIdle" value="${maxIdle}"></property>  
          <!-- 连接池最小空闲 -->  
          <property name="minIdle" value="${minIdle}"></property>  
          <!-- 获取连接最大等待时间 -->  
          <property name="maxWait" value="${maxWait}"></property>  
      </bean> 

    <!-- spring和MyBatis完美整合，不需要mybatis的配置映射文件 -->  
     <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">  
         <property name="dataSource" ref="dataSource" />  
         <!-- 自动扫描mapping.xml文件 -->  
         <property name="mapperLocations" value="classpath:com/cn/hnust/mapping/*.xml"></property>  
     </bean>  
     <!-- DAO接口所在包名，Spring会自动查找其下的类 -->  
     <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">  
         <property name="basePackage" value="com.cn.hnust.dao" />  
         <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"></property>  
     </bean>    
     <!-- (事务管理)transaction manager, use JtaTransactionManager for global tx -->  
     <bean id="transactionManager"  
         class="org.springframework.jdbc.datasource.DataSourceTransactionManager">  
         <property name="dataSource" ref="dataSource" />  
     </bean>
     
* 源码分析 todo