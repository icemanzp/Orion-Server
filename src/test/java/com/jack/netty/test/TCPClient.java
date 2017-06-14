package com.jack.netty.test;

import com.jack.netty.test.handler.TCPClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.CharsetUtil;
import junit.framework.TestCase;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;


/**
 * Created by Jack on 2017/6/12.
 */
public class TCPClient extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(TCPClient.class);

    static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8090"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    public static final ChannelFutureListener cfl = new ChannelFutureListener(){
        public void operationComplete(ChannelFuture f) throws Exception {
            if(f.isSuccess()){
                log.info("客户端连接成功！");
            }else{
                log.info("3s后重新连接服务器！");
                f.channel().eventLoop().schedule(new Runnable() {
                    @Override
                    public void run() {
                        testClient();
                    }
                }, 3, TimeUnit.SECONDS);
            }
        }
    };

    public static void testClient() {
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        PropertyConfigurator.configure("target/test-classes/config/log4j.properties");
        try {
            final SslContext sslCtx;
            if (SSL) {
                sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
            } else {
                sslCtx = null;
            }

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc(), HOST, PORT));
                            }
                            //p.addLast(new LoggingHandler(LogLevel.INFO));
                            //p.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                            p.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            p.addLast("frameEncoder", new LengthFieldPrepender(4));
                            p.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
                            p.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
                            p.addLast(new TCPClientHandler());
                        }
                    });

            // Start the client.
            ChannelFuture f = b.connect(HOST, PORT).sync();
            f.addListener(cfl);

            // Wait until the connection is closed.
            //f.channel().closeFuture().sync();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                f.channel().writeAndFlush(in.readLine() + "\r\n");
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args){
        TCPClient.testClient();
    }
}
