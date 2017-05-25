/**
 * @Probject Name: netty-wfj-server
 * @Path: com.jack.netty.server.containerSSLDispatcherChannelInitializer.java
 * @Create By Jack
 * @Create In 2015年9月8日 下午4:47:15
 * TODO
 */
package com.jack.netty.server.container.initializer;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.net.ssl.SSLEngine;

import com.jack.netty.server.container.factory.ServerConfigWrappar;
import com.jack.netty.server.container.handler.HTTPHandler;
import com.jack.netty.server.securechat.SecureChatSslContextFactory;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @Class Name HttpAndHttpsDispatcherChannelInitializer
 * @Author Jack
 * @Create In 2015年9月8日
 */
public class HttpAndHttpsDispatcherChannelInitializer extends AbstractDispatcherChannelInitalizer {

    public HttpAndHttpsDispatcherChannelInitializer(int port, Boolean isssl) throws Exception {
        super(port, isssl);
    }

    public HttpAndHttpsDispatcherChannelInitializer(int port, Boolean isssl,
            LinkedHashMap<String, ChannelHandler> customPipelineMap) throws Exception {
        super(port, isssl);
        while (customPipelineMap.keySet().iterator().hasNext()) {
            String key = customPipelineMap.keySet().iterator().next();
            this.addCustomerPipeline(key, customPipelineMap.get(key));
            ;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // TODO Auto-generated method stub
        ChannelPipeline pipeline = ch.pipeline();

        //注册入栈处理
        if (this.isSSL) {
            SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
            engine.setNeedClientAuth(
                    ServerConfigWrappar.getNettyServerInfo().getBaseInfo().getSshInfo().isNeedClientAuth()); // ssl双向认证
            engine.setUseClientMode(false);
            engine.setWantClientAuth(true);
            engine.setEnabledProtocols(new String[]{"SSLv3"});
            pipeline.addLast("ssl", new SslHandler(engine));
        }
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(100 * 1024 * 1024)); // 上传限制3M
        /**
         * 压缩
         * Compresses an HttpMessage and an HttpContent in gzip or deflate encoding
         * while respecting the "Accept-Encoding" header.
         * If there is no matching encoding, no compression is done.
         */
        pipeline.addLast("deflater", new HttpContentCompressor());

        //注册出栈处理
        pipeline.addLast("encoder", new HttpResponseEncoder());
        //异步流返回处理器
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());

        for (Iterator<Map.Entry<String, ChannelHandler>> it = customPipelineMap.entrySet()
                .iterator(); it.hasNext(); ) {
            Entry<String, ChannelHandler> entry = (Entry<String, ChannelHandler>) it.next();
            if (entry.getValue() != null) {
                String handlerName = entry.getKey();
                ChannelHandler handlerValue = entry.getValue();
                pipeline.addLast(handlerName, handlerValue);
            }
        }

        //开始业务处理 Handler 执行
        pipeline.addLast("handler", new HTTPHandler(servletContext));

    }

}
