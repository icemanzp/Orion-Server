package com.jack.netty.server.selector;

import com.jack.netty.conf.util.EnvPropertyConfig;
import com.jack.netty.server.container.factory.ServerChannelFactory;
import com.jack.netty.server.container.initializer.TcpAndTlsServerInitializer;
import com.jack.netty.server.dto.ChannelType;
import com.jack.netty.server.dto.ProtocolType;
import com.jack.netty.server.dto.StartupInfo;
import com.jack.netty.server.infc.IServer;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Package Name：com.jack.netty.server.selector
 * Created by Jack on 2017/6/7.
 */
public class TcpAndTlsServer extends AbstractServer implements IServer {
	public TcpAndTlsServer(int port, int cport, boolean isSSL){
		super(port, cport, isSSL);
	}

	/**
	 * @Title run
	 * @Description: 重写启动执行方法
	 * @Return:
	 * @Throws: Exception
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
	 * @Title buildChannelFuture
	 * @Description: 构建TCP or TLS 服务器
	 * @Return
	 * @Throws Exception
	 * Create In 2017/6/7 By Jack
	**/
	private void buildChannelFuture() throws Exception {

		// 1. 启动监控借口
		listenCtrl();

		// 2.初始化服务器
		StartupInfo siInfo = ServerChannelFactory
				.createAcceptorChannel(port, ChannelType.NIO, (isSSL ? ProtocolType.TLS : ProtocolType.TCP));
		this.bootstrap = siInfo.getBootstrap();
		this.childHandler = (TcpAndTlsServerInitializer) siInfo.getChildHandler();

		// 3.记录服务初始化时间，到毫秒
		this.startEndTime = new AtomicLong(System.currentTimeMillis());
		log.info("server startup init time (ms): " + String
				.valueOf(this.startEndTime.doubleValue() - this.startBeginTime.doubleValue()));

		siInfo.getFuture().channel().closeFuture().sync();
	}
}
