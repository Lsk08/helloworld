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
        * 略
    5. bean加载
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
    6. 容器的扩展功能 ApplicationContext
    7. AOP
