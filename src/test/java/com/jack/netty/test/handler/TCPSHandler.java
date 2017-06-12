/**
 * @Probject Name: netty-wfj-server
 * @Path: com.jack.netty.server.container.handlerTCPSHandler.java
 * @Create By Jack
 * @Create In 2015年9月8日 下午6:44:43
 * TODO
 */
package com.jack.netty.test.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Class Name TCPSHandler
 * @Author Jack
 * @Create In 2015年9月8日
 */
@ChannelHandler.Sharable
public class TCPSHandler extends ChannelInboundHandlerAdapter {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("SERVER接收到消息:" + msg);
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        log.error("ns exceptionCougnt", cause);
        cause.printStackTrace();
        ctx.close();
    }


    public TCPSHandler(){
        log.info("初始化 TCPHandler Test处理器");
    }

}
