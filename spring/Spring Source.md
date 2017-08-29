### Spring 源码解析 笔记
* Spring整体框架
    1. Core Container = Beans + Core + Context + Expression Language 【Ioc/DI】
        * Core Spring基本的工具类
        * Beans Spring的核心部分，提供Ioc/DI的实现。通过工厂模式，实现了单例和配置与业务逻辑的解耦。(BeanFactory)
        * Context 框架上下文，提供框架式的对象访问 ApplicationContext
        * Expression Language EL表达式
    2. Data Access/Integration = JDBC + ORM + OXM + JMS + Transaction  提供数据访问工具 【DataBase】
        * 封装JDBC
        * ORM 提供对象关系映射 myBatis
        * OXM 对象Xml映射 XmlBeans
        * JMS Java消息管理
        * Transaction 声明式事务
    3. Web = Web + Web-Servlet + Web-Struts + Web-Porlet 基于Web的上下文 【MVC】
        * Web 在Context之上，基于Web的上下文，并且提供Web服务的基础
        * Web-XXX 提供对应的集成，包括servlet和mvc的支持
    4. AOP 支持面向切面编程和声明式事务的支持 【AOP】
    5. Test 测试模块，支持JUnit的使用 【TEST】
    
* Core Container 的基本实现
    1. 基本思路是需要【2】个部分 ConfigReader + ReflectionUtil
        * ConfigReader 负责验证、读取配置文件并保存到内存
        * ReflectionUtil 通过反射得到对象
        
    2. 【2】个最核心的类 DefaultListableBeanFactory XmlBeanDefinitionReader 
        * DefaultListableBeanFactory 是【bean加载】的核心部分，也就是spring对于【bean的注册和获取】的默认实现。
        * XmlBeanFactory 是DefaultListableBeanFactory的子类，主要增加了通过 【XmlBeanDefinitionReader】从xml中读取【BeanDefinition】的功能
            * 对于XML进行【SAX】解析
            
    3. 容器的基础 XmlBeanFactory 对xml进行SAX解析 + 注册并管理beans
        * xml的解析 配置文件的封装 + 加载bean，而 加载bean又分为 XML验证、获取Document、解析并注册BeanDefinitions
            * 配置文件的封装 Resource 封装了各种协议，例如 classpath，file，返回inputstream
            * 通过上一步中的resource，调用this.reader.read(resource)，进入加载bean阶段
            * 加载bean 使用EncodedResource编码，调用doLoadBeanDefinition(inputSource,resource)进行加载
            * doLoadBeanDefinition 又分为 获取验证模式、加载XML并获取Document(SAX)、解析注册bean信息
                * XML的验证模式分为 DTD和XSD，区别为在是否含有 DOCTYPE 标签
                * 加载XML为标准的SAX解析
                * 注册bean信息分为 parseDefaultElement、delegate.parseCustomElement 也就是默认标签的解析和自定义标签的解析
        
    4. 默认标签的解析 + 自定义标签的解析
        * Xml文件被最终解析为接口BeanDefinition的实现子类，也就是`<bean>`对应着一个BeanDefinition，并且`<bean>`的每一个属性都对应着其中的一个字段
        * BeanDefinition的实现类有RootBeanDefinition、ChildBeanDefinition和GenericBeanDefinition分别代表 `<bean>` 的父子类关系和通用的BeanDefinition
        * 解析之后的注册也就是把BeanDefinition注册到BeanDefinitionRegistry中
        * BeanDefinitionRegistry包含一个Map，用于【配置信息】的读取，这里的BeanDefinition是xml配置文件的对象化，不是bean的实例
        * todo        
        
    5. bean加载 todo BeanWrapper 
        * 从BeanFactory加载bean
            1. 转换beanName transformedBeanName(name)
                * 例如，别名匹配
            2. 尝试从缓存加载单例
                *  单例在Spring中只会被创建一次，而之后都从缓存中取得，而为了避免循环依赖，Spring允许
                   Bean在被完全加载之前就放入缓存，于是就出现了缓存的分级 singletonObjects、singletonFactories、earlySingletonObjects
            3. bean的实例化
                * 关于FactoryBean 当某个class实现了FactoryBean接口之后，getBean返回的是FactoryBean.getObject()，
                  也就是用FactoryBean.getObject代理了BeanFactory.getBean，目的在于封装get/set方法，增加配置的灵活性
                * 在getBean中，如果不是从缓存中加载，最后就一定会由getObjectForBeanInstance()加载
                * getObjectForBeanInstance() 如果是bean、或者要求返回Factory就直接返回，否则就是用FactoryBean.getObject()返回对应的Bean实例
            4. 原型模式的依赖检查
                * 单例模式可以通过缓存来提前暴露没有完全初始化的对象来解决，但是原型模式没有缓存，所以无法解决循环依赖
                只能抛出 BeanCurrentlyInCreationException
            5. 尝试从parentFactory加载
            6. 把GenericBeanDefinition转化成RootBeanDefinition
            7. 依赖加载
            8. 根据不同的scope创建bean，例如 singleton、prototype、request之类
                * 获取单例getSingleton(String beanName,ObjectFactory singletonFactory)
                判断 + 前后置方法并记录状态 + 加载bean + 返回
                    1. 检验是否加载过
                    2. 如果没有，记录beanName的加载状态
                    3. 单例加载前记录状态 beforeSingletonCreation(String beanName)
                    4. 通过FactoryBean.getObject()得到对象，也就是createBean(beanBeam，mbd，args)
                        1. 解析class类
                        2. 加载OverrideMethod，对应于配置里的 lookup-method和replace-method
                            * 动态代理，生成动态代理类并替换对应方法
                        3. 调用 BeanPostProcessor 覆盖加载Bean，如果成功就直接返回
                        4. 创建bean doCreateBean()
                            1. 如果是单例就清除缓存
                            2. 构造BeanWrapper，调用对应的工厂方法、构造法
                                * 工厂方法比较简单，委托给BeanFactory即可
                                * 构造法 分为有参和无参数
                                    1. 有参数 todo
                                    2. 无参数 todo
                            3. MergedBeanDefinitionPostProcessor，例如Autowired注入
                            4. 依赖处理，这里只是把bean放入缓存，可以看出此时bean并没有加载完成
                            5. 属性注入
                                * todo
                            6. 依赖检查，根据(4)的缓存做依赖检查
                            7. 注册 DisposableBean，例如destroy-method的调用
                                * 类比于 init-method，实例销毁也会调用对应的注册方法
                            8. 完成创建并返回
                    5. 单例加载之后调用 afterSingletonCreation(String beanName)，移除正在加载的记录
                    6. 加入缓存(结果缓存)，清除加载过程的记录
                    7. 返回结果
            9. 根据参数进行转化，例如getBean(name,Type)，就会根据type验证并且转化成对应的类型，否则默认为Object
            
    6. 容器的扩展功能 ApplicationContext spring中BeanFactory接口中的getBean负责获得Bean，而ApplicationContext实现了这个接口
    并且增加了一些其他的功能    
        * 设置配置路径 解析路径，并保存
            1. setConfigLocations(String[] locations):支持以数组的形式传入多个locations
            2. resolvePath(locations[i]):循环取得每一个location并解析
            3. getEnvironment().resolveRequiredPlaceholders(path): 通过Environment中的属性解析path，例如${}中的变量
            
        * 扩展功能  通过obtainFreshBeanFactory 得到一个XmlBeanFactory作为xml解析和bean获取的实现
        此外增加了许多功能，例如配置环境变量，调用子类模板，激活各种处理器，包括beanProcessor，MessageSource，Listener等等       
            1. prepareRefresh(); 调用了 getEnvironment().validateRequiredProperties()，即验证必要的环境变量 
            2. obtainFreshBeanFactory() 初始化BeanFactory，并读取Xml获得BeanDefinition，注册bean到这个beanFactory，也就是这个ApplicationContext有BeanFactory的所有功能
            3. prepareBeanFactory(beanFactory) 为beanFactory配置标准的上下文，例如ClassLoader，post-processor
            4. postProcessBeanFactory(beanFactory); 子类的覆盖方法
            5. 激活各种处理器
                * invokeBeanFactoryPostProcessors(beanFactory);激活以bean形式注册的FactoryProcessor
                * registerBeanPostProcessors(beanFactory); 注册beanProcessor
                * initMessageSource();初始化消息源，即国际化处理
                * initApplicationEventMulticaster();初始化消息广播
                * onRefresh(); 子类的模板类
                * registerListeners();注册所有的Listener Bean
                * finishBeanFactoryInitialization(beanFactory); 初始化剩下的单例(非惰性)
                * finishRefresh(); 完成刷新过程，通知lifeCycle管理器
                
        * 环境准备 prepareRefresh();使用 todo
        
        * 加载 customizeBeanFactory 设置@Qualifier和@Autowired注解解析器 + loadBeanDefinitions 初始化DocumentReader，解析Xml并注册BeanFactory
            1. 定制BeanFactory 
                * customizeBeanFactory(beanFactory): todo
            2. 加载BeanDefinition
                * DefaultListableBeanFactory factory=obtainFreshBeanFactory(); XmlBeanFactory就是这个DefaultListableBeanFactory的子类也就是把
                解析xml到BeanDefinition到注册Bean完全交给XmlBeanFactory处理，也就是之前的解析标签和bean加载
            
        * 功能扩展
            1. 支持SpEL表达式 beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver()) 
                * Spring使用#{}当做SpEL的表达符 todo
            2. 属性注册编辑器 beanFactory.setPropertyEditorRegistrar(new ResourceEditorRegistrar(this,getEnvironment()))
                * 自定义的类型注入，例如通过String注入Date todo
            3. 添加AwareProcessor处理器 beanFactory.setBeanPostProcessor(new ApplicationContextAwareProcessor(this));
                * Spring在激活bean的init-method之后，会调用BeanPostProcessor的postProcess[Before/After]Initialization()
                这就是通过ApplicationContextAwareProcess注册的
                * 配置一些资源，例如Environment、ResourceLoader等等，可以看出是一些环境变量，特点实现类Aware接口
            4. 设置忽略依赖 beanFactory.ignoreDependencyInterface()
                * 忽略上一步中加载的环境配置
                * ResourceLoaderAware.class、MessageSourceAware.class、ApplicationContextAware.class、EnvironmentAware.class、EventPublishAware.class
                * Aware接口是一个标记接口，表示这是一个特殊的类型，在这里就是忽略依赖关系
            5. 注册依赖 beanFactory.registerResolvableDependency()
                * 对应于忽略依赖的绑定依赖关系
            6. 增加对AspectJ支持
            7. 添加系统环境
            
        * BeanFactory的后期处理 也就是之前先进行环境的配置，然后调用beanFactoryPostProcessor预处理，然后才是beanFactoryInitialization正式加载实例
        即通过BeanFactoryPostProcessor可以在bean加载之前，读取配置元数据信息，并且修改，也就是优先加载某些数据用于之后的对象实例化，
        例如PropertyPlaceholderConfigure
            1. 激活BeanFactoryPostProcessor invokeBeanFactoryPostProcessor(beanfactory)
                * todo
            2. 注册BeanPostProcessor registerBeanPostProcessor(beanfactory)
                * todo
            3. 初始化消息资源 initMessageSource()
                * MessageSource接口，处理国际化问题
            4. 初始化 ApplicationEventMulticaster initApplicationEventMulticaster()
                * 初始化 applicationEvent，默认使用SimpleApplicationEventMulticaster，观察者模式
                * 循环遍历所有的listener，并推送Event，Listener.onApplicationEvent(event); 也就是对应了Multicaster
            5. 子类模板 onRefresh()
            6. 注册监听器 registerListeners()
                * 分别处理了代码方式的listener 
                    * For 循环 + getApplicationEventMulticaster().addApplicationListener(listener);
                * 配置文件的listener 
                    * For 循环 + getApplicationEventMulticaster().addApplicationListenerBean(listName);
            
        * 初始化非延迟加载单例 finishBeanFactoryInitialization(beanFactory);
            1. ConversionService设置 类型转化
            2. 冻结配置 冻结bean的定义，不可以再修改或者进一步处理 this.frozenBeanDefinitionNames = StringUtils.toStringArray(this.beanDefinitionNames);
            3. 初始化非延迟加载 Spring默认在启动时对所有的单例提前实例化，创建并且配置所有的单例。 
            配置实例，`<bean id="lazy" class="com.foo.ExpensiveToCreateBean" lazy-init="true"></bean>`
        * finishRefresh() 使用LifeCycle接口的start/stop来统一控制后台程序的启动/停止
            1. 初始化 initLifeCycleProcessor()
            2. 启动 onRefresh()
            3. 发布Event通知Listener publishEvent(Event e)
        
    7. AOP
