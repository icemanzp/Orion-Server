/**
 * @Probject Name: WFJ-Base-Server-Dev
 * @Path: com.jack.netty.utilHTTPServletUtil.java
 * @Create By Jack
 * @Create In 2016年7月26日 下午3:50:26
 * TODO
 */
package com.jack.netty.server.http.util;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MixedAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jack.netty.conf.Constant;
import com.jack.netty.conf.util.EnvPropertyConfig;
import com.jack.netty.server.servlet.WFJHttpServletRequest;
import com.jack.netty.server.servlet.WFJServletContext;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Class Name HTTPServletUtil
 * @Author Jack
 * @Create In 2016年7月26日
 */
public class HTTPServletUtil {

    private static Logger log = LoggerFactory.getLogger(HTTPServletUtil.class);
    protected static final String URI_ENCODING = EnvPropertyConfig
            .getContextProperty(Constant.SYSTEM_SEETING_SERVER_DEFAULT_URI_ENCODING);

    /**
     * 完成 HTTP 转换
     *
     * @param msg 待转换请求
     * @param sc  ServletContext 变量
     * @return WFJHttpServletRequest
     * @throws Exception
     * @Methods Name createServletRequest
     * @Create In 2016年7月26日 By Jack
     */
    public static WFJHttpServletRequest createServletRequest(FullHttpRequest msg, WFJServletContext sc)
            throws Exception {
        WFJHttpServletRequest servletRequest = new WFJHttpServletRequest(sc);
        String uri = msg.getUri();
        uri = new String(uri.getBytes("ISO8859-1"), URI_ENCODING);
        uri = URLDecoder.decode(uri, URI_ENCODING);
        // 修复 WebService的请求问题，增加 Host 及 Port 设定,UriComponents不含有这些信息
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(uri).build();

        // cookies
        String cookieStr = msg.headers().get("Cookie");
        if (cookieStr != null && !"".equals(cookieStr.trim())) {
            cookieStr = cookieStr.trim();
            Set<Cookie> nettyCookies = CookieDecoder.decode(msg.headers().get("Cookie"));
            List<javax.servlet.http.Cookie> servletCookies = new ArrayList<javax.servlet.http.Cookie>();
            for (Cookie nCookie : nettyCookies) {
                try {
                    javax.servlet.http.Cookie sCookie = new javax.servlet.http.Cookie(
                            nCookie.getName(), nCookie.getValue());
                    sCookie.setComment(nCookie.getComment());
                    if (nCookie.getDomain() != null) {
                        sCookie.setDomain(nCookie.getDomain());
                    }
                    sCookie.setMaxAge((int) nCookie.getMaxAge());
                    sCookie.setPath(nCookie.getPath());
                    sCookie.setVersion(nCookie.getVersion());
                    servletCookies.add(sCookie);
                } catch (Exception e) {
                    // log.warn("无法解析的cookie", e);
                }
            }
            javax.servlet.http.Cookie[] _cookies = new javax.servlet.http.Cookie[servletCookies
                    .size()];
            servletRequest.setCookies(servletCookies.toArray(_cookies));
        }
        // headers
        for (String name : msg.headers().names()) {
            for (String value : msg.headers().getAll(name)) {
                servletRequest.addHeader(name, value);
            }
        }
        // request method
        servletRequest.setMethod(msg.getMethod().name());
        // request uri
        String path = uriComponents.getPath();
        path = URLDecoder.decode(path, URI_ENCODING);
        servletRequest.setRequestURI(path);

        String contexRoot = sc.getContextPath();

        if (EnvPropertyConfig.getContextProperty("env.setting.path.prefix.ignore").contains(path)
                || "/".equals(contexRoot)) {
            contexRoot = "";
            servletRequest.setServletPath(contexRoot);
        } else {
            contexRoot = contexRoot.indexOf("/") != 0 ? "/" + contexRoot : contexRoot;

            String temp = contexRoot.lastIndexOf("/") > 0 ? contexRoot : contexRoot + "/";
            String item = path;
            item = item.replace(".", "\\.").replace("*", ".+");
            Pattern pat = Pattern.compile(temp);

            Matcher mat = pat.matcher(item);

            if (EnvPropertyConfig.getContextProperty("env.setting.path.prefix.ignore").contains(path)
                    || mat.find()) {
                servletRequest.setServletPath(contexRoot);
                // servletRequest.setContextPath(contexRoot);
            } else {
                throw new Exception(
                        EnvPropertyConfig.getContextProperty("env.setting.server.error.00001004"));
            }
        }

        // 修复 Servlet Webservice 发现的 BUG
        servletRequest.setPathInfo(path.substring(contexRoot.length()));
        
        //修复ProtocolVersion及KeepAlive发现的Bug
        servletRequest.setProtocol(msg.getProtocolVersion().toString());

        // servletRequest.setCharacterEncoding("utf-8");
        if (uriComponents.getScheme() != null) {
            servletRequest.setScheme(uriComponents.getScheme());
        }
        // 修复主机名及 IP 错误uriComponents.getHost() != null
        if (msg.headers().get("host") != null) {
            if (msg.headers().get("host")
                    .contains(Constant.SYSTEM_SEETING_SERVER_DEFAULT_SERVER_HOST_PORT_SPLIT)) {
                String[] hostAndPort = msg.headers().get("host")
                        .split(Constant.SYSTEM_SEETING_SERVER_DEFAULT_SERVER_HOST_PORT_SPLIT);
                servletRequest.setServerName(hostAndPort[0]);
                servletRequest.setServerPort(Integer.valueOf(hostAndPort[1]));
            } else {
                servletRequest.setServerName(msg.headers().get("host"));
                servletRequest
                        .setServerPort(Constant.SYSTEM_SEETING_SERVER_DEFAULT_SERVER_PORT_DEFAULT);
            }
        }
        // if (uriComponents.getPort() != -1) {
        // servletRequest.setServerPort(uriComponents.getPort());
        // }
        // request content body
        ByteBuf content = msg.content();
        content.readerIndex(0);
        byte[] data = new byte[content.readableBytes()];
        content.readBytes(data);
        servletRequest.setContent(data);

        // request parameters
        try {
            if (uriComponents.getQuery() != null) {
                String query = UriUtils.decode(uriComponents.getQuery(), URI_ENCODING);
                servletRequest.setQueryString(query);
            }
            for (Entry<String, List<String>> entry : uriComponents.getQueryParams().entrySet()) {
                for (String value : entry.getValue()) {
                    servletRequest.addParameter(UriUtils.decode(entry.getKey(), URI_ENCODING),
                            UriUtils.decode(value == null ? "" : value, URI_ENCODING));
                }
            }
        } catch (UnsupportedEncodingException ex) {
            // shouldn't happen, or check your URI_ENCODING config
            throw new Exception(
                    EnvPropertyConfig.getContextProperty("env.setting.server.error.00001005"));
        }
        if (HttpMethod.POST.equals(msg.getMethod())) {
            Charset charset = Charset.forName(URI_ENCODING);
            HttpPostRequestDecoder postRequestDecoder = new HttpPostRequestDecoder(
                    new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE), msg, charset);
            if (postRequestDecoder.isMultipart()) {
                List<InterfaceHttpData> dataList = postRequestDecoder.getBodyHttpDatas();
                for (InterfaceHttpData _data : dataList) {
                    String name = _data.getName();
                    String value = null;
                    if (InterfaceHttpData.HttpDataType.Attribute == _data.getHttpDataType()) {
                        // 文本域
                        MixedAttribute attribute = (MixedAttribute) _data;
                        attribute.setCharset(charset);
                        value = attribute.getValue();
                        servletRequest.addParameter(UriUtils.decode(name, URI_ENCODING),
                                UriUtils.decode(value == null ? "" : value, URI_ENCODING));
                    } else if (InterfaceHttpData.HttpDataType.FileUpload == _data
                            .getHttpDataType()) {
                        // 文件域，不处理，已复制request content
                    } else if (InterfaceHttpData.HttpDataType.InternalAttribute == _data
                            .getHttpDataType()) {
                        // ???
                        // 网上的例子都没处理，API也没有说明。。。。。。
                    }
                }
            } else {
                String postContent = new String(data, Charset.forName(URI_ENCODING));
                log.debug(postContent);
                String[] params = postContent.split("&");
                for (String param : params) {
                    String[] _kv = param.split("=");
                    String[] kv = new String[2];
                    kv[0] = _kv[0];
                    if (_kv.length >= 2) {
                        kv[1] = URLDecoder.decode(_kv[1], URI_ENCODING);
                    } else {
                        kv[1] = "";
                    }
                    servletRequest.addParameter(kv[0], kv[1]);
                }
            }
        }
        return servletRequest;
    }
    
    public static boolean isKeepAlive(Object connection) {
        return connection != null && HttpHeaders.Values.KEEP_ALIVE.equalsIgnoreCase(String.valueOf(connection));
    }

    public static final class Names {
        public static final String X_WFJ_POWERED_BY = "X-Powered-By";

        public static final String X_WFJ_CUSTOMER_RESPONSE_HEADER_MONITOR_BEGINTIME = "X-WFJ-Customer-Response-Header-Monitor-Begintime";

        public static final String X_WFJ_CUSTOMER_RESPONSE_HEADER_MONITOR_ERROR_BEGINTIME = "X-WFJ-Customer-Response-Header-Monitor-Error-Begintime";

        public static final String X_WFJ_CUSTOMER_RESPONSE_HEADER_MONITOR_BEGINCUPTIME = "X-WFJ-Customer-Response-Header-Monitor-BeginCupTime";
    }
}
