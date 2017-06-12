/**
 * @Probject Name: netty-wfj-server
 * @Path: com.jack.netty.serverServerSelector.java
 * @Create By Jack
 * @Create In 2015年9月8日 下午5:11:22
 * TODO
 */
package com.jack.netty.server.selector;

import com.jack.netty.server.container.factory.ServerConfigWrappar;
import com.jack.netty.server.dto.ProtocolType;
import com.jack.netty.server.infc.IServer;

/**
 * @Class Name ServerSelector
 * @Author Jack
 * @Create In 2015年9月8日
 */
public class ServerSelector {

    public static IServer initServer(int port, int cport, String pt) throws Exception {

        switch (pt) {
            case ProtocolType.HTTP:
                ServerConfigWrappar.init(true);
                return new HttpAndHttpsServer(port, cport, false);

            case ProtocolType.HTTPS:
                ServerConfigWrappar.init(true);
                return new HttpAndHttpsServer(port, cport, true);

            case ProtocolType.TCP:
                ServerConfigWrappar.init(false);
                return new TcpAndTlsServer(port, cport, false);

            case ProtocolType.TLS:
                ServerConfigWrappar.init(true);
                return new TcpAndTlsServer(port, cport, true);

            case ProtocolType.WEBSOCKET:
                return null;

            case ProtocolType.CUSTOM:
                return null;

            default:
                ServerConfigWrappar.init(true);
                return new HttpAndHttpsServer(port, cport, false);
        }
    }

}
