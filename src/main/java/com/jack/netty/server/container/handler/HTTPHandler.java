/**
 * @Probject Name: WFJ-Base-Server-Dev
 * @Path: com.jack.netty.server.container.handlerHTTPHandler.java
 * @Create By Jack
 * @Create In 2016年4月28日 下午1:32:09
 * TODO
 */
package com.jack.netty.server.container.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.jack.netty.conf.Constant;
import com.jack.netty.conf.util.EnvPropertyConfig;
import com.jack.netty.server.dto.ErrorInfo;
import com.jack.netty.server.dto.MessageInfo;
import com.jack.netty.server.http.MediaType;
import com.jack.netty.server.http.util.HTTPServletUtil;
import com.jack.netty.server.servlet.WFJFilterChain;
import com.jack.netty.server.servlet.WFJFilterFactory;
import com.jack.netty.server.servlet.WFJHttpServletRequest;
import com.jack.netty.server.servlet.WFJHttpServletResponse;
import com.jack.netty.server.servlet.WFJServletContext;
import com.jack.netty.server.servlet.WFJServletDynamic;
import com.jack.netty.util.JacksonMapperUtil;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedStream;
import io.netty.util.CharsetUtil;

/**
 * @Class Name HTTPHandler
 * @Author Jack
 * @Create In 2016年4月28日
 */
