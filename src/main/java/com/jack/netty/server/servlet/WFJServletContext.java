package com.jack.netty.server.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jack.netty.conf.Constant;
import com.jack.netty.conf.infc.Resource;
import com.jack.netty.conf.infc.ResourceLoader;
import com.jack.netty.conf.io.DefaultResourceLoader;
import com.jack.netty.conf.util.EnvPropertyConfig;
import com.jack.netty.server.container.factory.ServerConfigWrappar;
import com.jack.netty.server.http.util.WebUtils;
import com.jack.netty.util.Assert;
import com.jack.netty.util.ClassUtils;
import com.jack.netty.util.ObjectUtils;

import javax.activation.FileTypeMap;
import javax.servlet.*;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class WFJServletContext implements ServletContext {

    /**
     * Default Servlet name used by Tomcat, Jetty, JBoss, and GlassFish:
     * {@value} .
     */
    private static final String COMMON_DEFAULT_SERVLET_NAME = "default";

    private static final String TEMP_DIR_SYSTEM_PROPERTY = "java.io.tmpdir";

    private static final Set<SessionTrackingMode> DEFAULT_SESSION_TRACKING_MODES = new LinkedHashSet<SessionTrackingMode>(
            3);

    static {
        DEFAULT_SESSION_TRACKING_MODES.add(SessionTrackingMode.COOKIE);
        DEFAULT_SESSION_TRACKING_MODES.add(SessionTrackingMode.URL);
        DEFAULT_SESSION_TRACKING_MODES.add(SessionTrackingMode.SSL);
    }

    private final Log logger = LogFactory.getLog(getClass());

    private final ResourceLoader resourceLoader;

    private final String resourceBasePath;

    private String contextPath = "";

    private final Map<String, ServletContext> contexts = new HashMap<String, ServletContext>();

    private List<Object> applicationEventListenersList = new CopyOnWriteArrayList<Object>();

    private int majorVersion = 3;

    private int minorVersion = 0;

    private int effectiveMajorVersion = 3;

    private int effectiveMinorVersion = 0;

    private final Map<String, RequestDispatcher> namedRequestDispatchers = new HashMap<String, RequestDispatcher>();

    private String defaultServletName = COMMON_DEFAULT_SERVLET_NAME;

    private final Map<String, String> initParameters = new LinkedHashMap<String, String>();

    private final Map<String, Object> attributes = new LinkedHashMap<String, Object>();

    private String servletContextName = "WFJServletContext";

    private String serverInfo = "Netty-WFJ-Server/Unknown";

    private final Set<String> declaredRoles = new LinkedHashSet<String>();

    private Set<SessionTrackingMode> sessionTrackingModes;

    private final SessionCookieConfig sessionCookieConfig = new WFJSessionCookieConfig();

    protected final Map<String, Dynamic> servletRegistration = new LinkedHashMap<String, Dynamic>();

    protected final Map<String, javax.servlet.FilterRegistration.Dynamic> filterRegistration = new LinkedHashMap<String, javax.servlet.FilterRegistration.Dynamic>();

    protected final Map<String, Servlet> servletInited = new LinkedHashMap<String, Servlet>();

    protected final Map<String, Filter> filterInited = new LinkedHashMap<String, Filter>();

    //默认启动加载为否
    public static int DEFAULT_LOAD_ON_STARTUP_FALSE = -1;
    public static int DEFAULT_LOAD_ON_STARTUP_TRUE = 1;

    public static String DEFAULT_SPRING_SERVLET_PACKEG = "org.springframework.web.servlet";

    /**
     * Create a new {@code MockServletContext}, using no base path and a
     * {@link DefaultResourceLoader} (i.e. the classpath root as WAR root).
     *
     * @see org.springframework.core.io.DefaultResourceLoader
     */
    public WFJServletContext() {
        this("", null);
    }

    /**
     * Create a new {@code MockServletContext}, using a
     * {@link DefaultResourceLoader}.
     *
     * @param resourceBasePath the root directory of the WAR (should not end with a slash)
     * @see org.springframework.core.io.DefaultResourceLoader
     */
    public WFJServletContext(String resourceBasePath) {
        this(resourceBasePath, null);
    }

    /**
     * Create a new {@code MockServletContext}, using the specified
     * {@link ResourceLoader} and no base path.
     *
     * @param resourceLoader the ResourceLoader to use (or null for the default)
     */
    public WFJServletContext(ResourceLoader resourceLoader) {
        this("", resourceLoader);
    }

    /**
     * Create a new {@code MockServletContext} using the supplied resource base
     * path and resource loader.
     * <p>
     * Registers a {@link MockRequestDispatcher} for the Servlet named
     * {@literal 'default'}.
     *
     * @param resourceBasePath the root directory of the WAR (should not end with a slash)
     * @param resourceLoader   the ResourceLoader to use (or null for the default)
     * @see #registerNamedDispatcher
     */
    public WFJServletContext(String resourceBasePath, ResourceLoader resourceLoader) {
        this.resourceLoader = (resourceLoader != null ? resourceLoader : new DefaultResourceLoader());
        this.resourceBasePath = (resourceBasePath != null ? resourceBasePath : "");

        // Use JVM temp dir as ServletContext temp dir.
        String tempDir = System.getProperty(TEMP_DIR_SYSTEM_PROPERTY);
        if (tempDir != null) {
            this.attributes.put(WebUtils.TEMP_DIR_CONTEXT_ATTRIBUTE, new File(tempDir));
        }

        registerNamedDispatcher(this.defaultServletName, new WFJRequestDispatcher(this.defaultServletName));
    }

    /**
     * Build a full resource location for the given path, prepending the
     * resource base path of this {@code MockServletContext}.
     *
     * @param path the path as specified
     * @return the full resource path
     */
    protected String getResourceLocation(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return this.resourceBasePath + path;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = (contextPath != null ? contextPath : "");
    }

    @Override
    public String getContextPath() {
        return this.contextPath;
    }

    public void registerContext(String contextPath, ServletContext context) {
        this.contexts.put(contextPath, context);
    }

    @Override
    public ServletContext getContext(String contextPath) {
        if (this.contextPath.equals(contextPath)) {
            return this;
        }
        return this.contexts.get(contextPath);
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    @Override
    public int getMajorVersion() {
        return this.majorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    @Override
    public int getMinorVersion() {
        return this.minorVersion;
    }

    public void setEffectiveMajorVersion(int effectiveMajorVersion) {
        this.effectiveMajorVersion = effectiveMajorVersion;
    }

    @Override
    public int getEffectiveMajorVersion() {
        return this.effectiveMajorVersion;
    }

    public void setEffectiveMinorVersion(int effectiveMinorVersion) {
        this.effectiveMinorVersion = effectiveMinorVersion;
    }

    @Override
    public int getEffectiveMinorVersion() {
        return this.effectiveMinorVersion;
    }

    /**
     * This method uses the default
     * {@link javax.activation.FileTypeMap#getDefaultFileTypeMap() FileTypeMap}
     * from the Java Activation Framework to resolve MIME types.
     * <p>
     * The Java Activation Framework returns {@code "application/octet-stream"}
     * if the MIME type is unknown (i.e., it never returns {@code null}). Thus,
     * in order to honor the {@link ServletContext#getMimeType(String)}
     * contract, this method returns {@code null} if the MIME type is
     * {@code "application/octet-stream"}.
     * <p>
     * {@code MockServletContext} does not provide a direct mechanism for
     * setting a custom MIME type; however, if the default {@code FileTypeMap}
     * is an instance of {@code javax.activation.MimetypesFileTypeMap}, a custom
     * MIME type named {@code text/enigma} can be registered for a custom
     * {@code .puzzle} file extension in the following manner:
     * <p>
     * <pre style="code">
     * MimetypesFileTypeMap mimetypesFileTypeMap = (MimetypesFileTypeMap) FileTypeMap.getDefaultFileTypeMap();
     * mimetypesFileTypeMap.addMimeTypes(&quot;text/enigma    puzzle&quot;);
     * </pre>
     */
    @Override
    public String getMimeType(String filePath) {
        String mimeType = FileTypeMap.getDefaultFileTypeMap().getContentType(filePath);
        return ("application/octet-stream".equals(mimeType) ? null : mimeType);
    }

    @Override
    public Set<String> getResourcePaths(String path) {
        String actualPath = (path.endsWith("/") ? path : path + "/");
        Resource resource = this.resourceLoader.getResource(getResourceLocation(actualPath));
        try {
            File file = resource.getFile();
            String[] fileList = file.list();
            if (ObjectUtils.isEmpty(fileList)) {
                return null;
            }
            Set<String> resourcePaths = new LinkedHashSet<String>(fileList.length);
            for (String fileEntry : fileList) {
                String resultPath = actualPath + fileEntry;
                if (resource.createRelative(fileEntry).getFile().isDirectory()) {
                    resultPath += "/";
                }
                resourcePaths.add(resultPath);
            }
            return resourcePaths;
        } catch (IOException ex) {
            logger.debug("Couldn't get resource paths for " + resource, ex);
            return null;
        }
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        Resource resource = this.resourceLoader.getResource(getResourceLocation(path));
        if (!resource.exists()) {
            return null;
        }
        try {
            return resource.getURL();
        } catch (MalformedURLException ex) {
            throw ex;
        } catch (IOException ex) {
            logger.warn("Couldn't get URL for " + resource, ex);
            return null;
        }
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        Resource resource = this.resourceLoader.getResource(getResourceLocation(path));
        if (!resource.exists()) {
            return null;
        }
        try {
            return resource.getInputStream();
        } catch (IOException ex) {
            logger.warn("Couldn't open InputStream for " + resource, ex);
            return null;
        }
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("RequestDispatcher path at ServletContext level must start with '/'");
        }
        return new WFJRequestDispatcher(path);
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String path) {
        return this.namedRequestDispatchers.get(path);
    }

    /**
     * Register a {@link RequestDispatcher} (typically a
     * {@link MockRequestDispatcher}) that acts as a wrapper for the named
     * Servlet.
     *
     * @param name              the name of the wrapped Servlet
     * @param requestDispatcher the dispatcher that wraps the named Servlet
     * @see #getNamedDispatcher
     * @see #unregisterNamedDispatcher
     */
    public void registerNamedDispatcher(String name, RequestDispatcher requestDispatcher) {
        Assert.notNull(name, "RequestDispatcher name must not be null");
        Assert.notNull(requestDispatcher, "RequestDispatcher must not be null");
        this.namedRequestDispatchers.put(name, requestDispatcher);
    }

    /**
     * Unregister the {@link RequestDispatcher} with the given name.
     *
     * @param name the name of the dispatcher to unregister
     * @see #getNamedDispatcher
     * @see #registerNamedDispatcher
     */
    public void unregisterNamedDispatcher(String name) {
        Assert.notNull(name, "RequestDispatcher name must not be null");
        this.namedRequestDispatchers.remove(name);
    }

    /**
     * Get the name of the <em>default</em> {@code Servlet}.
     * <p>
     * Defaults to {@literal 'default'}.
     *
     * @see #setDefaultServletName
     */
    public String getDefaultServletName() {
        return this.defaultServletName;
    }

    /**
     * Set the name of the <em>default</em> {@code Servlet}.
     * <p>
     * Also {@link #unregisterNamedDispatcher unregisters} the current default
     * {@link RequestDispatcher} and {@link #registerNamedDispatcher replaces}
     * it with a {@link MockRequestDispatcher} for the provided
     * {@code defaultServletName}.
     *
     * @param defaultServletName the name of the <em>default</em> {@code Servlet}; never
     *                           {@code null} or empty
     * @see #getDefaultServletName
     */
    public void setDefaultServletName(String defaultServletName) {
        Assert.notNull(defaultServletName, "defaultServletName must not be null or empty");
        unregisterNamedDispatcher(this.defaultServletName);
        this.defaultServletName = defaultServletName;
        registerNamedDispatcher(this.defaultServletName, new WFJRequestDispatcher(this.defaultServletName));
    }

    /**
     * 获取Filter 实例，如没有初始化则初始化
     *
     * @param name
     * @return Filter
     * @Methods Name getFilter
     * @Create In 2016年8月16日 By Jack
     */
    public Filter getFilter(String name) {
        if (this.filterInited.containsKey(name)) {
            return this.filterInited.get(name);
        }

        WFJFilterRegistration item = (WFJFilterRegistration) this.filterRegistration.get(name);
        FilterConfig config = ServerConfigWrappar.createFilterConfig(name);
        try {
            Filter result = createFilter(item.getContainer());
            result.init(config);
            this.filterInited.put(name, result);
            return result;
        } catch (ServletException e) {
            // TODO Auto-generated catch block
            logger.error(this.getClass().getName() + " : Could not load Filter!");
            throw new UnsupportedOperationException();
        }

    }

    @Override
    @Deprecated
    public Servlet getServlet(String name) {
        if (servletInited.containsKey(name)) {
            return servletInited.get(name);
        }

        WFJServletDynamic item = (WFJServletDynamic) this.servletRegistration.get(name);
        if (item.getLoadOnStartup() > DEFAULT_LOAD_ON_STARTUP_FALSE) {
            Servlet servlet = buildASSpring(item.getContainer());
            this.servletInited.put(name, servlet);
            return servlet;
        } else {
            ServletConfig config = ServerConfigWrappar.createServletConfig(name);
            Servlet result = buildASSpring(item.getContainer());
            try {
                result.init(config);
                this.servletInited.put(name, result);
                return result;
            } catch (ServletException e) {
                // TODO Auto-generated catch block
                logger.error(this.getClass().getName() + " : Could not load servlet!");
                throw new UnsupportedOperationException();
            }

        }
    }

    @Override
    @Deprecated
    public Enumeration<Servlet> getServlets() {
        Set<Servlet> cc = new HashSet<Servlet>();
        Iterator<Dynamic> registrations = this.servletRegistration.values().iterator();
        while (registrations.hasNext()) {
            cc.add(buildASSpring(((WFJServletDynamic) registrations.next()).getContainer()));
        }
        return Collections.enumeration(cc);
    }

    @Override
    @Deprecated
    public Enumeration<String> getServletNames() {
        return Collections.enumeration(this.servletRegistration.keySet());
    }

    @Override
    public void log(String message) {
        logger.info(message);
    }

    @Override
    @Deprecated
    public void log(Exception ex, String message) {
        logger.info(message, ex);
    }

    @Override
    public void log(String message, Throwable ex) {
        logger.info(message, ex);
    }

    @Override
    public String getRealPath(String path) {
        Resource resource = this.resourceLoader.getResource(getResourceLocation(path));
        try {
            return resource.getFile().getAbsolutePath();
        } catch (IOException ex) {
            logger.warn("Couldn't determine real path of resource " + resource, ex);
            return null;
        }
    }

    @Override
    public String getServerInfo() {
        return this.serverInfo;
    }

    /**
     * @Param String serverInfo to set
     */
    public void setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
    }

    @Override
    public String getInitParameter(String name) {
        Assert.notNull(name, "Parameter name must not be null");
        return this.initParameters.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(this.initParameters.keySet());
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        Assert.notNull(name, "Parameter name must not be null");
        if (this.initParameters.containsKey(name)) {
            return false;
        }
        this.initParameters.put(name, value);
        return true;
    }

    public void addInitParameter(String name, String value) {
        Assert.notNull(name, "Parameter name must not be null");
        this.initParameters.put(name, value);
    }

    @Override
    public Object getAttribute(String name) {
        Assert.notNull(name, "Attribute name must not be null");
        return this.attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(new LinkedHashSet<String>(this.attributes.keySet()));
    }

    @Override
    public void setAttribute(String name, Object value) {
        Assert.notNull(name, "Attribute name must not be null");
        if (value != null) {
            this.attributes.put(name, value);
        } else {
            this.attributes.remove(name);
        }
    }

    @Override
    public void removeAttribute(String name) {
        Assert.notNull(name, "Attribute name must not be null");
        this.attributes.remove(name);
    }

    public void setServletContextName(String servletContextName) {
        this.servletContextName = servletContextName;
    }

    @Override
    public String getServletContextName() {
        return this.servletContextName;
    }

    @Override
    public ClassLoader getClassLoader() {
        return ClassUtils.getDefaultClassLoader();
    }

    @Override
    public void declareRoles(String... roleNames) {
        Assert.notNull(roleNames, "Role names array must not be null");
        for (String roleName : roleNames) {
            //Assert.hasLength(roleName, "Role name must not be empty");
            this.declaredRoles.add(roleName);
        }
    }

    public Set<String> getDeclaredRoles() {
        return Collections.unmodifiableSet(this.declaredRoles);
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes)
            throws IllegalStateException, IllegalArgumentException {
        this.sessionTrackingModes = sessionTrackingModes;
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return DEFAULT_SESSION_TRACKING_MODES;
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return (this.sessionTrackingModes != null ? Collections
                .unmodifiableSet(this.sessionTrackingModes) : DEFAULT_SESSION_TRACKING_MODES);
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        return this.sessionCookieConfig;
    }

    // ---------------------------------------------------------------------
    // Unsupported Servlet 3.0 registration methods
    // ---------------------------------------------------------------------

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        try {
            WFJServletDynamic wsr = new WFJServletDynamic(servletName,
                    (Class<? extends Servlet>) ClassLoader.getSystemClassLoader().loadClass(className));
            wsr.setLoadOnStartup(DEFAULT_LOAD_ON_STARTUP_FALSE);
            return wsr;
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            throw new UnsupportedOperationException();
        }

    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        WFJServletDynamic wsr = new WFJServletDynamic(servletName, servlet.getClass());
        wsr.setLoadOnStartup(DEFAULT_LOAD_ON_STARTUP_FALSE);
        return wsr;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        WFJServletDynamic wsr = new WFJServletDynamic(servletName, servletClass);
        wsr.setLoadOnStartup(DEFAULT_LOAD_ON_STARTUP_FALSE);
        return wsr;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Servlet> T createServlet(Class<T> c) throws ServletException {
        return (T) buildASSpring(c);
    }

    /**
     * 构造 Servlet，如果是Spring 则注入 Application Context
     *
     * @param c
     * @return Servlet
     * @Methods Name buildASSpring
     * @Create In 2016年5月2日 By Jack
     */
    private Servlet buildASSpring(Class<? extends Servlet> c) {
        try {
            return c.newInstance();

        } catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException e) {
            // TODO Auto-generated catch block
            throw new UnsupportedOperationException();
        }
    }

    /**
     * This method always returns {@code null}.
     *
     * @see javax.servlet.ServletContext#getServletRegistration(java.lang.String)
     */
    @Override
    public ServletRegistration getServletRegistration(String servletName) {
        return this.servletRegistration.get(servletName);
    }

    /**
     * This method always returns an {@linkplain Collections#emptyMap empty map}
     * .
     *
     * @see javax.servlet.ServletContext#getServletRegistrations()
     */
    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        if (this.servletRegistration.isEmpty()) {
            return Collections.emptyMap();
        } else {
            return this.servletRegistration;
        }
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        try {
            WFJFilterRegistration wfr = new WFJFilterRegistration(filterName,
                    (Class<? extends Filter>) ClassLoader.getSystemClassLoader().loadClass(className));
            return wfr;
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
        		logger.error("Class[" + className + "] Could Not Found In Project!");
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        WFJFilterRegistration wfr = new WFJFilterRegistration(filterName, filter.getClass());
        return wfr;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        WFJFilterRegistration wfr = new WFJFilterRegistration(filterName, filterClass);
        return wfr;
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> c) throws ServletException {
        try {
            return c.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            // TODO Auto-generated catch block
        		logger.error("Class[" + c.getName() + "] Could Not newInstance In Project!");
            throw new UnsupportedOperationException();
        }
    }

    /**
     * This method always returns {@code null}.
     *
     * @see javax.servlet.ServletContext#getFilterRegistration(java.lang.String)
     */
    @Override
    public FilterRegistration getFilterRegistration(String filterName) {
        return this.filterRegistration.get(filterName);
    }

    /**
     * This method always returns an {@linkplain Collections#emptyMap empty map}
     * .
     *
     * @see javax.servlet.ServletContext#getFilterRegistrations()
     */
    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        if (this.filterRegistration.isEmpty()) {
            return Collections.emptyMap();
        } else {
            return this.filterRegistration;
        }
    }

    @Override
    public void addListener(Class<? extends EventListener> listenerClass) {
        EventListener listener;
        try {
            listener = createListener(listenerClass);
        } catch (ServletException e) {
            throw new IllegalArgumentException(
                    "applicationContext.addListener.iae.init" + listenerClass.getName() + e.getMessage());
        }
        addListener(listener);
    }

    @Override
    public void addListener(String className) {

        try {
            Object obj = ClassLoader.getSystemClassLoader().loadClass(className);

            if (!(obj instanceof EventListener)) {
                throw new IllegalArgumentException("applicationContext.addListener.iae.wrongType" + className);
            }
            EventListener listener = (EventListener) obj;
            addListener(listener);

        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("applicationContext.addListener.iae.cnfe" + className + e.getMessage());
        }
    }

    @Override
    public <T extends EventListener> void addListener(T t) {

        boolean match = false;
        if (t instanceof ServletContextAttributeListener ||
                t instanceof ServletRequestListener ||
                t instanceof ServletRequestAttributeListener ||
                t instanceof HttpSessionIdListener ||
                t instanceof HttpSessionAttributeListener) {

            this.applicationEventListenersList.add(t);
            match = true;
        }

        if (t instanceof HttpSessionListener
                || t instanceof ServletContextListener) {
            // Add listener directly to the list of instances rather than to
            // the list of class names.
            this.applicationEventListenersList.add(t);
            match = true;
        }

        if (match) {
            return;
        }

        if (t instanceof ServletContextListener) {
            throw new IllegalArgumentException(
                    "applicationContext.addListener.iae.sclNotAllowed" + t.getClass().getName());
        } else {
            throw new IllegalArgumentException("applicationContext.addListener.iae.wrongType" + t.getClass().getName());
        }
    }

    @Override
    public <T extends EventListener> T createListener(Class<T> c) throws ServletException {
        try {
            EventListener instance = (EventListener) c.newInstance();

            if (instance instanceof ServletContextListener ||
                    instance instanceof ServletContextAttributeListener ||
                    instance instanceof ServletRequestListener ||
                    instance instanceof ServletRequestAttributeListener ||
                    instance instanceof HttpSessionListener ||
                    instance instanceof HttpSessionIdListener ||
                    instance instanceof HttpSessionAttributeListener) {
                return (T) instance;
            }
            throw new IllegalArgumentException(
                    "applicationContext.addListener.iae.wrongType" + instance.getClass().getName());
        } catch (IllegalAccessException e) {
            throw new ServletException(e);
        } catch (InstantiationException e) {
            throw new ServletException(e);
        }
    }

    @Override
    public String getVirtualServerName() {
        // TODO Auto-generated method stub
        return EnvPropertyConfig.getContextProperty(Constant.SYSTEM_SEETING_SERVER_DEFALUT_NAME) + ":" +
                EnvPropertyConfig.getContextProperty(Constant.SYSTEM_SEETING_SERVER_DEFALUT_VERSION);
    }

    /**
     * 增加注册 Filter 信息
     *
     * @param key
     * @param value
     * @return javax.servlet.FilterRegistration.Dynamic
     * @Methods Name addFilterRegistration
     * @Create In 2016年8月16日 By Jack
     */
    public javax.servlet.FilterRegistration.Dynamic addFilterRegistration(String key,
            javax.servlet.FilterRegistration.Dynamic value) {
        return updateFilterRegistration(key, value);
    }

    /**
     * 更新 Filter 信息
     *
     * @param name
     * @param dynamic
     * @return javax.servlet.FilterRegistration.Dynamic
     * @Methods Name updateFilterRegistration
     * @Create In 2016年8月16日 By Jack
     */
    public javax.servlet.FilterRegistration.Dynamic updateFilterRegistration(String name,
            javax.servlet.FilterRegistration.Dynamic dynamic) {
        if (this.filterRegistration.containsKey(name)) {
            this.filterRegistration.remove(name);
        }
        return this.filterRegistration.put(name, dynamic);
    }

    /**
     * 更新注册 Servlet 的信息
     *
     * @param name
     * @param dynamic
     * @return Dynamic
     * @Methods Name updateServletRegistration
     * @Create In 2016年5月1日 By Jack
     */
    public Dynamic updateServletRegistration(String name, Dynamic dynamic) {
        if (this.servletRegistration.containsKey(name)) {
            this.servletRegistration.remove(name);
        }
        return this.servletRegistration.put(name, dynamic);
    }

    /**
     * 增加注册 Servlet 信息
     *
     * @param key
     * @param value
     * @return Dynamic
     * @Methods Name addServletRegistration
     * @Create In 2016年5月1日 By Jack
     */
    public Dynamic addServletRegistration(String key, Dynamic value) {
        return updateServletRegistration(key, value);
    }

    /**
     * @Return the ConcurrentMap<String,Servlet> servletInited
     */
    public Map<String, Servlet> getServletInited() {
        return servletInited;
    }

    /**
     * @Return the ConcurrentMap<String,Filter> servletInited
     */
    public Map<String, Filter> getFilterInited() {
        return filterInited;
    }

    /**
     * @Return the List<Object> applicationEventListenersList
     */
    public List<Object> getApplicationEventListenersList() {
        return applicationEventListenersList;
    }

    /**
     * @Param List<Object> applicationEventListenersList to set
     */
    public void setApplicationEventListenersList(List<Object> applicationEventListenersList) {
        this.applicationEventListenersList = applicationEventListenersList;
    }


}