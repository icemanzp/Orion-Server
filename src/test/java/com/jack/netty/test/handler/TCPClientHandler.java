package com.jack.netty.test.handler;

import com.jack.netty.test.TCPClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by Jack on 2017/6/12.
 */
public class TCPClientHandler extends SimpleChannelInboundHandler<String> {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Creates a client-side handler.
     */
    public TCPClientHandler() {
        log.info(" 测试 tcp client");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        log.info(s);
    }

    @Override
    public void channelInactive (ChannelHandlerContext ctx) throws Exception {
        log.info("[Client] 服务端：" + ctx.channel().remoteAddress() + "离线，连接断开");
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                log.info("[Client]开始重连服务器");
                TCPClient.testClient();
            }
        }, 3, TimeUnit.SECONDS);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        log.error("ns exceptionCougnt", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
