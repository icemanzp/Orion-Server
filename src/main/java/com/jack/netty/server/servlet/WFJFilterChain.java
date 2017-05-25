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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jack.netty.util.ExceptionUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Implementation of <code>javax.servlet.FilterChain</code> used to manage the
 * execution of a set of filters for a particular request. When the set of
 * defined filters has all been executed, the next call to
 * <code>doFilter()</code> will execute the servlet's <code>service()</code>
 * method itself.
 *
 * @author Craig R. McClanahani
 */
public final class WFJFilterChain implements FilterChain {

    private Logger logger = LoggerFactory.getLogger(WFJFilterChain.class);

    private Servlet servlet = null;

    private Filter[] filterInited = new Filter[0];

    /**
     * The int which is used to maintain the current position in the filter
     * chain.
     */
    private int pos = 0;

    /**
     * The int which gives the current number of filters in the chain.
     */
    private int n = 0;

    public static final int INCREMENT = 10;

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.FilterChain#doFilter(javax.servlet.ServletRequest,
     * javax.servlet.ServletResponse)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        // TODO Auto-generated method stub
        internalDoFilter(request, response);
    }

    private void internalDoFilter(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {

        // Call the next filter if there is one
        if (pos < n) {
            Filter filter = null;
            try {
                filter = filterInited[pos++];
                filter.doFilter(request, response, this);

            } catch (IOException | ServletException | RuntimeException e) {
                logger.error("Do filter excurte error: " + e.getMessage());
                throw e;
            } catch (Throwable e) {
                e = ExceptionUtils.unwrapInvocationTargetException(e);
                ExceptionUtils.handleThrowable(e);
                logger.error("Do filter excurte error: " + e.getMessage());
                throw new ServletException("Do filter excurte error" + e);
            }
            return;
        }

        // We fell off the end of the chain -- call the servlet instance
        try {
            // Use potentially wrapped request from this point
            if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
                servlet.service(request, response);
            } else {
                servlet.service(request, response);
            }

        } catch (IOException e) {
            logger.error(
                    "Do servlet[" + servlet.getServletConfig().getServletName() + "] excurte error: " + e.getMessage());
            throw e;
        } catch (ServletException e) {
            logger.error(
                    "Do servlet[" + servlet.getServletConfig().getServletName() + "] excurte error: " + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            logger.error(
                    "Do servlet[" + servlet.getServletConfig().getServletName() + "] excurte error: " + e.getMessage());
            throw e;
        } catch (Throwable e) {
            ExceptionUtils.handleThrowable(e);
            logger.error(
                    "Do servlet[" + servlet.getServletConfig().getServletName() + "] excurte error: " + e.getMessage());
            throw new ServletException("Do servlet[" + servlet.getServletConfig().getServletName() + "] excurte error",
                    e);
        }
    }

    /**
     * Add a filter to the set of filters that will be executed in this chain.
     *
     * @param filterConfig The FilterConfig for the servlet to be executed
     */
    public void addFilter(Filter filter) {

        // Prevent the same filter being added multiple times
        for (Filter item : filterInited) {
            if (item == filter) {
                return;
            }
        }

        if (n == filterInited.length) {
            Filter[] newFilters = new Filter[n + INCREMENT];
            System.arraycopy(filterInited, 0, newFilters, 0, n);
            filterInited = newFilters;
        }
        filterInited[n++] = filter;

    }

    /**
     * Release references to the filters and wrapper executed by this chain.
     */
    public void release() {

        for (int i = 0; i < n; i++) {
            filterInited[i] = null;
        }
        n = 0;
        pos = 0;
        servlet = null;

    }

    /**
     * Prepare for reuse of the filters and wrapper executed by this chain.
     */
    void reuse() {
        pos = 0;
    }

    /**
     * @Return the Servlet servlet
     */
    public Servlet getServlet() {
        return servlet;
    }

    /**
     * @Param Servlet servlet to set
     */
    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

}
