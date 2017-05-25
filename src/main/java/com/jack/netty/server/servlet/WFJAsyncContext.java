/**
 * @Probject Name: netty-wfj-base-v1
 * @Path: com.jack.netty.server.servletWFJAsyncContext.java
 * @Create By Jack
 * @Create In 2016年4月14日 下午7:08:56
 * TODO
 */
package com.jack.netty.server.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jack.netty.server.http.util.WebUtils;
import com.jack.netty.util.Assert;
import com.jack.netty.util.ClassUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Class Name WFJAsyncContext
 * @Author Jack
 * @Create In 2016年4月14日
 */
public class WFJAsyncContext implements AsyncContext {
    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private final List<AsyncListener> listeners = new ArrayList<AsyncListener>();

    private String dispatchedPath;

    private long timeout = 10 * 1000L;    // 10 seconds is Tomcat's default

    private final List<Runnable> dispatchHandlers = new ArrayList<Runnable>();


    public WFJAsyncContext(ServletRequest request, ServletResponse response) {
        this.request = (HttpServletRequest) request;
        this.response = (HttpServletResponse) response;
    }


    public void addDispatchHandler(Runnable handler) {
        Assert.notNull(handler);
        this.dispatchHandlers.add(handler);
    }

    @Override
    public ServletRequest getRequest() {
        return this.request;
    }

    @Override
    public ServletResponse getResponse() {
        return this.response;
    }

    @Override
    public boolean hasOriginalRequestAndResponse() {
        return (this.request instanceof WFJHttpServletRequest) && (this.response instanceof WFJHttpServletResponse);
    }

    @Override
    public void dispatch() {
        dispatch(this.request.getRequestURI());
    }

    @Override
    public void dispatch(String path) {
        dispatch(null, path);
    }

    @Override
    public void dispatch(ServletContext context, String path) {
        this.dispatchedPath = path;
        for (Runnable r : this.dispatchHandlers) {
            r.run();
        }
    }

    public String getDispatchedPath() {
        return this.dispatchedPath;
    }

    @Override
    public void complete() {
        WFJHttpServletRequest mockRequest = WebUtils.getNativeRequest(request, WFJHttpServletRequest.class);
        if (mockRequest != null) {
            mockRequest.setAsyncStarted(false);
        }
        for (AsyncListener listener : this.listeners) {
            try {
                listener.onComplete(new AsyncEvent(this, this.request, this.response));
            } catch (IOException e) {
                throw new IllegalStateException("AsyncListener failure", e);
            }
        }
    }

    @Override
    public void start(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void addListener(AsyncListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void addListener(AsyncListener listener, ServletRequest request, ServletResponse response) {
        this.listeners.add(listener);
    }

    public List<AsyncListener> getListeners() {
        return this.listeners;
    }

    @Override
    public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException {
        try {
            return ClassUtils.instantiateClass(clazz);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new ServletException("Create AsyncListener Failed! ", e);
        }
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public long getTimeout() {
        return this.timeout;
    }

}
