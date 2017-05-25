/**
 * @Probject Name: netty-wfj-base-v1
 * @Path: MockSessionCookieConfigWFJSessionCookieConfig.java
 * @Create By Jack
 * @Create In 2016年4月14日 下午7:02:21
 * TODO
 */
package com.jack.netty.server.servlet;

import javax.servlet.SessionCookieConfig;

/**
 * @Class Name WFJSessionCookieConfig
 * @Author Jack
 * @Create In 2016年4月14日
 */
public class WFJSessionCookieConfig implements SessionCookieConfig {

    private String name;

    private String domain;

    private String path;

    private String comment;

    private boolean httpOnly;

    private boolean secure;

    private int maxAge;


    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String getDomain() {
        return this.domain;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    @Override
    public boolean isHttpOnly() {
        return this.httpOnly;
    }

    @Override
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    @Override
    public boolean isSecure() {
        return this.secure;
    }

    @Override
    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public int getMaxAge() {
        return this.maxAge;
    }

}
