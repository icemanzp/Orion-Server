/**
 * @Probject Name: netty-wfj-base-v1
 * @Path: com.jack.netty.server.servletWFJServletRegistration.java
 * @Create By Jack
 * @Create In 2016年4月15日 下午5:13:16
 * TODO
 */
package com.jack.netty.server.servlet;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.ServletSecurityElement;
import java.util.*;


/**
 * @Class Name WFJServletRegistration
 * @Author Jack
 * @Create In 2016年4月15日
 */
public class WFJServletDynamic implements Dynamic {

    private MultipartConfigElement mce;
    private String runAsRole;
    private String name;
    private Class<? extends Servlet> container;

    private boolean asyncSupported;
    private int loadOnStartup;
    private ServletSecurityElement sse;

    private boolean isSpring = false;

    private final Set<String> conflicts = new HashSet<String>();

    private final Map<String, String> initParameters = new LinkedHashMap<String, String>();

    /**
     * @Return the Servlet container
     */
    public Class<? extends Servlet> getContainer() {
        return container;
    }

    /**
     * @Return the int loadOnStartup
     */
    public int getLoadOnStartup() {
        return loadOnStartup;
    }

    public WFJServletDynamic(String name, Class<? extends Servlet> container) {
        initServlet(name, container);
    }

    /**
     * @param name
     * @param className void
     * @Methods Name initServlet
     * @Create In 2016年4月22日 By Jack
     */
    private void initServlet(String name, Class<? extends Servlet> container) {
        this.name = name;
        this.container = container;

        this.runAsRole = "";
        this.asyncSupported = false;
        loadOnStartup = -1;
        sse = null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRegistration#addMapping(java.lang.String[])
     */
    @Override
    public Set<String> addMapping(String... urlPatterns) {
        // TODO Auto-generated method stub
        if (urlPatterns == null) {
            return Collections.emptySet();
        }

        for (String urlPattern : urlPatterns) {
            conflicts.add(urlPattern);
        }

        if (!conflicts.isEmpty()) {
            return conflicts;
        }
        return Collections.emptySet();
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRegistration#getMappings()
     */
    @Override
    public Collection<String> getMappings() {
        // TODO Auto-generated method stub
        if (this.conflicts.isEmpty()) {
            return Collections.emptySet();
        }

        return conflicts;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRegistration#getRunAsRole()
     */
    @Override
    public String getRunAsRole() {
        // TODO Auto-generated method stub
        return this.runAsRole;
    }

    /* (non-Javadoc)
     * @see javax.servlet.Registration#getName()
     */
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return this.name;
    }

    /* (non-Javadoc)
     * @see javax.servlet.Registration#getClassName()
     */
    @Override
    public String getClassName() {
        // TODO Auto-generated method stub
        return this.container.getClass().getName();
    }

    /* (non-Javadoc)
     * @see javax.servlet.Registration#setInitParameter(java.lang.String, java.lang.String)
     */
    @Override
    public boolean setInitParameter(String name, String value) {
        // TODO Auto-generated method stub
        if (this.initParameters.containsKey(name)) {
            return false;
        } else {
            this.initParameters.put(name, value);

            return true;
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.Registration#getInitParameter(java.lang.String)
     */
    @Override
    public String getInitParameter(String name) {
        // TODO Auto-generated method stub
        return this.initParameters.get(name);
    }

    /* (non-Javadoc)
     * @see javax.servlet.Registration#setInitParameters(java.util.Map)
     */
    @Override
    public Set<String> setInitParameters(Map<String, String> initParameters) {
        // TODO Auto-generated method stub
        this.initParameters.clear();
        this.initParameters.putAll(initParameters);
        return this.initParameters.keySet();
    }

    /* (non-Javadoc)
     * @see javax.servlet.Registration#getInitParameters()
     */
    @Override
    public Map<String, String> getInitParameters() {
        // TODO Auto-generated method stub
        return this.initParameters;
    }

    /* (non-Javadoc)
     * @see javax.servlet.Registration.Dynamic#setAsyncSupported(boolean)
     */
    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {
        // TODO Auto-generated method stub
        this.asyncSupported = isAsyncSupported;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRegistration.Dynamic#setLoadOnStartup(int)
     */
    @Override
    public void setLoadOnStartup(int loadOnStartup) {
        // TODO Auto-generated method stub
        this.loadOnStartup = loadOnStartup;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRegistration.Dynamic#setServletSecurity(javax.servlet.ServletSecurityElement)
     */
    @Override
    public Set<String> setServletSecurity(ServletSecurityElement constraint) {
        // TODO Auto-generated method stub
        this.sse = constraint;
        Set<String> cc = new HashSet<String>();
        while (this.sse.getMethodNames().iterator().hasNext()) {
            cc.add(this.sse.getMethodNames().iterator().next());
        }
        return cc;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRegistration.Dynamic#setMultipartConfig(javax.servlet.MultipartConfigElement)
     */
    @Override
    public void setMultipartConfig(MultipartConfigElement multipartConfig) {
        // TODO Auto-generated method stub
        this.mce = multipartConfig;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRegistration.Dynamic#setRunAsRole(java.lang.String)
     */
    @Override
    public void setRunAsRole(String roleName) {
        // TODO Auto-generated method stub
        this.runAsRole = roleName;
    }

    /**
     * @Return the boolean isSpring
     */
    public boolean isSpring() {
        return isSpring;
    }

    /**
     * @Param boolean isSpring to set
     */
    public void setSpring(boolean isSpring) {
        this.isSpring = isSpring;
    }

}
