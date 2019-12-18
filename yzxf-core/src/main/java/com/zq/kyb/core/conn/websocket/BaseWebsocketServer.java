package com.zq.kyb.core.conn.websocket;

import com.zq.kyb.core.conn.http.*;
import com.zq.kyb.core.init.Constants;
import org.apache.log4j.Logger;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;

import javax.servlet.DispatcherType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;

/**
 *
 */
public class BaseWebsocketServer {

    protected void bindBackend(WebSocketServlet websocketServlet) throws IOException {
        long start = System.currentTimeMillis();
        Properties prop = loadServiceConfig();

        Runtime runtime = Runtime.getRuntime();
        ShutdownHook engineShutdownHook = new ShutdownHook();
        runtime.addShutdownHook(engineShutdownHook);

        QueuedThreadPool threadPool = new QueuedThreadPool(500, 10, 5000);//线程池,max=500个,min=10个,idleTimeout=5000毫秒
        Server server = new Server(threadPool);

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(Integer.valueOf(prop.getProperty("module_port")));
        connector.setAcceptQueueSize(2000);//接受的队列大小
        connector.setIdleTimeout(5000);//5秒钟超时


        //connector.getStopTimeout();
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Add a websocket to a specific path spec
        EnumSet<DispatcherType> all = EnumSet.of(DispatcherType.ASYNC, DispatcherType.ERROR, DispatcherType.FORWARD,
                DispatcherType.INCLUDE, DispatcherType.REQUEST);
        FilterHolder baseFilter = new FilterHolder(new RpcFilter());
        //baseFilter.setAsyncSupported(true);
        context.addFilter(baseFilter, "/rpc/api/*", all);

        if ("common".equals(Constants.moduleName)) {
            ServletHolder holderEvents = new ServletHolder("ws-events", websocketServlet);
            context.addServlet(holderEvents, "/events/*");
        }
        try {
            server.start();
            server.dump(System.out);
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
        Logger.getLogger(getClass()).info(Constants.moduleName + " server started in " + (System.currentTimeMillis() - start) + " ms.");

    }


    protected void bindFront(WebSocketServlet websocketServlet) throws IOException {
        long start = System.currentTimeMillis();
        Properties prop = loadServiceConfig();

        System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        //String basePath = ".";//pro
        String basePath = prop.getProperty("web_base_path");
        Logger.getLogger(getClass()).info(new File(basePath).getCanonicalPath());

        try {

            Runtime runtime = Runtime.getRuntime();
            ShutdownHook engineShutdownHook = new ShutdownHook();
            runtime.addShutdownHook(engineShutdownHook);

            QueuedThreadPool threadPool = new QueuedThreadPool(500, 10, 5000);//线程池,max=500个,min=10个,idleTimeout=5000毫秒
            Server server = new Server(threadPool);

            Integer module_port = Integer.valueOf(prop.getProperty("module_port"));


            // 设置ssl连接器

//            HttpConfiguration https_config = new HttpConfiguration();
//            https_config.setSecureScheme("https");
//            https_config.setSecurePort(module_port);
//            https_config.setOutputBufferSize(32768);
//            https_config.addCustomizer(new SecureRequestCustomizer());
//
//            SslContextFactory sslContextFactory = new SslContextFactory();
//            sslContextFactory.setKeyStorePath(prop.getProperty("ssl_crt_path"));
            //OBF-主要目的是编码keystore的password,使其不源码展示在代码中
//            sslContextFactory.setKeyStorePassword("OBF:1nlx1juc1jue1l8f1k8k1igd1ku318jj1kqr1idt1k5m1l4v1jrc1jre1ni1");
//            sslContextFactory.setKeyManagerPassword("OBF:1nlx1juc1jue1l8f1k8k1igd1ku318jj1kqr1idt1k5m1l4v1jrc1jre1ni1");
//
//            ServerConnector httpsConnector = new ServerConnector(server,
//                    new SslConnectionFactory(sslContextFactory, "http/1.1"),
//                    new HttpConnectionFactory(https_config));
//
//            httpsConnector.setPort(module_port);
//            httpsConnector.setIdleTimeout(500000);
//            server.addConnector(httpsConnector);

            //http,发布时取消
//            ServerConnector connector = new ServerConnector(server);
//            connector.setPort(module_port);
//            server.addConnector(connector);


            //普通http设置
            ServerConnector connector = new ServerConnector(server);
            connector.setPort(module_port);
            connector.setAcceptQueueSize(2000);//接受的队列大小
            connector.setIdleTimeout(5000);//5秒钟超时

            server.addConnector(connector);

            // Set JSP to use Standard JavaC always
            System.setProperty("org.apache.jasper.compiler.disablejsr199", "false");
            WebAppContext context = new WebAppContext();
            context.setContextPath("/");

            File tempDir = new File("./");
            File scratchDir = new File(tempDir.toString(), "embedded-jetty-jsp");
            context.setAttribute("javax.http.context.tempdir", scratchDir);

            context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                    ".*/[^/]*http-api-[^/]*\\.jar$|.*/javax.http.jsp.jstl-.*\\.jar$|.*/.*taglibs.*\\.jar$");
            //context.setResourceBase(baseUri.toASCIIString());

            JettyJasperInitializer sci = new JettyJasperInitializer();
            ContainerInitializer initializer = new ContainerInitializer(sci, null);
            List<ContainerInitializer> initializers = new ArrayList<ContainerInitializer>();
            initializers.add(initializer);
            context.setAttribute("org.eclipse.jetty.containerInitializers", initializers);

            context.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
            context.addBean(new ServletContainerInitializersStarter(context), true);

            ClassLoader jspClassLoader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
            context.setClassLoader(jspClassLoader);

            context.setWar(basePath + "/src/main/webapp");

            ServletHolder holderJsp = new ServletHolder("jsp", JettyJspServlet.class);
            holderJsp.setInitOrder(0);
            holderJsp.setInitParameter("logVerbosityLevel", "DEBUG");
            holderJsp.setInitParameter("fork", "false");
            holderJsp.setInitParameter("xpoweredBy", "false");
            holderJsp.setInitParameter("compilerTargetVM", "1.7");
            holderJsp.setInitParameter("compilerSourceVM", "1.7");
            holderJsp.setInitParameter("keepgenerated", "true");
            context.addServlet(holderJsp, "*.jsp");

            //websocket
            ServletHolder websocketholder = new ServletHolder("ws-events", websocketServlet);
            context.addServlet(websocketholder, "/events/*");

            EnumSet<DispatcherType> all = EnumSet.of(DispatcherType.ASYNC, DispatcherType.ERROR, DispatcherType.FORWARD,
                    DispatcherType.INCLUDE, DispatcherType.REQUEST);
            String contentType = "text/html,text/plain,text/xml,text/javascript,text/css,application/javascript,image/svg+xml";

            //开启压缩
            //baseFilter.setAsyncSupported(true);
            GzipHandler gzipHandler = new GzipHandler();
            gzipHandler.setIncludedMimeTypes("text/html", "text/plain", "text/xml",
                    "text/css", "application/javascript", "text/javascript", "application/json");
            gzipHandler.setHandler(context);

            //basefilter
            FilterHolder baseFilter = new FilterHolder(new BaseFilter());
            //baseFilter.setAsyncSupported(true);
            context.addFilter(baseFilter, "/s_admin/api/*", all);
            context.addFilter(baseFilter, "/s_user/api/*", all);
            context.addFilter(baseFilter, "/s_agent/api/*", all);
            context.addFilter(baseFilter, "/s_member/api/*", all);


            //filefilter
            FilterHolder imgFilter = new FilterHolder(new ImgFilter());
            //baseFilter.setAsyncSupported(true);
            context.addFilter(imgFilter, "/s_img/*", all);


            FilterHolder baseJsFilter = new FilterHolder(new BaseJsFilter());
            //baseFilter.setAsyncSupported(true);
//            baseFilter.setInitParameter("mimeTypes", contentType);
//            baseFilter.setInitParameter("minGzipSize", "256");
            context.addFilter(baseJsFilter, "/view_js/*", EnumSet.of(DispatcherType.REQUEST));

            //html5filter
            FilterHolder baseJspfilter = new FilterHolder(new BaseJspFilter());
            //html5filter.setAsyncSupported(true);
            context.addFilter(baseJspfilter, "/front_view/*", EnumSet.of(DispatcherType.REQUEST));
            context.addFilter(baseJspfilter, "/view/*", EnumSet.of(DispatcherType.REQUEST));

            //html5filter
            FilterHolder html5filter = new FilterHolder(new Html5PageFilter());
            //html5filter.setAsyncSupported(true);
            context.addFilter(html5filter, "/seller/*", EnumSet.of(DispatcherType.REQUEST));
            context.addFilter(html5filter, "/store/*", EnumSet.of(DispatcherType.REQUEST));
            context.addFilter(html5filter, "/member/*", EnumSet.of(DispatcherType.REQUEST));

            context.addFilter(html5filter, "/yzxfMall/*", EnumSet.of(DispatcherType.REQUEST));
            context.addFilter(html5filter, "/yzxfMember/*", EnumSet.of(DispatcherType.REQUEST));
            context.addFilter(html5filter, "/yzxfSeller/*", EnumSet.of(DispatcherType.REQUEST));

            server.setHandler(context);

            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Logger.getLogger(getClass()).info(Constants.moduleName + " server started in " + (System.currentTimeMillis() - start) + " ms.");
    }

    public static Properties loadServiceConfig() throws IOException {
        Properties prop = new Properties();
        InputStream in = BaseWebsocketServer.class.getClass().getResourceAsStream("/service.properties");
        prop.load(in);

        Constants.confProerties = prop;
        Constants.moduleType = prop.getProperty("module_type");
        Constants.moduleName = prop.getProperty("module_name");
        Constants.moduleHost = prop.getProperty("module_host");
        Constants.modulePort = Integer.valueOf(prop.getProperty("module_port"));

        //配置文件的优先级最高
        if (prop.containsKey("main_db")) {
            Constants.mainDB = prop.getProperty("main_db");
        }

        if (prop.containsKey("admin_host")) {
            Constants.adminHost = prop.getProperty("admin_host");
        }
        if (prop.containsKey("admin_port")) {
            Constants.adminPort = Integer.valueOf(prop.getProperty("admin_port"));
        }

        if (prop.containsKey("background_run_class")) {
            Constants.backgroundRunClass = prop.getProperty("background_run_class");
        }
        if (prop.containsKey("background_run_time")) {
            Constants.backgroundRunTime = Long.valueOf(prop.getProperty("background_run_time"));
        }
        return prop;
    }


}
