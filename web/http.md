### Http
* 超文本传输协议 属于应用层，基于请求/相应的无状态协议 广泛应用于WWW
* 特点
    * 无连接 每个链接只处理一个 请求/相应 之后就断开链接 节约传输时间
    * 无状态 协议没有记忆能力，如果需要之前的信息，需要重传，但是如果不需要应答就会很快捷
    * 灵活 可以传输任意格式的数据对象 以Content-Type标注即可
    * 简单快速 请求只需要 方法 和 路径，所以通讯快速
    * 支持 C/S
* Http请求报文 三部分，请求行、请求头、请求体
    * 请求行分为【3】部分  请求方法 请求路径 请求协议 例如 POST index.jsp HTTP/1.1
        * HTTP/1.1中 请求方法有 8 种 GET、POST、PUT、DELETE 对应RESTFUL接口的crud  OPTIONS、TRACE、HEAD、CONNECT
    * 请求头 key: value的键值对 表示一些附加信息
        * 请求/响应中常见的内容 
            * Content-Type 类型 text/html application/json
            * Content-length 请求/响应的长度 字节为单位
            * Accept 接受类型 多个值，隔开
            * Accept-Encoding 我方接受类型
            * Cache-Control 缓存控制 no-cache、max-age=xx 有效时间(秒)
            
        * 常见请求头
            * Host 主机端口号
            * User-Agent 用户标示、OS 版本等等
    * 请求体
        * 以POST的形式提交参数
        
* Http请求体的不同形式
    * 使用POST JSON提交时，服务器不会解析请求体 需要自己处理
    * Content-Type: application/x-www-form-urlencoded 要求数据以key=value&key=value格式 使用urlEncode编码
    * 文件上传 Content-Type: multipart/form-data;boundary={boundary} 
      和 Content-Disposition: form-data;name=file;filename=http.txt Content-Type: application/octet-stream
      
* Http 响应 分为 响应行 响应头 响应体
    * 响应行也是【3】部分 协议版本 响应码 响应信息 例如 HTTP/1.1 200 OK
    * 响应头
        * 常见 Date Last-Modified Server Transfer-Encoding: chunked 表示Content-Length不确定 set-Cookie: 设置cookie
