package com.jack.netty;

import com.jack.netty.conf.Constant;
import com.jack.netty.conf.util.EnvPropertyConfig;
import com.jack.netty.server.container.factory.ServerConfigWrappar;
import com.jack.netty.server.dto.ProtocolType;
import com.jack.netty.server.infc.IServer;
import com.jack.netty.server.selector.ServerSelector;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;

/**
 * server
 *
 * @author liufl
 * @date 20130916
 * @email hawkdowen@126.com
 */
public class Server implements IServer {
    com.jack.netty.server.infc.IServer currentServer;

    /**
     * 默认服务器初始化 服务器默认按照 HTTP方式访问 服务器默认监控端口： 访问端口：8080 控制端口：8085
     *
     * @throws Exception
     */
    public Server() throws Exception {
        initServer(
                Integer.valueOf(EnvPropertyConfig.getContextProperty(Constant.SYSTEM_SEETING_SERVER_DEFAULT_SERVER_PORT)),
                Integer.valueOf(EnvPropertyConfig.getContextProperty(Constant.SYSTEM_SEETING_SERVER_DEFAULT_SERVER_CONTROL_PORT)),
                ProtocolType.HTTP
        );
    }

    /**
     * 服务器初始化 服务器默认按照 HTTP方式访问
     *
     * @param port  服务器监听端口
     * @param cport 服务器控制监听端口
     * @throws Exception
     */
    public Server(int port, int cport) throws Exception {
        initServer(port, cport, ProtocolType.HTTP);
    }

    /**
     * 服务器初始化
     *
     * @param port  服务器监听端口
     * @param cport 服务器控制监听端口
     * @param pt    启动服务器模式 HTTP|HTTPS|TCP|TLS|CUSTOM|WEBSOCKET
     * @throws Exception
     */
    public Server(int port, int cport, String pt) throws Exception {
        initServer(port, cport, pt);
    }

    /**
     * 初始化服务器
     *
     * @param port  服务器监听端口
     * @param cport 服务器控制监听端口
     * @param pt    启动服务器模式 HTTP|HTTPS|TCP|TLS|CUSTOM|WEBSOCKET
     * @throws Exception
     * @Methods Name initServer
     * @Create In 2016年4月22日 By Jack
     */
    private void initServer(int port, int cport, String pt) throws Exception {
        System.setProperty(Constant.SYSTEM_SEETING_SERVER_DEFAULT_SERVER_PORT, String.valueOf(port));
        currentServer = ServerSelector.initServer(port, cport, pt);
    }

    /**
     * 服务器默认启动方法，Spring配置文件执行默认方案 默认方案如下：
     * <p>
     * Spring配置文件默认位置是：Classpath根目录
     * ApplicationContext的默认名称是：applicationContext.xml
     * MVCContext的默认名称是：springmvc-servlet.xml
     *
     * @throws Exception void
     * @Methods Name run
     * @Create In 2015年8月25日 By Jack
     */
    public void run() throws Exception {
        currentServer.run();
    }

    /**
     * 停止服务器
     *
     * @param code 系统信息码，例如 0、-1等，具体参见java代码指南
     * @Methods Name shutdown
     * @Create In 2015年8月26日 By Jack void
     */
    public void shutdown(int code) {
        currentServer.shutdown(code);
    }

    public ChannelInitializer<?> getDispatcherServletChannelInitializer() {
        return currentServer.getDispatcherServletChannelInitializer();
    }

    public ServerBootstrap getBootstrap() {
        return currentServer.getBootstrap();
    }

}
