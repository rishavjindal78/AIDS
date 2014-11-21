package org.shunya;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalJettyRunner {
    final static Logger logger = LoggerFactory.getLogger(LocalJettyRunner.class);

    private static final Server server = new Server(9291);

    public LocalJettyRunner() throws Exception {
//        MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
//        server.addBean(mbContainer);

        WebAppContext context = new WebAppContext("src/main/webapp", "/");
        context.setResourceBase("src/main/webapp");
        context.setLogUrlOnStart(true);
//        context.setResourceBase(".");
//        context.setContextPath("/");

//        ServletHandler handler = new ServletHandler();
//        server.setHandler(handler);
//        handler.addServletWithMapping(HelloServlet.class, "/*");

//        HashLoginService loginService = new HashLoginService();
//        loginService.setName("Test Realm");
//        loginService.setConfig("src/test/resources/realm.properties");
//        server.addBean(loginService);

//        context.setParentLoaderPriority(true);

//        context.addServlet(new ServletHolder(new ServerHealthMonitor()), "/healthMonitor/*");
//        context.addServlet(new ServletHolder(new FileUpload()), "/fileUpload/*");
//        context.addServlet(new ServletHolder(new FileDownload()), "/fileDownload/*");
//        context.addServlet(new ServletHolder(new UploadData()), "/upload/*");
//        context.addServlet(new ServletHolder(new DownloadData()), "/download/*");

//        context.addServlet(new ServletHolder(new HelloServlet()),"/hello/*");
        context.configure();
        server.setHandler(context);
    }

    public void start() throws Exception {
        server.start();
        logger.info("Local Jetty Runner Started");
    }

    public static void main(String[] args) throws Exception {
        LocalJettyRunner jettyRunner = new LocalJettyRunner();
        jettyRunner.start();
        System.in.read();
        jettyRunner.stop();
        System.exit(0);
    }

    public void join() throws InterruptedException {
        server.join();
    }

    public void stop() throws Exception {
        server.stop();
        logger.info("Local Jetty Runner Stopped");
    }
}