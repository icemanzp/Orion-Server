package com.jack.netty.test;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Jack on 2017/6/5.
 */
public class RestIncommCount extends TestCase {

    public RestIncommCount(String name){
        super(name);
    }

    public void testRestIncomeCount(){
        Socket s;
        try {
            s = new Socket("localhost", 8083);
            PrintWriter w = new PrintWriter(s.getOutputStream());
            w.println("setcount:1500");
            w.flush();
            w.close();
            s.close();
            System.out.println("Hello World! Netty Reset Income Count !");
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
