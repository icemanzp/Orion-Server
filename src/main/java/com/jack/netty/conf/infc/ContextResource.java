/**
 * @Probject Name: WFJ-Base-Server-Dev
 * @Path: com.jack.netty.conf.infcContextResource.java
 * @Create By Jack
 * @Create In 2016年8月16日 上午10:14:53
 * TODO
 */
package com.jack.netty.conf.infc;

/**
 * @Class Name ContextResource
 * @Author Jack
 * @Create In 2016年8月16日
 */
public interface ContextResource extends Resource {
    /**
     * Return the path within the enclosing 'context'.
     * <p>This is typically path relative to a context-specific root directory,
     * e.g. a ServletContext root or a PortletContext root.
     */
    String getPathWithinContext();
}
