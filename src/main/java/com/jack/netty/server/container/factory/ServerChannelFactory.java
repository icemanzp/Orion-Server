package com.jack.netty.server.container.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jack.netty.server.container.initializer.CustomServerInitializer;
import com.jack.netty.server.container.initializer.HttpAndHttpsServerInitializer;
import com.jack.netty.server.container.initializer.TcpAndTlsServerInitializer;
import com.jack.netty.server.container.initializer.WebsocketServerInitializer;
import com.jack.netty.server.dto.ProtocolType;
import com.jack.netty.server.dto.StartupInfo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * @Package com.jack.netty.server.container.factory
 * @Class Name ServerChannelFactory
 * @Description
 * <p> 服务器启动选择器 </p>
 *
 * Create In 2017/6/7 By Jack
**/
public class ServerChannelFactory {

    private static Logger log = LoggerFactory.getLogger(ServerChannelFactory.class);

    /**
     * @Methods Name createAcceptorChannel
     * @Description:
     * <p> 创建可允许启动服务 </p>
     *
     * @param port 服务端口号
     * @param channelType  服务接入模式：NIO or OIO
     * @param protoctoType 服务器模式：Http or Https or Tcp or Tls or Websocket or Customer
     * @return StartupInfo 服务器实例
     * @throws RuntimeException
     * Create In 2017/6/7 By Jack
    **/
    public static StartupInfo createAcceptorChannel(int port, int channelType, String protoctoType) throws RuntimeException {
        try {
            final ServerBootstrap serverBootstrap = ServerBootstrapFactory.createServerBootstrap(channelType);
            final ChannelInitializer<SocketChannel> childHandler = getChannelInitializer(port, protoctoType);
            serverBootstrap.childHandler(childHandler);
            log.info("Create Server now ...");
            log.info("Server listens to port " + port);
            ChannelFuture future = serverBootstrap.localAddress(port).bind().sync();
            future.addListener(new GenericFutureListener<Future<? super Void>>() {
                public void operationComplete(Future<? super Void> future) throws Exception {
                    //服务启动成功后
                    log.info("server heartlive startup secussed.............");
                    log.info("server startup...............");
                }
            });

            return new StartupInfo(serverBootstrap, future, childHandler);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ChannelInitializer<SocketChannel> getChannelInitializer(int port, String protoctoType)
            throws Exception {
        switch (protoctoType) {
            case ProtocolType.HTTP:
                return new HttpAndHttpsServerInitializer(port, false);

            case ProtocolType.HTTPS:
                return new HttpAndHttpsServerInitializer(port, true);

            case ProtocolType.TCP:
                return new TcpAndTlsServerInitializer(port, false);

            case ProtocolType.CUSTOM:
                return new CustomServerInitializer(port, false);

            case ProtocolType.WEBSOCKET:
                return new WebsocketServerInitializer(port, false);

            default:
                return new HttpAndHttpsServerInitializer(port, false);
        }

    }
}
