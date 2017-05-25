package com.jack.netty.server.container.initializer;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.net.ssl.SSLEngine;

import com.jack.netty.server.container.factory.ServerConfigWrappar;
import com.jack.netty.server.container.handler.WebsocketServerHandler;
import com.jack.netty.server.securechat.SecureChatSslContextFactory;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class WebsocketServerInitializer extends AbstractDispatcherChannelInitalizer {

    private LinkedHashMap<String, ChannelHandler> customPipelineMap;

    public WebsocketServerInitializer(int port, Boolean isssl) throws Exception {
        super(port, isssl);
    }

    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

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

        //注册出栈处理
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

        pipeline.addLast("handler", new WebsocketServerHandler());
    }

    /**
     * @Return the LinkedHashMap<String,ChannelHandler> customPipelineMap
     */
    public LinkedHashMap<String, ChannelHandler> getCustomPipelineMap() {
        return customPipelineMap;
    }

    /**
     * @Param LinkedHashMap<String,ChannelHandler> customPipelineMap to set
     */
    public void setCustomPipelineMap(LinkedHashMap<String, ChannelHandler> customPipelineMap) {
        this.customPipelineMap = customPipelineMap;
    }

}
