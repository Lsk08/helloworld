### jvm类加载机制
* 把class数据从*.class文件【加载】到内存，经过【校验、准备、解析】和【初始化】，
    最终成为能够被虚拟机直接【使用】的java类型 (对应于jvm的方法区和常量池)
    
* Java的类加载是在程序运行期间执行，这样带来的特性有
    * 多态 动态绑定
    * 热部署 JSP、OSGi

### 类加载的时机
* Java在类的第一次【主动引用】才会进行类的【初始化】，当然加载、校验、解析也会在这之前完成
    * new 创建对象 或者 读取或者设置一个static【静态字段】 或者 调用【静态方法】
    * java.lang.reflect 反射调用类
    * 初始化一个类但是它的父类没有初始化的时候，优先初始化父类
    * 虚拟机启动时的执行类
    * 动态调用，如果java.lang.invoke.MethodHandler实例最后的解析结果为 REF_getStatic、 REF_putStatic、 REF_invokeStatic
    时
* 【反例】 被动引用
    * 通过子类引用父类的静态字段
    * 定义数组类型
    * 引用类中的常量 final 常量会进入常量池，与具体的类解绑
    
### 类加载的步骤
* 加载
    * 通过类的【全限定名】定义 【二进制字节流】 文件
        * 最常见的.class文件
        * 压缩包格式 war ear jar
        * 运行时生成 代理类 XX$Proxy
        * 其他来源 由Jsp生成的Class类
    * 读取文件内容加载到方法区
    * 在方法区生成一个class对象，作为这个类数据的访问入口
    * 关于数组
        * 数组本身由JVM直接创建 但是数组的引用类型任然由上述的类加载机制触发
        
* 验证 
    * 文件格式、内容、执行性方面的验证

* 准备
    * 为【类变量】分配空间 并且 赋【零值】
    * 类变量也就是static 变量 ，实例变量的分配在【实例化】进行
    * 零值 变量的赋值在初始化 常量才会在准备阶段赋值 final
    
* 解析
    * 符号引用转化为直接引用
    
* 初始化 唯一一个由程序员主导内容的阶段
    * 初始化变量和其他资源
    * 由jvm自动生成的&lt;clint&gt;()完成，包括赋值语句和静态代码块
    * &lt;clint&gt;()和构造器不同，&lt;clint&gt;()针对类，一个类只调用一次 而 构造器针对实例对象 每个实例调用一次
    * jvm也保证 &lt;clint&gt;() 会先初始化父类 和 线程安全
    
 ####注意 类的【加载】 和 【初始化】是两个不同的概念    加载只是把二进制流加载到方法区并且创建class类  初始化是在实例化时进行赋值和加载资源的操作
* 类加载器  完成类加载的工作
    * 类加载器 相当于class的【命名空间】 只要classloader不同，class一定不同
    * 双亲委派
        * BootstrapClassloader、ExtClassLoader、AppClassLoader 优先由父类加载器加载，否则再由子类加载器加载
            * BootstrapClassloader jvm的一部分由C++编写，ExtClassloader、AppClassLoader 由sun.misc.Launcher$ExtClassLoader
            和 syn.misc.Launcher$AppClassLoader 实现
            
    * 反双亲委派
        * 重写classloader 自定义 findClass
        * 系统api调用用户api 如JDBC、JNDI
        * 模块热部署 OSGi
