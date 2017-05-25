/**
 * @Probject Name: netty-wfj-server
 * @Path: com.jack.netty.server.infcDispatcherChannelInitalizer.java
 * @Create By Jack
 * @Create In 2015年9月8日 下午4:40:13
 * TODO
 */
package com.jack.netty.server.container.initializer;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jack.netty.server.container.factory.ServerConfigWrappar;

import javax.servlet.ServletContext;
import java.util.LinkedHashMap;

/**
 * @Class Name DispatcherChannelInitalizer
 * @Author Jack
 * @Create In 2015年9月8日
 */
public abstract class AbstractDispatcherChannelInitalizer extends ChannelInitializer<SocketChannel> {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    protected final ServletContext servletContext;

    protected final LinkedHashMap<String, ChannelHandler> customPipelineMap;

    protected final Boolean isSSL;

    public AbstractDispatcherChannelInitalizer(int port, Boolean isssl) throws Exception {
        log.info("init spring and spring mvc");

        this.isSSL = isssl;
        servletContext = ServerConfigWrappar.getServletContext();
        customPipelineMap = new LinkedHashMap<String, ChannelHandler>();
    }

    /**
     * @Return the LinkedHashMap<String,ChannelHandler> customPipelineMap
     */
    public LinkedHashMap<String, ChannelHandler> getCustomPipelineMap() {
        return customPipelineMap;
    }

    /**
     * 增加自定义处理器
     *
     * @param key
     * @param handler void
     * @Methods Name addCustomerPipeline
     * @Create In 2016年4月28日 By Jack
     */
    public void addCustomerPipeline(String key, ChannelHandler handler) {
        customPipelineMap.put(key, handler);
    }

}
