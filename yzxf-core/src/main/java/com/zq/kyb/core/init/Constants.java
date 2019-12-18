package com.zq.kyb.core.init;

import com.zq.kyb.core.dao.CacheService;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

/**
 * 作用:定义项目的静态常量
 * <p/>
 * Date: 2007-4-18 Time: 9:35:17
 */
public class Constants {
    public static final File TEMP_DIR = new File("/tmp");
    public static final String ALGORITHM = "SHA-1";
    public static final String API_Version = "1";
    public static String DEFAULT_ENCODING = "UTF-8";
    public static final String basePackage = "com.zq.kyb";
    public static String mainDB = "yzxf_new";//主数据库
    public static String dbConfig="yzxf_db";//数据库的链接配置


    public static String moduleType; //当前进程的模块类型
    public static String moduleName; //当前进程的模块名称
    public static String moduleHost; //当前进程的模块绑定的主机
    public static Integer modulePort; //当前进程的模块绑定的端口
    public static String moduleHostHouse; //主机所在机房

    public static String backgroundRunClass; //当前模块轮询的class
    public static long backgroundRunTime; //当前轮询执行的间隔时间

    //管理服务器地址
    public static String adminHost;
    //管理服务器端口
    public static Integer adminPort;

    //根据配置文件设置对应的cache类
    public static Class<CacheService> cacheImplClass;
    public static Properties confProerties;

    //平台的Agent Id
    public static String AGENT_PROFORM_ID = "A-000001";
}
