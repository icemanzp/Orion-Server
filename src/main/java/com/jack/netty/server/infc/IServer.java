/**
 * @Probject Name: netty-wfj-base
 * @Path: com.jack.netty.infcServer.java
 * @Create By Jack
 * @Create In 2015年8月26日 下午8:20:37
 * TODO
 */
package com.jack.netty.server.infc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;

/**
 * @Class Name Server
 * @Author Jack
 * @Create In 2015年8月26日
 */
public interface IServer {
    /**
     * 服务器默认启动方法，Spring配置文件执行默认方案
     * 默认方案如下：
     * <p>
     * Spring配置文件默认位置是：Classpath根目录
     * ApplicationContext的默认名称是：applicationContext.xml
     * MVCContext的默认名称是：springmvc-servlet.xml
     *
     * @throws Exception void
     * @Methods Name run
     * @Create In 2015年8月25日 By Jack
     */
    public void run() throws Exception;

    /**
     * 停止服务器
     *
     * @param int code 系统信息码，例如 0、-1等，具体参见java代码指南
     * @Methods Name shutdown
     * @Create In 2015年8月26日 By Jack void
     */
    public void shutdown(int code);

    /**
     * 获取目前在执行的 ChannelInitializer
     *
     * @return ChannelInitializer<?>
     * @Methods Name getDispatcherServletChannelInitializer
     * @Create In 2015年9月8日 By Jack
     */
    public ChannelInitializer<?> getDispatcherServletChannelInitializer();

    /**
     * 获取生效的服务器
     *
     * @return ServerBootstrap
     * @Methods Name getBootstrap
     * @Create In 2015年9月8日 By Jack
     */
    public ServerBootstrap getBootstrap();

}
