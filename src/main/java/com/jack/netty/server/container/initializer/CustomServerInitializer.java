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

// @DependsOn({"customServerHandler"})
public class CustomServerInitializer extends AbstractDispatcherChannelInitalizer {

    public CustomServerInitializer(int port, Boolean isSSL) throws Exception {
        super(port, isSSL);
    }

    public CustomServerInitializer(int port, Boolean isSSL, LinkedHashMap<String, ChannelHandler> customPipelineMap)
            throws Exception {
        super(port, isSSL);
        while (customPipelineMap.keySet().iterator().hasNext()) {
            String key = customPipelineMap.keySet().iterator().next();
            this.addCustomerPipeline(key, customPipelineMap.get(key));
        }
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

        // 注册入栈处理
        if (this.isSSL) {
            SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
            engine.setNeedClientAuth(ServerConfigWrappar.getNettyServerInfo().getBaseInfo()
                    .getSshInfo().isNeedClientAuth()); // ssl双向认证
            engine.setUseClientMode(false);
            engine.setWantClientAuth(true);
            engine.setEnabledProtocols(new String[]{"SSLv3"});
            pipeline.addLast("ssl", new SslHandler(engine));
        }

        /**
         * http-request解码器 http服务器端对request解码
         */
        pipeline.addLast("decoder", new HttpRequestDecoder());
        /**
         * http-response解码器 http服务器端对response编码
         */
        pipeline.addLast("encoder", new HttpResponseEncoder());

        /**
         * http报文的数据组装成为封装好的httprequest对象
         */
        pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
        /**
         * 压缩 Compresses an HttpMessage and an HttpContent in gzip or deflate
         * encoding while respecting the "Accept-Encoding" header. If there is
         * no matching encoding, no compression is done.
         */
        pipeline.addLast("deflater", new HttpContentCompressor());

        // 注册出栈处理
        pipeline.addLast("encoder", new HttpResponseEncoder());
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

    }

    public LinkedHashMap<String, ChannelHandler> getCustomPipelineMap() {
        return customPipelineMap;
    }

}
