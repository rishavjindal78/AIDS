package org.shunya.server;

import org.shunya.shared.TaskContext;
import org.shunya.shared.model.Agent;
import org.springframework.stereotype.Service;

import javax.servlet.AsyncContext;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AgentContextListner {
    final ExecutorService executorService = Executors.newCachedThreadPool();
    Map<Agent, AsyncContext> agentAsyncContextMap = new ConcurrentHashMap<>(100);

    public void register(Agent agent, AsyncContext asyncContext) {
        agentAsyncContextMap.put(agent, asyncContext);
        System.out.println("agent registered= " + agent);
    }

    public void runTaskOnAgent(TaskContext taskContext, Agent agent) {
        AsyncContext asyncContext = agentAsyncContextMap.get(agent);
        executorService.submit(() -> {
            try {
                asyncContext.getResponse().getWriter().write("New Job Received @ " + new Date());
//                    asyncContext.getResponse().getWriter().flush();
                asyncContext.complete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
