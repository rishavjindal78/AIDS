package org.shunya.server.services;

import org.shunya.shared.TaskContext;
import org.shunya.server.model.Agent;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@Service
public class RestClient {
    private static final Logger logger = Logger.getLogger(RestClient.class.getName());
    private RestTemplate restTemplate = new RestTemplate();

    public boolean ping(Agent agent) {
        ResponseEntity<String> entity = restTemplate.getForEntity(agent.getBaseUrl() + "/agent/ping", String.class);
//        String body = entity.getBody();
        if (entity.getStatusCode() == HttpStatus.ACCEPTED) {
            return true;
        }
        return false;
    }

    public String agentVersion(Agent agent) {
        ResponseEntity<String> entity = restTemplate.getForEntity(agent.getBaseUrl() + "/agent/version", String.class);
//        String body = entity.getBody();
        if (entity.getStatusCode() == HttpStatus.ACCEPTED) {
            return entity.getBody();
        }
        return "NA";
    }

    public void submitTaskToAgent(TaskContext taskContext, Agent agent) {
        if (agent.getBaseUrl() == null || agent.getBaseUrl().isEmpty())
            throw new RuntimeException("No Host Configured for this Agent " + agent.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Content-Type", "application/json; charset=utf-8");
        headers.set("Accept", "application/json; charset=utf-8");
        HttpEntity httpEntity = new HttpEntity<>(taskContext, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(agent.getBaseUrl() + "/agent/submitTaskStep", httpEntity, String.class);
        logger.fine(() -> "TaskStep sent to Agent for execution " + responseEntity.getBody());
    }

    public String getMemoryLogs(long stepRunId, Agent agent, long start) {
        if (agent.getBaseUrl() == null || agent.getBaseUrl().isEmpty())
            throw new RuntimeException("No Host Configured for this Agent " + agent.getName());
        ResponseEntity<String> entity = restTemplate.getForEntity(agent.getBaseUrl() + "/agent/getMemoryLogs/{taskRunId}?start={start}", String.class, stepRunId, start);
        String body = entity.getBody();
        logger.fine(() -> "Logs fetched from the Agent");
        return body;
    }

    public Boolean checkStepRunning(long stepRunId, Agent agent) {
        if (agent.getBaseUrl() == null || agent.getBaseUrl().isEmpty())
            throw new RuntimeException("No Host Configured for this Agent " + agent.getName());
        ResponseEntity<Boolean> entity = restTemplate.getForEntity(agent.getBaseUrl() + "/agent/isStepRunning/{taskRunId}", Boolean.class, stepRunId);
        return entity.getBody();
    }
}
