/**
 * @Probject Name: WFJ-Base-Server-Dev
 * @Path: com.jack.netty.server.servletWFJFilterRegistration.java
 * @Create By Jack
 * @Create In 2016年8月16日 下午12:59:30
 * TODO
 */
package com.jack.netty.server.servlet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration.Dynamic;

import com.jack.netty.server.container.factory.ServerConfigWrappar;

import java.util.*;

/**
 * @Class Name WFJFilterRegistration
 * @Author Jack
 * @Create In 2016年8月16日
 */
public class WFJFilterRegistration implements Dynamic {

    private String name;

    private boolean asyncSupported;

    private Class<? extends Filter> container;

    private final Map<String, String> initParameters = new LinkedHashMap<String, String>();

    public WFJFilterRegistration(String name, Class<? extends Filter> container) {
        init(name, container, false);
    }

    /**
     * @param name2
     * @param container2
     * @param b          void
     * @Methods Name WFJFilterRegistration
     * @Create In 2016年8月16日 By Jack
     */
    private void init(String name, Class<? extends Filter> container, boolean asyncSupported) {
        // TODO Auto-generated method stub
        this.name = name;
        this.container = container;
        this.asyncSupported = asyncSupported;
    }

    public WFJFilterRegistration(String name, Class<? extends Filter> container, boolean asyncSupported) {
        init(name, container, asyncSupported);
    }

    /* (non-Javadoc)
     * @see javax.servlet.FilterRegistration#addMappingForServletNames(java.util.EnumSet, boolean, java.lang.String[])
     */
    @Override
    public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter,
            String... servletNames) {
        // TODO Auto-generated method stub
        if (servletNames == null) {
            return;
        }

        WFJFilterMap filterMap = new WFJFilterMap();

        filterMap.setFilterName(this.name);

        if (dispatcherTypes != null) {
            for (DispatcherType dispatcherType : dispatcherTypes) {
                filterMap.setDispatcher(dispatcherType.name());
            }
        }

        if (servletNames != null) {
            for (String servletName : servletNames) {
                filterMap.addServletName(servletName);
            }
        }

        if (isMatchAfter) {
            ServerConfigWrappar.addFilterMap(filterMap);
        } else {
            ServerConfigWrappar.addFilterMapBefore(filterMap);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.FilterRegistration#getServletNameMappings()
     */
    @Override
    public Collection<String> getServletNameMappings() {
        // TODO Auto-generated method stub
        Collection<String> result = new HashSet<>();

        WFJFilterMap[] filterMaps = ServerConfigWrappar.findFilterMaps();

        for (WFJFilterMap filterMap : filterMaps) {
            if (filterMap.getFilterName().equals(this.name)) {
                for (String servletName : filterMap.getServletNames()) {
                    result.add(servletName);
                }
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see javax.servlet.FilterRegistration#addMappingForUrlPatterns(java.util.EnumSet, boolean, java.lang.String[])
     */
    @Override
    public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter,
            String... urlPatterns) {
        // TODO Auto-generated method stub
        if (urlPatterns == null) {
            return;
        }

        WFJFilterMap filterMap = new WFJFilterMap();

        filterMap.setFilterName(this.name);

        if (dispatcherTypes != null) {
            for (DispatcherType dispatcherType : dispatcherTypes) {
                filterMap.setDispatcher(dispatcherType.name());
            }
        }

        if (urlPatterns != null) {
            for (String urlPattern : urlPatterns) {
                filterMap.addURLPattern(urlPattern);
            }

            if (isMatchAfter) {
                ServerConfigWrappar.addFilterMap(filterMap);
            } else {
                ServerConfigWrappar.addFilterMapBefore(filterMap);
            }
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.FilterRegistration#getUrlPatternMappings()
     */
    @Override
    public Collection<String> getUrlPatternMappings() {
        // TODO Auto-generated method stub
        Collection<String> result = new HashSet<>();

        WFJFilterMap[] filterMaps = ServerConfigWrappar.findFilterMaps();

        for (WFJFilterMap filterMap : filterMaps) {
            if (filterMap.getFilterName().equals(this.name)) {
                for (String urlPattern : filterMap.getURLPatterns()) {
                    result.add(urlPattern);
                }
            }
        }
        return result;
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
        return container.getClass().getName();
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

    /**
     * @Return the boolean asyncSupported
     */
    public boolean isAsyncSupported() {
        return asyncSupported;
    }

    /**
     * @Return the Filter container
     */
    public Class<? extends Filter> getContainer() {
        return this.container;
    }
}
