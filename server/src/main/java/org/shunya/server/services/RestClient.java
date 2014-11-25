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
        ResponseEntity<String> entity = restTemplate.getForEntity(agent.getBaseUrl() + "rest/agent/ping", String.class);
//        String body = entity.getBody();
        if(entity.getStatusCode() == HttpStatus.ACCEPTED){
            return true;
        }
        return false;
    }

    public void submitTaskToAgent(TaskContext taskContext , Agent agent) {
        if(agent.getBaseUrl()==null || agent.getBaseUrl().isEmpty())
            throw new RuntimeException("No Host Configured for this Agent " + agent.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Content-Type", "application/json; charset=utf-8");
        headers.set("Accept", "application/json; charset=utf-8");
        HttpEntity httpEntity = new HttpEntity<>(taskContext, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(agent.getBaseUrl() + "rest/agent/submitTaskStep", httpEntity, String.class);
        logger.fine(() -> "TaskStep sent to Agent for execution " + responseEntity.getBody());
    }


}
