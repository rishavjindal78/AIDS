package org.shunya.server.services;

import org.shunya.server.AgentStatus;
import org.shunya.server.model.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class AgentStatusService {
    private static final Logger logger = LoggerFactory.getLogger(AgentStatusService.class);
    private ConcurrentMap<Agent, AgentStatus> statusCache = new ConcurrentHashMap<>();

    @Autowired
    private DBService dbService;

    @Autowired
    private RestClient restClient;

    @Scheduled(cron = "0 0/2 * * * ?")
    public void checkStatus() {
        logger.info("Checking Agent Status");
        List<Agent> agents = dbService.listAgents();
        agents.parallelStream().forEach(agent -> {
            try {
                boolean status = restClient.ping(agent);
                if (status)
                    statusCache.put(agent, AgentStatus.UP);
                else
                    statusCache.put(agent, AgentStatus.DOWN);
            } catch (Exception e) {
                statusCache.put(agent, AgentStatus.DOWN);
            }
        });
    }

    public void getStatus(List<Agent> agents) {

    }

    public AgentStatus getStatus(Agent agent) {
        return statusCache.get(agent);
    }
}
