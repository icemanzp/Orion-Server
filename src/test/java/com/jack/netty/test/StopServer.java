package com.jack.netty.test;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class StopServer extends TestCase {

    public StopServer(String name) {
        super(name);
    }

    public void testStop() {
        Socket s;
        try {
            s = new Socket("localhost", 8083);
            PrintWriter w = new PrintWriter(s.getOutputStream());
            w.println("stop");
            w.flush();
            w.close();
            s.close();
            System.out.println("Hello World! Netty Shutdown!");
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertTrue(true);
    }
}
