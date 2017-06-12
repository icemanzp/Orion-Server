package com.jack.netty.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import junit.framework.TestCase;
import org.apache.log4j.PropertyConfigurator;


/**
 * Created by Jack on 2017/6/12.
 */
public class TCPClient extends TestCase {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8090"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    public void testClient() {
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
                            p.addLast(new EchoClientHandler());
                        }
                    });

            // Start the client.
            ChannelFuture f = b.connect(HOST, PORT).sync();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }
}
