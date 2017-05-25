/**
 * @Probject Name: WFJ-Base-Server-Dev
 * @Path: com.jack.netty.server.servletWFJServletRegistration.java
 * @Create By Jack
 * @Create In 2016年4月29日 下午2:22:26
 * TODO
 */
package com.jack.netty.server.servlet;

import javax.servlet.Servlet;
import javax.servlet.ServletRegistration;
import java.util.*;

/**
 * @Class Name WFJServletRegistration
 * @Author Jack
 * @Create In 2016年4月29日
 */
public class WFJServletRegistration implements ServletRegistration {

    private String runAsRole;
    private String name;
    private Servlet container;

    private final Set<String> conflicts = new HashSet<String>();
    private Map<String, String> initParameters = new LinkedHashMap<String, String>();

    public WFJServletRegistration(Servlet container) {
        init(container.getServletConfig().getServletName(), container, "");
    }

    public WFJServletRegistration(String name, Servlet container) {
        init(name, container, "");
    }

    public WFJServletRegistration(String name, Servlet container, String runAsRole) {
        init(name, container, runAsRole);
    }

    /**
     * @param name
     * @param className
     * @param runAsRole void
     * @Methods Name init
     * @Create In 2016年4月29日 By Jack
     */
    private void init(String name, Servlet container, String runAsRole) {
        this.runAsRole = runAsRole;
        this.name = name;
        this.container = container;
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
        return initParameters.put(name, value) != null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.Registration#getInitParameter(java.lang.String)
     */
    @Override
    public String getInitParameter(String name) {
        // TODO Auto-generated method stub
        return initParameters.get(name);
    }

    /* (non-Javadoc)
     * @see javax.servlet.Registration#setInitParameters(java.util.Map)
     */
    @Override
    public Set<String> setInitParameters(Map<String, String> initParameters) {
        // TODO Auto-generated method stub
        this.initParameters = initParameters;
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

    /**
     * @Return the Servlet container
     */
    public Servlet getContainer() {
        return container;
    }

}
