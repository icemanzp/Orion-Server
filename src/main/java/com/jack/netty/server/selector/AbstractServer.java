/**
 * @Probject Name: netty-wfj-server
 * @Path: com.jack.netty.server.selectorAbstractServer.java
 * @Create By Jack
 * @Create In 2015年9月8日 下午5:17:59
 * TODO
 */
package com.jack.netty.server.selector;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jack.netty.conf.Constant;
import com.jack.netty.server.container.factory.ServerConfigWrappar;
import com.jack.netty.server.container.initializer.HttpAndHttpsDispatcherChannelInitializer;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Class Name AbstractServer
 * @Author Jack
 * @Create In 2015年9月8日
 */
public abstract class AbstractServer {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    protected final int port;
    protected final int cport;
    protected final Boolean isSSL;

    protected ServerBootstrap bootstrap;
    protected HttpAndHttpsDispatcherChannelInitializer childHandler;

    protected AtomicLong startBeginTime;
    protected AtomicLong startEndTime;


    public AbstractServer(int port, int cport, Boolean isssl) {
        this.port = port;
        this.cport = cport;
        this.isSSL = isssl;
    }


    /**
     * 启动内部控制服务
     *
     * @Methods Name listenCtrl
     * @Create In 2015年8月26日 By Jack
     */
    protected void listenCtrl() {

        // 记录服务启动初始时间
        this.startBeginTime = new AtomicLong(System.currentTimeMillis());
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    log.info("server listen to control port " + cport);
                    ServerSocket ss = new ServerSocket(cport);
                    while (true) {
                        Socket s = ss.accept();
                        Scanner sc = new Scanner(s.getInputStream());
                        String line = sc.nextLine();
                        if (Constant.SYSTEM_SEETING_SERVER_DEFAULT_COMMAND_STOP.equalsIgnoreCase(line)) {
                            log.info("server is stoping...");
                            shutdown(Constant.SYSTEM_SEETING_PROCESS_RESULT_CODE_STOP);
                        }else if(line.contains(Constant.SYSTEM_SEETING_SERVER_DEFAULT_COMMAND_MAXCOUNT)){
                            String[] setCount = line.split(":");
                            Integer currentCount = setCount.length>1 ? Integer.valueOf(setCount[1]) : Integer.valueOf(Constant.SYSTEM_SEETING_SERVER_DEFAULT_MAX_INCOME_COUNTS);
                            bootstrap.option(ChannelOption.SO_BACKLOG, currentCount);
                            log.info("resetting income count to:" + currentCount.toString());
                        }
                    }
                } catch (Exception e) {
                    log.error("listen to control port fail", e);
                    shutdown(Constant.SYSTEM_SEETING_PROCESS_RESULT_CODE_EXIT);
                }
            }
        }, "netty-wfj-server-ctrl-thread");
        t.start();
    }


    /**
     * 停止服务器
     *
     * @param code 系统信息码，例如 0、-1等，具体参见java代码指南
     * @Methods Name shutdown
     * @Create In 2015年8月26日 By Jack void
     */
    public void shutdown(int code) {
        //1.停止处理容器
        ServerConfigWrappar.destroy();
        //2.停止服务器
        log.info("server is stoped...");
        System.exit(code);
    }


    public HttpAndHttpsDispatcherChannelInitializer getDispatcherServletChannelInitializer() {
        return childHandler;
    }

    public ServerBootstrap getBootstrap() {
        return bootstrap;
    }


}
