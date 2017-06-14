package com.jack.netty.server.container.factory;


import com.jack.netty.conf.Constant;
import com.jack.netty.server.dto.ChannelType;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;


/**
 * @author Jack
 * @version 1.0
 * @function:工厂模式 server服务器 ServerBootstrap创建
 */
public class ServerBootstrapFactory {

    public static ServerBootstrap createServerBootstrap(int channelType) throws UnsupportedOperationException {

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        Integer maxCount = ServerConfigWrappar.getNettyServerInfo().getBaseInfo().getMaxIncomeThreadCount() != null ?
                ServerConfigWrappar.getNettyServerInfo().getBaseInfo().getMaxIncomeThreadCount()
                        .intValue() : Constant.SYSTEM_SEETING_SERVER_DEFAULT_MAX_INCOME_COUNTS;

        switch (channelType) {
            case ChannelType.NIO:
                serverBootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup());
                serverBootstrap.channel(NioServerSocketChannel.class);
                //连接的最大队列长度。如果队列满时收到连接指示，则拒绝该连接
                serverBootstrap.option(ChannelOption.SO_BACKLOG, maxCount);

                return serverBootstrap;

            case ChannelType.OIO:
                serverBootstrap.group(new OioEventLoopGroup());
                serverBootstrap.channel(OioServerSocketChannel.class);
                //连接的最大队列长度。如果队列满时收到连接指示，则拒绝该连接
                serverBootstrap.option(ChannelOption.SO_BACKLOG, maxCount);

                return serverBootstrap;
            default:
                throw new UnsupportedOperationException(
                        "Failed to create ServerBootstrap,  " + (channelType == ChannelType.NIO ? ChannelType.NIO : ChannelType.OIO) + " not supported!");
        }
    }
}
