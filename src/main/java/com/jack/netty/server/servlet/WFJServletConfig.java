/**
 * @Probject Name: netty-wfj-base-v1
 * @Path: com.jack.netty.server.servletWFJServletConfig.java
 * @Create By Jack
 * @Create In 2016年4月14日 下午7:41:20
 * TODO
 */
package com.jack.netty.server.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import com.jack.netty.util.Assert;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Class Name WFJServletConfig
 * @Author Jack
 * @Create In 2016年4月14日
 */
public class WFJServletConfig implements ServletConfig {

    private final ServletContext servletContext;

    private final String servletName;

    private final Map<String, String> initParameters = new LinkedHashMap<String, String>();


    /**
     * Create a new MockServletConfig with a default {@link MockServletContext}.
     */
    public WFJServletConfig() {
        this(null, "");
    }

    /**
     * Create a new MockServletConfig with a default {@link MockServletContext}.
     *
     * @param servletName the name of the servlet
     */
    public WFJServletConfig(String servletName) {
        this(null, servletName);
    }

    /**
     * Create a new MockServletConfig.
     *
     * @param servletContext the ServletContext that the servlet runs in
     */
    public WFJServletConfig(ServletContext servletContext) {
        this(servletContext, "");
    }

    /**
     * Create a new MockServletConfig.
     *
     * @param servletContext the ServletContext that the servlet runs in
     * @param servletName    the name of the servlet
     */
    public WFJServletConfig(ServletContext servletContext, String servletName) {
        this.servletContext = (servletContext != null ? servletContext : new WFJServletContext());
        this.servletName = servletName;
        initParameters();
    }

    private void initParameters() {
        Enumeration<String> iterator = this.servletContext.getInitParameterNames();
        while (iterator.hasMoreElements()) {
            String key = iterator.nextElement();
            this.addInitParameter(key, this.servletContext.getInitParameter(key));
        }
    }


    @Override
    public String getServletName() {
        return this.servletName;
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public void addInitParameter(String name, String value) {
        Assert.notNull(name, "Parameter name must not be null");
        this.initParameters.put(name, value);
    }

    @Override
    public String getInitParameter(String name) {
        Assert.notNull(name, "Parameter name must not be null");
        return this.initParameters.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(this.initParameters.keySet());
    }

}
