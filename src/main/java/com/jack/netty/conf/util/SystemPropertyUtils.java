/**
 * @Probject Name: WFJ-Base-Server-Dev
 * @Path: com.jack.netty.utilSystemPropertyUtils.java
 * @Create By Jack
 * @Create In 2016年8月16日 上午9:50:44
 * TODO
 */
package com.jack.netty.conf.util;

import com.jack.netty.conf.util.PropertyPlaceholderHelper.PlaceholderResolver;

/**
 * @Class Name SystemPropertyUtils
 * @Author Jack
 * @Create In 2016年8月16日
 */
public abstract class SystemPropertyUtils {

    /**
     * Prefix for system property placeholders: "${"
     */
    public static final String PLACEHOLDER_PREFIX = "${";

    /**
     * Suffix for system property placeholders: "}"
     */
    public static final String PLACEHOLDER_SUFFIX = "}";

    /**
     * Value separator for system property placeholders: ":"
     */
    public static final String VALUE_SEPARATOR = ":";


    private static final PropertyPlaceholderHelper strictHelper =
            new PropertyPlaceholderHelper(PLACEHOLDER_PREFIX, PLACEHOLDER_SUFFIX, VALUE_SEPARATOR, false);

    private static final PropertyPlaceholderHelper nonStrictHelper =
            new PropertyPlaceholderHelper(PLACEHOLDER_PREFIX, PLACEHOLDER_SUFFIX, VALUE_SEPARATOR, true);


    /**
     * Resolve ${...} placeholders in the given text, replacing them with corresponding system property values.
     *
     * @param text the String to resolve
     * @return the resolved String
     * @throws IllegalArgumentException if there is an unresolvable placeholder
     * @see #PLACEHOLDER_PREFIX
     * @see #PLACEHOLDER_SUFFIX
     */
    public static String resolvePlaceholders(String text) {
        return resolvePlaceholders(text, false);
    }

    /**
     * Resolve ${...} placeholders in the given text, replacing them with corresponding system property values.
     * Unresolvable placeholders with no default value are ignored and passed through unchanged if the
     * flag is set to true.
     *
     * @param text                           the String to resolve
     * @param ignoreUnresolvablePlaceholders flag to determine is unresolved placeholders are ignored
     * @return the resolved String
     * @throws IllegalArgumentException if there is an unresolvable placeholder and the flag is false
     * @see #PLACEHOLDER_PREFIX
     * @see #PLACEHOLDER_SUFFIX
     */
    public static String resolvePlaceholders(String text, boolean ignoreUnresolvablePlaceholders) {
        PropertyPlaceholderHelper helper = (ignoreUnresolvablePlaceholders ? nonStrictHelper : strictHelper);
        return helper.replacePlaceholders(text, new SystemPropertyPlaceholderResolver(text));
    }


    private static class SystemPropertyPlaceholderResolver implements PlaceholderResolver {

        private final String text;

        public SystemPropertyPlaceholderResolver(String text) {
            this.text = text;
        }

        public String resolvePlaceholder(String placeholderName) {
            try {
                String propVal = System.getProperty(placeholderName);
                if (propVal == null) {
                    // Fall back to searching the system environment.
                    propVal = System.getenv(placeholderName);
                }
                return propVal;
            } catch (Throwable ex) {
                System.err.println("Could not resolve placeholder '" + placeholderName + "' in [" +
                        this.text + "] as system property: " + ex);
                return null;
            }
        }
    }
}
