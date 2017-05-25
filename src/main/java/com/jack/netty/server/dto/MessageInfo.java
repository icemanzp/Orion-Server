package com.jack.netty.server.dto;

public class MessageInfo {

    private String success;

    private Object data;

    /**
     * @Return the String success
     */
    public String getSuccess() {
        return success;
    }

    /**
     * @Param String success to set
     */
    public void setSuccess(String success) {
        this.success = success;
    }

    /**
     * @Return the Object data
     */
    public Object getData() {
        return data;
    }

    /**
     * @Param Object data to set
     */
    public void setData(Object data) {
        this.data = data;
    }


}
