/**
 * @Probject Name: WFJ-Base-Server-Dev
 * @Path: com.jack.netty.conf.utilNestedExceptionUtils.java
 * @Create By Jack
 * @Create In 2016年8月16日 上午10:06:34
 * TODO
 */
package com.jack.netty.conf.util;

/**
 * @Class Name NestedExceptionUtils
 * @Author Jack
 * @Create In 2016年8月16日
 */
public abstract class NestedExceptionUtils {

    /**
     * Build a message for the given base message and root cause.
     *
     * @param message the base message
     * @param cause   the root cause
     * @return the full exception message
     */
    public static String buildMessage(String message, Throwable cause) {
        if (cause != null) {
            StringBuilder sb = new StringBuilder();
            if (message != null) {
                sb.append(message).append("; ");
            }
            sb.append("nested exception is ").append(cause);
            return sb.toString();
        } else {
            return message;
        }
    }
}
