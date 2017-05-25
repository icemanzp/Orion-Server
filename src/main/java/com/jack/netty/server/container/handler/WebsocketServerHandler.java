package com.jack.netty.server.container.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import com.jack.netty.server.cache.ChannelHashTable;
import com.jack.netty.server.infc.IWebSocketServerService;

import java.util.Iterator;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;

public class WebsocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private static Logger log = Logger.getLogger(WebsocketServerHandler.class);

    private IWebSocketServerService webSocketServerService;

    private WebSocketServerHandshaker handshaker;


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("error when proccessing websocket request", cause);
        ChannelHashTable.remove(ctx.channel().hashCode());
        ctx.close();
    }

    /**
     * @param @param  ctx
     * @param @param  req
     * @param @throws Exception
     * @return void
     * @throws
     * @Title: handleHttpRequest
     * @Description: 处理HTTP请求
     * @author omen www.liyidong.com
     * @date 2015年5月29日 下午4:34:15
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        // 如果HTTP解码失败，返回HTTP异常
        if (!req.getDecoderResult().isSuccess()
                || (!"websocket".equals(req.headers().get("Upgrade")))) {
            log.debug("HTTP解码失败，返回HTTP异常");
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.BAD_REQUEST));
            return;
        }

        // 构造握手响应返回
        WebSocketServerHandshakerFactory wsShakerFactory = new WebSocketServerHandshakerFactory(
                "ws://" + req.headers().get(HttpHeaders.Names.HOST), null, false);
        WebSocketServerHandshaker wsShakerHandler = wsShakerFactory.newHandshaker(req);
        if (null == wsShakerHandler) {
            // 无法处理的websocket版本
            log.debug("无法处理的websocket版本");
            wsShakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        } else {
            // 向客户端发送websocket握手,完成握手
            // 客户端收到的状态是101 switching protocol
            log.debug("客户端发送websocket握手,完成握手");
            Iterator it = req.headers().iterator();
            if (it.hasNext()) {
                it.next();
            }

            wsShakerHandler.handshake(ctx.channel(), req);

            for (String pro : wsShakerHandler.subprotocols()) {
                log.debug("protocols:" + pro);
            }

            log.debug("WS version=" + wsShakerHandler.version());

        }

    }

    /**
     * @param @param  ctx
     * @param @param  frame
     * @param @throws Exception
     * @return void
     * @throws
     * @Title: handleWebSocketFrame
     * @Description: 处理websocket消息
     * @author omen www.liyidong.com
     * @date 2015年5月29日 下午4:34:52
     */
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame)
            throws Exception {

        // 判断是否是关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }

        // 判断是否是ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            // log.debug("ping pong returns!");
            return;
        }
        // TODO
        ChannelHashTable.put("test", ctx.channel());

        // TODO 目前仅支持文本消息，不支持二进制消息
        if (frame instanceof TextWebSocketFrame) {
            // 业务请求返回消息处理
            String request = ((TextWebSocketFrame) frame).text();
            log.debug("server 收到 文本消息[" + request + "]");
            String res = webSocketServerService.doService(request);
            log.debug("server 返回 文本消息[" + res + "]");
            ctx.channel().write(new TextWebSocketFrame(res));

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    ChannelHashTable.getCtx("test").writeAndFlush(new TextWebSocketFrame("second"));
                }
            });

            t.start();

        } else {
            throw new UnsupportedOperationException(String.format("%s frame types not supported",
                    frame.getClass().getName()));
        }

    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req,
            FullHttpResponse res) {
        // 返回应答给客户端
        if (res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            res.headers().setContentLength(res, res.content().readableBytes());
        }

        // 如果是非keep-alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!isKeepAlive(req) || res.getStatus().code() != 200) {
            ChannelHashTable.remove(ctx.channel().hashCode());
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * @param @param  req
     * @param @return
     * @return boolean
     * @throws
     * @Title: isKeepAlive
     * @Description: 判断HTTP请求是否为keep-alive
     * @author omen www.liyidong.com
     * @date 2015年5月29日 下午4:35:16
     */
    private static boolean isKeepAlive(FullHttpRequest req) {
        boolean keepAlive = !req.headers().contains(CONNECTION, HttpHeaders.Values.CLOSE, true)
                && (!req.getProtocolVersion().equals(HttpVersion.HTTP_1_0) || req.headers()
                .contains(CONNECTION, HttpHeaders.Values.KEEP_ALIVE, true));
        return keepAlive;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // TODO Auto-generated method stub
        // 虽然是websocket，但在建立websocket连接前，先进行http握手,所以，这时也要处理http请求
        // 在http握手完成后，才是websocket下的通信
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }
        // websocket接入
        else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

}
