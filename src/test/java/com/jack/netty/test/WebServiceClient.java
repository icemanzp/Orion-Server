package com.jack.netty.test;

import junit.framework.TestCase;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Jack on 2017/6/15.
 */
public class WebServiceClient extends TestCase {

    private static final Logger log = LoggerFactory.getLogger(WebServiceClient.class);

    public void testServer() {
        PropertyConfigurator.configure("target/test-classes/config/log4j.properties");

        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        Client client = dcf.createClient("http://127.0.0.1:8090/netty-test/webservice/hw?wsdl");
        try {
            Object[] objs = client.invoke("sayHi", "1");
            log.info(objs[0].toString());
        } catch (Exception e) {
            log.error(e.getMessage());
            assertFalse(true);
        }
        assertTrue(true);
    }
}
