# Orion-Server

## 1. 功能说明

基于 Netty 的私有容器，支持 Servlet3.1以下

1. 支持 Spring 标准 Web 项目使用方式，便于从传统 Tomcat 项目迁移
2. 支持 Http|Https|TCP 通信，未来支持 Websocket

## 2. 使用方式：
1. 新建 Java Application或 Maven 项目，并引入 orion-server的 Jar 或者依赖到项目中
2. 配置 nettyserver.xml文件中的信息，除基本信息外其余类似 web.xml；如使用 TCP 模式，请在nettyserer.xml中自行实现消息处理器及内容转换器
3. 编译完成项目后，执行java -jar xxx.jar start 访问端口号 控制端口号 <启动模式：HTTP|HTTPS|TCP,默认不输入启动HTTP模式>
4. 访问方式：<br/>
   a. 如 HTTP|HTTPS 方式启动：<br/>
      通过浏览器或者其他工程访问http://IP:访问端口号/应用名/服务名，即可返回数据，如出错则返回相应 Http 响应吗及错误信息<br/>
   b. 如 TCP|TLS 方式启动：<br/>
      使用代码客户端或者工具直接访问：IP:访问端口号
   
