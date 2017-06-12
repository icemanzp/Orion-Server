package com.jack.netty.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Jack on 2017/6/12.
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final String firstMessage;

    /**
     * Creates a client-side handler.
     */
    public EchoClientHandler() {
        firstMessage = String.valueOf("发送测试数据");

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("Client 发送到消息:" + firstMessage);
        ctx.writeAndFlush(firstMessage);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("Client 接收到消息:" + msg);
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
}
