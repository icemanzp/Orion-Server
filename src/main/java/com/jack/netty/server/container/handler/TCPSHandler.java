/**
 * @Probject Name: netty-wfj-server
 * @Path: com.jack.netty.server.container.handlerTCPSHandler.java
 * @Create By Jack
 * @Create In 2015年9月8日 下午6:44:43
 * TODO
 */
package com.jack.netty.server.container.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Class Name TCPSHandler
 * @Author Jack
 * @Create In 2015年9月8日
 */
@ChannelHandler.Sharable
public class TCPSHandler extends SimpleChannelInboundHandler<Object>  {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public TCPSHandler(){
        log.info("初始化 TCPHandler 处理器");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("SERVER接收到消息:" + msg);
        ctx.channel().writeAndFlush("yes, server is accepted you ,nice !" + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("ns exceptionCougnt", cause);
        ctx.close();
    }

}
