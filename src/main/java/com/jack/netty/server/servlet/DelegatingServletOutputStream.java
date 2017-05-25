/**
 * @Probject Name: netty-wfj-base-v1
 * @Path: com.jack.netty.server.servletDelegatingServletOutputStream.java
 * @Create By Jack
 * @Create In 2016年4月14日 下午7:15:23
 * TODO
 */
package com.jack.netty.server.servlet;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

import com.jack.netty.util.Assert;

import java.io.IOException;
import java.io.OutputStream;


/**
 * @Class Name DelegatingServletOutputStream
 * @Author Jack
 * @Create In 2016年4月14日
 */
public class DelegatingServletOutputStream extends ServletOutputStream {

    private final OutputStream targetStream;

    private WriteListener writelListener;

    public DelegatingServletOutputStream(OutputStream targetStream, WriteListener writelListener) {
        Assert.notNull(targetStream, "Target OutputStream must not be null");
        Assert.notNull(writelListener, "Writel Listener must not be null");
        this.targetStream = targetStream;
        this.writelListener = writelListener;
    }


    /**
     * Create a DelegatingServletOutputStream for the given target stream.
     *
     * @param targetStream the target stream (never {@code null})
     */
    public DelegatingServletOutputStream(OutputStream targetStream) {
        Assert.notNull(targetStream, "Target OutputStream must not be null");
        this.targetStream = targetStream;
    }

    /**
     * Return the underlying target stream (never {@code null}).
     */
    public final OutputStream getTargetStream() {
        return this.targetStream;
    }


    @Override
    public void write(int b) throws IOException {
        this.targetStream.write(b);
    }

    @Override
    public void flush() throws IOException {
        super.flush();
        this.targetStream.flush();
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.targetStream.close();
    }

    @Override
    public boolean isReady() {
        // TODO Auto-generated method stub

        return this.targetStream != null ? true : false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        // TODO Auto-generated method stub
        this.writelListener = writelListener;
    }
}
