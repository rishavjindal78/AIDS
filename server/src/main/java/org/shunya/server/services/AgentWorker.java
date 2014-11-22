package org.shunya.server.services;

import org.shunya.shared.TaskContext;
import org.shunya.shared.model.Agent;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class AgentWorker {
    private static final Logger logger = Logger.getLogger(AgentWorker.class.getName());
    private RestTemplate restTemplate = new RestTemplate();

    public void ping(Agent agent) {
        ResponseEntity<String> entity = restTemplate.getForEntity(agent.getBaseUrl() + "rest/agent/ping", String.class);
        String body = entity.getBody();
        System.out.println("ping = " + body);
    }

    public void submitTaskToAgent(TaskContext taskContext) {
        if(taskContext.getTaskStepRun().getAgent().getBaseUrl()==null || taskContext.getTaskStepRun().getAgent().getBaseUrl().isEmpty())
            throw new RuntimeException("No Host Configured for this Agent " + taskContext.getTaskStepRun().getAgent().getName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Content-Type", "application/json; charset=utf-8");
        headers.set("Accept", "application/json; charset=utf-8");
        HttpEntity httpEntity = new HttpEntity<>(taskContext, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(taskContext.getTaskStepRun().getAgent().getBaseUrl() + "rest/agent/submitTaskStep", httpEntity, String.class);
        logger.fine(() -> "TaskStep sent to Agent for execution " + responseEntity.getBody());
    }
}
