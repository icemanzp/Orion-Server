/**
 * @Probject Name: netty-wfj-base-dev
 * @Path: com.jack.netty.server.dtoErrorInfo.java
 * @Create By Jack
 * @Create In 2015年8月27日 下午6:32:47
 * TODO
 */
package com.jack.netty.server.dto;

/**
 * @Class Name ErrorInfo
 * @Author Jack
 * @Create In 2015年8月27日
 */
public class ErrorInfo {

    private String errorCode;

    private String errorMsg;

    /**
     * @Return the String errCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @Param String errCode to set
     */
    public void setErrorCode(String errCode) {
        this.errorCode = errCode;
    }

    /**
     * @Return the String errMsg
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * @Param String errMsg to set
     */
    public void setErrorMsg(String errMsg) {
        this.errorMsg = errMsg;
    }
}
