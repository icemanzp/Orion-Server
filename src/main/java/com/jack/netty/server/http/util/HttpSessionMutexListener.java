/**
 * @Probject Name: WFJ-Base-Server-Dev
 * @Path: com.jack.netty.utilHttpSessionMutexListener.java
 * @Create By Jack
 * @Create In 2016年8月16日 上午9:47:17
 * TODO
 */
package com.jack.netty.server.http.util;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.Serializable;


/**
 * @Class Name HttpSessionMutexListener
 * @Author Jack
 * @Create In 2016年8月16日
 */
public class HttpSessionMutexListener implements HttpSessionListener {

    public void sessionCreated(HttpSessionEvent event) {
        event.getSession().setAttribute(WebUtils.SESSION_MUTEX_ATTRIBUTE, new Mutex());
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        event.getSession().removeAttribute(WebUtils.SESSION_MUTEX_ATTRIBUTE);
    }


    /**
     * The mutex to be registered.
     * Doesn't need to be anything but a plain Object to synchronize on.
     * Should be serializable to allow for HttpSession persistence.
     */
    @SuppressWarnings("serial")
    private static class Mutex implements Serializable {
    }
}
