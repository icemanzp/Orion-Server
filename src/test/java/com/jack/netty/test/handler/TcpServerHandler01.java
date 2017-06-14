package com.jack.netty.test.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * Created by Jack on 2017/6/14.
 */
public class TcpServerHandler01 extends ChannelInboundHandlerAdapter {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    // 接收到新的数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        try {
            // 接收客户端的数据
            log.info("channelRead:" + msg.toString());

            // 发送到客户端
            byte[] responseByteArray = ("你好，您发送的是：" + String.valueOf(msg)).getBytes("UTF-8");
            ByteBuf out = ctx.alloc().buffer(responseByteArray.length);
            out.writeBytes(responseByteArray);
            ctx.writeAndFlush(out);

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("channelActive: " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        log.info("channelInactive: " + ctx.channel().remoteAddress()+"掉线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("channelException: " + ctx.channel().remoteAddress() + "\r\n" + cause.toString());
        cause.printStackTrace();
        ctx.close();
    }
}
