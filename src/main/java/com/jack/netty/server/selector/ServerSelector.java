/**
 * @Probject Name: netty-wfj-server
 * @Path: com.jack.netty.serverServerSelector.java
 * @Create By Jack
 * @Create In 2015年9月8日 下午5:11:22
 * TODO
 */
package com.jack.netty.server.selector;

import com.jack.netty.server.dto.ProtocolType;
import com.jack.netty.server.infc.IServer;

/**
 * @Class Name ServerSelector
 * @Author Jack
 * @Create In 2015年9月8日
 */
public class ServerSelector {

    public static IServer initServer(int port, int cport, String pt) {

        switch (pt) {
            case ProtocolType.HTTP:
                return new HTTPServer(port, cport);

            case ProtocolType.HTTPS:
                return new HTTPSServer(port, cport);

            case ProtocolType.TCP:
                return null;

            case ProtocolType.TLS:
                return new HTTPSServer(port, cport);

            case ProtocolType.WEBSOCKET:
                return null;

            case ProtocolType.CUSTOM:
                return null;

            default:
                return new HTTPServer(port, cport);
        }
    }

}
