/**
 * @Probject Name: WFJ-Base-Server-Dev
 * @Path: com.jack.netty.utilLog4jWebConfigurer.java
 * @Create By Jack
 * @Create In 2016年8月16日 上午9:48:10
 * TODO
 */
package com.jack.netty.util;

import javax.servlet.ServletContext;

import com.jack.netty.conf.util.ResourceUtils;
import com.jack.netty.conf.util.ServletContextPropertyUtils;
import com.jack.netty.server.http.util.WebUtils;

import java.io.FileNotFoundException;


/**
 * @Class Name Log4jWebConfigurer
 * @Author Jack
 * @Create In 2016年8月16日
 */
public abstract class Log4jWebConfigurer {

    /**
     * Parameter specifying the location of the log4j config file
     */
    public static final String CONFIG_LOCATION_PARAM = "log4jConfigLocation";

    /**
     * Parameter specifying the refresh interval for checking the log4j config file
     */
    public static final String REFRESH_INTERVAL_PARAM = "log4jRefreshInterval";

    /**
     * Parameter specifying whether to expose the web app root system property
     */
    public static final String EXPOSE_WEB_APP_ROOT_PARAM = "log4jExposeWebAppRoot";


    /**
     * Initialize log4j, including setting the web app root system property.
     *
     * @param servletContext the current ServletContext
     * @see WebUtils#setWebAppRootSystemProperty
     */
    public static void initLogging(ServletContext servletContext) {
        // Expose the web app root system property.
        if (exposeWebAppRoot(servletContext)) {
            WebUtils.setWebAppRootSystemProperty(servletContext);
        }

        // Only perform custom log4j initialization in case of a config file.
        String location = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
        if (location != null) {
            // Perform actual log4j initialization; else rely on log4j's default initialization.
            try {
                // Resolve property placeholders before potentially resolving a real path.
                location = ServletContextPropertyUtils.resolvePlaceholders(location, servletContext);

                // Leave a URL (e.g. "classpath:" or "file:") as-is.
                if (!ResourceUtils.isUrl(location)) {
                    // Consider a plain file path as relative to the web
                    // application root directory.
                    location = WebUtils.getRealPath(servletContext, location);
                }

                // Write log message to server log.
                servletContext.log("Initializing log4j from [" + location + "]");

                // Check whether refresh interval was specified.
                String intervalString = servletContext.getInitParameter(REFRESH_INTERVAL_PARAM);
                if (intervalString != null) {
                    // Initialize with refresh interval, i.e. with log4j's watchdog thread,
                    // checking the file in the background.
                    try {
                        long refreshInterval = Long.parseLong(intervalString);
                        Log4jConfigurer.initLogging(location, refreshInterval);
                    } catch (NumberFormatException ex) {
                        throw new IllegalArgumentException(
                                "Invalid 'log4jRefreshInterval' parameter: " + ex.getMessage());
                    }
                } else {
                    // Initialize without refresh check, i.e. without log4j's watchdog thread.
                    Log4jConfigurer.initLogging(location);
                }
            } catch (FileNotFoundException ex) {
                throw new IllegalArgumentException("Invalid 'log4jConfigLocation' parameter: " + ex.getMessage());
            }
        }
    }

    /**
     * Shut down log4j, properly releasing all file locks
     * and resetting the web app root system property.
     *
     * @param servletContext the current ServletContext
     * @see WebUtils#removeWebAppRootSystemProperty
     */
    public static void shutdownLogging(ServletContext servletContext) {
        servletContext.log("Shutting down log4j");
        try {
            Log4jConfigurer.shutdownLogging();
        } finally {
            // Remove the web app root system property.
            if (exposeWebAppRoot(servletContext)) {
                WebUtils.removeWebAppRootSystemProperty(servletContext);
            }
        }
    }

    /**
     * Return whether to expose the web app root system property,
     * checking the corresponding ServletContext init parameter.
     *
     * @see #EXPOSE_WEB_APP_ROOT_PARAM
     */
    private static boolean exposeWebAppRoot(ServletContext servletContext) {
        String exposeWebAppRootParam = servletContext.getInitParameter(EXPOSE_WEB_APP_ROOT_PARAM);
        return (exposeWebAppRootParam == null || Boolean.valueOf(exposeWebAppRootParam));
    }

}
