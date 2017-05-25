/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jack.netty.server.servlet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.jack.netty.server.container.factory.ServerConfigWrappar;

/**
 * Factory for the creation and caching of Filters and creation
 * of Filter Chains.
 *
 * @author Greg Murray
 * @author Remy Maucherat
 */
public final class WFJFilterFactory {

    private WFJFilterFactory() {
        // Prevent instance creation. This is a utility class.
    }


    /**
     * Construct and return a FilterChain implementation that will wrap the
     * execution of the specified servlet instance.  If we should not execute
     * a filter chain at all, return <code>null</code>.
     *
     * @param request The servlet request we are processing
     * @param servlet The servlet instance to be wrapped
     */
    public static WFJFilterChain createFilterChain(ServletRequest request, Servlet servlet) {

        // 如果Servlet 没有内容则不执行任何创建
        if (servlet == null) {
            return (null);
        }

        // 从请求中获取分发类型
        DispatcherType dispatcher = request.getDispatcherType();
        String requestPath = ((HttpServletRequest) request).getRequestURI();


        // 开始构建过滤链
        WFJFilterChain filterChain = new WFJFilterChain();
        filterChain.setServlet(servlet);

        //从 Servlet 获取 ServletContext
        WFJServletContext context = (WFJServletContext) servlet.getServletConfig().getServletContext();
        WFJFilterMap filterMaps[] = ServerConfigWrappar.findFilterMaps();

        // 如果不存在后续过滤链，则无需处理返回空对象，执行后续 Servlet
        if ((filterMaps == null) || (filterMaps.length == 0)) {
            return (filterChain);
        }

        //  设定 Servlet 对象名称
        String servletName = servlet.getServletConfig().getServletName();

        // 首先根据请求路径检索是否需要过滤，如需要则加入过滤链中
        for (int i = 0; i < filterMaps.length; i++) {
            if (!matchDispatcher(filterMaps[i], dispatcher)) {
                continue;
            }
            if (!matchFiltersURL(filterMaps[i], requestPath)) {
                continue;
            }
            Filter filter = context.getFilter(filterMaps[i].getFilterName());
            if (filter == null) {
                // FIXME - log configuration problem
                continue;
            }
            filterChain.addFilter(filter);
        }

        // 其次根据 Servlet 名称检索是否需要过滤，如需要则键入过滤链中
        for (int i = 0; i < filterMaps.length; i++) {
            if (!matchDispatcher(filterMaps[i], dispatcher)) {
                continue;
            }
            if (!matchFiltersServlet(filterMaps[i], servletName)) {
                continue;
            }
            Filter filter = context.getFilter(filterMaps[i].getFilterName());
            if (filter == null) {
                // FIXME - log configuration problem
                continue;
            }
            filterChain.addFilter(filter);
        }

        // 完成构建返回
        return (filterChain);
    }

    /**
     * Return <code>true</code> if the context-relative request path
     * matches the requirements of the specified filter mapping;
     * otherwise, return <code>false</code>.
     *
     * @param filterMap   Filter mapping being checked
     * @param requestPath Context-relative request path of this request
     */
    private static boolean matchFiltersURL(WFJFilterMap filterMap, String requestPath) {

        // Check the specific "*" special URL pattern, which also matches
        // named dispatches
        if (filterMap.getMatchAllUrlPatterns()) {
            return (true);
        }

        if (requestPath == null) {
            return (false);
        }

        // Match on context relative request path
        String[] testPaths = filterMap.getURLPatterns();

        for (int i = 0; i < testPaths.length; i++) {
            if (matchFiltersURL(testPaths[i], requestPath)) {
                return (true);
            }
        }

        // No match
        return (false);

    }


    /**
     * Return <code>true</code> if the context-relative request path
     * matches the requirements of the specified filter mapping;
     * otherwise, return <code>false</code>.
     *
     * @param testPath    URL mapping being checked
     * @param requestPath Context-relative request path of this request
     */
    private static boolean matchFiltersURL(String testPath, String requestPath) {

        if (testPath == null) {
            return (false);
        }

        // Case 1 - Exact Match
        if (testPath.equals(requestPath)) {
            return (true);
        }

        // Case 2 - Path Match ("/.../*")
        if (testPath.equals("/*")) {
            return (true);
        }
        if (testPath.endsWith("/*")) {
            if (testPath.regionMatches(0, requestPath, 0,
                    testPath.length() - 2)) {
                if (requestPath.length() == (testPath.length() - 2)) {
                    return (true);
                } else if ('/' == requestPath.charAt(testPath.length() - 2)) {
                    return (true);
                }
            }
            return (false);
        }

        // Case 3 - Extension Match
        if (testPath.startsWith("*.")) {
            int slash = requestPath.lastIndexOf('/');
            int period = requestPath.lastIndexOf('.');
            if ((slash >= 0) && (period > slash)
                    && (period != requestPath.length() - 1)
                    && ((requestPath.length() - period)
                    == (testPath.length() - 1))) {
                return (testPath.regionMatches(2, requestPath, period + 1,
                        testPath.length() - 2));
            }
        }

        // Case 4 - "Default" Match
        return (false); // NOTE - Not relevant for selecting filters

    }


    /**
     * Return <code>true</code> if the specified servlet name matches
     * the requirements of the specified filter mapping; otherwise
     * return <code>false</code>.
     *
     * @param filterMap   Filter mapping being checked
     * @param servletName Servlet name being checked
     */
    private static boolean matchFiltersServlet(WFJFilterMap filterMap,
            String servletName) {

        if (servletName == null) {
            return (false);
        }
        // Check the specific "*" special servlet name
        else if (filterMap.getMatchAllServletNames()) {
            return (true);
        } else {
            String[] servletNames = filterMap.getServletNames();
            for (int i = 0; i < servletNames.length; i++) {
                if (servletName.equals(servletNames[i])) {
                    return (true);
                }
            }
            return false;
        }

    }


    /**
     * Convenience method which returns true if  the dispatcher type
     * matches the dispatcher types specified in the FilterMap
     */
    private static boolean matchDispatcher(WFJFilterMap filterMap, DispatcherType type) {
        switch (type) {
            case FORWARD:
                if ((filterMap.getDispatcherMapping() & WFJFilterMap.FORWARD) > 0) {
                    return true;
                }
                break;
            case INCLUDE:
                if ((filterMap.getDispatcherMapping() & WFJFilterMap.INCLUDE) > 0) {
                    return true;
                }
                break;
            case REQUEST:
                if ((filterMap.getDispatcherMapping() & WFJFilterMap.REQUEST) > 0) {
                    return true;
                }
                break;
            case ERROR:
                if ((filterMap.getDispatcherMapping() & WFJFilterMap.ERROR) > 0) {
                    return true;
                }
                break;
            case ASYNC:
                if ((filterMap.getDispatcherMapping() & WFJFilterMap.ASYNC) > 0) {
                    return true;
                }
                break;
        }
        return false;
    }
}
