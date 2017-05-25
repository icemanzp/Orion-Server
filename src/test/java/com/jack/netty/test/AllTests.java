package com.jack.netty.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(AllTests.class.getName());
        //$JUnit-BEGIN$
        suite.addTestSuite(StartServer.class);
        suite.addTestSuite(com.jack.netty.test.MonitorTest.class);
        suite.addTestSuite(StopServer.class);
        //$JUnit-END$
        return suite;
    }

}
