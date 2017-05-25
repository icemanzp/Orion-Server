/**
 * @Probject Name: netty-wfj-base
 * @Path: com.jack.netty.confConstant.java
 * @Create By Jack
 * @Create In 2015年8月27日 上午10:21:27
 * TODO
 */
package com.jack.netty.conf;

/**
 * @Class Name Constant
 * @Author Jack
 * @Create In 2015年8月27日
 */
public class Constant {

    public static String SYSTEM_SEETING_SERVER_DEFAULT_COMMAND_STARTUP = "start";

    public static String SYSTEM_SEETING_SERVER_DEFAULT_COMMAND_STOP = "stop";

    public static Integer SYSTEM_SEETING_SERVER_DEFAULT_COMMAND_STARTUP_PORT = 1;

    public static Integer SYSTEM_SEETING_SERVER_DEFAULT_COMMAND_STARTUP_CPORT = 2;

    public static Integer SYSTEM_SEETING_SERVER_DEFAULT_COMMAND_STOP_CPORT = 1;

    public static final String SYSTEM_SEETING_SERVER_DEFAULT_MAX_INCOME = "system.setting.max-income-counts";

    public static final Integer SYSTEM_SEETING_SERVER_DEFAULT_MAX_INCOME_COUNTS = 100;

    public static final String SYSTEM_SEETING_SERVER_DEFAULT_RESOURCE_DIR = "env.setting.path.resourceDir";

    public static final String SYSTEM_SEETING_SERVER_DEFAULT_URI_ENCODING = "env.setting.path.uri.encoding";

    public static final String SYSTEM_SEETING_SERVER_DEFAULT_SERVER_PORT = "env.setting.path.port";

    public static final Integer SYSTEM_SEETING_SERVER_DEFAULT_SERVER_PORT_DEFAULT = 80;

    public static final String SYSTEM_SEETING_SERVER_DEFAULT_SERVER_HOST_PORT_SPLIT = ":";

    public static final String SYSTEM_SEETING_SERVER_DEFAULT_SERVER_MONITOR_ROOT_PATH = "env.setting.server.monitor.root.path";

    public static final String SYSTEM_SEETING_SERVER_DEFAULT_SERVER_MONITOR_ROOT_DESC = "env.setting.server.monitor.root.desc";

    public static final String SYSTEM_SEETING_SERVER_DEFAULT_SERVER_CONTROL_PORT = "env.setting.path.cport";

    public static final String SYSTEM_SEETING_SERVER_DEFAULT_SERVER_POWERBY = "env.setting.server.system.powerby";
    
    public static final String SYSTEM_SEETING_SERVER_DEFAULT_SERVER_VERSION = "env.setting.server.system.version";

    public static final String SYSTEM_SEETING_SERVER_DEFAULT_COOKIE_DOMAIN = "env.setting.path.cookieDomain";

    public static final String SYSTEM_SEETING_SERVER_DEFAULT_PREFIX_IGNORE = "/favicon.ico";

    public static final String SYSTEM_SEETING_SERVER_DEFAULT_CONTEXT_CODE = "system.setting.context-code";

    public static final String SYSTEM_SEETING_SERVER_DEFAULT_CONTEXT_ROOT = "system.setting.context-root";

    public static final String SYSTEM_SEETING_SERVER_DEFAULT_CONTEXT_NAME = "system.setting.context-name";

    public static final String SYSTEM_SEETING_SERVER_DEFAULT_INSTANCE_KEY = "system.setting.context-name-instant";

    public static final String SYSTEM_SEETING_SERVER_DEFAULT_CONTEXT_DESC = "system.setting.context-desc";

    public static final String SYSTEM_SEETING_SERVER_DEFAULT_INSTANCE_ID = "-i1";

    public static final String SYSTEM_SEETING_SERVER_DEFALUT_ETHERNET = "system.setting.interface.name";

    public static final Integer SYSTEM_SEETING_PROCESS_RESULT_CODE_STOP = new Integer(0);

    public static final Integer SYSTEM_SEETING_PROCESS_RESULT_CODE_EXIT = new Integer(-1);

    public static final Integer SYSTEM_SEETING_PROCESS_RESULT_CODE_LOSS = new Integer(-4);

    public static final Integer SYSTEM_SEETING_PROCESS_RESULT_CODE_NODEEXISTS = new Integer(-110);

    public static final Integer SYSTEM_SEETING_PROCESS_RESULT_CODE_SESSIONEXPIRED = new Integer(-112);

    public static final String SYSTEM_SEETING_MONITOR_SETTING_DEFAULT_IP = "127.0.0.1";

    public static final String SYSTEM_SEETING_KAFKA_BOOTSTRAP_SERVER = "system.setting.kafka.bootstrap.servers";

    public static final String SYSTEM_SEETING_KAFKA_ACKS = "system.setting.kafka.acks";

    public static final String SYSTEM_SEETING_KAFKA_RETRIES = "system.setting.kafka.retries";

    public static final String SYSTEM_SEETING_KAFKA_BATCH_SIZE = "system.setting.kafka.batch.size";

    public static final String SYSTEM_SEETING_KAFKA_RECEIVER = "system.setting.kafka.sql.topic";

    public static final String SYSTEM_SEETING_SERVER_DEFALUT_NAME = "env.setting.server.system.name";

    public static final String SYSTEM_SEETING_SERVER_DEFALUT_VERSION = "env.setting.server.system.version";

    public static final String SYSTEM_SEETING_SERVER_DEFALUT_NAME_VERSION_SPLIT = "/";
}
