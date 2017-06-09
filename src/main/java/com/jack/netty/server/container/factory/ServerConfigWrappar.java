/**
 *
 */
package com.jack.netty.server.container.factory;

import com.jack.netty.conf.Constant;
import com.jack.netty.conf.util.EnvPropertyConfig;
import com.jack.netty.server.config.NettyServerInfo;
import com.jack.netty.server.config.NettyServerInfo.BaseInfo.BaseParams.Param;
import com.jack.netty.server.config.NettyServerInfo.DispatherInfos.Servlet;
import com.jack.netty.server.config.NettyServerInfo.DispatherInfos.Servlet.InitParam;
import com.jack.netty.server.config.NettyServerInfo.FilterInfos.Filter;
import com.jack.netty.server.config.NettyServerInfo.ListenerInfos.Listener;
import com.jack.netty.server.servlet.*;
import com.jack.netty.util.ObjectUtils;
import com.jack.netty.util.StringUtils;
import io.netty.channel.ChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

/**
 * @Class Name ServerConfigWrappar
 * @Author Jack
 * @Create In 2016年4月22日
 */
public class ServerConfigWrappar {

    private static Logger logger = LoggerFactory.getLogger(ServerConfigWrappar.class);

    private static NettyServerInfo nsi = null;

    private static ServletContext sc = null;

    private static final String RESOURCE_PATH_SHUX = "classpath";

    private static final String PROPERTY_CONTEXT_PATH_CUSTOMER = "nettyserver.xml";

    private static final String URL_PATTERN_SPLITE = ",";

    private final static Map<Class<?>, AnnotationCacheEntry[]> annotationCache = new WeakHashMap<>();

    public static final boolean IS_SECURITY_ENABLED = (System.getSecurityManager() != null);

    private final static LinkedHashMap<String, ChannelHandler> customPipelineMap = new LinkedHashMap<String, ChannelHandler>();

    private static enum AnnotationCacheEntryType {
        FIELD, SETTER, POST_CONSTRUCT, PRE_DESTROY
    }

    /**
     * The set of filter mappings for this application, in the order
     * they were defined in the deployment descriptor with additional mappings
     * added via the {@link ServletContext} possibly both before and after those
     * defined in the deployment descriptor.
     */
    private final static ContextFilterMaps filterMaps = new ContextFilterMaps();

    /**
     * 容器销毁
     *
     * @Methods Name destroy
     * @Create In 2016年8月2日 By Jack void
     */
    @SuppressWarnings("deprecation")
    public static void destroy() {
        try {
            //1. 清理 Servlet
            Iterator<String> servlets = ((WFJServletContext) sc).getServletInited().keySet().iterator();
            while (servlets.hasNext()) {
                ((WFJServletContext) sc).getServlet(servlets.next()).destroy();
            }
            //2. 清理Filter
            Iterator<String> filters = ((WFJServletContext) sc).getFilterInited().keySet().iterator();
            while (filters.hasNext()) {
                ((WFJServletContext) sc).getFilter(filters.next()).destroy();
            }
            //2. 清理 Listener
            Iterator<Object> listeners = ((WFJServletContext) sc).getApplicationEventListenersList().iterator();
            ServletContextEvent event = new ServletContextEvent(sc);
            while (listeners.hasNext()) {
                Object item = listeners.next();
                if (item instanceof ServletContextListener) {
                    ServletContextListener listener = (ServletContextListener) item;
                    listener.contextDestroyed(event);
                }
                preDestroy(item, item.getClass());
            }
        } catch (IllegalAccessException e) {
            logger.error("Server Listener Destroy Error: " + e.getMessage());
        } catch (InvocationTargetException e) {
            logger.error("Server Listener Destroy Error: " + e.getMessage());
        } finally {
            ((WFJServletContext) sc).getServletInited().clear();
            ((WFJServletContext) sc).getFilterInited().clear();
            ((WFJServletContext) sc).getApplicationEventListenersList().clear();
        }
    }

    /**
     * 反射类型执行对象销毁
     *
     * @param instance
     * @param clazz
     * @throws IllegalAccessException
     * @throws InvocationTargetException void
     * @Methods Name preDestroy
     * @Create In 2016年8月2日 By Jack
     */
    protected static void preDestroy(Object instance, final Class<?> clazz)
            throws IllegalAccessException, InvocationTargetException {
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != Object.class) {
            preDestroy(instance, superClass);
        }

