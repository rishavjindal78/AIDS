package org.shunya.server.poc;

import org.shunya.server.poc.AgentContextListner;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Config implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        System.out.println("Context Initialized");
        AgentContextListner listner = new AgentContextListner();
        event.getServletContext().setAttribute("listner", listner);
//        scheduler.scheduleAtFixedRate(new UpdateCounts(), 0, 1, TimeUnit.DAYS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        scheduler.shutdownNow();
    }

}