/**
 * @Probject Name: netty-wfj-base-v1
 * @Path: com.jack.netty.server.servletWFJRequestDispatcher.java
 * @Create By Jack
 * @Create In 2016年4月14日 下午6:54:12
 * TODO
 */
package com.jack.netty.server.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jack.netty.util.Assert;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


/**
 * @Class Name WFJRequestDispatcher
 * @Author Jack
 * @Create In 2016年4月14日
 */
public class WFJRequestDispatcher implements RequestDispatcher {

    private final Log logger = LogFactory.getLog(getClass());

    private final String resource;


    /**
     * Create a new MockRequestDispatcher for the given resource.
     *
     * @param resource the server resource to dispatch to, located at a
     *                 particular path or given by a particular name
     */
    public WFJRequestDispatcher(String resource) {
        Assert.notNull(resource, "resource must not be null");
        this.resource = resource;
    }


    @Override
    public void forward(ServletRequest request, ServletResponse response) {
        Assert.notNull(request, "Request must not be null");
        Assert.notNull(response, "Response must not be null");
        if (response.isCommitted()) {
            throw new IllegalStateException("Cannot perform forward - response is already committed");
        }
        getWFJHttpServletResponse(response).setForwardedUrl(this.resource);
        if (logger.isDebugEnabled()) {
            logger.debug("WFJRequestDispatcher: forwarding to [" + this.resource + "]");
        }
    }

    @Override
    public void include(ServletRequest request, ServletResponse response) {
        Assert.notNull(request, "Request must not be null");
        Assert.notNull(response, "Response must not be null");
        getWFJHttpServletResponse(response).addIncludedUrl(this.resource);
        if (logger.isDebugEnabled()) {
            logger.debug("WFJRequestDispatcher: including [" + this.resource + "]");
        }
    }

    /**
     * Obtain the underlying {@link WFJHttpServletResponse}, unwrapping
     * {@link HttpServletResponseWrapper} decorators if necessary.
     */
    protected WFJHttpServletResponse getWFJHttpServletResponse(ServletResponse response) {
        if (response instanceof WFJHttpServletResponse) {
            return (WFJHttpServletResponse) response;
        }
        if (response instanceof HttpServletResponseWrapper) {
            return getWFJHttpServletResponse(((HttpServletResponseWrapper) response).getResponse());
        }
        throw new IllegalArgumentException("WFJRequestDispatcher requires WFJHttpServletResponse");
    }

}
