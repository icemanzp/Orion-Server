/**
 * @Probject Name: WFJ-Base-Server-Dev
 * @Path: com.jack.netty.server.servletWFJFilterConfig.java
 * @Create By Jack
 * @Create In 2016年8月16日 下午2:17:03
 * TODO
 */
package com.jack.netty.server.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jack.netty.util.Assert;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Class Name WFJFilterConfig
 * @Author Jack
 * @Create In 2016年8月16日
 */
public final class WFJFilterConfig implements FilterConfig, Serializable {

    private static Logger logger = LoggerFactory.getLogger(WFJFilterConfig.class);
    /**
     * @Field long serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private ServletContext sc;

    private String name;

    private final Map<String, String> initParameters = new LinkedHashMap<String, String>();

    public WFJFilterConfig(ServletContext sc, String name) {
        this.sc = sc;
        this.name = name;
        initParameters();
    }

    private void initParameters() {
        Enumeration<String> iterator = this.sc.getInitParameterNames();
        while (iterator.hasMoreElements()) {
            String key = iterator.nextElement();
            this.addInitParameter(key, this.sc.getInitParameter(key));
        }
    }

    @Override
    public ServletContext getServletContext() {
        // TODO Auto-generated method stub
        return this.sc;
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        // TODO Auto-generated method stub
        return Collections.enumeration(this.initParameters.keySet());
    }

    @Override
    public String getInitParameter(String name) {
        // TODO Auto-generated method stub
        Assert.notNull(name, "Parameter name must not be null");
        return this.initParameters.get(name);
    }

    @Override
    public String getFilterName() {
        // TODO Auto-generated method stub
        return this.name;
    }

    public void addInitParameter(String name, String value) {
        Assert.notNull(name, "Parameter name must not be null");
        this.initParameters.put(name, value);
    }
}
