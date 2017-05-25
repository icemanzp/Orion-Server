/**
 * @Probject Name: WFJ-Base-Server-Dev
 * @Path: com.jack.netty.utilWebAppRootListener.java
 * @Create By Jack
 * @Create In 2016年8月16日 上午9:54:43
 * TODO
 */
package com.jack.netty.server.http.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @Class Name WebAppRootListener
 * @Author Jack
 * @Create In 2016年8月16日
 */
public class WebAppRootListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        WebUtils.setWebAppRootSystemProperty(event.getServletContext());
    }

    public void contextDestroyed(ServletContextEvent event) {
        WebUtils.removeWebAppRootSystemProperty(event.getServletContext());
    }
}
