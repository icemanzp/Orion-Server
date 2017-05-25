/**
 * @Probject Name: netty
 * @Path: com.jack.netty.infcIPropertyConfig.java
 * @Create By Jack
 * @Create In 2015年8月25日 上午5:04:49
 * TODO
 */
package com.jack.netty.server.infc;

/**
 * @Class Name IPropertyConfig
 * @Author Jack
 * @Create In 2015年8月25日
 */
public interface IPropertyConfig {

    public String getContextProperty(String name);

    public String getContextProperty(String name, String defultValue);

    public String fromatter(String source, Object[] tagets);
}
