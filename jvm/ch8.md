### 虚拟机执行引擎
* 虚拟机 与 物理机
    * 物理机 基于 特定的硬件和指令集
    * 虚拟机 基于 自定义的执行结构和指令集
    * 虚拟机内部可能有多个执行引擎，解释执行和编译执行
    * 虚拟机对外都是 输入字节码 输出执行结果
    
* 方法运行的本质 栈帧的入栈、出栈
每一个栈帧就是正在执行的方法，对应着 局部变量表、操作数栈、动态链接、返回地址
    * 局部变量表 编译时可以确定大小
        * 包含 方法参数 和 局部变量
        * 基本单位为 32位的slot 对于 long和double 需要使用连续2个slot的【原子性】操作
        * slot的索引0表示当前的实例this，参数列表从1开始
        * slot可以复用
        
    * 操作数栈 先入后出的stack结构 通过出栈入栈来操作局部运算
    也是可以编译时确定最大深度
        * 保证类型匹配
        * 可以与其他栈帧共享，以提高性能
        
    * 动态链接 这个栈帧所属方法的符号引用，指向运行常量池(?)中的符号引用，
    用于运行时动态把调用方法的符号引用转化为直接引用 也就是方法的重写
    
    * 方法返回地址
        * 方法退出时需要返回调用地址 方法退出有2种情况
            * 正常退出 遇到return 根据是否有返回值判断具体流程 利用PC返回
            * 异常退出 遇到throw 并且 没有异常处理器 这时没有返回值 利用异常处理表来返回
            
* 方法的调用 
    * 在编译阶段 确定调用方法的版本，不涉及具体的执行 本质是保存一个符号引用，在类加载或者运行时转化为真实地址(直接引用)
    * 【解析】调用 在编译期间、类加载时 可以将一部分【不可变的】符号引用转化为真实引用 也就是内存地址
        * 非虚方法 包括静态方法、私有方法、final 方法、构造器和父类方法 也就是子类无法访问或者重写的方法
        * 对应着 invokestatic 和 invokespecial 字节码命令
    * 【分派】调用
        * 静态分派 依赖静态类型来确定执行版本，例如重载 
            * 在编译阶段完成
            * 不一定定位到合适的版本，例如遇到字面常量时
        * 动态分派 在运行时依赖实际类型确定执行版本，例如重写
            * 运行时完成
            * invokevirtual 指令本身会依赖实际类型寻找方法的实际引用，这就是重写的本质
            * 虚拟方法表、内联缓存等方式 提高效率
            
    * 在编译阶段引入符号引用，在类加载阶段对一部分【静态分派】，另一部分在运行时【动态分派】
    
* 执行引擎
    * Java语言中，Javac编译器完成了程序代码经过词法分析、 语法分析到抽象语法树，再遍历语法树生成线性的字节码指令流的过程。 
    因为这一部分动作是在Java虚拟机之外进行的，而解释器在虚拟机的内部，所以Java程序的编译就是半独立的实现。
    另外，java代码生成语法树之后也可以通过生成器生成目标代码从而编译执行。具体的实现需要具体的jvm来决定。
    * Java语言经过编译之后形成【基于栈的指令集】，也就是基于操作数栈来进行操作
    对比于另一种情况是【基于寄存器的指令集】，也就是基于CPU来操作
        * 基于栈的指令集 可移植、实现简单 但是 执行速度比较慢