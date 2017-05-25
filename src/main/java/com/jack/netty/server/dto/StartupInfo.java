/**
 * @Probject Name: netty-wfj-server
 * @Path: com.jack.netty.server.dtojava
 * @Create By Jack
 * @Create In 2015年9月8日 下午8:08:25
 * TODO
 */
package com.jack.netty.server.dto;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @Class Name StartupInfo
 * @Author Jack
 * @Create In 2015年9月8日
 */
public class StartupInfo {

    private ChannelInitializer<SocketChannel> childHandler;


    /**
     * @Return the AbstractDispatcherChannelInitalizer childHandler
     */
    public ChannelInitializer<SocketChannel> getChildHandler() {
        return childHandler;
    }

    /**
     * @Param AbstractDispatcherChannelInitalizer childHandler to set
     */
    public void setChildHandler(ChannelInitializer<SocketChannel> childHandler) {
        this.childHandler = childHandler;
    }

    private ServerBootstrap bootstrap;

    /**
     * @Return the ServerBootstrap bootstrap
     */
    public ServerBootstrap getBootstrap() {
        return bootstrap;
    }

    /**
     * @Param ServerBootstrap bootstrap to set
     */
    public void setBootstrap(ServerBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    /**
     * @Return the ChannelFuture future
     */
    public ChannelFuture getFuture() {
        return future;
    }

    /**
     * @Param ChannelFuture future to set
     */
    public void setFuture(ChannelFuture future) {
        this.future = future;
    }

    private ChannelFuture future;

    public StartupInfo(ServerBootstrap bootstrap, ChannelFuture future,
            ChannelInitializer<SocketChannel> childHandler) {
        this.bootstrap = bootstrap;
        this.future = future;
        this.childHandler = childHandler;
    }
}
