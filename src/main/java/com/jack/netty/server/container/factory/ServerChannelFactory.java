package com.jack.netty.server.container.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jack.netty.server.container.initializer.CustomServerInitializer;
import com.jack.netty.server.container.initializer.HttpAndHttpsDispatcherChannelInitializer;
import com.jack.netty.server.container.initializer.TcpServerInitializer;
import com.jack.netty.server.container.initializer.WebsocketServerInitializer;
import com.jack.netty.server.dto.ProtocolType;
import com.jack.netty.server.dto.StartupInfo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class ServerChannelFactory {

    private static Logger log = LoggerFactory.getLogger(ServerChannelFactory.class);

    public static StartupInfo createAcceptorChannel(int port, int cport, int channelType,
            String protoctoType) throws RuntimeException {
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
                return new HttpAndHttpsDispatcherChannelInitializer(port, false);

            case ProtocolType.HTTPS:
                return new HttpAndHttpsDispatcherChannelInitializer(port, true);

            case ProtocolType.TCP:
                return new TcpServerInitializer(port, false);

            case ProtocolType.CUSTOM:
                return new CustomServerInitializer(port, false);

            case ProtocolType.WEBSOCKET:
                return new WebsocketServerInitializer(port, false);

            default:
                return new HttpAndHttpsDispatcherChannelInitializer(port, false);
        }

    }
}
