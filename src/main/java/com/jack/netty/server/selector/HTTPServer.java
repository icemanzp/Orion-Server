package com.jack.netty.server.selector;

import com.jack.netty.conf.util.EnvPropertyConfig;
import com.jack.netty.server.container.factory.ServerChannelFactory;
import com.jack.netty.server.container.initializer.HttpAndHttpsDispatcherChannelInitializer;
import com.jack.netty.server.dto.ChannelType;
import com.jack.netty.server.dto.ProtocolType;
import com.jack.netty.server.dto.StartupInfo;

import java.util.concurrent.atomic.AtomicLong;

/**
 * server
 *
 * @author liufl
 * @date 20130916
 * @email hawkdowen@126.com
 */
public class HTTPServer extends AbstractServer implements com.jack.netty.server.infc.IServer {

    /**
     * 服务器初始化
     *
     * @param port  服务器监听端口
     * @param cport 服务器控制监听端口
     */
    public HTTPServer(int port, int cport) {
        super(port, cport, false);
    }

    /**
     * 服务器默认启动方法，Spring配置文件执行默认方案 默认方案如下：
     * <p>
     * Spring配置文件默认位置是：Classpath根目录
     * ApplicationContext的默认名称是：applicationContext.xml
     * MVCContext的默认名称是：springmvc-servlet.xml
     *
     * @throws Exception void
     * @Methods Name run
     * @Create In 2015年8月25日 By Jack
     */
    public void run() throws Exception {

        // 1.根据默认配置启动
        if (this.bootstrap != null) {
            throw new IllegalStateException(EnvPropertyConfig.getContextProperty("env.setting.server.error.00001001"));
        }

        // 3.启动服务器
        buildChannelFuture();
    }


    /**
     * @throws Exception
     * @throws InterruptedException void
     * @Methods Name buildChannelFuture
     * @Create In 2015年8月25日 By Jack
     */
    private void buildChannelFuture() throws Exception {

        // 1.启动控制服务
        listenCtrl();

        // 2.初始化服务器
        StartupInfo siInfo = ServerChannelFactory
                .createAcceptorChannel(port, ChannelType.NIO, ProtocolType.HTTP);
        this.bootstrap = siInfo.getBootstrap();
        this.childHandler = (HttpAndHttpsDispatcherChannelInitializer) siInfo.getChildHandler();

        // 3.记录服务初始化时间，到毫秒
        this.startEndTime = new AtomicLong(System.currentTimeMillis());
        log.info("server startup init time (ms): " + String
                .valueOf(this.startEndTime.doubleValue() - this.startBeginTime.doubleValue()));

        // 4. 注册MXBean

        siInfo.getFuture().channel().closeFuture().sync();
    }

}
