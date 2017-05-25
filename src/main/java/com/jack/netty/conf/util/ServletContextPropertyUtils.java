/**
 * @Probject Name: WFJ-Base-Server-Dev
 * @Path: com.jack.netty.utilServletContextPropertyUtils.java
 * @Create By Jack
 * @Create In 2016年8月16日 上午9:53:39
 * TODO
 */
package com.jack.netty.conf.util;

import javax.servlet.ServletContext;


/**
 * @Class Name ServletContextPropertyUtils
 * @Author Jack
 * @Create In 2016年8月16日
 */
public abstract class ServletContextPropertyUtils {

    private static final PropertyPlaceholderHelper strictHelper =
            new PropertyPlaceholderHelper(SystemPropertyUtils.PLACEHOLDER_PREFIX,
                    SystemPropertyUtils.PLACEHOLDER_SUFFIX, SystemPropertyUtils.VALUE_SEPARATOR, false);

    private static final PropertyPlaceholderHelper nonStrictHelper =
            new PropertyPlaceholderHelper(SystemPropertyUtils.PLACEHOLDER_PREFIX,
                    SystemPropertyUtils.PLACEHOLDER_SUFFIX, SystemPropertyUtils.VALUE_SEPARATOR, true);


    /**
     * Resolve ${...} placeholders in the given text, replacing them with corresponding
     * servlet context init parameter or system property values.
     *
     * @param text           the String to resolve
     * @param servletContext the servletContext to use for lookups.
     * @return the resolved String
     * @throws IllegalArgumentException if there is an unresolvable placeholder
     * @see SystemPropertyUtils#PLACEHOLDER_PREFIX
     * @see SystemPropertyUtils#PLACEHOLDER_SUFFIX
     * @see SystemPropertyUtils#resolvePlaceholders(String, boolean)
     */
    public static String resolvePlaceholders(String text, ServletContext servletContext) {
        return resolvePlaceholders(text, servletContext, false);
    }

    /**
     * Resolve ${...} placeholders in the given text, replacing them with corresponding
     * servlet context init parameter or system property values. Unresolvable placeholders
     * with no default value are ignored and passed through unchanged if the flag is set to true.
     *
     * @param text                           the String to resolve
     * @param servletContext                 the servletContext to use for lookups.
     * @param ignoreUnresolvablePlaceholders flag to determine is unresolved placeholders are ignored
     * @return the resolved String
     * @throws IllegalArgumentException if there is an unresolvable placeholder and the flag is false
     * @see SystemPropertyUtils#PLACEHOLDER_PREFIX
     * @see SystemPropertyUtils#PLACEHOLDER_SUFFIX
     * @see SystemPropertyUtils#resolvePlaceholders(String, boolean)
     */
    public static String resolvePlaceholders(String text, ServletContext servletContext,
            boolean ignoreUnresolvablePlaceholders) {
        PropertyPlaceholderHelper helper = (ignoreUnresolvablePlaceholders ? nonStrictHelper : strictHelper);
        return helper.replacePlaceholders(text, new ServletContextPlaceholderResolver(text, servletContext));
    }


    private static class ServletContextPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

        private final String text;

        private final ServletContext servletContext;

        public ServletContextPlaceholderResolver(String text, ServletContext servletContext) {
            this.text = text;
            this.servletContext = servletContext;
        }

        public String resolvePlaceholder(String placeholderName) {
            try {
                String propVal = this.servletContext.getInitParameter(placeholderName);
                if (propVal == null) {
                    // Fall back to system properties.
                    propVal = System.getProperty(placeholderName);
                    if (propVal == null) {
                        // Fall back to searching the system environment.
                        propVal = System.getenv(placeholderName);
                    }
                }
                return propVal;
            } catch (Throwable ex) {
                System.err.println("Could not resolve placeholder '" + placeholderName + "' in [" +
                        this.text + "] as ServletContext init-parameter or system property: " + ex);
                return null;
            }
        }
    }
}