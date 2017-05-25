/**
 * @Probject Name: netty-wfj-base-v1
 * @Path: com.jack.netty.server.servletDelegatingServletInputStream.java
 * @Create By Jack
 * @Create In 2016年4月14日 下午7:29:14
 * TODO
 */
package com.jack.netty.server.servlet;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

import com.jack.netty.util.Assert;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Class Name DelegatingServletInputStream
 * @Author Jack
 * @Create In 2016年4月14日
 */
public class DelegatingServletInputStream extends ServletInputStream {

    private final InputStream sourceStream;

    private ReadListener rl;

    public DelegatingServletInputStream(InputStream sourceStream, ReadListener readListener) {
        Assert.notNull(sourceStream, "Source InputStream must not be null");
        Assert.notNull(readListener, "Read Listener must not be null");
        this.sourceStream = sourceStream;
        this.rl = readListener;
    }


    /**
     * Create a DelegatingServletInputStream for the given source stream.
     *
     * @param sourceStream the source stream (never {@code null})
     */
    public DelegatingServletInputStream(InputStream sourceStream) {
        Assert.notNull(sourceStream, "Source InputStream must not be null");
        this.sourceStream = sourceStream;
    }

    /**
     * Return the underlying source stream (never {@code null}).
     */
    public final InputStream getSourceStream() {
        return this.sourceStream;
    }


    @Override
    public int read() throws IOException {
        return this.sourceStream.read();
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.sourceStream.close();
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletInputStream#isReady()
     */
    @Override
    public boolean isReady() {
        // TODO Auto-generated method stub
        boolean result = false;
        try {
            if (this.sourceStream != null && this.sourceStream.available() > 0) {
                result = true;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            result = false;
        }

        return false;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletInputStream#setReadListener(javax.servlet.ReadListener)
     */
    @Override
    public void setReadListener(ReadListener readListener) {
        // TODO Auto-generated method stub
        this.rl = readListener;
    }

    @Override
    public boolean isFinished() {
        // TODO Auto-generated method stub
        boolean result = false;
        try {
            result = this.sourceStream.available() <= 0 ? true : false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            result = false;
        }
        return result;
    }

}
