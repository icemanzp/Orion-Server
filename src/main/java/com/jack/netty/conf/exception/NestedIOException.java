/**
 * @Probject Name: WFJ-Base-Server-Dev
 * @Path: com.jack.netty.conf.exceptionNestedIOException.java
 * @Create By Jack
 * @Create In 2016年8月16日 上午10:06:01
 * TODO
 */
package com.jack.netty.conf.exception;

import java.io.IOException;

import com.jack.netty.conf.util.NestedExceptionUtils;


/**
 * @Class Name NestedIOException
 * @Author Jack
 * @Create In 2016年8月16日
 */
public class NestedIOException extends IOException {

    static {
        // Eagerly load the NestedExceptionUtils class to avoid classloader deadlock
        // issues on OSGi when calling getMessage(). Reported by Don Brown; SPR-5607.
        NestedExceptionUtils.class.getName();
    }


    /**
     * Construct a {@code NestedIOException} with the specified detail message.
     *
     * @param msg the detail message
     */
    public NestedIOException(String msg) {
        super(msg);
    }

    /**
     * Construct a {@code NestedIOException} with the specified detail message
     * and nested exception.
     *
     * @param msg   the detail message
     * @param cause the nested exception
     */
    public NestedIOException(String msg, Throwable cause) {
        super(msg);
        initCause(cause);
    }


    /**
     * Return the detail message, including the message from the nested exception
     * if there is one.
     */
    @Override
    public String getMessage() {
        return NestedExceptionUtils.buildMessage(super.getMessage(), getCause());
    }
}
