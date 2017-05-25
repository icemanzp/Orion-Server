package com.jack.netty.server.infc;


public interface IWebSocketServerService {

    /**
     * @param @param  request 业务请求报文
     * @param @return
     * @return String
     * @throws
     * @Title: doService
     * @Description: websocket 业务请求处理类
     * @author omen  www.liyidong.com
     * @date 2014年12月25日 下午2:59:22
     */
    public String doService(String request);

}
