/**
 * @Probject Name: WFJ-Base-Server-Dev
 * @Path: com.jack.netty.util.infcResourceLoader.java
 * @Create By Jack
 * @Create In 2016年8月16日 上午9:56:31
 * TODO
 */
package com.jack.netty.conf.infc;


import com.jack.netty.conf.util.ResourceUtils;

/**
 * @Class Name ResourceLoader
 * @Author Jack
 * @Create In 2016年8月16日
 */
public interface ResourceLoader {

    /**
     * Pseudo URL prefix for loading from the class path: "classpath:"
     */
    String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;


    /**
     * Return a Resource handle for the specified resource.
     * The handle should always be a reusable resource descriptor,
     * allowing for multiple {@link Resource#getInputStream()} calls.
     * <p><ul>
     * <li>Must support fully qualified URLs, e.g. "file:C:/test.dat".
     * <li>Must support classpath pseudo-URLs, e.g. "classpath:test.dat".
     * <li>Should support relative file paths, e.g. "WEB-INF/test.dat".
     * (This will be implementation-specific, typically provided by an
     * ApplicationContext implementation.)
     * </ul>
     * <p>Note that a Resource handle does not imply an existing resource;
     * you need to invoke {@link Resource#exists} to check for existence.
     *
     * @param location the resource location
     * @return a corresponding Resource handle
     * @see #CLASSPATH_URL_PREFIX
     * @see org.springframework.core.io.Resource#exists
     * @see org.springframework.core.io.Resource#getInputStream
     */
    Resource getResource(String location);

    /**
     * Expose the ClassLoader used by this ResourceLoader.
     * <p>Clients which need to access the ClassLoader directly can do so
     * in a uniform manner with the ResourceLoader, rather than relying
     * on the thread context ClassLoader.
     *
     * @return the ClassLoader (never {@code null})
     */
    ClassLoader getClassLoader();

}
