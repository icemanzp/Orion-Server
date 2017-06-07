/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.jack.netty.server.selector;

import java.util.concurrent.atomic.AtomicLong;

import com.jack.netty.conf.util.EnvPropertyConfig;
import com.jack.netty.server.container.factory.ServerChannelFactory;
import com.jack.netty.server.container.initializer.HttpAndHttpsDispatcherChannelInitializer;
import com.jack.netty.server.dto.ChannelType;
import com.jack.netty.server.dto.ProtocolType;
import com.jack.netty.server.dto.StartupInfo;

/**
 * Simple SSL chat server modified from {@link io.netty.example.telnet.TelnetServer}.
 */
public class HTTPSServer extends AbstractServer implements com.jack.netty.server.infc.IServer {

    public HTTPSServer(int port, int cport) {
        super(port, cport, true);
    }

    @Override
    public void run() throws Exception {
        // 1.根据默认配置启动
        if (this.bootstrap != null) {
            throw new IllegalStateException(EnvPropertyConfig.getContextProperty("env.setting.server.error.00001001"));
        }

        // 2.初始化服务器
        buildChannelFuture();
    }

    /**
     * 建立服务
     *
     * @throws Exception
     * @throws InterruptedException void
     * @Methods Name buildChannelFuture
     * @Create In 2015年8月25日 By Jack
     */
    private void buildChannelFuture() throws Exception {

        // 1. 启动监控借口
        listenCtrl();

        // 2.初始化服务器
        StartupInfo siInfo = ServerChannelFactory
                .createAcceptorChannel(port, ChannelType.NIO, ProtocolType.HTTPS);
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
