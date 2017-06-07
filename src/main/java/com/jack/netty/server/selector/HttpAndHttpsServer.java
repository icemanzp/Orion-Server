package com.jack.netty.server.selector;

import com.jack.netty.conf.util.EnvPropertyConfig;
import com.jack.netty.server.container.factory.ServerChannelFactory;
import com.jack.netty.server.container.initializer.HttpAndHttpsServerInitializer;
import com.jack.netty.server.dto.ChannelType;
import com.jack.netty.server.dto.ProtocolType;
import com.jack.netty.server.dto.StartupInfo;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Package com.jack.netty.server.selector
 * @Class Name HttpAndHttpsServer
 * @Description
 * <p> Http or Https服务器服务类 </p>
 *
 * Create In 2017/6/7 By Jack
**/
public class HttpAndHttpsServer extends AbstractServer implements com.jack.netty.server.infc.IServer {

	public HttpAndHttpsServer(int port, int cport, boolean isSSL){
		super(port, cport, isSSL);
	}

	/**
	 * @Methods Name run
	 * @Description:
	 * <p> 重写启动执行方法 </p>
	 *
	 * @throws Exception
	 * Create In 2017/6/7 By Jack
	**/
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
	 * @Methods Name buildChannelFuture
	 * @Description:
	 * <p> 构建服务器服务 </p>
	 *
	 * @throws Exception
	 * Create In 2017/6/7 By Jack
	**/
	private void buildChannelFuture() throws Exception {

		// 1. 启动监控借口
		listenCtrl();

		// 2.初始化服务器
		StartupInfo siInfo = ServerChannelFactory
				.createAcceptorChannel(port, ChannelType.NIO, (isSSL ? ProtocolType.HTTPS : ProtocolType.HTTP));
		this.bootstrap = siInfo.getBootstrap();
		this.childHandler = (HttpAndHttpsServerInitializer) siInfo.getChildHandler();

		// 3.记录服务初始化时间，到毫秒
		this.startEndTime = new AtomicLong(System.currentTimeMillis());
		log.info("server startup init time (ms): " + String
				.valueOf(this.startEndTime.doubleValue() - this.startBeginTime.doubleValue()));

		siInfo.getFuture().channel().closeFuture().sync();
	}
}
