package com.jack.netty.test;

import com.jack.netty.Server;
import com.jack.netty.server.dto.ProtocolType;
import junit.framework.TestCase;
import org.apache.log4j.PropertyConfigurator;

import java.net.BindException;


public class StartServer extends TestCase {

    public StartServer(String name) {
        super(name);
    }

    /**
     * Rigourous Test :-)
     *
     * @throws Exception
     */
    public void testStart() throws Exception {

        System.out.println("Hello World! Netty Startup!");
        PropertyConfigurator.configure("target/test-classes/config/log4j.properties");

        Server server = new Server(8090, 8083, ProtocolType.TCP);
        try {
            server.run();
        } catch (BindException e) {
            System.out.println("服务器已启动，请不要重复启动");
            e.printStackTrace();
            server.shutdown(-1);

        } catch (Exception e) {
            e.printStackTrace();
            server.shutdown(-1);
        }

        assertTrue(true);
    }
}
