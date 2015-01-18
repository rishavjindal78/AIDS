package org.shunya.serverwatcher;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalJettyRunner {
    final static Logger logger = LoggerFactory.getLogger(LocalJettyRunner.class);

    private static final Server server = new Server(9290);

    public LocalJettyRunner() throws Exception {
        WebAppContext context = new WebAppContext("src/main/webapp", "/");
        context.setResourceBase("src/main/webapp");
        context.setLogUrlOnStart(true);
        context.setContextPath("/testm");
        context.setParentLoaderPriority(true);
        context.configure();
        server.setHandler(context);
    }

    public void start() throws Exception {
        server.start();
    }

    public static void main(String[] args) throws Exception {
        LocalJettyRunner jettyRunner = new LocalJettyRunner();
        jettyRunner.start();
        logger.info("Local Jetty Runner Started");
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