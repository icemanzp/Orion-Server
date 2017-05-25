/**
 * @Probject Name: WFJ-Base-Server-Dev
 * @Path: com.jack.netty.server.exceptionInvalidMediaTypeException.java
 * @Create By Jack
 * @Create In 2016年8月16日 上午10:29:16
 * TODO
 */
package com.jack.netty.server.exception;

/**
 * @Class Name InvalidMediaTypeException
 * @Author Jack
 * @Create In 2016年8月16日
 */
public class InvalidMediaTypeException extends IllegalArgumentException {

    private String mediaType;


    /**
     * Create a new InvalidMediaTypeException for the given media type.
     *
     * @param mediaType the offending media type
     * @param msg       a detail message indicating the invalid part
     */
    public InvalidMediaTypeException(String mediaType, String msg) {
        super("Invalid media type \"" + mediaType + "\": " + msg);
        this.mediaType = mediaType;

    }


    /**
     * Return the offending media type.
     */
    public String getMediaType() {
        return this.mediaType;
    }
}