        // At the end the postconstruct annotated
        // method is invoked
        AnnotationCacheEntry[] annotations = null;
        synchronized (annotationCache) {
            annotations = annotationCache.get(clazz);
        }
        if (annotations == null) {
            // instance not created through the instance manager
            return;
        }
        for (AnnotationCacheEntry entry : annotations) {
            if (entry.getType() == AnnotationCacheEntryType.PRE_DESTROY) {
                Method preDestroy = getMethod(clazz, entry);
                synchronized (preDestroy) {
                    boolean accessibility = preDestroy.isAccessible();
                    preDestroy.setAccessible(true);
                    preDestroy.invoke(instance);
                    preDestroy.setAccessible(accessibility);
                }
            }
        }
    }

    /**
     * 反射类型方法处理器
     *
     * @param clazz
     * @param entry
     * @return Method
     * @Methods Name getMethod
     * @Create In 2016年8月2日 By Jack
     */
    private static Method getMethod(final Class<?> clazz, final AnnotationCacheEntry entry) {
        Method result = null;
        if (IS_SECURITY_ENABLED) {
            result = AccessController.doPrivileged(new PrivilegedAction<Method>() {
                @Override
                public Method run() {
                    Method result = null;
                    try {
                        result = clazz.getDeclaredMethod(entry.getAccessibleObjectName(),
                                entry.getParamTypes());
                    } catch (NoSuchMethodException e) {
                        // Should never happen. On that basis don't log
                        // it.
                    }
                    return result;
                }
            });
        } else {
            try {
                result = clazz.getDeclaredMethod(entry.getAccessibleObjectName(),
                        entry.getParamTypes());
            } catch (NoSuchMethodException e) {
                // Should never happen. On that basis don't log it.
            }
        }
        return result;
    }

    /**
     * 内部注解缓存对象
     *
     * @Class Name AnnotationCacheEntry
     * @Author Jack
     * @Create In 2016年8月2日
     */
    private static final class AnnotationCacheEntry {
        private final String accessibleObjectName;
        private final Class<?>[] paramTypes;
        private final String name;
        private final AnnotationCacheEntryType type;

        @SuppressWarnings("unused")
        public AnnotationCacheEntry(String accessibleObjectName, Class<?>[] paramTypes, String name,
                AnnotationCacheEntryType type) {
            this.accessibleObjectName = accessibleObjectName;
            this.paramTypes = paramTypes;
            this.name = name;
            this.type = type;
        }

        public String getAccessibleObjectName() {
            return accessibleObjectName;
        }

        public Class<?>[] getParamTypes() {
            return paramTypes;
        }

        @SuppressWarnings("unused")
        public String getName() {
            return name;
        }

        public AnnotationCacheEntryType getType() {
            return type;
        }
    }

    /**
     * 根据 nettyserver.xml，构建服务器配置文件实例
     * @Methods Name buildServerConfig
     * @return 成功：true；失败：false
     * @Create In 2017年6月07日 By Jack
     */
    private static boolean buildServerConfig() {
        JAXBContext jc;
        boolean isInited = false;
        try {
            //1.初始化系统资源文件
            EnvPropertyConfig.init();
            //2.开始解析应用配置 XML，必须应用根目录下
            jc = JAXBContext.newInstance("com.jack.netty.server.config");
            Unmarshaller u = jc.createUnmarshaller();

            InputStream in = ServerConfigWrappar.class.getClassLoader()
                    .getResourceAsStream(PROPERTY_CONTEXT_PATH_CUSTOMER);

            nsi = (NettyServerInfo) u.unmarshal(in);
            in.close();
            isInited = true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error(EnvPropertyConfig.getContextProperty("env.setting.server.error.00000000"));
            logger.error("Details: " + e.getMessage());
            e.printStackTrace();
            isInited = false;
        }
        return isInited;
    }

    /**
     * 服务器容器初始化
     *
     * @Methods Name init
     * @param isHttp 是否 Http|Https 服务器
     * @throws Exception
     * @Create In 2017年6月07日 By Jack
     */
    public static void init(boolean isHttp) throws Exception{
        //1.构建服务器配置实例
        if (!buildServerConfig()) {
            throw new RuntimeException(
                    EnvPropertyConfig.getContextProperty("env.setting.server.error.00000000"));
        }
        if(isHttp){
            //2.开始分步骤初始化应用
            createHttpModeServer();
        }else{
            //2.开始分步骤初始化应用非 Http 类型服务器
            createTcpModeServer();
        }
    }

    /**
     * 获取服务器配置信息
     *
     * @return NettyServerInfo
     * @throws Exception
     * @Methods Name getNettyServerInfo
     * @Create In 2016年4月22日 By Jack
     */
    public static NettyServerInfo getNettyServerInfo() {
        if (nsi == null) {
            try {
                buildServerConfig();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.error(
                        EnvPropertyConfig.getContextProperty("env.setting.server.error.00000000"));
                logger.error("Details: " + e.getMessage());
            }
        }

        return nsi;
    }

    /**
     * 初始化 TCP 类型的服务器
     *
     * @Methods Name createTcpModeServer
     * @Create In 2017年6月07日 By Jack
     */
    private static void createTcpModeServer(){
        try {
            // 0.初始化服务器信息，并初始化 ServletContext
            NettyServerInfo nsi = getNettyServerInfo();
            WFJServletContext wsc = new WFJServletContext(nsi.getBaseInfo().getServerRoot());
            wsc.setServletContextName(nsi.getBaseInfo().getServerName());
            wsc.setContextPath(nsi.getBaseInfo().getServerRoot());
            wsc.setServerInfo(EnvPropertyConfig.getContextProperty(Constant.SYSTEM_SEETING_SERVER_DEFALUT_NAME) +
                    Constant.SYSTEM_SEETING_SERVER_DEFALUT_NAME_VERSION_SPLIT +
                    EnvPropertyConfig.getContextProperty(Constant.SYSTEM_SEETING_SERVER_DEFALUT_VERSION));
            // 1.初始化 Context 参数
            buildContextParams(nsi, wsc);
            // 2.初始化监听器
            buildListener(nsi, wsc);
            // 3.构造自定义处理器，完成后续任务处理
            buildCustomerHandler(nsi);

        } catch (Exception e) {
            throw new RuntimeException(
                    EnvPropertyConfig.getContextProperty("env.setting.server.error.00000000"),e);
        }
    }

    /**
     * 创建 Http 类型的服务器
     *
     * @return ServletContext
     * @Methods Name createHttpModeServer
     * @Create In 2016年5月1日 By Jack
     */
    private static ServletContext createHttpModeServer() {

        if (sc != null) {
            return sc;
        }
        try {
            // 0.初始化服务器信息，并初始化 ServletContext
            NettyServerInfo nsi = getNettyServerInfo();
            WFJServletContext wsc = new WFJServletContext(nsi.getBaseInfo().getServerRoot());
            wsc.setServletContextName(nsi.getBaseInfo().getServerName());
            wsc.setContextPath(nsi.getBaseInfo().getServerRoot());
            wsc.setServerInfo(EnvPropertyConfig.getContextProperty(Constant.SYSTEM_SEETING_SERVER_DEFALUT_NAME) +
                    Constant.SYSTEM_SEETING_SERVER_DEFALUT_NAME_VERSION_SPLIT +
                    EnvPropertyConfig.getContextProperty(Constant.SYSTEM_SEETING_SERVER_DEFALUT_VERSION));
            // 1.初始化 Context 参数
            buildContextParams(nsi, wsc);
            // 2.初始化监听器
            buildListener(nsi, wsc);
            // 3.构建Filter
            buildFilter(nsi, wsc);
            // 4.初始化 Servlet
            buildServlets(nsi, wsc);
            // 5.完成 ServletContext 构建
            sc = wsc;
            // 6.判断启动时是否初始化 Filter
            if(nsi.getFilterInfos() != null && nsi.getFilterInfos().isOnStartup()){
            		initialFilter();
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    EnvPropertyConfig.getContextProperty("env.setting.server.error.00000000"), e);
        }

        return sc;
    }

    /**
    * @Description:
    * <p> 构建自定义处理器，应用于 TCP、TLS 类型的服务启动的执行支持 </p>
    *
    * @Methods Name buildCustomerHandler
    * @param nsi
    * @throws
    * @return
    * @Create In 2017/6/9 By Jack
    **/
    private static void buildCustomerHandler(NettyServerInfo nsi){
        if (nsi.getHandlerInfos() == null || nsi.getHandlerInfos().getHandler() == null){
            return ;
        }
        Iterator handlers = nsi.getHandlerInfos().getHandler().iterator();
        try {
            while (handlers.hasNext()) {
                NettyServerInfo.HandlerInfos.Handler item = (NettyServerInfo.HandlerInfos.Handler) handlers.next();
                Class clazz = Class.forName(item.getHandlerClass());
                if (item.getConstruction() != null) {
                    NettyServerInfo.HandlerInfos.Handler.Construction construct = item.getConstruction();
                    if (construct.isParams() && construct.getArgs() != null) {
                        Iterator args = construct.getArgs().getArg().iterator();
                        List<Class> clsArgs = new ArrayList<Class>();
                        List<Object> clsArgValue = new ArrayList<Object>();
                        while (args.hasNext()) {
                            NettyServerInfo.HandlerInfos.Handler.Construction.Args.Arg argItem = (NettyServerInfo.HandlerInfos.Handler.Construction.Args.Arg)args.next();
                            clsArgs.add(Class.forName(argItem.getKey()));
                            clsArgValue.add(argItem.getValue());
                        }
                        Class[] paramsType = new Class[clsArgs.size()];
                        clsArgs.toArray(paramsType);
                        Constructor conn = clazz.getConstructor(paramsType);
                        customPipelineMap.put(item.getName(), (ChannelHandler)conn.newInstance(clsArgValue.toArray()));
                    }else{
                        Constructor conn = clazz.getConstructor();
                        customPipelineMap.put(item.getName(),(ChannelHandler)conn.newInstance());
                    }
                } else {
                    customPipelineMap.put(item.getName(), (ChannelHandler)clazz.newInstance());
                }
            }
        }catch (Exception e){
            throw new RuntimeException(
                    EnvPropertyConfig.getContextProperty("env.setting.server.error.00001019"), e);
        }
    }
    
    /**
     * 初始化 Filter
     * @Methods Name initialFilter
     * @Create In 2016年9月13日 By Jack void
     */
    private static void initialFilter(){
        Iterator<String> filters = ((WFJServletContext)sc).getFilterRegistrations().keySet().iterator();
        while(filters.hasNext()){
            ((WFJServletContext)sc).getFilter(filters.next());
        }
    }

    /**
     * 初始化 Context 参数
     *
     * @param nsi
     * @param wsc void
     * @Methods Name buildContextParams
     * @Create In 2016年8月2日 By Jack
     */
    @SuppressWarnings("deprecation")
    private static void buildContextParams(NettyServerInfo nsi, WFJServletContext wsc) {
        if (nsi.getBaseInfo() == null || nsi.getBaseInfo().getBaseParams() == null) {
            return;
        }
        Iterator<Param> params = nsi.getBaseInfo().getBaseParams().getParam().iterator();

        while (params.hasNext()) {
            Param item = params.next();
            if (item.getParamValue().contains(RESOURCE_PATH_SHUX)) {
                item.setParamValue(URLDecoder.decode(item.getParamValue()));
            }
            wsc.setInitParameter(item.getParamName(), item.getParamValue());
        }
    }

    /**
     * 初始化监听器
     *
     * @param nsi
     * @param wsc void
     * @Methods Name buildListener
     * @Create In 2016年8月2日 By Jack
     */
    private static void buildListener(NettyServerInfo nsi, WFJServletContext wsc) {
        if (nsi.getListenerInfos() == null || nsi.getListenerInfos().getListener() == null) {
            return;
        }
        Iterator<Listener> listeners = nsi.getListenerInfos().getListener().iterator();
        while (listeners.hasNext()) {

            try {
                Listener item = listeners.next();
                Object obj = ClassLoader.getSystemClassLoader().loadClass(item.getListenerClass()).newInstance();
                if (obj instanceof ServletContextListener) {
                    ServletContextListener listener = (ServletContextListener) obj;
                    listener.contextInitialized(new ServletContextEvent(wsc));
                    wsc.addListener(listener);
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                // TODO Auto-generated catch block
                throw new RuntimeException(
                        EnvPropertyConfig.getContextProperty("env.setting.server.error.00000000"), e);
            }
        }

    }

    /**
     * 根据配置构建 Filter 信息
     *
     * @param nsi
     * @param wsc void
     * @Methods Name buildFilter
     * @Create In 2016年8月16日 By Jack
     */
    private static void buildFilter(NettyServerInfo nsi, WFJServletContext wsc) {
        if (nsi.getFilterInfos() == null || nsi.getFilterInfos().getFilter() == null) {
            return;
        }

        Iterator<Filter> filters = nsi.getFilterInfos().getFilter().iterator();

        while (filters.hasNext()) {

            // 根据类名创建对应 Filter
            Filter item = filters.next();
            // 增加动态 Servlet 注册类
            WFJFilterRegistration wfjfr = (WFJFilterRegistration) wsc
                    .addFilter(item.getFilterName(), item.getFilterClass());
            // 装载初始化参数，并处理 Spring 初始化参数
            if (item.getInitParam() != null && !item.getInitParam().isEmpty()) {
                for (com.jack.netty.server.config.NettyServerInfo.FilterInfos.Filter.InitParam param : item
                        .getInitParam()) {
                    wfjfr.setInitParameter(param.getParamName(), param.getParamValue());
                }
            }

            //判断是否存在需要过滤的请求类型，如没有默认 REQUEST
            EnumSet<DispatcherType> dispatcherTypes = EnumSet.noneOf(DispatcherType.class);
            if (!ObjectUtils.isEmpty(item.getFilterMapping().getDispatcher())) {
                for (String value : item.getFilterMapping().getDispatcher()) {
                    dispatcherTypes.add(DispatcherType.valueOf(value));
                }
            }
            if (dispatcherTypes.isEmpty()) {
                dispatcherTypes.add(DispatcherType.REQUEST);
            }

            // 装载 URL 类型处理规则
            if (!StringUtils.isEmpty(item.getFilterMapping().getUrlPattern())) {
                String[] urlPattern = item.getFilterMapping().getUrlPattern().split(URL_PATTERN_SPLITE);
                wfjfr.addMappingForUrlPatterns(dispatcherTypes, true, urlPattern);
            }

            // 装在 Servlet 类型过滤规则
            if (!StringUtils.isEmpty(item.getFilterMapping().getServletName())) {
                String[] servlets = item.getFilterMapping().getServletName().split(URL_PATTERN_SPLITE);
                wfjfr.addMappingForServletNames(dispatcherTypes, true, servlets);
            }

            // 更新注册信息
            wsc.addFilterRegistration(item.getFilterName(), wfjfr);
        }

    }

    /**
     * 根据配置构建 Servlet
     *
     * @param nsi
     * @param wsc void
     * @Methods Name buildServlets
     * @Create In 2016年8月2日 By Jack
     */
    @SuppressWarnings("deprecation")
    private static void buildServlets(NettyServerInfo nsi, WFJServletContext wsc) {
        if (nsi.getDispatherInfos() == null || nsi.getDispatherInfos().getServlet() == null) {
            return;
        }

        // 初始化 Servlet
        Iterator<Servlet> servlets = nsi.getDispatherInfos().getServlet().iterator();
        while (servlets.hasNext()) {
            // 根据类名创建对应 Servlet
            NettyServerInfo.DispatherInfos.Servlet item = servlets.next();
            // 增加动态 Servlet 注册类
            WFJServletDynamic wfjsd = (WFJServletDynamic) wsc.addServlet(item.getServletName(), item.getServletClass());
            // 装载初始化参数，并处理 Spring 初始化参数
            if (item.getInitParam() != null && !item.getInitParam().isEmpty()) {
                for (InitParam param : item.getInitParam()) {
                    wfjsd.setInitParameter(param.getParamName(), param.getParamValue());
                }
            }

            // 装载 URL 处理规则
            String[] urlPattern = item.getServletSuffix().split(URL_PATTERN_SPLITE);
            for (String urlPatterns : urlPattern) {
                wfjsd.addMapping(URLDecoder.decode(urlPatterns));
            }
            // 更新注册信息
            wsc.addServletRegistration(item.getServletName(), wfjsd);
        }
    }

    /**
     * 获取 Servlet 配置
     *
     * @param servletName
     * @return ServletConfig
     * @Methods Name createServletConfig
     * @Create In 2016年5月1日 By Jack
     */
    public static ServletConfig createServletConfig(String servletName) {
        NettyServerInfo nsi = getNettyServerInfo();

        WFJServletConfig config = new WFJServletConfig(sc, servletName);
        for (NettyServerInfo.DispatherInfos.Servlet item : nsi.getDispatherInfos().getServlet()) {
            if (item.getServletName().equals(servletName)) {
                if (item.getInitParam() != null && !item.getInitParam().isEmpty()) {
                    for (InitParam initParam : item.getInitParam()) {
                        config.addInitParameter(initParam.getParamName(), initParam.getParamValue());
                    }
                }
                break;
            }
        }
        return config;
    }

    /**
     * 根据配置构建 FilterConfig
     *
     * @param filterName
     * @return FilterConfig
     * @Methods Name createFilterConfig
     * @Create In 2016年8月16日 By Jack
     */
    public static FilterConfig createFilterConfig(String filterName) {
        NettyServerInfo nsi = getNettyServerInfo();
        WFJFilterConfig config = new WFJFilterConfig(sc, filterName);
        for (NettyServerInfo.FilterInfos.Filter item : nsi.getFilterInfos().getFilter()) {
            if (item.getFilterName().equals(filterName)) {
                if (item.getInitParam() != null && !item.getInitParam().isEmpty()) {
                    for (com.jack.netty.server.config.NettyServerInfo.FilterInfos.Filter.InitParam initParam : item
                            .getInitParam()) {
                        config.addInitParameter(initParam.getParamName(), initParam.getParamValue());
                    }
                }
                break;
            }
        }
        return config;
    }

    /**
     * 返回 ServletContext对象，确保每个应用单例
     *
     * @return ServletContext
     * @Methods Name getServletContext
     * @Create In 2016年8月15日 By Jack
     */
    public static ServletContext getServletContext() {
        if (sc != null) {
            return sc;
        } else {
            return createHttpModeServer();
        }
    }


    /**
     * Return the set of filter mappings for this Context.
     */
    public static WFJFilterMap[] findFilterMaps() {
        return filterMaps.asArray();
    }


    /**
     * Add a filter mapping to this Context at the end of the current set
     * of filter mappings.
     *
     * @param filterMap The filter mapping to be added
     * @throws IllegalArgumentException if the specified filter name
     *                                  does not match an existing filter definition, or the filter mapping
     *                                  is malformed
     */
    public static void addFilterMap(WFJFilterMap filterMap) {
        validateFilterMap(filterMap);
        // Add this filter mapping to our registered set
        filterMaps.add(filterMap);
    }


    /**
     * Add a filter mapping to this Context before the mappings defined in the
     * deployment descriptor but after any other mappings added via this method.
     *
     * @param filterMap The filter mapping to be added
     * @throws IllegalArgumentException if the specified filter name
     *                                  does not match an existing filter definition, or the filter mapping
     *                                  is malformed
     */
    public static void addFilterMapBefore(WFJFilterMap filterMap) {
        validateFilterMap(filterMap);
        // Add this filter mapping to our registered set
        filterMaps.addBefore(filterMap);
    }


    /**
     * Validate the supplied FilterMap.
     */
    private static void validateFilterMap(WFJFilterMap filterMap) {
        // Validate the proposed filter mapping
        String[] servletNames = filterMap.getServletNames();
        String[] urlPatterns = filterMap.getURLPatterns();
//        if (findFilterDef(filterName) == null)
//            throw new IllegalArgumentException("standardContext.filterMap.name: " + filterName);

        if (!filterMap.getMatchAllServletNames() &&
                !filterMap.getMatchAllUrlPatterns() &&
                (servletNames.length == 0) && (urlPatterns.length == 0)) {
            throw new IllegalArgumentException("standardContext.filterMap.either");
        }
        // FIXME: Older spec revisions may still check this
        /*
        if ((servletNames.length != 0) && (urlPatterns.length != 0))
            throw new IllegalArgumentException
                (sm.getString("standardContext.filterMap.either"));
        */
        for (int i = 0; i < urlPatterns.length; i++) {
            if (!validateURLPattern(urlPatterns[i])) {
                throw new IllegalArgumentException("standardContext.filterMap.pattern" + urlPatterns[i]);
            }
        }
    }

    /**
     * Validate the syntax of a proposed <code>&lt;url-pattern&gt;</code>
     * for conformance with specification requirements.
     *
     * @param urlPattern URL pattern to be validated
     */
    private static boolean validateURLPattern(String urlPattern) {

        if (urlPattern == null) {
            return (false);
        }
        if (urlPattern.indexOf('\n') >= 0 || urlPattern.indexOf('\r') >= 0) {
            return (false);
        }
        if (urlPattern.equals("")) {
            return true;
        }
        if (urlPattern.startsWith("*.")) {
            if (urlPattern.indexOf('/') < 0) {
                checkUnusualURLPattern(urlPattern);
                return (true);
            } else {
                return (false);
            }
        }
        if ((urlPattern.startsWith("/")) &&
                (urlPattern.indexOf("*.") < 0)) {
            checkUnusualURLPattern(urlPattern);
            return (true);
        } else {
            return (false);
        }
    }

    /**
     * Check for unusual but valid <code>&lt;url-pattern&gt;</code>s.
     * See Bugzilla 34805, 43079 & 43080
     */
    private static void checkUnusualURLPattern(String urlPattern) {
        if (logger.isInfoEnabled()) {
            // First group checks for '*' or '/foo*' style patterns
            // Second group checks for *.foo.bar style patterns
            if ((urlPattern.endsWith("*") && (urlPattern.length() < 2 ||
                    urlPattern.charAt(urlPattern.length() - 2) != '/')) ||
                    urlPattern.startsWith("*.") && urlPattern.length() > 2 &&
                            urlPattern.lastIndexOf('.') > 1) {
                logger.info("Suspicious url pattern: \"" + urlPattern + "\"" +
                        " in context [" + ServerConfigWrappar.class.getName() + "] - see" +
                        " sections 12.1 and 12.2 of the Servlet specification");
            }
        }
    }

    /**
     * A helper class to manage the filter mappings in a Context.
     */
    private static final class ContextFilterMaps {
        private final Object lock = new Object();

        /**
         * The set of filter mappings for this application, in the order they
         * were defined in the deployment descriptor with additional mappings
         * added via the {@link ServletContext} possibly both before and after
         * those defined in the deployment descriptor.
         */
        private WFJFilterMap[] array = new WFJFilterMap[0];

        /**
         * Filter mappings added via {@link ServletContext} may have to be
         * inserted before the mappings in the deployment descriptor but must be
         * inserted in the order the {@link ServletContext} methods are called.
         * This isn't an issue for the mappings added after the deployment
         * descriptor - they are just added to the end - but correctly the
         * adding mappings before the deployment descriptor mappings requires
         * knowing where the last 'before' mapping was added.
         */
        private int insertPoint = 0;

        /**
         * Return the set of filter mappings.
         */
        public WFJFilterMap[] asArray() {
            synchronized (lock) {
                return array;
            }
        }

        /**
         * Add a filter mapping at the end of the current set of filter
         * mappings.
         *
         * @param filterMap The filter mapping to be added
         */
        public void add(WFJFilterMap filterMap) {
            synchronized (lock) {
                WFJFilterMap results[] = Arrays.copyOf(array, array.length + 1);
                results[array.length] = filterMap;
                array = results;
            }
        }

        /**
         * Add a filter mapping before the mappings defined in the deployment
         * descriptor but after any other mappings added via this method.
         *
         * @param filterMap The filter mapping to be added
         */
        public void addBefore(WFJFilterMap filterMap) {
            synchronized (lock) {
                WFJFilterMap results[] = new WFJFilterMap[array.length + 1];
                System.arraycopy(array, 0, results, 0, insertPoint);
                System.arraycopy(array, insertPoint, results, insertPoint + 1,
                        array.length - insertPoint);
                results[insertPoint] = filterMap;
                array = results;
                insertPoint++;
            }
        }

        /**
         * Remove a filter mapping.
         *
         * @param filterMap The filter mapping to be removed
         */
        @SuppressWarnings("unused")
        public void remove(WFJFilterMap filterMap) {
            synchronized (lock) {
                // Make sure this filter mapping is currently present
                int n = -1;
                for (int i = 0; i < array.length; i++) {
                    if (array[i] == filterMap) {
                        n = i;
                        break;
                    }
                }
                if (n < 0) {
                    return;
                }

                // Remove the specified filter mapping
                WFJFilterMap results[] = new WFJFilterMap[array.length - 1];
                System.arraycopy(array, 0, results, 0, n);
                System.arraycopy(array, n + 1, results, n, (array.length - 1)
                        - n);
                array = results;
                if (n < insertPoint) {
                    insertPoint--;
                }
            }
        }
    }

    /**
    * @Description:
    * <p> 返回定义的 CustomerHandler List </p>
    *
    * @Methods Name getCustomerPipline
    * @return LinkedHashMap<String, ChannelHandler>
    * @Create In 2017/6/9 By Jack
    **/
    public LinkedHashMap<String, ChannelHandler> getCustomerPipline(){
            return customPipelineMap;
    }
}
