package org.shunya.server;

import javax.servlet.AsyncContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@WebListener
public class SlowWebService implements ServletContextListener {
 
    public void contextInitialized(final ServletContextEvent sce) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startExecutor(sce);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
//        t.start();
    }

    private void startExecutor(ServletContextEvent sce) throws InterruptedException {
        System.out.println("Context Initialized SlowWebService");
        Queue<AsyncContext> jobQueue = new ConcurrentLinkedQueue<AsyncContext>();
        sce.getServletContext().setAttribute("slowWebServiceJobQueue", jobQueue);
        // pool size matching Web services capacity
        Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        while(true)
        {
            if(!jobQueue.isEmpty())
            {
                final AsyncContext aCtx = jobQueue.poll();
                executor.execute(new Runnable(){
                    public void run() {
                        ServletRequest request = aCtx.getRequest();
                        String agentId = request.getParameter("agentId");
                        // get parameteres
                        // invoke a Web service endpoint
                        // set results
                        try {
                            aCtx.getResponse().getWriter().println("This is working fine");
                            aCtx.getResponse().getWriter().flush();
                            aCtx.complete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                        aCtx.dispatch("/result.jsp");
                    }
                });
            }
            Thread.sleep(2000);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Context Destroyed");
    }
}