public class HTTPHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Logger log = LoggerFactory.getLogger(HTTPHandler.class);

    protected static final String URI_ENCODING = EnvPropertyConfig
            .getContextProperty(Constant.SYSTEM_SEETING_SERVER_DEFAULT_URI_ENCODING);

    protected final WFJServletContext sc;

    protected static String LOGTHREAD_ID = "logthreadId";
    
    public HTTPHandler(ServletContext servletContext) {
        sc = (WFJServletContext) servletContext;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
        // 目前仅支持 POST+GET+PUT+DELETE
        boolean flag = HttpMethod.POST.equals(msg.getMethod())
                || HttpMethod.GET.equals(msg.getMethod())
                || HttpMethod.DELETE.equals(msg.getMethod())
                || HttpMethod.PUT.equals(msg.getMethod());
        MDC.put(LOGTHREAD_ID, java.util.UUID.randomUUID().toString().replace("-", ""));
        // 是否有 Servlet 处理请求
        boolean isDispatch = false;

        try {
            // 首先判断是否解码成功，不成功则请求异常，直接返回错误请求
            if (!msg.getDecoderResult().isSuccess()) {
                sendError(ctx, HttpResponseStatus.BAD_REQUEST,
                        EnvPropertyConfig.getContextProperty("env.setting.server.error.00001000"));
                return;
            }
            // 接着判断是否是支持的方法，如不支持则返回不接受的请求
            if (!flag) {
                sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED,
                        EnvPropertyConfig.getContextProperty("env.setting.server.error.00001000"));
                return;
            }

            // 1.将netty的http请求转换为servlethttp请求
            WFJHttpServletRequest servletRequest = HTTPServletUtil.createServletRequest(msg, this.sc);
            // 2.创建一个空的servlethttp响应对象
            WFJHttpServletResponse servletResponse = new WFJHttpServletResponse();
            // 3.调用Servlet处理
            String requestURI = servletRequest.getRequestURI();

            // 4.执行 Servlet
            isDispatch = doService(servletRequest, servletResponse, requestURI);

            if (!isDispatch) {
                // 返回异常信息字符串
                sendError(ctx, HttpResponseStatus.NOT_FOUND,
                        EnvPropertyConfig.getContextProperty("env.setting.server.error.00001002"));
                return;
            }

            // 5.将Servlethttp响应转换为Netty的Http响应并处理
            sendResponse(ctx, servletResponse, servletRequest);

        } catch (ServletException e) {
            // TODO Auto-generated catch block
            if (e.getRootCause() instanceof java.lang.NoSuchFieldError) {
                sendError(ctx, HttpResponseStatus.NOT_FOUND, "00001002 : " + EnvPropertyConfig
                                .getContextProperty("env.setting.server.error.00001002") + "-" + e.getMessage());
            } else {
                sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, "00001000 : " + EnvPropertyConfig
                                .getContextProperty("env.setting.server.error.00001000") + "-" + e.getMessage());
            }
            log.error(EnvPropertyConfig.getContextProperty("env.setting.server.error.00001000"), e);
        } catch (IOException e) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    "00001000 : " + EnvPropertyConfig.getContextProperty("env.setting.server.error.00001000") + "-" + e
                            .getMessage());
            log.error(EnvPropertyConfig.getContextProperty("env.setting.server.error.00001000"), e);
        } catch (Exception e) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    "00001000 : " + EnvPropertyConfig.getContextProperty("env.setting.server.error.00001000") + "-" + e
                            .getMessage());
            log.error(EnvPropertyConfig.getContextProperty("env.setting.server.error.00001000"), e);
        } finally {
            MDC.remove(LOGTHREAD_ID);
        }
    }

    /**
     * 根据加载的 Servlet 列表判断执行
     *
     * @param servletRequest
     * @param servletResponse
     * @param requestURI
     * @return
     * @throws ServletException
     * @throws IOException      boolean
     * @Methods Name doService
     * @Create In 2016年8月16日 By Jack
     */
    @SuppressWarnings("deprecation")
    private boolean doService(WFJHttpServletRequest servletRequest,
            WFJHttpServletResponse servletResponse, String requestURI)
            throws ServletException, IOException {
        boolean isDispatch = false;

        for (String item : sc.getServletRegistrations().keySet()) {
            WFJServletDynamic wfjsc = (WFJServletDynamic) sc.getServletRegistration(item);
            Iterator<String> mappings = wfjsc.getMappings().iterator();
            while (mappings.hasNext()) {
                String pattern = mappings.next();

                pattern = pattern.replace(".", "\\.").replace("*", ".+");
                Pattern pat = Pattern.compile(pattern);
                Matcher mat = pat.matcher(requestURI);

                if (mat.find()) {
                    Servlet excute = sc.getServlet(wfjsc.getName());
                    WFJFilterChain wfjfc = WFJFilterFactory.createFilterChain(servletRequest, excute);
                    wfjfc.doFilter(servletRequest, servletResponse);
                    //excute.service(servletRequest, servletResponse);
                    isDispatch = true;
                    break;
                }
            }
            if (isDispatch) {
                break;
            }
        }
        return isDispatch;
    }

    /**
     * 发送正常请求
     *
     * @param ctx
     * @param servletResponse
     * @param servletRequest
     * @Methods Name sendResponse
     * @Create In 2016年7月27日 By Jack
     */
    protected void sendResponse(ChannelHandlerContext ctx, WFJHttpServletResponse servletResponse,
            WFJHttpServletRequest servletRequest) {
    		    		
        HttpResponseStatus status = HttpResponseStatus.valueOf(servletResponse.getStatus());
        if (servletResponse.getStatus() >= HttpServletResponse.SC_BAD_REQUEST
                && servletResponse.getStatus() != HttpServletResponse.SC_UNAUTHORIZED) {
            if (ctx.channel().isActive()) {
                sendError(ctx, status,
                        EnvPropertyConfig.getContextProperty("env.setting.server.error.00001002"));
                return;
            }
        }
        
        //增加请求协议判断，如发起request为http 1.0 则应答同样应为http 1.0
        HttpVersion httpVersion = null;
        HttpResponse response = null;
        if(HttpVersion.HTTP_1_1.equals(HttpVersion.valueOf(servletRequest.getProtocol()))){
        	httpVersion = HttpVersion.HTTP_1_1;
        	response = new DefaultHttpResponse(httpVersion, status);
        }else{
        	httpVersion = HttpVersion.HTTP_1_0;
        	response = new DefaultFullHttpResponse(httpVersion, status,
                    Unpooled.copiedBuffer(servletResponse.getContentAsByteArray()));
        }

        //判断是否需要会话保持
        boolean isKeepAlive = HTTPServletUtil.isKeepAlive(servletRequest.getHeader(HttpHeaders.Names.CONNECTION));
        if (isKeepAlive) {
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        //设置头部参数
        for (Object name : servletResponse.getHeaderNames()) {
            for (Object value : servletResponse.getHeaders(name.toString())) {
                response.headers().add(name.toString(), value);
            }
        }
        //设置 Cookie
        String domain = EnvPropertyConfig
                .getContextProperty(Constant.SYSTEM_SEETING_SERVER_DEFAULT_COOKIE_DOMAIN);
        for (javax.servlet.http.Cookie sCookie : servletResponse.getCookies()) {
            StringBuilder _cookie = new StringBuilder();
            _cookie.append(sCookie.getName()).append('=').append(sCookie.getValue());
            if (sCookie.getMaxAge() != 0) {
                long _now = System.currentTimeMillis();
                long _exp = _now + sCookie.getMaxAge() * 1000;
                Date expDate = new Date(_exp);
                String date = getGMTtime(expDate);
                _cookie.append("; expires=").append(date);
            }
            if (StringUtils.isNotEmpty(sCookie.getDomain())) {
                domain = sCookie.getDomain();
            }
            String path = "/";
            if (StringUtils.isNotEmpty(sCookie.getPath())) {
                path = sCookie.getPath();
            }
            _cookie.append("; path=").append(path);
            _cookie.append("; domain=").append(domain);
            response.headers().set(HttpHeaders.Names.SET_COOKIE, _cookie.toString());
            // XXX cookie 只有火狐认
        }

        // 增加版本信息并禁止各类缓存 begin
        String cache_control = HttpHeaders.Values.NO_CACHE + "," + HttpHeaders.Values.NO_STORE + ","
                + HttpHeaders.Values.MUST_REVALIDATE + "," + HttpHeaders.Values.PRIVATE;
        String server_infoString = EnvPropertyConfig
                .getContextProperty(Constant.SYSTEM_SEETING_SERVER_DEFALUT_NAME)
                + EnvPropertyConfig
                .getContextProperty(Constant.SYSTEM_SEETING_SERVER_DEFALUT_VERSION);
        // 数据立即过期每次均从后台后去，适用于服务
        response.headers().set(HttpHeaders.Names.EXPIRES, "-1");
        // 兼容老版本 IE
        response.headers().set(HttpHeaders.Names.PRAGMA, HttpHeaders.Values.NO_CACHE);
        response.headers().set(HttpHeaders.Names.CACHE_CONTROL, cache_control);
        response.headers().set(HttpHeaders.Names.DATE, getGMTtime(new Date()));
        response.headers().set(HttpHeaders.Names.SERVER, server_infoString);
        response.headers().set(HTTPServletUtil.Names.X_WFJ_POWERED_BY, server_infoString);
       // 增加版本信息并禁止各类缓存 end
        

        ChannelFuture writeFuture = null;
        // 设置Chunked 协议支持
        if(httpVersion.equals(HttpVersion.HTTP_1_1)){
        	//Http 1.1 必须支持Chunked传输不定长度内容
        	response.headers().set(HttpHeaders.Names.TRANSFER_ENCODING, HttpHeaders.Values.CHUNKED);
        
	        // Write the initial line and the header.
	        ctx.write(response);
	        
	        // 写入内容
	        if(servletResponse.getContentAsByteArray().length >0){
	        		writeFuture = ctx.write(new HttpChunkedInput(new ChunkedStream(new ByteArrayInputStream(servletResponse.getContentAsByteArray()))));
	        }
	        //写入最后一个空字符结束接收
	        writeFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        }else{
        	//Http 1.0 强行设置内容长度
        	response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, servletResponse.getContentAsByteArray().length);
        	 // Write the initial line and the header.
        	writeFuture = ctx.writeAndFlush(response);
        }
        
        //非保持，则关闭
        if (!isKeepAlive) {
        		writeFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    protected String getGMTtime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z",
                Locale.ENGLISH);
        return dateFormat.format(date);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.
     * channel.ChannelHandlerContext, java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause instanceof IOException) {
            if("Connection reset by peer".equals(cause.getMessage())){
                log.warn(cause.toString());
                return;
            }
        }
        log.error("ns exceptionCougnt", cause);
        if (ctx.channel().isActive()) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    EnvPropertyConfig.getContextProperty("env.setting.server.error.00001002"));
        }
    }

    /**
     * 发送异常信息到客户端
     *
     * @param ctx
     * @param status
     * @param msg
     * @Methods Name sendError
     * @Create In 2016年4月28日 By Jack
     */
    protected void sendError(ChannelHandlerContext ctx, HttpResponseStatus status,
            String msg) {

        ErrorInfo ei = new ErrorInfo();
        ei.setErrorCode(String.valueOf(status.code()));
        ei.setErrorMsg(msg);

        MessageInfo mi = new MessageInfo();
        mi.setData(ei);
        mi.setSuccess(String.valueOf(false));
        String sendString;
        try {
            sendString = JacksonMapperUtil.objectToJson(mi);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            StringBuilder error = new StringBuilder();
            error.append("{").append("success:false,data:{").append("status:").append(status.code()).append(",msg:")
                    .append(msg == null ? "" : msg).append("}").append("}");
            sendString = error.toString();
        }

        HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer(sendString, CharsetUtil.UTF_8));

        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE  + "; charset=" + CharsetUtil.UTF_8.name());
        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
