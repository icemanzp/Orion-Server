package com.jack.netty.server.container.initializer;

import com.jack.netty.server.container.factory.ServerConfigWrappar;
import com.jack.netty.server.securechat.SecureChatSslContextFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.CharsetUtil;

import javax.net.ssl.SSLEngine;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @Description: 
 * @package: com.jack.netty.server.container.initializer
 * Create In 2017/6/7 By Jack
**/
public class TcpAndTlsServerInitializer extends AbstractDispatcherChannelInitalizer {

    public TcpAndTlsServerInitializer(int port, Boolean isSSL) throws Exception {
        super(port, isSSL);
    }
    public TcpAndTlsServerInitializer(int port, Boolean isSSL,
            LinkedHashMap<String, ChannelHandler> customPipelineMap) throws Exception {
        super(port, isSSL);
        while (customPipelineMap.keySet().iterator().hasNext()) {
            String key = customPipelineMap.keySet().iterator().next();
            this.addCustomerPipeline(key, customPipelineMap.get(key));
        }
    }
    
    /**
     * @Methods Name initChannel
     * @Description: 初始化服务接入
     * @param ch 连接接入管道
     * @throws Exception
     * Create In 2017/6/7 By Jack
    **/
    @Override
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
        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
        pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));

        for (Iterator<Map.Entry<String, ChannelHandler>> it = customPipelineMap.entrySet()
                .iterator(); it.hasNext(); ) {
            Entry<String, ChannelHandler> entry = (Entry<String, ChannelHandler>) it.next();
            if (entry.getValue() != null) {
                String handlerName = entry.getKey();
                ChannelHandler handlerValue = entry.getValue();
                pipeline.addLast(handlerName, handlerValue);
            }
        }
        //通过上面的自定义Pipeline装在后续的处理器完成通信处理，这部分在客户化代码中实现，并通过nettyserver.xml配置
        //pipeline.addLast(new TCPSHandler());
    }

    public LinkedHashMap<String, ChannelHandler> getCustomPipelineMap() {
        return customPipelineMap;
    }

}

