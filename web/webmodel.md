### 网络分层
* ISO 7 层模型
    1. 物理层 比特流到电信号的转化
    2. 链路层 互联设备之间传输和识别数据帧
    3. 网络层 地址管理和路由选择  ip
    4. 连接层 管理节点之间的数据，保证可靠传输 tcp
    5. 会话层 新建和管理数据连接
    6. 表现层 数据固有格式和网络传输格式
    7. 应用层 针对每个应用的协议 ftp smtp telnet http
    
 * TCP/IP 4 层模型
    1. 网络接口层 物理+链路
    2. 互联网层  网络层 ip arp(根据ip获取物理地址)
    3. 传输层    连接层 tcp udp
    4. 应用层     会话、表现、应用  http tls/ssl smtp pop imap telnet ssh stp
